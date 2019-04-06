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

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.ee.pfanalyzer.PowerFlowAnalyzer;
import net.ee.pfanalyzer.model.PowerFlowCase;
import net.ee.pfanalyzer.model.data.AbstractModelElementData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.data.NetworkParameterValueRestriction;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.ui.db.ModelDBDialog;

public class ParameterModelIDField extends ParameterTextField {
	
	private JPanel valuePanel;
	private int mode;

	public ParameterModelIDField(ParameterMasterViewer element,
			NetworkParameter property, NetworkParameter propertyValue) {
		super(element, property, propertyValue);
		if(NetworkParameterValueRestriction.MODEL_OR_CLASS_ID.equals(property.getRestriction()))
			mode = ModelDBDialog.GET_MODEL_OR_CLASS_MODE;
		else if(NetworkParameterValueRestriction.MODEL_ID.equals(property.getRestriction()))
			mode = ModelDBDialog.GET_MODEL_MODE;
		else
			throw new RuntimeException("Invalid restriction value: " + property.getRestriction());
	}

	
	protected void createValuePanel() {
		super.createValuePanel();
		valuePanel = new JPanel(new BorderLayout());
		valuePanel.add(getTextField(), BorderLayout.CENTER);
		JButton changeModelButton = PowerFlowAnalyzer.createButton("Select an element from the database", "database_go.png", "Select", false);
		changeModelButton.setMargin(new Insets(2, 2, 1, 1));
		changeModelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ModelDBDialog dialog = new ModelDBDialog(SwingUtilities.getWindowAncestor(ParameterModelIDField.this), 
						mode, getPowerFlowCase().getModelDB(), true, "Select element");
				dialog.showDialog(900, 500);
				AbstractModelElementData selected = dialog.getSelectedElement();
				if(selected != null) {
					getTextField().setText(ModelDBUtils.getFullElementID(selected));
					setNewValue();
//					if(titleField.getText().isEmpty() && selected.getLabel() != null)
//						titleField.setText(selected.getLabel());
				}
			}
		});
		valuePanel.add(changeModelButton, BorderLayout.EAST);
	}
	
	protected JComponent getValuePanel() {
		return valuePanel;
	}
	
	private PowerFlowCase getPowerFlowCase() {
		return ((ParameterMasterViewer) getMasterElement()).getPowerFlowCase();
	}
}
