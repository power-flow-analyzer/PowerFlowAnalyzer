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

import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.NetworkChangeEvent;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ParameterSupport;

public class ParameterMasterNetwork implements IParameterMasterElement {

	private Network network;
	private boolean parametersAreRequired, showCheckBoxes;
	
	public ParameterMasterNetwork(Network network, boolean parametersAreRequired) {
		this(network, parametersAreRequired, true);
	}
	
	public ParameterMasterNetwork(Network network, boolean parametersAreRequired, boolean showCheckBoxes) {
		this.network = network;
		this.parametersAreRequired = parametersAreRequired;
		this.showCheckBoxes = showCheckBoxes;
	}
	
	public Network getNetwork() {
		return network;
	}
	
	@Override
	public boolean isRequired(String parameterID) {
		return parametersAreRequired;
	}
	
	@Override
	public boolean showCheckBoxes(String parameterID) {
		return showCheckBoxes;
	}
	
	@Override
	public NetworkParameter getParameter(String parameterID, boolean create) {
		return network.getParameter(parameterID, create);
	}
	
	@Override
	public boolean hasParameterDefinition(String parameterID) {
		return false;// networks never contain parameter definitions
	}

	@Override
	public NetworkParameter getParameterValue(String parameterID) {
		return network.getParameterValue(parameterID);
	}
	
	@Override
	public NetworkParameter getOwnParameter(String parameterID) {
		return network.getOwnParameter(parameterID);
	}
	
	@Override
	public void removeOwnParameter(String parameterID) {
//		network.removeOwnParameter(parameterID);
	}
	
	@Override
	public void fireValueChanged(String parameterID, String oldValue, String newValue) {
		NetworkChangeEvent event = new NetworkChangeEvent(network, parameterID, oldValue, newValue);
		network.fireNetworkElementChanged(event);
	}

	@Override
	public ParameterSupport getParameterSupport() {
		return network;
	}
}
