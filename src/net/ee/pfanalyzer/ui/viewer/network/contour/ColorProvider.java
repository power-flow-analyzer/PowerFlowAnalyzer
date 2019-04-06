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

import java.awt.Color;

public abstract class ColorProvider {
	
	private double colorSteps = 0.0;
	
	private Color maxColor = new Color(255, 0, 0);
	private Color upperHalfColor = new Color(255, 255, 0);
	private Color middleColor = new Color(255, 255, 255);
	private Color lowerHalfColor = new Color(0, 255, 255);
	private Color minColor = new Color(0, 0, 255);
	private int transparency = 185;
	
	protected ColorProvider() {
	}
	
	protected ColorProvider(ColorProvider oldValues) {
		setColorSteps(oldValues.getColorSteps());
		setMaxColor(oldValues.getMaxColor());
		setUpperHalfColor(oldValues.getUpperHalfColor());
		setMiddleColor(oldValues.getMiddleColor());
		setLowerHalfColor(oldValues.getLowerHalfColor());
		setMinColor(oldValues.getMinColor());
		setTransparency(oldValues.getTransparency());
	}
	
	public int[] getARGB(double min, double max, double value) {
		double ratio = getRatio(value / max);
		if (ratio > 1.0)
			ratio = 1.0;
		return getARGB(ratio);
	}
	
	protected abstract int[] getARGB(double ratio);
	
	public Color getColor(double ratio) {
		int[] data = getARGB(getRatio(ratio));
		return new Color(data[0], data[1], data[2], data[3]);
	}
	
	public double getRatio(double ratio) {
		if(colorSteps <= 0)
			return ratio;
		int temp = (int) Math.round(ratio * colorSteps);
		return temp / colorSteps;
	}
	
	public double getColorSteps() {
		return colorSteps;
	}

	public void setColorSteps(double colorSteps) {
		this.colorSteps = colorSteps;
	}

	public Color getMaxColor() {
		return maxColor;
	}

	public void setMaxColor(Color maxColor) {
		this.maxColor = maxColor;
	}

	public Color getUpperHalfColor() {
		return upperHalfColor;
	}

	public void setUpperHalfColor(Color upperHalfColor) {
		this.upperHalfColor = upperHalfColor;
	}

	public Color getMiddleColor() {
		return middleColor;
	}

	public void setMiddleColor(Color middleColor) {
		this.middleColor = middleColor;
	}

	public Color getLowerHalfColor() {
		return lowerHalfColor;
	}

	public void setLowerHalfColor(Color lowerHalfColor) {
		this.lowerHalfColor = lowerHalfColor;
	}

	public Color getMinColor() {
		return minColor;
	}

	public void setMinColor(Color minColor) {
		this.minColor = minColor;
	}

	public int getTransparency() {
		return transparency;
	}

	public void setTransparency(int transparency) {
		this.transparency = transparency;
	}

	public static class ComplexColorProvider extends ColorProvider {
		
		public ComplexColorProvider() {
		}
		
		public ComplexColorProvider(ColorProvider oldValues) {
			super(oldValues);
		}
		
		protected int[] getARGB(double ratio) {
			int[] result = new int[4];
			if(ratio >= 0) {
				if(ratio > 0.5) {
					result[0] = (int) (getUpperHalfColor().getRed() + (ratio - 0.5) * 2 * (getMaxColor().getRed() - getUpperHalfColor().getRed()));
					result[1] = (int) (getUpperHalfColor().getGreen() + (ratio - 0.5) * 2 * (getMaxColor().getGreen() - getUpperHalfColor().getGreen()));
					result[2] = (int) (getUpperHalfColor().getBlue() + (ratio - 0.5) * 2 * (getMaxColor().getBlue() - getUpperHalfColor().getBlue()));
				} else {
					result[0] = (int) (getMiddleColor().getRed() + ratio * 2.0 * (getUpperHalfColor().getRed() - getMiddleColor().getRed()));
					result[1] = (int) (getMiddleColor().getGreen() + ratio * 2.0 * (getUpperHalfColor().getGreen() - getMiddleColor().getGreen()));
					result[2] = (int) (getMiddleColor().getBlue() + ratio * 2.0 * (getUpperHalfColor().getBlue() - getMiddleColor().getBlue()));
				}
			} else {
				if(ratio < -0.5) {
					result[0] = (int) (getLowerHalfColor().getRed() + (-ratio - 0.5) * 2 * (getMinColor().getRed() - getLowerHalfColor().getRed()));
					result[1] = (int) (getLowerHalfColor().getGreen() + (-ratio - 0.5) * 2 * (getMinColor().getGreen() - getLowerHalfColor().getGreen()));
					result[2] = (int) (getLowerHalfColor().getBlue() + (-ratio - 0.5) * 2 * (getMinColor().getBlue() - getLowerHalfColor().getBlue()));
				} else {
					result[0] = (int) (getMiddleColor().getRed() - ratio * 2.0 * (getLowerHalfColor().getRed() - getMiddleColor().getRed()));
					result[1] = (int) (getMiddleColor().getGreen() - ratio * 2.0 * (getLowerHalfColor().getGreen() - getMiddleColor().getGreen()));
					result[2] = (int) (getMiddleColor().getBlue() - ratio * 2.0 * (getLowerHalfColor().getBlue() - getMiddleColor().getBlue()));
				}
			}
			result[3] = getTransparency();
			return result;
		}
	}
	
	public static class SimpleColorProvider extends ColorProvider {
		
		public SimpleColorProvider() {
		}
		
		public SimpleColorProvider(ColorProvider oldValues) {
			super(oldValues);
		}
		
		protected int[] getARGB(double ratio) {
			int[] result = new int[4];
			if(ratio >= 0) {
				result[0] = (int) (getMiddleColor().getRed() + ratio * (getMaxColor().getRed() - getMiddleColor().getRed()));
				result[1] = (int) (getMiddleColor().getGreen() + ratio * (getMaxColor().getGreen() - getMiddleColor().getGreen()));
				result[2] = (int) (getMiddleColor().getBlue() + ratio * (getMaxColor().getBlue() - getMiddleColor().getBlue()));
			} else {
				result[0] = (int) (getMiddleColor().getRed() - ratio * (getMinColor().getRed() - getMiddleColor().getRed()));
				result[1] = (int) (getMiddleColor().getGreen() - ratio * (getMinColor().getGreen() - getMiddleColor().getGreen()));
				result[2] = (int) (getMiddleColor().getBlue() - ratio * (getMinColor().getBlue() - getMiddleColor().getBlue()));
			}
			result[3] = getTransparency();
			return result;
		}
	}
}
