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

import net.ee.pfanalyzer.model.matpower.IBranchDataConstants;
import net.ee.pfanalyzer.model.matpower.IGeneratorDataConstants;

public interface IInternalParameters {

	public final static String LONGITUDE = ICoordinatesParameters.PROPERTY_LONGITUDE;
	public final static String LATITUDE = ICoordinatesParameters.PROPERTY_LATITUDE;
	
	public final static String FROM_BUS = IBranchDataConstants.PROPERTY_FROM_BUS_NUMBER;
	public final static String TO_BUS = IBranchDataConstants.PROPERTY_TO_BUS_NUMBER;
	
	public final static String GEN_BUS = IGeneratorDataConstants.PROPERTY_BUS_NUMBER;
}
