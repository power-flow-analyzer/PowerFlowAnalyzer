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
import java.awt.Window;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import net.ee.pfanalyzer.preferences.IPreferenceConstants;
import net.ee.pfanalyzer.ui.util.FileSelectionPanel;
import net.miginfocom.swing.MigLayout;

public class ImportMatpowerDialog extends BaseDialog implements IPreferenceConstants {
	
	public static FileFilter MATPOWER_CASE_FILTER = new FileFilter() {
		@Override
		public boolean accept(File f) {
			return f.isDirectory() || (f.getName().startsWith("case") && f.getName().endsWith(".m"));
		}

		@Override
		public String getDescription() {
			return "Matpower Case Files (case*.m)";
		}
	};
	
	private String selectedInputFile;
	private JLabel workingDirLabel;
	private FileSelectionPanel matpowerCaseInputPanel;
	
	public ImportMatpowerDialog(Window frame) {
		super(frame, "Open Matpower case");
		setText("Select a Matpower case and press OK.");

		matpowerCaseInputPanel = new FileSelectionPanel(PROPERTY_MATPOWER_CASE_FILES, MATPOWER_CASE_FILTER, false);
		JComponent openMatlabDataFilePanel = new JPanel(new MigLayout("insets 10", "[]10[grow]"));
		openMatlabDataFilePanel.add(new JLabel("Matpower Case: "));
		openMatlabDataFilePanel.add(matpowerCaseInputPanel, "growx, width 300::");
		JPanel resizer = new JPanel(new BorderLayout());
		resizer.add(openMatlabDataFilePanel, BorderLayout.NORTH);

		addOKButton();
		addCancelButton();
		
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(resizer, BorderLayout.CENTER);
		
		setCenterComponent(contentPane);
	}
	
	public void showDialog(int width, int height) {
		super.showDialog(width, height);
	}
	
	protected boolean checkInput() {
		return matpowerCaseInputPanel.checkFile();
	}
	
	protected void okPressed() {
		matpowerCaseInputPanel.setNewLocation(null);
		selectedInputFile = matpowerCaseInputPanel.getFilePath();
		matpowerCaseInputPanel.saveEntriesToProperties();
	}
	
	public String getSelectedInputFile() {
		return selectedInputFile;
	}
	
	public void setWorkingDirectory(final String workingDirectory) {
		workingDirLabel.setText(workingDirectory);				
	}
}
