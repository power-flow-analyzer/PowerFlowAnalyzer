package net.ee.pfanalyzer.model.util;

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
}
