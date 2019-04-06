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

public class BusIndexConverter {

	public static int getRealBusIndex(int busIndex) {
		if(busIndex > 10000 && busIndex < 20000)
			return (int) Math.floor((busIndex - 10000) / 10) - 1;
		if(busIndex > 20000 && busIndex < 30000)
			return (int) Math.floor((busIndex - 20000) / 10);
		return busIndex;
	}
}
