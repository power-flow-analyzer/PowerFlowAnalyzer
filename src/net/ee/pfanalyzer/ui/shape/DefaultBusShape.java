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
import java.awt.geom.Ellipse2D;

public class DefaultBusShape implements IElementShape {
	
	public static String ID = "shape.bus.default";
	
	private double m_size;
	
	public DefaultBusShape(double size) {
		m_size = size;
	}
	
	@Override
	public double getSize() {
		return m_size;
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
