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
package net.ee.pfanalyzer.model;

import net.ee.pfanalyzer.model.data.AbstractNetworkElementData;
import net.ee.pfanalyzer.ui.shape.DefaultBusShape;

public class Bus extends AbstractNetworkElement implements ICoordinatesParameters {

	public Bus(Network data, int index) {
		super(data, index);
	}
	
	public Bus(Network data, AbstractNetworkElementData elementData, int index) {
		super(data, elementData, index);
	}
	
	@Override
	public String getDefaultModelID() {
		return ModelDB.DEFAULT_BUS_MODEL;
	}
	
	@Override
	public String getDefaultShapeID() {
		return DefaultBusShape.ID;
	}
	
	public int getBusNumber() {
		return getIntParameter(PROPERTY_BUS_NUMBER, -1);
	}
	
	public void setBusNumber(int value) {
		setParameter(PROPERTY_BUS_NUMBER, value);
	}
	
	public int getBusType() {
		return getIntParameter(PROPERTY_BUS_TYPE, -1);
	}
	
	public void setBusType(int value) {
		setParameter(PROPERTY_BUS_TYPE, value);
	}
	
	public boolean isSlackNode() {
		return getBusType() == BUS_TYPE_REFERENCE;
	}
	
	public int getBaseVoltage() {
		return getIntParameter(PROPERTY_BASE_VOLTAGE, 0);
	}
	
	public void setName(String name) {
		setParameter(PROPERTY_NAME, name);
	}
	
	public String getName() {
		return getTextParameter(PROPERTY_NAME);
	}
	
	public double getLongitude() {
		return getDoubleParameter(PROPERTY_LONGITUDE, Double.NaN);
	}
	
	public void setLongitude(double value) {
		setParameter(PROPERTY_LONGITUDE, value);
	}
	
	public double getLatitude() {
		return getDoubleParameter(PROPERTY_LATITUDE, Double.NaN);
	}
	
	public void setLatitude(double value) {
		setParameter(PROPERTY_LATITUDE, value);
	}
	
	@Override
	public String getDisplayName(int displayFlags) {
		String name = getName();
		String text = "Bus " + (getBusNumber());
		if(name != null)
			text += " (" + name + ")";
		if(getBaseVoltage() > 0 && (displayFlags & DISPLAY_ADDITIONAL_INFO) != 0)
			text += " (" + getBaseVoltage() + " kV)";
		if(isSlackNode())
			text += " (Slack Bus)";
		return text;
	}
}
