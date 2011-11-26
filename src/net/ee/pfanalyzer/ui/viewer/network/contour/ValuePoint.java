package net.ee.pfanalyzer.ui.viewer.network.contour;

import java.awt.geom.Point2D;

public class ValuePoint extends Point2D.Double {
	
	private double value;

	public ValuePoint(Point2D.Double point, double value) {
		this(point.getX(), point.getY(), value);
	}

	public ValuePoint(double x, double y, double value) {
		super(x, y);
		this.value = value;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
}