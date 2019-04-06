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

import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;

public abstract class AbstractPropertyDialog extends BaseDialog implements ActionListener {

	protected abstract void applyProperty(String property, boolean value);
	
	private List<PropertyCheckBox> boxes = new ArrayList<PropertyCheckBox>();

	protected AbstractPropertyDialog(Frame frame, String title, boolean modal) {
		super(frame, title, modal);
	}
	
	protected void addBooleanProperty(Container parent, String label, String propertyName, boolean value) {
		PropertyCheckBox checkBox = new PropertyCheckBox(label, propertyName, value);
//		checkBox.setAlignmentX(LEFT_ALIGNMENT);
		parent.add(checkBox);
		checkBox.addActionListener(this);
		boxes.add(checkBox);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if(o instanceof PropertyCheckBox) {
			PropertyCheckBox pcb = (PropertyCheckBox) o;
			applyProperty(pcb.getPropertyName(), pcb.getValue());
		}
	}
	
	class PropertyCheckBox extends JCheckBox {
		
		private String propertyName;
		
		PropertyCheckBox(String label, String property, boolean value) {
			super(label, value);
			propertyName = property;
		}
		
		public String getPropertyName() {
			return propertyName;
		}
		
		public boolean getValue() {
			return isSelected();
		}
	}

}
