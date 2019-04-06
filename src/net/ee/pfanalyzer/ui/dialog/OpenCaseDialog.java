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
import java.awt.Frame;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import net.ee.pfanalyzer.preferences.IPreferenceConstants;
import net.ee.pfanalyzer.ui.util.FileSelectionPanel;
import net.miginfocom.swing.MigLayout;

public class OpenCaseDialog extends BaseDialog implements IPreferenceConstants {
	
	public static FileFilter CASE_FILE_FILTER = new FileFilter() {
		@Override
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith(".case");
		}

		@Override
		public String getDescription() {
			return "Case Files (*.case)";
		}
	};
	
	private String selectedInputFile;
	private FileSelectionPanel caseFileInputPanel;
	
	public OpenCaseDialog(Frame frame) {
		super(frame, "Open case");
		setText("Select a case file and press OK.");

		caseFileInputPanel = new FileSelectionPanel(PROPERTY_CASE_FILES, CASE_FILE_FILTER, false);
		JComponent openCaseFilePanel = new JPanel(new MigLayout("insets 10", "[]10[grow]"));
		openCaseFilePanel.add(new JLabel("Case File: "));
		openCaseFilePanel.add(caseFileInputPanel, "growx, width 300::");
		
		addOKButton();
		addCancelButton();
		
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(openCaseFilePanel, BorderLayout.CENTER);
		
		setCenterComponent(contentPane);
	}
	
	public void showDialog(int width, int height) {
		super.showDialog(width, height);
	}
	
	protected boolean checkInput() {
		return caseFileInputPanel.checkFile();
	}
	
	protected void okPressed() {
		caseFileInputPanel.setNewLocation(null);
		selectedInputFile = caseFileInputPanel.getFilePath();
		caseFileInputPanel.saveEntriesToProperties();
	}
	
	public String getSelectedInputFile() {
		return selectedInputFile;
	}
}
