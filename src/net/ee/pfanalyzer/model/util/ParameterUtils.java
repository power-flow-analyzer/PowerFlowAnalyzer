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
