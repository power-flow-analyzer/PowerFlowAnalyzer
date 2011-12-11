package net.ee.pfanalyzer.ui.shape;

import java.awt.Shape;
import java.awt.geom.Line2D;

public class DefaultBranchShape implements IElementShape {
	
	public static String ID = "shape.branch.default";
	
	private double[][] decorationsPlaces;
	
	public DefaultBranchShape(int size) {
	}
	
	@Override
	public double getSize() {
		return -1;
	}
	
	@Override
	public Shape[] getTranslatedShapes(double x1, double y1, double x2, double y2, boolean highlighted) {
		if(Double.isInfinite(x2) || Double.isInfinite(y2))
			return null;
		decorationsPlaces = new double[][] { { x1, y1, x2, y2 } };
		return new Shape[] { new Line2D.Double(x1, y1, x2, y2) };
	}
	
	@Override
	public double[][] getAdditionalDecorationsPlace() {
		return decorationsPlaces;
	}

	@Override
	public boolean[] fillShape() {
		return new boolean[] { false };
	}
	
	@Override
	public boolean[] useCustomStroke() {
		return new boolean[] { true };
	}
}
