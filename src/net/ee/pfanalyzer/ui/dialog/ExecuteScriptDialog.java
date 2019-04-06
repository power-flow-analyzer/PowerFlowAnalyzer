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
package net.ee.pfanalyzer.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JPanel;

import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.data.ModelData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.ui.parameter.ParameterMasterNetwork;
import net.ee.pfanalyzer.ui.util.Group;
import net.ee.pfanalyzer.ui.viewer.element.ModelElementPanel;

public class ExecuteScriptDialog extends BaseDialog {

	private final static String DEFAULT_TEXT = "<html>Select the desired script parameters and press OK.";
	private final static String TEXT_MULTI = "<br>These parameters will be applied to all selected networks.";
	private final static String ERROR_TEXT = "<br><font color=\"red\">All values must be filled in to proceed.";
	
	private Network network;
	private ModelData script;
	private boolean showDialog = true;
	private String text;
	
	public ExecuteScriptDialog(Frame frame, Network network, ModelData script, boolean multipleNetworks) {
		super(frame, "Script parameters", true);
		this.network = network;
		this.script = script;
		text = DEFAULT_TEXT + (multipleNetworks ? TEXT_MULTI : "");
		setText(text);
		JPanel contentPane = new JPanel(new BorderLayout());
		
		ModelElementPanel parameterPanel = new ModelElementPanel(null);
//		parameterPanel.setShowNetworkParameters(true);
		parameterPanel.setEditable(true);// default setting
		parameterPanel.setParameterMaster(new ParameterMasterNetwork(network, true, false));
		Group paramPanel = new Group("Script Parameters");
		for (NetworkParameter parameter : script.getParameter()) {
			if(ModelDBUtils.isInternalScriptParameter(parameter.getID())) {
				NetworkParameter propertyValue = ModelDBUtils.getParameterValue(script, parameter.getID());
				if(propertyValue != null && propertyValue.getValue() != null && propertyValue.getValue().trim().length() > 0) {
					network.setParameter(parameter.getID(), propertyValue.getValue());
					// only save the value in the network, do not show the property
					continue;
				}
			}
			NetworkParameter paramDef = ModelDBUtils.getParameterDefinition(script, parameter.getID());
			NetworkParameter propertyValue = network.getParameterValue(parameter.getID());
			if(propertyValue == null || propertyValue.getValue() == null) {
				propertyValue = ModelDBUtils.getParameterValue(script, parameter.getID());
				network.setParameter(parameter.getID(), propertyValue.getValue());
				propertyValue = network.getParameterValue(parameter.getID());
			}
			parameterPanel.addParameter(paramDef, propertyValue, paramPanel);
		}
		
		showDialog = parameterPanel.getParameterCount() > 0;
		addOKButton();
		addCancelButton();
		
		contentPane.add(paramPanel, BorderLayout.CENTER);
		setCenterComponent(contentPane);
	}
	
	public boolean shouldShowDialog() {
		return showDialog;
	}

	@Override
	protected boolean checkInput() {
		for (NetworkParameter parameter : script.getParameter()) {
			NetworkParameter value = network.getParameterValue(parameter.getID());
			if(value == null || value.getValue() == null) {
				setText(text + ERROR_TEXT);
				pack();
				return false;
			}
		}
		setText(text);
		pack();
		return true;
	}
}
