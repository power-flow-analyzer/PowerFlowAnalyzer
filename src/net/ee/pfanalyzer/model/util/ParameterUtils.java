package net.ee.pfanalyzer.model.util;

import java.awt.Color;

import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.data.NetworkParameterType;

public class ParameterUtils {

	public static String getNormalizedParameterValue(NetworkParameter parameter, String value) {
		// convert double values to integers if necessary
		// (otherwise option cannot be found by using equals)
		if(NetworkParameterType.INTEGER.equals(parameter.getType()) && value.endsWith(".0"))
			return value.substring(0, value.length() - 2);
		return value;
	}
	
	public static Color parseColor(String text) {
		String[] rgbText = text.split(",");
		int[] rgb = new int[4];
		for (int i = 0; i < Math.min(rgb.length, rgbText.length); i++) {
			rgb[i] = Integer.parseInt(rgbText[i]);
		}
		return new Color(rgb[0], rgb[1], rgb[2]);// FIXME alpha channel is not used
	}
}
