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
package net.ee.pfanalyzer.ui.viewer;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.ee.pfanalyzer.PowerFlowAnalyzer;
import net.ee.pfanalyzer.model.data.ModelData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.ui.dialog.BaseDialog;

public class SelectViewerDialog extends BaseDialog {

	private ModelData selectedViewer;
	
	public SelectViewerDialog(Window frame) {
		super(frame, "Select viewer");
		setText("<html><center><b>Select a viewer from the list below");
		
		Box contentPane = Box.createVerticalBox();
		for (final ModelData viewer : PowerFlowAnalyzer.getConfiguration().getConfigurations("viewer")) {
			String text = "<html>"  + "<b>" + viewer.getLabel() + "</b>";
			if(viewer.getDescription() != null)
				text += "<br>" + viewer.getDescription();
			JButton button = new JButton(text);
			button.setHorizontalAlignment(SwingConstants.LEFT);
			button.setIconTextGap(10);
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					selectedViewer = viewer;
					okPressed = true;
					SelectViewerDialog.this.setVisible(false);
				}
			});
			NetworkParameter iconName = ModelDBUtils.getParameterValue(viewer, "ICON");
			if(iconName != null && iconName.getValue() != null)
				button.setIcon(new ImageIcon(PowerFlowAnalyzer.getIconURL(iconName.getValue(), true)));
			contentPane.add(button);
			contentPane.add(Box.createVerticalStrut(10));
		}
		addCancelButton();
		
		JPanel contentPaneResizer = new JPanel();
		contentPaneResizer.add(contentPane);
		
		getContentPane().add(contentPaneResizer, BorderLayout.CENTER);
	}

	@Override
	protected boolean checkInput() {
		return getSelectedViewer() != null;
	}
	
	public ModelData getSelectedViewer() {
		return selectedViewer;
	}
}
