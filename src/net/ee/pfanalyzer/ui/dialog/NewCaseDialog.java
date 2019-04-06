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
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.filechooser.FileFilter;

import net.ee.pfanalyzer.PowerFlowAnalyzer;
import net.ee.pfanalyzer.preferences.IPreferenceConstants;
import net.ee.pfanalyzer.preferences.Preferences;
import net.ee.pfanalyzer.ui.util.FileSelectionPanel;
import net.miginfocom.swing.MigLayout;

public class NewCaseDialog extends BaseDialog implements IPreferenceConstants {
	
	public final static int CASE_FILE_INPUT_SOURCE = 0;
	
	public final static int MATPOWER_CASE_INPUT_SOURCE = 1;
	
	public final static int MATLAB_SCRIPT_INPUT_SOURCE = 2;
	
	public static FileFilter PARAMETER_FILE_FILTER = new FileFilter() {
		@Override
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith(".xml");
		}

		@Override
		public String getDescription() {
			return "Parameter Files (*.xml)";
		}
	};
	
	private String parameterFile;
	private int selectedParameterSource;
	private FileSelectionPanel parameterFileInputPanel;
	private JRadioButton matpowerParametersButton, customParametersButton;
	
	public NewCaseDialog(PowerFlowAnalyzer frame, String title) {
		super(frame, title);
		setText("Select a parameter database and press OK.");
		
		parameterFileInputPanel = new FileSelectionPanel(PROPERTY_PARAMETERS_FILES, PARAMETER_FILE_FILTER, false);
		matpowerParametersButton = new JRadioButton("Matpower Compatible");
		customParametersButton = new JRadioButton("Custom Parameters");
		JComponent openMatpowerCasePanel = new JPanel(new MigLayout("insets 10", "[]10[]20[grow]"));
		openMatpowerCasePanel.add(new JLabel("Parameters: "));
		openMatpowerCasePanel.add(matpowerParametersButton);
		openMatpowerCasePanel.add(customParametersButton, "wrap");
		final JLabel parameterFileLabel = new JLabel("Parameter File: ");
		openMatpowerCasePanel.add(parameterFileLabel);
		openMatpowerCasePanel.add(parameterFileInputPanel, "span 2, growx, width 300::");
		ButtonGroup bg = new ButtonGroup();
		bg.add(matpowerParametersButton);
		bg.add(customParametersButton);
		ActionListener radioListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parameterFileInputPanel.setEnabled(customParametersButton.isSelected());
				parameterFileLabel.setEnabled(customParametersButton.isSelected());
			}
		};
		matpowerParametersButton.addActionListener(radioListener);
		customParametersButton.addActionListener(radioListener);
		String parameterSelection = Preferences.getProperty(PROPERTY_PARAMETER_MODE_SELECTION, true);
		if(parameterSelection == null || "matpower".equals(parameterSelection))
			matpowerParametersButton.doClick();
		else if("custom".equals(parameterSelection))
			customParametersButton.doClick();
		
		addOKButton();
		addCancelButton();
		
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(openMatpowerCasePanel, BorderLayout.CENTER);
		
		setCenterComponent(contentPane);
	}
	
	public void showDialog(int width, int height) {
		super.showDialog(width, height);
	}
	
	protected boolean checkInput() {
		if(customParametersButton.isSelected())
			return parameterFileInputPanel.checkFile();
		return true;
	}
	
	protected void okPressed() {
		if(matpowerParametersButton.isSelected()) {
			selectedParameterSource = 0;
		} else if(customParametersButton.isSelected()) {
			selectedParameterSource = 1;
			parameterFileInputPanel.setNewLocation(null);
			parameterFile = parameterFileInputPanel.getFilePath();
			parameterFileInputPanel.saveEntriesToProperties();
		}
		Preferences.setProperty(PROPERTY_PARAMETER_MODE_SELECTION, customParametersButton.isSelected() ? "custom" : "matpower");
	}
	
	public int getSelectedParameterSource() {
		return selectedParameterSource;
	}
	
	public String getSelectedParameterFile() {
		return parameterFile;
	}
}
