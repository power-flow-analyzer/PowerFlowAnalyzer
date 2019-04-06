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

import java.awt.Window;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JTabbedPane;

import net.ee.pfanalyzer.model.data.AbstractModelElementData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.ui.util.Group;
import net.ee.pfanalyzer.ui.viewer.element.ModelElementPanel;

public abstract class ParameterChooserDialog extends BaseDialog {

	private Map<String, Group> groups = new HashMap<String, Group>();
	private Map<String, Box> tabs = new HashMap<String, Box>();
	private JTabbedPane tabbedPane;

	protected ParameterChooserDialog(Window frame, String title, boolean modal) {
		super(frame, title, modal);
		tabbedPane = new JTabbedPane();
		setCenterComponent(tabbedPane);
	}
	
	protected void addParameters(AbstractModelElementData element, ModelElementPanel parameterPanel) {
		if(element.getParent() != null)
			addParameters(element.getParent(), parameterPanel);
		for (NetworkParameter paramDef : element.getParameter()) {
			if(ModelDBUtils.isInternalViewerParameter(paramDef.getID()))
				continue;
			NetworkParameter parameterValue = parameterPanel.getParameterMaster().getParameter(paramDef.getID(), true);
			if(parameterValue.getValue() == null) {
				NetworkParameter modelValue = ModelDBUtils.getParameterValue(element, paramDef.getID());
				if(modelValue != null)
					parameterValue.setValue(modelValue.getValue());
			}
			parameterValue = parameterPanel.getParameterMaster().getParameter(paramDef.getID(), true);
			String groupTitle = paramDef.getDisplay() == null ? null : paramDef.getDisplay().getGroup();
			String sectionTitle = paramDef.getDisplay() == null ? null : paramDef.getDisplay().getSection();
			parameterPanel.addParameter(paramDef, parameterValue, getGroup(groupTitle, sectionTitle));
		}
	}
	
	protected Group getGroup(String name, String section) {
		Group group = groups.get(name);
		if(group == null) {
			group = new Group(name);
			groups.put(name, group);
			getSection(section).add(group);
		}
		return group;
	}
	
	protected Box getSection(String name) {
		Box box = tabs.get(name);
		if(box == null) {
			box = Box.createVerticalBox();
			box.setOpaque(true);
			tabs.put(name, box);
			String title = name == null || name.isEmpty() ? "Common" : name;
			tabbedPane.addTab(title, box);
		}
		return box;
	}
	
	protected void finishLayout() {
		for (Box box : tabs.values()) {
			box.add(Box.createVerticalGlue());
			box.add(Box.createVerticalGlue());
			box.add(Box.createVerticalGlue());
		}
	}
}
