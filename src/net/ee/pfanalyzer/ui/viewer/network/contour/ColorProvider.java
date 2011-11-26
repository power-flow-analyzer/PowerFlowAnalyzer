package net.ee.pfanalyzer.ui.viewer.network.contour;

import java.awt.Color;

public abstract class ColorProvider {
	
	double colorSteps = 0.0;
	
	Color maxColor = new Color(255, 0, 0);
	Color upperHalfColor = new Color(255, 255, 0);
	Color middleColor = new Color(255, 255, 255);
	Color lowerHalfColor = new Color(0, 255, 255);
	Color minColor = new Color(0, 0, 255);
	int transparency = 185;
	
	public int[] getARGB(double min, Color minColor, double max, Color maxColor, double value) {
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
	
	public static class ComplexColorProvider extends ColorProvider {
		protected int[] getARGB(double ratio) {
			int[] result = new int[4];
			if(ratio >= 0) {
				if(ratio > 0.5) {
					result[0] = (int) (upperHalfColor.getRed() + (ratio - 0.5) * 2 * (maxColor.getRed() - upperHalfColor.getRed()));
					result[1] = (int) (upperHalfColor.getGreen() + (ratio - 0.5) * 2 * (maxColor.getGreen() - upperHalfColor.getGreen()));
					result[2] = (int) (upperHalfColor.getBlue() + (ratio - 0.5) * 2 * (maxColor.getBlue() - upperHalfColor.getBlue()));
				} else {
					result[0] = (int) (middleColor.getRed() + ratio * 2.0 * (upperHalfColor.getRed() - middleColor.getRed()));
					result[1] = (int) (middleColor.getGreen() + ratio * 2.0 * (upperHalfColor.getGreen() - middleColor.getGreen()));
					result[2] = (int) (middleColor.getBlue() + ratio * 2.0 * (upperHalfColor.getBlue() - middleColor.getBlue()));
				}
			} else {
				if(ratio < -0.5) {
					result[0] = (int) (lowerHalfColor.getRed() + (-ratio - 0.5) * 2 * (minColor.getRed() - lowerHalfColor.getRed()));
					result[1] = (int) (lowerHalfColor.getGreen() + (-ratio - 0.5) * 2 * (minColor.getGreen() - lowerHalfColor.getGreen()));
					result[2] = (int) (lowerHalfColor.getBlue() + (-ratio - 0.5) * 2 * (minColor.getBlue() - lowerHalfColor.getBlue()));
				} else {
					result[0] = (int) (middleColor.getRed() - ratio * 2.0 * (lowerHalfColor.getRed() - middleColor.getRed()));
					result[1] = (int) (middleColor.getGreen() - ratio * 2.0 * (lowerHalfColor.getGreen() - middleColor.getGreen()));
					result[2] = (int) (middleColor.getBlue() - ratio * 2.0 * (lowerHalfColor.getBlue() - middleColor.getBlue()));
				}
			}
			result[3] = transparency;
			return result;
		}
	}
	
	public static class SimpleColorProvider extends ColorProvider {
		protected int[] getARGB(double ratio) {
			int[] result = new int[4];
			if(ratio >= 0) {
				result[0] = (int) (middleColor.getRed() + ratio * (maxColor.getRed() - middleColor.getRed()));
				result[1] = (int) (middleColor.getGreen() + ratio * (maxColor.getGreen() - middleColor.getGreen()));
				result[2] = (int) (middleColor.getBlue() + ratio * (maxColor.getBlue() - middleColor.getBlue()));
			} else {
				result[0] = (int) (middleColor.getRed() - ratio * (minColor.getRed() - middleColor.getRed()));
				result[1] = (int) (middleColor.getGreen() - ratio * (minColor.getGreen() - middleColor.getGreen()));
				result[2] = (int) (middleColor.getBlue() - ratio * (minColor.getBlue() - middleColor.getBlue()));
			}
			result[3] = transparency;
			return result;
		}
	}
}
