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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;
import javax.swing.JTextField;

import net.ee.pfanalyzer.model.data.NetworkParameter;

public class ParameterTextField extends ParameterValuePanel implements ActionListener, KeyListener {
	
	private JTextField textfield;
	
	public ParameterTextField(IParameterMasterElement element, NetworkParameter property, NetworkParameter propertyValue) {
		super(element, property, propertyValue);
		textfield.addActionListener(this);
		textfield.addKeyListener(this);
	}
	
	protected void createValuePanel() {
		textfield = new JTextField();
	}
	
	protected JTextField getTextField() {
		return textfield;
	}
	
	protected JComponent getValuePanel() {
		return textfield;
	}
	
	protected void setValue(String value) {
		textfield.setText(value);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		setNewValue();
	}
	
	protected void setNewValue() {
		if(ignoreAction())
			return;
		NetworkParameter property = getMasterElement().getParameter(getPropertyID(), true);
		String value = textfield.getText();
		String oldValue = property.getValue();
		property.setValue(value);
		fireValueChanged(oldValue, value);
		refresh();
	}
	
	protected void updateView() {
		// empty implementation
	}

	@Override
	public void keyReleased(KeyEvent e) {
		setNewValue();
	}

	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyPressed(KeyEvent e) {}
}
