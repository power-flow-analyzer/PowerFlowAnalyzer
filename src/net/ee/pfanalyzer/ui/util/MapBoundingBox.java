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
package net.ee.pfanalyzer.ui.util;

public class MapBoundingBox {

	private double latitudeMin;
	private double latitudeMax;
	private double longitudeMin;
	private double longitudeMax;
	
	public MapBoundingBox() {
		reset();
	}
	
	public void reset() {
		latitudeMin = Double.NaN;
		latitudeMax = Double.NaN;
		longitudeMin = Double.NaN;
		longitudeMax = Double.NaN;
	}
	
	public void add(double latitude, double longitude) {
		if(Double.isNaN(latitude) || Double.isNaN(longitude))
			return;
		if(Double.isNaN(latitudeMin))
			latitudeMin = latitude;
		else
			latitudeMin = Math.min(latitudeMin, latitude);
		if(Double.isNaN(latitudeMax))
			latitudeMax = latitude;
		else
			latitudeMax = Math.max(latitudeMax, latitude);
		if(Double.isNaN(longitudeMin))
			longitudeMin = longitude;
		else
			longitudeMin = Math.min(longitudeMin, longitude);
		if(Double.isNaN(longitudeMax))
			longitudeMax = longitude;
		else
			longitudeMax = Math.max(longitudeMax, longitude);
	}
	
	public void add(MapBoundingBox box) {
		add(box.getLatitudeMin(), box.getLongitudeMin());
		add(box.getLatitudeMax(), box.getLongitudeMax());
	}
	
	public boolean isIncomplete() {
		return Double.isNaN(latitudeMin) || Double.isNaN(latitudeMax)
				|| Double.isNaN(longitudeMin) || Double.isNaN(longitudeMax);
	}
	
	public double getLatitudeMin() {
		return latitudeMin;
	}
	
	public void setLatitudeMin(double latitudeMin) {
		this.latitudeMin = latitudeMin;
	}
	
	public double getLatitudeMax() {
		return latitudeMax;
	}
	
	public void setLatitudeMax(double latitudeMax) {
		this.latitudeMax = latitudeMax;
	}
	
	public double getLongitudeMin() {
		return longitudeMin;
	}
	
	public void setLongitudeMin(double longitudeMin) {
		this.longitudeMin = longitudeMin;
	}
	
	public double getLongitudeMax() {
		return longitudeMax;
	}
	
	public void setLongitudeMax(double longitudeMax) {
		this.longitudeMax = longitudeMax;
	}
}
