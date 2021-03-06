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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.ee.pfanalyzer.PowerFlowAnalyzer;
import net.ee.pfanalyzer.model.ModelDB;
import net.ee.pfanalyzer.model.data.ModelClassData;
import net.ee.pfanalyzer.preferences.IPreferenceConstants;

public class SelectModelClassDialog extends BaseDialog implements IPreferenceConstants {
	
	private JCheckBox selectNetworkClass, selectScriptClass, selectFlagClass, selectOutlineClass;
	private boolean isNetworkClassSelected, isScriptClassSelected, isFlagClassSelected, isOutlineClassSelected;
	
	public SelectModelClassDialog(PowerFlowAnalyzer frame, ModelDB modelDB, String title) {
		super(frame, title);
		setText("Select the desired database sections and press OK to continue.");
		
		selectNetworkClass = new JCheckBox("Models and network parameters");
		if(modelDB.getNetworkClass() == null || isEmpty(modelDB.getNetworkClass()))
			selectNetworkClass.setEnabled(false);
		selectScriptClass = new JCheckBox("Script definitions");
		if(modelDB.getScriptClass() == null || isEmpty(modelDB.getScriptClass()))
			selectScriptClass.setEnabled(false);
		selectOutlineClass = new JCheckBox("Borders (outlines)");
		if(modelDB.getOutlineClass() == null || isEmpty(modelDB.getOutlineClass()))
			selectOutlineClass.setEnabled(false);
		selectFlagClass = new JCheckBox("Operating grades (flags)");
		if(modelDB.getFlagClass() == null || isEmpty(modelDB.getFlagClass()))
			selectFlagClass.setEnabled(false);
		Box checkBoxPane = Box.createVerticalBox();
		checkBoxPane.setBorder(new EmptyBorder(10, 40, 10, 10));
		checkBoxPane.add(selectNetworkClass);
		checkBoxPane.add(selectScriptClass);
		checkBoxPane.add(selectFlagClass);
		checkBoxPane.add(selectOutlineClass);
		ActionListener boxListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateSelections();
			}
		};
		selectNetworkClass.addActionListener(boxListener);
		selectScriptClass.addActionListener(boxListener);
		selectOutlineClass.addActionListener(boxListener);
		selectFlagClass.addActionListener(boxListener);
		
		addOKButton();
		addCancelButton();
		
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(checkBoxPane, BorderLayout.CENTER);
		
		setCenterComponent(contentPane);
	}
	
	private boolean isEmpty(ModelClassData clazz) {
		return clazz.getModelClass().isEmpty() && clazz.getModel().isEmpty();
	}
	
	public void showDialog(int width, int height) {
		super.showDialog(width, height);
	}
	
	protected boolean checkInput() {
		return ! (isNetworkClassSelected == false && isScriptClassSelected == false 
				&& isOutlineClassSelected == false && isFlagClassSelected == false);
	}
	
	private void updateSelections() {
		isNetworkClassSelected = selectNetworkClass.isSelected();
		isScriptClassSelected = selectScriptClass.isSelected();
		isOutlineClassSelected = selectOutlineClass.isSelected();
		isFlagClassSelected = selectFlagClass.isSelected();
	}
	
	public boolean isNetworkParameterClassSelected() {
		return isNetworkClassSelected;
	}
	
	public boolean isScriptClassSelected() {
		return isScriptClassSelected;
	}
	
	public boolean isOutlineClassSelected() {
		return isOutlineClassSelected;
	}
	
	public boolean isFlagClassSelected() {
		return isFlagClassSelected;
	}
}
