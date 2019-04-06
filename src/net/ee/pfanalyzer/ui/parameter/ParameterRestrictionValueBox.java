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
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.IDisplayConstants;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.data.NetworkParameter;

public class ParameterRestrictionValueBox extends ParameterValuePanel implements ActionListener {
	
	private JComboBox box;
	private String oldValue;
	Vector<String> values, labels;
	
	public ParameterRestrictionValueBox(ParameterMasterNetworkElement element, NetworkParameter property, NetworkParameter propertyValue) {
		super(element, property, propertyValue);
		box.addActionListener(this);
	}
	
	public Network getNetwork() {
		return ((ParameterMasterNetworkElement) getMasterElement()).getMasterElement().getNetwork();
	}
	
	protected void createValuePanel() {
		values = new Vector<String>();
		values.add(null);
		labels = new Vector<String>();
		labels.add("");
		box = new JComboBox();
		String parameterID = getDisplayOptions().getParameterID();
		if(parameterID == null || parameterID.isEmpty())
			return;
		String elementRestriction = getDisplayOptions().getElementRestriction();
		List<AbstractNetworkElement> elements = getNetwork().getElements(elementRestriction);
		for (AbstractNetworkElement element : elements) {
			NetworkParameter parameter = element.getParameterValue(parameterID);
			if(parameter != null && parameter.getValue() != null) {
				if(parameter.getValue() != null)
					values.add(getNormalizedParameterValue(parameter.getValue()));
				else
					values.add(null);
				String label = element.getParameterDisplayValue(parameterID, 
						IDisplayConstants.PARAMETER_DISPLAY_DEFAULT) 
						+ " (" + element.getDisplayName(AbstractNetworkElement.DISPLAY_DEFAULT) + ")";
				labels.add(label);
			}
		}
		box.setModel(new DefaultComboBoxModel(labels));
	}
	
	protected JComponent getValuePanel() {
		return box;
	}
	
	protected void setValue(String value) {
		int index = values.indexOf(value);
		if(index > -1)
			box.setSelectedIndex(index);
		else
			box.setSelectedIndex(0);
		
//		int optionIndex = getOptionIndexForValue(value);
//		if(optionIndex > -1) {
//			box.setSelectedIndex(optionIndex + 1);// first is empty
//			oldValue = value;
//		} else {
//			box.setSelectedIndex(0);
//			oldValue = null;
//		}
	}
	
//	private int getOptionIndexForLabel(String value) {
//		for (int i = 0; i < getOptions().size(); i++) {
//			if(getOptions().get(i).getLabel().equals(value)) {
//				return i;
//			}
//		}
//		return -1;
//	}
	
//	private int getOptionIndexForValue(String value) {
//		for (int i = 0; i < getOptions().size(); i++) {
//			if(getOptions().get(i).getValue().equals(value)) {
//				return i;
//			}
//		}
//		return -1;
//	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(ignoreAction())
			return;
		NetworkParameter property = getMasterElement().getParameter(getPropertyID(), true);
//		int optionIndex = getOptionIndexForLabel((String) box.getSelectedItem());
//		String value = null;
//		if(optionIndex > -1) {
//			value = getOptions().get(optionIndex).getValue();
//		} else {
//		}
		String value = values.get(box.getSelectedIndex());
		property.setValue(value);
		fireValueChanged(oldValue, value);
		oldValue = value;
		refresh();
	}
}
