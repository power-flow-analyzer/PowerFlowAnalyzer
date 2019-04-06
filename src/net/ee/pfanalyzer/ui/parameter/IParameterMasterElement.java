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
package net.ee.pfanalyzer.ui.parameter;

import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ParameterSupport;

public interface IParameterMasterElement {
	
	NetworkParameter getParameter(String parameterID, boolean create);

	boolean hasParameterDefinition(String parameterID);
	
	NetworkParameter getParameterValue(String parameterID);
	
	NetworkParameter getOwnParameter(String parameterID);
	
	void removeOwnParameter(String parameterID);
	
	boolean isRequired(String parameterID);
	
	boolean showCheckBoxes(String parameterID);
	
	void fireValueChanged(String parameterID, String oldValue, String newValue);
	
	ParameterSupport getParameterSupport();
}
