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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.data.NetworkParameterPurposeRestriction;
import net.ee.pfanalyzer.model.data.NetworkParameterValueDisplay;
import net.ee.pfanalyzer.model.data.NetworkParameterValueOption;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.model.util.ParameterUtils;

public abstract class ParameterValuePanel extends JPanel {
	
	private IParameterMasterElement master;
	private NetworkParameter propertyDefinition, propertyValue;
	private JCheckBox inheritBox, emptyBox;
	private boolean userAction = true;
	private boolean showFullParameterInfo = true;

	public ParameterValuePanel(IParameterMasterElement element, NetworkParameter property, 
			NetworkParameter propertyValue) {
		this(element, property, propertyValue, true);
	}

	public ParameterValuePanel(IParameterMasterElement element, NetworkParameter property, 
			NetworkParameter propertyValue, boolean editable) {
		super(new BorderLayout());
		this.master = element;
		this.propertyDefinition = property;
		this.propertyValue = propertyValue;
		
		createValuePanel();
		boolean isDefinition = getMasterElement().hasParameterDefinition(property.getID());
		boolean isResult = NetworkParameterPurposeRestriction.RESULT.equals(propertyDefinition.getPurpose());
		boolean isScenarioParam = NetworkParameterPurposeRestriction.SCENARIO.equals(propertyDefinition.getPurpose());
		if( editable && ! isResult && ! isScenarioParam && getMasterElement().showCheckBoxes(property.getID())) {
			if(isDefinition) {
				emptyBox = new JCheckBox("empty");
				emptyBox.setToolTipText("Leave this value empty.");
				emptyBox.setSelected(propertyValue.isEmpty());
				getValuePanel().setEnabled( ! propertyValue.isEmpty());
				add(emptyBox, BorderLayout.EAST);
				
				emptyBox.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						ParameterValuePanel.this.propertyValue.setEmpty(emptyBox.isSelected());
						if(emptyBox.isSelected()) {
							ParameterValuePanel.this.propertyValue.setValue(null);
							getValuePanel().setEnabled(false);
						} else {
							getValuePanel().setEnabled(true);
						}
					}
				});
			} else {
				boolean required = getMasterElement().isRequired(property.getID());
				boolean inherited = required ? false : getMasterElement().getOwnParameter(property.getID()) == null;
				inheritBox = new JCheckBox("inherit");
				if(required)
					inheritBox.setToolTipText("<html>This value cannot be inherited from the super type(s)<br>since no value is specified there.");
				else
					inheritBox.setToolTipText("<html>Inherit this value from the super type(s),<br>i.e. do not define a particular value for this type.");
				inheritBox.setSelected(inherited);
				inheritBox.setEnabled( ! required);
				getValuePanel().setEnabled( ! inherited);
				add(inheritBox, BorderLayout.EAST);
				
				inheritBox.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if(inheritBox.isSelected()) {
							inheritValue();
						} else {
							getValuePanel().setEnabled(true);
						}
					}
				});
			}
		}
		setValue(propertyValue);
		add(getValuePanel(), BorderLayout.CENTER);
	}
	
	protected IParameterMasterElement getMasterElement() {
		return master;
	}
	
	protected String[] getOptionLabels() {
		String[] optionLabels = new String[getOptions().size()];
		for (int i = 0; i < optionLabels.length; i++) {
			optionLabels[i] = getOptions().get(i).getLabel();
		}
		return optionLabels;
	}
	
	protected abstract void setValue(String value);
	
	protected abstract JComponent getValuePanel();
	
	protected abstract void createValuePanel();
	
	protected void setValue(NetworkParameter p) {
		String value = null;
		if(p == null)
			value = "";
		else if(p.getValue() != null)
			value = p.getValue();
		else if(p.getDefaultValue() != null)
			value = p.getDefaultValue();
		if(value != null) {
			value = ParameterUtils.getNormalizedParameterValue(propertyDefinition, value);
			// set data in panel
			setValue(value);
		}
		// set the tooltip text for the value panel
		String tooltipText = ModelDBUtils.getParameterDescription(propertyDefinition, value, showFullParameterInfo);
		getValuePanel().setToolTipText(tooltipText);
	}
	
	protected String getNormalizedParameterValue(String value) {
		return ParameterUtils.getNormalizedParameterValue(propertyDefinition, value);
	}
	
	protected void fireValueChanged(String oldValue, String newValue) {
		getMasterElement().fireValueChanged(propertyDefinition.getID(), oldValue, newValue);
	}
	
	private void inheritValue() {
		userAction = false;
		getMasterElement().removeOwnParameter(getPropertyID());
		propertyValue = getMasterElement().getParameterValue(getPropertyID());
		setValue(propertyValue);
		getValuePanel().setEnabled(false);
		userAction = true;
	}
	
	protected boolean ignoreAction() {
		return ! userAction;
	}
	
	public String getPropertyID() {
		return propertyDefinition.getID();
	}
	
	protected List<NetworkParameterValueOption> getOptions() {
		return propertyDefinition.getOption();
	}
	
	protected NetworkParameterValueDisplay getDisplayOptions() {
		return propertyDefinition.getDisplay();
	}
	
	protected double getMin() {
		NetworkParameterValueDisplay options = getDisplayOptions();
		if(options == null || options.getMin() == null)
			return Double.NaN;
		return options.getMin();
	}
	
	protected double getMax() {
		NetworkParameterValueDisplay options = getDisplayOptions();
		if(options == null || options.getMax() == null)
			return Double.NaN;
		return options.getMax();
	}
	
	protected double getIncrement() {
		NetworkParameterValueDisplay options = getDisplayOptions();
		if(options != null)
			return options.getIncrement();
		return 1;
	}
	
//	protected double getPrecision() {
//		ModelPropertyDisplay options = getDisplayOptions();
//		if(options != null && options.getPrecision() != null)
//			return options.getPrecision().doubleValue();
//		return 1;
//	}
	
	protected String getDecimalFormatPattern() {
		NetworkParameterValueDisplay options = getDisplayOptions();
		if(options != null && options.getDecimalFormatPattern() != null)
			return options.getDecimalFormatPattern();
		return "#.########";
	}
	
	protected void refresh() {
		
	}
	
	public void setShowFullParameterInfo(boolean flag) {
		showFullParameterInfo = flag;
		setValue(propertyValue);
	}
}
