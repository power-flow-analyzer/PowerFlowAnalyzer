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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import net.ee.pfanalyzer.PowerFlowAnalyzer;
import net.ee.pfanalyzer.model.PowerFlowCase;
import net.ee.pfanalyzer.ui.NetworkViewer;
import net.ee.pfanalyzer.ui.util.SwingUtils;

public class DataViewerContainer extends JPanel {
	
	private INetworkDataViewer viewer;
	private PowerFlowCase powerFlowCase;
	private NetworkViewer powerFlowviewer;
	private Box buttonPane;

	public DataViewerContainer(INetworkDataViewer viewer, NetworkViewer powerFlowviewer) {
		super(new BorderLayout());
		this.viewer = viewer;
		this.powerFlowCase = powerFlowviewer.getPowerFlowCase();
		this.powerFlowviewer = powerFlowviewer;
		
		buttonPane = Box.createVerticalBox();
		buttonPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		addAction("Edit viewer properties", "wrench.png", "Edit viewer properties", false,
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					showPropertiesDialog();
				}
		});
		// add actions from viewer
		viewer.addViewerActions(this);

		add(buttonPane, BorderLayout.EAST);
		add(SwingUtils.createScrollpane(viewer.getComponent()), BorderLayout.CENTER);
	}
	
	public AbstractButton addAction(String tooltipText, String iconName, 
			String altText, boolean toggleAction, ActionListener listener) {
		AbstractButton button = toggleAction ? 
				PowerFlowAnalyzer.createToggleButton(tooltipText, iconName, altText, false) : 
					PowerFlowAnalyzer.createButton(tooltipText, iconName, altText, false);
		button.setMargin(new Insets(2, 2, 2, 2));
		button.addActionListener(listener);
		buttonPane.add(button);
		buttonPane.add(Box.createVerticalStrut(10));
		return button;
	}
	
	private void showPropertiesDialog() {
		DataViewerParameterDialog dialog = new DataViewerParameterDialog(SwingUtilities.getWindowAncestor(this), 
				"Viewer Properties", getViewer().getViewerConfiguration(), powerFlowCase, false);
		dialog.showDialog(-1, -1);
		if(dialog.isOkPressed()) {
			getViewer().refresh();
			powerFlowviewer.updateTabTitles();
		}
	}

	public INetworkDataViewer getViewer() {
		return viewer;
	}
}
