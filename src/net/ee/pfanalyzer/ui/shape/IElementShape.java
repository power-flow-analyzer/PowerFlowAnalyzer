package net.ee.pfanalyzer.ui.shape;

import java.awt.Shape;

public interface IElementShape {
	
	boolean[] fillShape();
	
	boolean[] useCustomStroke();

//	Shape getShape();
	
	Shape[] getTranslatedShapes(double x1, double y1, double x2, double y2, boolean highlighted);
	
	double[][] getAdditionalDecorationsPlace();
	
	double getSize();
}
