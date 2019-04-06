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

import net.ee.pfanalyzer.model.DatabaseChangeEvent;
import net.ee.pfanalyzer.model.PowerFlowCase;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ParameterSupport;
import net.ee.pfanalyzer.ui.viewer.DataViewerConfiguration;

public class ParameterMasterViewer implements IParameterMasterElement {

	private PowerFlowCase caze;
	private DataViewerConfiguration viewer;
	private boolean parametersAreRequired;
	
	public ParameterMasterViewer(PowerFlowCase caze, DataViewerConfiguration viewer, boolean parametersAreRequired) {
		this.caze = caze;
		this.viewer = viewer;
		this.parametersAreRequired = parametersAreRequired;
	}
	
	public DataViewerConfiguration getViewerConfiguration() {
		return viewer;
	}
	
	public PowerFlowCase getPowerFlowCase() {
		return caze;
	}
	
	@Override
	public boolean isRequired(String parameterID) {
		return parametersAreRequired;
	}
	
	@Override
	public boolean showCheckBoxes(String parameterID) {
		return false;
	}

	@Override
	public NetworkParameter getParameter(String parameterID, boolean create) {
		return getViewerConfiguration().getParameter(parameterID, create);
	}
	
	@Override
	public boolean hasParameterDefinition(String parameterID) {
		return false;// viewers never contain parameter definitions
	}

	@Override
	public NetworkParameter getParameterValue(String parameterID) {
		return getViewerConfiguration().getParameterValue(parameterID);
	}
	
	@Override
	public NetworkParameter getOwnParameter(String parameterID) {
		return getViewerConfiguration().getOwnParameter(parameterID);
	}
	
	@Override
	public void removeOwnParameter(String parameterID) {
//		network.removeOwnParameter(parameterID);
	}
	
	@Override
	public void fireValueChanged(String parameterID, String oldValue, String newValue) {
		DatabaseChangeEvent event = new DatabaseChangeEvent(DatabaseChangeEvent.CHANGED, parameterID, oldValue, newValue);
//		event.setElementData(getMasterElement());
		getViewerConfiguration().fireParameterChanged(event);
	}

	@Override
	public ParameterSupport getParameterSupport() {
		return getViewerConfiguration();
	}
}
