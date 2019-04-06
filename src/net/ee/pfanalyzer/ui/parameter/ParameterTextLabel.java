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

import javax.swing.JComponent;
import javax.swing.JLabel;

import net.ee.pfanalyzer.model.IDisplayConstants;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ModelDBUtils;

public class ParameterTextLabel extends ParameterValuePanel {
	
	private NetworkParameter propertyDefinition;
	private JLabel label;
	
	public ParameterTextLabel(IParameterMasterElement element, 
			NetworkParameter property, NetworkParameter propertyValue) {
		super(element, property, propertyValue, false);
		this.propertyDefinition = property;
		setValue(propertyValue);
	}
	
	protected void createValuePanel() {
		label = new JLabel("<no value>");
	}
	
	protected JComponent getValuePanel() {
		return label;
	}
	
	protected void setValue(String value) {
		String text;
		if(propertyDefinition != null && getMasterElement().getParameterSupport() != null)
			text = ModelDBUtils.getParameterDisplayValue(
					getMasterElement().getParameterSupport(), propertyDefinition, IDisplayConstants.PARAMETER_DISPLAY_DEFAULT);
		else
			text = value;
		if(text == null || text.isEmpty())
			text = "<no value>";
		label.setText(text);
	}
}
