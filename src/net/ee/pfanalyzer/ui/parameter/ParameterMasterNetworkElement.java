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

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.NetworkChangeEvent;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.model.util.ParameterSupport;

public class ParameterMasterNetworkElement implements IParameterMasterElement {

	private AbstractNetworkElement master;
	
	public ParameterMasterNetworkElement(AbstractNetworkElement element) {
		this.master = element;
	}
	
	protected AbstractNetworkElement getMasterElement() {
		return master;
	}
	
	@Override
	public boolean isRequired(String parameterID) {
		if(getMasterElement().getModel() == null)
			return false;
		NetworkParameter parameter = ModelDBUtils.getParameterValue(getMasterElement().getModel(), parameterID);
		return parameter == null || parameter.getValue() == null;
	}
	
	@Override
	public boolean showCheckBoxes(String parameterID) {
		return true;
	}
	
	@Override
	public NetworkParameter getParameter(String parameterID, boolean create) {
		return getMasterElement().getParameter(parameterID, create);
	}
	
	@Override
	public boolean hasParameterDefinition(String parameterID) {
		return false;// network elements never contain parameter definitions
	}

	@Override
	public NetworkParameter getParameterValue(String parameterID) {
		return getMasterElement().getParameterValue(parameterID);
	}
	
	@Override
	public NetworkParameter getOwnParameter(String parameterID) {
		return getMasterElement().getOwnParameter(parameterID);
	}
	
	@Override
	public void removeOwnParameter(String parameterID) {
		getMasterElement().removeOwnParameter(parameterID);
	}
	
	@Override
	public void fireValueChanged(String parameterID, String oldValue, String newValue) {
		NetworkChangeEvent event = new NetworkChangeEvent(getMasterElement(), parameterID, oldValue, newValue);
		getMasterElement().getNetwork().fireNetworkElementChanged(event);
	}

	@Override
	public ParameterSupport getParameterSupport() {
		return getMasterElement();
	}
}
