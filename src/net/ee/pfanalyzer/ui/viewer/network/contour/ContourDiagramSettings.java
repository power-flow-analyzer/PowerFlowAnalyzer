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

public class ContourDiagramSettings {

	private String elementIDPrefix, parameterName;
	
	private double maxValue = Double.NaN;
	private double middleValue = Double.NaN;
	private double minValue = Double.NaN;
	private double maxDistance = Double.NaN;
	private double maxRelDistance = Double.NaN;
	
	private ColorProvider colorProvider = new ColorProvider.SimpleColorProvider();
	private int outOfBoundsAction = -1;
	
	public boolean isIncomplete() {
		return elementIDPrefix == null || parameterName == null
			|| Double.isNaN(maxValue) || Double.isNaN(minValue) || Double.isNaN(middleValue) 
			|| Double.isNaN(maxDistance) || Double.isNaN(maxRelDistance) || outOfBoundsAction == -1
			|| getColorProvider().getTransparency() == -1
			|| getOutOfBoundsAction() == -1;
	}

	public String getElementIDPrefix() {
		return elementIDPrefix;
	}

	public void setElementIDPrefix(String elementIDPrefix) {
		this.elementIDPrefix = elementIDPrefix;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	public double getMiddleValue() {
		return middleValue;
	}

	public void setMiddleValue(double middleValue) {
		this.middleValue = middleValue;
	}

	public double getMinValue() {
		return minValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public double getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(double maxDistance) {
		this.maxDistance = maxDistance;
	}

	public double getMaxRelDistance() {
		return maxRelDistance;
	}

	public void setMaxRelDistance(double maxRelDistance) {
		this.maxRelDistance = maxRelDistance;
	}

	public ColorProvider getColorProvider() {
		return colorProvider;
	}

	public void setColorProvider(ColorProvider colorProvider) {
		this.colorProvider = colorProvider;
	}

	public int getOutOfBoundsAction() {
		return outOfBoundsAction;
	}

	public void setOutOfBoundsAction(int outOfBoundsAction) {
		this.outOfBoundsAction = outOfBoundsAction;
	}
}
