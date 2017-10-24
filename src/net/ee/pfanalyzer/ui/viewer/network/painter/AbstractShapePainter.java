package net.ee.pfanalyzer.ui.viewer.network.painter;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.DatabaseChangeEvent;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.ui.shape.ElementShapeProvider;
import net.ee.pfanalyzer.ui.shape.IElementShape;
import net.ee.pfanalyzer.ui.util.MapBoundingBox;
import net.ee.pfanalyzer.ui.viewer.INetworkDataViewer;
import net.ee.pfanalyzer.ui.viewer.IPaintListener;
import net.ee.pfanalyzer.ui.viewer.network.NetworkMapViewer;

public abstract class AbstractShapePainter implements IPaintListener {
	
	protected Network network;
	protected NetworkMapViewer viewer;
	private boolean isActive = true;
	protected MapBoundingBox boundingBox = new MapBoundingBox();
	
	protected ElementShapeProvider shapeProvider;
	protected Rectangle2D.Double visibleRect = new Rectangle2D.Double(0, 0, 0, 0);
	
	protected float fontSize = -1;
	protected Stroke[] strokesNormal, strokesBold;
	protected Stroke otherStrokeNormal, otherStrokeBold;
	
	public abstract void paint(Graphics2D g2d);
	
	public abstract void update();
	
	public abstract AbstractNetworkElement getObjectFromScreen(int x, int y);
	
	protected AbstractShapePainter(Network network, NetworkMapViewer viewer) {
		this.network = network;
		this.viewer = viewer;
		shapeProvider = new ElementShapeProvider();
		createStrokes(1.0f, 2.5f);
	}

	protected Network getNetwork() {
		return this.network;
	}
	
	protected IElementShape drawShape(AbstractNetworkElement element, Graphics2D g2d, 
			double x1, double y1, double x2, double y2, boolean highlighted, String label, Stroke customStroke) {
		IElementShape elementShape = shapeProvider.getShape(element.getShapeID());
		if(elementShape != null) {
			Shape[] shapes = elementShape.getTranslatedShapes(x1, y1, x2, y2, highlighted);
			if(shapes == null)
				return elementShape;
			boolean[] fillShapes = elementShape.fillShape();
			boolean[] useCustomStrokes = elementShape.useCustomStroke();
			for (int shapeIndex = 0; shapeIndex < shapes.length; shapeIndex++) {
				Shape shape = shapes[shapeIndex];
				if(shape == null)
					continue;
				if(customStroke != null && useCustomStrokes.length > shapeIndex && useCustomStrokes[shapeIndex]) {
					g2d.setStroke(customStroke);
				} else {
					if(highlighted)
						g2d.setStroke(strokesBold[0]);
					else
						g2d.setStroke(strokesNormal[0]);
				}
				if(fillShapes.length > shapeIndex && fillShapes[shapeIndex])
					g2d.fill(shape);
				else
					g2d.draw(shape);
			}
		}
		// draw the marker's name
		if(label != null) {
			if(fontSize > -1)
				g2d.setFont(g2d.getFont().deriveFont(fontSize));
			double offset = elementShape != null ? elementShape.getSize() / 1.5 : 15;
			g2d.drawString(label, (int) (x1+offset), (int) (y1+offset+g2d.getFont().getSize()/2));
		}
		return elementShape;
	}
	
	@Override
	public INetworkDataViewer getViewer() {
		return viewer;
	}

	@Override
	public boolean needsUpdate(DatabaseChangeEvent event) {
		return true;
	}

	@Override
	public boolean isActive() {
		return isActive;
	}

	@Override
	public void setActive(boolean flag) {
		isActive = flag;
	}

	@Override
	public MapBoundingBox getBoundingBox() {
		return boundingBox;
	}

	private void createStrokes(float widthNormal, float widthBold) {
		strokesNormal = new Stroke[] {
				// 380kV
				new BasicStroke(widthNormal),
				// 220kV
				new BasicStroke(widthNormal, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                        10.0f, new float[] { 10.0f }, 0.0f), 
                // 110kV
				new BasicStroke(widthNormal, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                        1.0f, new float[] { 2.0f, 7.0f }, 0.0f)
		};
		strokesBold = new Stroke[] {
				// 380kV
				new BasicStroke(widthBold),
				// 220kV
				new BasicStroke(widthBold, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                        10.0f, new float[] { 10.0f }, 0.0f), 
                // 110kV
				new BasicStroke(widthBold, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                        1.0f, new float[] { 2.0f, 7.0f }, 0.0f)
		};
		otherStrokeNormal = new BasicStroke(widthNormal, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                1.0f, new float[] { 3.0f, 2.0f, 5.0f }, 0.0f);
		otherStrokeBold = new BasicStroke(widthBold, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                1.0f, new float[] { 3.0f, 2.0f, 3.0f }, 0.0f);
	}
	
	protected boolean isOutsideView(double x1, double y1, double x2, double y2, double count) {
		double xn = (x2 - x1) / count;
		double yn = (y2 - y1) / count;
		for (double i = 1; i < count; i++) {
			if(isOutsideView(x1 + xn * i, y1 + yn * i) == false)
				return false;
		}
		return true;
	}
	
	protected boolean isOutsideView(double x, double y) {
		int space = 10;
		return x < -space || x > viewer.getWidth() + space || y < -space || y > viewer.getHeight() + space;
	}
}
