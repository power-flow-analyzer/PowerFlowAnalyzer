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

public class MarkerElement extends AbstractNetworkElement implements ICommonParameters, ICoordinatesParameters {

	private Bus parentBus;
	
	public MarkerElement(Network data) {
		this(data, 0);
	}

	public MarkerElement(Network data, int index) {
		super(data, index);
	}

	MarkerElement(Network data, AbstractNetworkElementData elementData, int index) {
		super(data, elementData, index);
	}
	
	@Override
	public String getDefaultModelID() {
		return ModelDB.DEFAULT_MARKER_MODEL;
	}

	@Override
	public String getDisplayName(int displayFlags) {
		String name = getName();
		if(name == null || name.isEmpty())
			name = "Marker " + getIndex();
		return name;
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
	
	public int getParentBusNumber() {
		return getIntParameter(PROPERTY_PARENT_BUS, -1);
	}

	public Bus getParentBus() {
		return parentBus;
	}

	public void setParentBus(Bus parentBus) {
		this.parentBus = parentBus;
	}
}
