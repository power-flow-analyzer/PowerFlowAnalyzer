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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.data.ModelClassData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.data.NetworkParameterPurposeRestriction;
import net.ee.pfanalyzer.ui.parameter.ParameterMasterNetwork;
import net.ee.pfanalyzer.ui.util.Group;
import net.ee.pfanalyzer.ui.viewer.element.ModelElementPanel;

public class CaseCalculationDialog extends BaseDialog {

	private final static String DEFAULT_TEXT = "Select the desired scenario parameters and press OK.";
	private final static String ERROR_TEXT = "<html><font color=\"red\">All values must be filled in to proceed.";
	
	private List<NetworkParameter> parameters = new ArrayList<NetworkParameter>();
	private Network network;
	
	public CaseCalculationDialog(Frame frame, Network network) {
		super(frame, "Calculate Power Flow", true);
		this.network = network;
		setText(DEFAULT_TEXT);
		JPanel contentPane = new JPanel(new BorderLayout());
		
		ModelElementPanel parameterPanel = new ModelElementPanel(null);
		parameterPanel.setEditable(true);// default setting
		parameterPanel.setParameterMaster(new ParameterMasterNetwork(network, true));
		ModelClassData clazz = network.getGlobalParameterClass();
		Group paramPanel = new Group("Scenario Parameters");
		for (NetworkParameter parameter : clazz.getParameter()) {
			boolean isScenarioParam = NetworkParameterPurposeRestriction.SCENARIO.equals(parameter.getPurpose());
			if(isScenarioParam == false)
				continue;
			NetworkParameter propertyValue = network.getParameterValue(parameter.getID());
			parameterPanel.addParameter(parameter, propertyValue, paramPanel);
			parameters.add(parameter);
		}
		
		addOKButton();
		addCancelButton();
		
		contentPane.add(paramPanel, BorderLayout.CENTER);
		setCenterComponent(contentPane);
	}

	@Override
	protected boolean checkInput() {
		for (NetworkParameter parameter : parameters) {
			NetworkParameter value = network.getParameterValue(parameter.getID());
			if(value == null || value.getValue() == null) {
				setText(ERROR_TEXT);
				return false;
			}
		}
		setText(DEFAULT_TEXT);
		return true;
	}
}
