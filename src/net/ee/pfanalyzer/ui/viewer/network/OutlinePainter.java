package net.ee.pfanalyzer.ui.viewer.network;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.ee.pfanalyzer.model.DatabaseChangeEvent;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.ui.CaseViewer;
import net.ee.pfanalyzer.ui.viewer.INetworkDataViewer;
import net.ee.pfanalyzer.ui.viewer.IPaintListener;

public class OutlinePainter implements IPaintListener {

	public final static String PAINT_ID = "paint.network.outline";
	
	private NetworkMapViewer viewer;
	private boolean isActive = true;
	private List<Outline> outlines;
	private Collection<Outline> lastOutlineList;

	public OutlinePainter(NetworkMapViewer viewer) {
		this.viewer = viewer;
	}
	
	@Override
	public void paint(Graphics2D g2d) {
		for (Outline outline : getOutlines()) {
			Color outlineColor = outline.getBorderColor();
			Color backgroundColor = outline.getBackgroundColor();
			int[][] coords = outline.getScreenPoints();
			if(coords.length == 0)
				continue;
			double lastX = viewer.getX(coords[0]);
			double lastY = viewer.getY(coords[0]);
			GeneralPath polygon = new GeneralPath();
			polygon.moveTo(lastX, lastY);
			for (int i = 1; i < coords.length; i++) {
				// check if polygon must be closed
				if(coords[i][0] == -1 && coords[i][1] == -1) {
					if(polygon != null) {
						polygon.closePath();
						drawOutline(g2d, polygon, outlineColor, backgroundColor);
						// new polygon will be created next round
						polygon = null;
					}
					continue;
				}
				double x = viewer.getX(coords[i]);
				double y = viewer.getY(coords[i]);
				if(polygon == null) {
					polygon = new GeneralPath();
					polygon.moveTo(x, y);
					
					continue;
				}
//				g2d.draw(new Line2D.Double(lastX, lastY, x, y));
//				lastX = x;
//				lastY = y;
				polygon.lineTo(x, y);
			}
			if(polygon != null) {
				polygon.closePath();
				drawOutline(g2d, polygon, outlineColor, backgroundColor);
			}
		}
	}
	
	private void drawOutline(Graphics2D g2d, GeneralPath polygon, Color outlineColor, Color backgroundColor) {
		if(backgroundColor != null) {
			g2d.setColor(backgroundColor);
			g2d.fill(polygon);
		}
		if(outlineColor != null)
			g2d.setColor(outlineColor);
		else
			g2d.setColor(Color.LIGHT_GRAY);
		g2d.draw(polygon);
	}
	
	@Override
	public void update() {
//		System.out.println("updateOutlines");
		outlines = new ArrayList<Outline>();
		CaseViewer container = viewer.getNetworkContainer();
		lastOutlineList = container.getOutlines();
		for (Outline outline : lastOutlineList) {
			String paramID = "OUTLINE." + outline.getOutlineID();
			NetworkParameter param = getViewer().getViewerConfiguration().getParameterValue(paramID);
			if(param == null)
				param = ModelDBUtils.getParameterValue(outline.getOutlineData(), "ENABLED");
			boolean defaultEnabled = param == null ? false : Boolean.parseBoolean(param.getValue());
			if(getViewer().getViewerConfiguration().getBooleanParameter(paramID, defaultEnabled))
				outlines.add(outline);
		}
//		repaint();
	}
	
	private List<Outline> getOutlines() {
		if(outlines == null)
			update();
		CaseViewer container = viewer.getNetworkContainer();
		if(container != null && container.getOutlines() != lastOutlineList)
			update();
		return outlines;
	}
	
	@Override
	public INetworkDataViewer getViewer() {
		return viewer;
	}
	
	@Override
	public String getPaintID() {
		return PAINT_ID;
	}

	@Override
	public boolean needsUpdate(DatabaseChangeEvent event) {
		return event.getParameterID().startsWith("OUTLINE.");
	}

	@Override
	public boolean isActive() {
		return isActive;
	}

	@Override
	public void setActive(boolean flag) {
		isActive = flag;
	}
}
