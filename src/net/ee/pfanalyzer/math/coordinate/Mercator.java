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


public class Mercator implements ICoordinateConverter {

	public final static double earth_radius = 6371000;
	
	public int getX(double E) {
		return (int) Math.round( E * earth_radius * Math.PI / 180.0 );
	}

	public int getY(double N) {
		return (int) Math.round( Math.log(Math.tan(N * Math.PI / 360.0 + Math.PI / 4.0)) * earth_radius );
	}
	
	public double getLongitude(int x) {
		return x * 180.0 / (earth_radius * Math.PI);
	}
	
	public double getLatitude(int y) {
		return 2 * (Math.atan(Math.exp(y / earth_radius)) - Math.PI / 4.0) / Math.PI * 180.0;
	}

	public static void main(String[] args) {
		Mercator cc = new Mercator();
		System.out.println("X=" + cc.getX(12));
		System.out.println("Y=" + cc.getY(54));
		System.out.println("X=" + cc.getX(14));
		System.out.println("Y=" + cc.getY(50));
		System.out.println("X=" + cc.getX(6));
		System.out.println("Y=" + cc.getY(51));
//		System.out.println("X=" + cc.getX(-8.578056));
//		System.out.println("Y=" + cc.getY(-53.544167));
//		System.out.println("X=" + cc.getX(8.578056));
//		System.out.println("Y=" + cc.getY(53.544167));
//		System.out.println("Latitude=" + cc.getLatitude(953836));
//		System.out.println("Longitude=" + cc.getLongitude(7076368));
	}
}
