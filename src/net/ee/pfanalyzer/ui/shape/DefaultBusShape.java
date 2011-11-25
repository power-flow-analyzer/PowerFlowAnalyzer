package net.ee.pfanalyzer.ui.shape;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class DefaultBusShape implements IElementShape {
	
	public static String ID = "shape.bus.default";
	
	private double m_size;
	
	public DefaultBusShape(double size) {
		m_size = size;
	}
	
	@Override
	public Shape[] getTranslatedShapes(double x1, double y1, double x2, double y2, boolean highlighted) {
		double halfSize = m_size / 2.0;
		Ellipse2D.Double baseShape = new Ellipse2D.Double(
				x1 - halfSize, y1 - halfSize, m_size, m_size);
		if(highlighted)
			return new Shape[] {
				baseShape,
				new Ellipse2D.Double(
						x1 - halfSize - 3.0, y1 - halfSize - 3.0, 
						m_size + 6.0, m_size + 6.0),
			};
		else
			return new Shape[] { baseShape };
	}
	
	@Override
	public double[][] getAdditionalDecorationsPlace() {
		return null;
	}

	@Override
	public boolean[] fillShape() {
		return new boolean[] { true, false, false };
	}
	
	@Override
	public boolean[] useCustomStroke() {
		return new boolean[] { false, false, false };
	}
}
