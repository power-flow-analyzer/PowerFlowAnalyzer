package net.ee.pfanalyzer.ui.viewer.network.contour;

import java.awt.Graphics2D;
import java.util.List;

import net.ee.pfanalyzer.model.Bus;
import net.ee.pfanalyzer.model.DatabaseChangeEvent;
import net.ee.pfanalyzer.ui.viewer.INetworkDataViewer;
import net.ee.pfanalyzer.ui.viewer.IPaintListener;
import net.ee.pfanalyzer.ui.viewer.network.NetworkMapViewer;

public class ContourPainter implements IPaintListener {

	public final static String PAINT_ID = "paint.network.contour";
	
	private NetworkMapViewer viewer;
	private boolean isActive = true;
	private ContourDiagramSettings settings;

	public ContourPainter(NetworkMapViewer viewer, ContourDiagramSettings settings) {
		this.viewer = viewer;
		this.settings = settings;
	}
	
	private ValuePoint[] generatePoints() {
		String propName = "VM";
		List<Bus> busses = viewer.getNetwork().getBusses();
		ValuePoint[] points = new ValuePoint[busses.size()];
		for (int i = 0; i < points.length; i++) {
			Bus bus = busses.get(i);
			Double value = bus.getDoubleParameter(propName);
			if(value == null)
				continue;
			double[] coords = viewer.getBusXYDouble(bus.getBusNumber());
			double x = coords[0];
			double y = coords[1];
			points[i] = new ValuePoint(x, y, value);
		}
		return points;
	}
	
	@Override
	public void paint(Graphics2D g2d) {
		ValuePoint[] points = generatePoints();
		g2d.setPaint(new ValueGradientPaint(points, settings));
		g2d.fillRect(0, 0, viewer.getWidth(), viewer.getHeight());
	}
	
	@Override
	public void update() {
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
		return false;
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
