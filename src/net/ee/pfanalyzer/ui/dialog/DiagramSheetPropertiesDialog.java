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

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import net.ee.pfanalyzer.model.diagram.DiagramSheetProperties;
import net.ee.pfanalyzer.model.matpower.BranchDescriptor;
import net.ee.pfanalyzer.model.matpower.BusDescriptor;
import net.ee.pfanalyzer.model.matpower.GeneratorDescriptor;

public class DiagramSheetPropertiesDialog extends BaseDialog {

	private DiagramSheetProperties diagramProps;
	
	private JTabbedPane tabbedPane;
	private JCheckBox[] busDataBoxes, branchDataBoxes, generatorDataBoxes;
	private JTextField diagramName;
	
	public DiagramSheetPropertiesDialog(Frame frame) {
		this(frame, null);
	}
	
	public DiagramSheetPropertiesDialog(Frame frame, DiagramSheetProperties props) {
		super(frame, props == null ? "Create new Diagram Sheet" : "Diagram Sheet Properties");
		setText("Select the data to be displayed in this sheet");
		if(props == null)
			props = new DiagramSheetProperties();// create default properties
		diagramProps = props;
		
		JPanel contentPane = new JPanel(new BorderLayout());
		tabbedPane = new JTabbedPane();
		// busses
		Box busDataBoxPane = Box.createVerticalBox();
		busDataBoxes = new JCheckBox[BusDescriptor.DATA_FIELD_COUNT];
		for (int i = 0; i < busDataBoxes.length; i++) {
			busDataBoxes[i] = new JCheckBox(BusDescriptor.getDescription(i),
					props.hasBusDataDiagram(i));
			busDataBoxPane.add(busDataBoxes[i]);
		}
		tabbedPane.addTab("Bus Data", new JScrollPane(busDataBoxPane));
		// branches
		Box branchDataBoxPane = Box.createVerticalBox();
		branchDataBoxes = new JCheckBox[BranchDescriptor.DATA_FIELD_COUNT];
		for (int i = 0; i < branchDataBoxes.length; i++) {
			branchDataBoxes[i] = new JCheckBox(BranchDescriptor.getDescription(i),
					props.hasBranchDataDiagram(i));
			branchDataBoxPane.add(branchDataBoxes[i]);
		}
		tabbedPane.addTab("Branch Data", new JScrollPane(branchDataBoxPane));
		// generators
		Box generatorDataBoxPane = Box.createVerticalBox();
		generatorDataBoxes = new JCheckBox[GeneratorDescriptor.DATA_FIELD_COUNT];
		for (int i = 0; i < generatorDataBoxes.length; i++) {
			generatorDataBoxes[i] = new JCheckBox(GeneratorDescriptor.getDescription(i),
					props.hasGeneratorDataDiagram(i));
			generatorDataBoxPane.add(generatorDataBoxes[i]);
		}
		tabbedPane.addTab("Generator Data", new JScrollPane(generatorDataBoxPane));
		
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		JPanel optionsPanel = new JPanel(new BorderLayout());
		diagramName = new JTextField(diagramProps.getTitle() == null ? "Diagram" : diagramProps.getTitle());
		optionsPanel.add(new JLabel("Diagram Title: "), BorderLayout.WEST);
		optionsPanel.add(diagramName, BorderLayout.CENTER);
		contentPane.add(optionsPanel, BorderLayout.SOUTH);
		
		getContentPane().add(contentPane, BorderLayout.CENTER);
		
		addOKButton();
		addCancelButton();
		showDialog(400, 500);
	}
	
	protected void okPressed() {
		for (int i = 0; i < busDataBoxes.length; i++)
			diagramProps.setBusDataDiagram(i, busDataBoxes[i].isSelected());
		for (int i = 0; i < branchDataBoxes.length; i++)
			diagramProps.setBranchDataDiagram(i, branchDataBoxes[i].isSelected());
		for (int i = 0; i < generatorDataBoxes.length; i++)
			diagramProps.setGeneratorDataDiagram(i, generatorDataBoxes[i].isSelected());
		diagramProps.setTitle(diagramName.getText().trim());
	}

	public DiagramSheetProperties getDiagramSheetProperties() {
		return diagramProps;
	}
}
