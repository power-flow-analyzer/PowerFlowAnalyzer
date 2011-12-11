package net.ee.pfanalyzer.ui.shape;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

public class TransformerShape implements IElementShape {
	
	public static String ID = "shape.branch.transformer";
	
	private double m_size;
	private double[][] decorationsPlaces;

	public TransformerShape(double size) {
		m_size = size;
	}
	
	@Override
	public double getSize() {
		return m_size;
	}
	
	@Override
	public Shape[] getTranslatedShapes(double x1, double y1, double x2, double y2, boolean highlighted) {
		if(Double.isInfinite(x2) || Double.isInfinite(y2))
			return null;
		double halfSize = m_size / 2.0;
		double angle = Math.atan((y2 - y1) / (x2 - x1));
		if(x1 > x2) {
			angle += Math.PI;
		}
		double xDiff = Math.abs(x1 - x2);
		double yDiff = Math.abs(y1 - y2);
		double length = Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));
		double centerX = (length) / 2.0;
		double centerX1 = centerX - m_size * (1.0 / 3.0);
		double centerX2 = centerX + m_size * (1.0 / 3.0);
		Ellipse2D.Double circle1 = new Ellipse2D.Double(centerX1 - halfSize, -halfSize, m_size, m_size);
		Ellipse2D.Double circle2 = new Ellipse2D.Double(centerX2 - halfSize, -halfSize, m_size, m_size);
		Line2D.Double line1 = new Line2D.Double(0, 0, centerX1 - halfSize, 0);
		Line2D.Double line2 = new Line2D.Double(centerX2 + halfSize, 0, length, 0);
		// create transformations
		AffineTransform transformation = AffineTransform.getTranslateInstance(x1, y1);
		transformation.concatenate(AffineTransform.getRotateInstance(angle));
		// determine place for additional decorations
		decorationsPlaces = new double[2][2];
//		decorationsPlaces[0][0] = 
//		Line2D.Double firstPlace = new Line2D.Double()
		double[] places = new double[2 * 2];
		places[0] = 0;
		places[1] = 0;
		places[2] = centerX1 - halfSize;
		places[3] = 0;
		double[] transformedPlaces = new double[places.length];
		transformation.transform(places, 0, transformedPlaces, 0, places.length / 2);
		decorationsPlaces = new double[][] { transformedPlaces };
		// transform shapes
		return new Shape[] {
				transformation.createTransformedShape(line1), 
				transformation.createTransformedShape(circle1),
				transformation.createTransformedShape(circle2),
				transformation.createTransformedShape(line2)
		};
	}
	
	@Override
	public double[][] getAdditionalDecorationsPlace() {
		return decorationsPlaces;
	}
	
	@Override
	public boolean[] fillShape() {
		return new boolean[] { false, false, false, false };
	}
	
	@Override
	public boolean[] useCustomStroke() {
		return new boolean[] { true, false, false, true };
	}
}
