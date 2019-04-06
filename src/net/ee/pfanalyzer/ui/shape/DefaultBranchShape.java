/*******************************************************************************
 * Copyright 2019 Markus Gronau
 * 
 * This file is part of PowerFlowAnalyzer.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
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
