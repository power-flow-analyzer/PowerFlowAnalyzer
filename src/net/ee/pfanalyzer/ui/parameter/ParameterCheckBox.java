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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import net.ee.pfanalyzer.model.data.NetworkParameter;

public class ParameterCheckBox extends ParameterValuePanel implements ActionListener {
	
	private JCheckBox checkBox;
	
	public ParameterCheckBox(IParameterMasterElement element, NetworkParameter property, NetworkParameter propertyValue) {
		super(element, property, propertyValue);
		checkBox.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(ignoreAction())
			return;
		NetworkParameter property = getMasterElement().getParameter(getPropertyID(), true);
		String value = Boolean.toString(checkBox.isSelected());
		String oldValue = property.getValue();
		property.setValue(value);
		fireValueChanged(oldValue, value);
		refresh();
	}
	
	@Override
	protected void createValuePanel() {
		checkBox = new JCheckBox();
	}
	
	public JCheckBox getCheckBox() {
		return checkBox;
	}

	@Override
	protected JComponent getValuePanel() {
		return checkBox;
	}

	@Override
	protected void setValue(String value) {
		checkBox.setSelected(Boolean.parseBoolean(value));
	}
}
