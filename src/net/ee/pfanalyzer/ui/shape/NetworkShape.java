package net.ee.pfanalyzer.ui.shape;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

public class NetworkShape implements IElementShape {
	
	public static String ID = "shape.network";

	private GeneralPath networkShape;
	private double size;
	
	public NetworkShape(double size) {
		this.size = size;
		networkShape = new GeneralPath();
		int stepSize = Math.max(1, (int) (size / 3.0));
		
		double x = -size / 2.0;
		double y = -size / 2.0;
		int counter = 0;
		for (int step = 1; step * stepSize < 2 * size; step++) {
			boolean upperHalf = counter >= size;
			if(upperHalf) {
				networkShape.append(new Line2D.Double(x + counter - size, y, x + size, y + size - counter + size), false);
				networkShape.append(new Line2D.Double(x + size, y + counter - size, x - size + counter, y + size), false);
			} else {
				networkShape.append(new Line2D.Double(x, y + size - counter, x + counter, y + size), false);
				networkShape.append(new Line2D.Double(x, y + counter, x + counter, y), false);
			}
			counter += stepSize;
		}
		networkShape.append(new Line2D.Double(x, y, x + size, y), false);// oben
		networkShape.append(new Line2D.Double(x, y, x, y + size), false);// links
		networkShape.append(new Line2D.Double(x, y + size, x + size, y + size), false);// unten
		networkShape.append(new Line2D.Double(x + size, y, x + size, y + size), false);// rechts
	}
	
	@Override
	public double getSize() {
		return size;
	}
	
	@Override
	public Shape[] getTranslatedShapes(double x1, double y1, double x2, double y2, boolean highlighted) {
		Shape connectionLine = null;
		if(Double.isInfinite(x2) == false && Double.isInfinite(y2) == false) {
			// determine where the line should be connected to the marker
			double markerOutlineX = x1;
			double markerOutlineY = y1;
			double xDiff = x2 - x1;
			double yDiff = y2 - y1;
			if(xDiff < yDiff || yDiff == 0)
				markerOutlineX += (Math.signum(xDiff) == 1 ? 1 : -1) * size / 2.0;
			else
				markerOutlineY += (Math.signum(yDiff) == 1 ? 1 : -1) * size / 2.0;
			// create the line
			connectionLine = new Line2D.Double(markerOutlineX, markerOutlineY, x2, y2);
		}
		AffineTransform transformation = AffineTransform.getTranslateInstance(x1, y1);
		return new Shape[] {networkShape.createTransformedShape(transformation), connectionLine };
	}
	
	@Override
	public double[][] getAdditionalDecorationsPlace() {
		return null;
	}

	@Override
	public boolean[] fillShape() {
		return new boolean[] { false };
	}
	
	@Override
	public boolean[] useCustomStroke() {
		return new boolean[] { false };
	}
}
