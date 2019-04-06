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

import java.awt.PaintContext;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class ValueGradientContext implements PaintContext {
	
	public final static int ACTION_CUT_VALUES = 0;
	public final static int ACTION_OMIT_VALUES = 1;
	
	private ContourDiagramSettings settings;
	private ValuePoint[] points;

	public ValueGradientContext(ValuePoint[] points, ContourDiagramSettings settings) {
		this.points = points;
		this.settings = settings;
	}

	public void dispose() {
	}

	public ColorModel getColorModel() {
		return ColorModel.getRGBdefault();
	}

	public Raster getRaster(int x, int y, int width, int height) {
		double maxValue = settings.getMaxValue();
		double minValue = settings.getMinValue();
		double middleValue = settings.getMiddleValue();
		double maxDistance = settings.getMaxDistance();
		double maxRelDistance = settings.getMaxRelDistance();
		int outOfBoundsAction = settings.getOutOfBoundsAction();
		WritableRaster raster = getColorModel().createCompatibleWritableRaster(width, height);
		if(settings.isIncomplete())
			return raster;
		int[] pixels = new int[width * height * 4];
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				double ratio = 0;
				for (int p = 0; p < points.length; p++) {
					if(points[p] == null)
						continue;
					// limit value if necessary or omit this point
					double value = points[p].getValue();
					if(outOfBoundsAction == ACTION_CUT_VALUES) {
						if(value > maxValue)
							value = maxValue;
						else if(value < minValue)
							value = minValue;
					} else if(outOfBoundsAction == ACTION_OMIT_VALUES) {
						if(value > maxValue || value < minValue)
							continue;
					}
					
					// calculate relative value
					double relValue;
					if(value >= middleValue)
						relValue = Math.abs((value - middleValue) / (maxValue - middleValue));
					else
						relValue = -Math.abs((middleValue - value) / (middleValue - minValue));
					
					// limit distance if necessary
					double distance = points[p].distance(x + i, y + j);
					if(distance > maxDistance)
						distance = maxDistance;
					
					// calculate relative value
					double relDistance = ((maxDistance - distance) / maxDistance);
					
					// calculate ratio for this point
					double newRatio = relDistance * relValue;
					
					// draw small values at least in direct sourroundings of point
					if(relDistance > maxRelDistance) {
						ratio = newRatio;
						break;
					}
					
					// check if ratio for this point is the highest until now
					if(Math.abs(newRatio) > Math.abs(ratio)) {
						ratio = newRatio;
					}
				}

				// create a color based on the ratio
				int[] argb = getColorProvider().getARGB(0, 1, ratio);
				
				// change this pixel to the new color
				int pixelIndex = (j * width + i) * 4;
				pixels[pixelIndex + 0] = argb[0];// red
				pixels[pixelIndex + 1] = argb[1];// green
				pixels[pixelIndex + 2] = argb[2];// blue
				pixels[pixelIndex + 3] = argb[3];// alpha
			}
		}
		raster.setPixels(0, 0, width, height, pixels);

		return raster;
	}
	
	public ColorProvider getColorProvider() {
		return settings.getColorProvider();
	}
}
