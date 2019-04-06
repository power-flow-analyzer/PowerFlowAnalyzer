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
package net.ee.pfanalyzer.preferences;

public interface IPreferenceConstants {

	public final static String PROPERTY_VERSION = "version";
	
	public final static String PROPERTY_BRANDING_IMAGE = "branding.image";
	
	public final static String PROPERTY_MATPOWER_DIRECTORY = "property.matpower.directory";
	
	public final static String PROPERTY_CASE_FILES = "property.cases.files";
	public final static String PROPERTY_MATLAB_SCRIPT_FILES = "property.cases.matlab.scripts";
	public final static String PROPERTY_MATPOWER_CASE_FILES = "property.cases.matpower";
	public final static String PROPERTY_PARAMETERS_FILES = "property.cases.parameter.files";
	public final static String PROPERTY_PARAMETER_MODE_SELECTION = "property.cases.parameter.selection";
	public final static String PROPERTY_POWER_FLOW_ALGORITHM = "property.cases.powerFlow.algo";
	
	public final static String PROPERTY_UI_LARGE_ICONS = "property.ui.useLargeIcons";
	public final static String PROPERTY_UI_SHOW_SUCCESS_MESSAGE = "property.ui.showSuccessMessage";
	
	public final static String PROPERTY_UI_SHOW_MODEL_PREFIX = "property.ui.model.show.";
}
