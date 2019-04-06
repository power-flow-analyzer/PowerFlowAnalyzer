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
package net.ee.pfanalyzer.math.coordinate;

public interface ICoordinateConverter {

	/**
	 * Returns the screen coordinate x for the given longitude.
	 * @param E the longitude
	 * @return screen coordinate x
	 */
	int getX(double E);
	
	/**
	 * Returns the screen coordinate y for the given latitude.
	 * @param N the latitude
	 * @return screen coordinate y
	 */
	int getY(double N);
	
	/**
	 * Returns the latitude for the given screen coordinate y.
	 * @param x screen coordinate y
	 * @return the latitude
	 */
	double getLatitude(int y);
	
	/**
	 * Returns the longitude for the given screen coordinate x.
	 * @param y screen coordinate x
	 * @return the longitude
	 */
	double getLongitude(int x);
}
