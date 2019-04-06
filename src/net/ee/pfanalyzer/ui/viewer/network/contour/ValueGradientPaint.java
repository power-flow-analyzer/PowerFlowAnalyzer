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
package net.ee.pfanalyzer.ui.viewer.network.contour;

import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

public class ValueGradientPaint implements Paint {
	
	private ValuePoint[] points;
	private ValueGradientContext context;
	private ContourDiagramSettings settings;

	public ValueGradientPaint(ValuePoint[] points, ContourDiagramSettings settings) {
		this.points = points;
		this.settings = settings;
	}

	public PaintContext createContext(ColorModel cm,
		Rectangle deviceBounds, Rectangle2D userBounds,
		AffineTransform at, RenderingHints hints) {
		ValuePoint[] transformedPoints = new ValuePoint[points.length];
		for (int i = 0; i < points.length; i++) {
			if(points[i] == null)
				continue;
			transformedPoints[i] = new ValuePoint(points[i], points[i].getValue());
			at.transform(points[i], transformedPoints[i]);
		}
		context = new ValueGradientContext(transformedPoints, settings);
		return context;
	}

	public int getTransparency() {
		return TRANSLUCENT;
	}
}
