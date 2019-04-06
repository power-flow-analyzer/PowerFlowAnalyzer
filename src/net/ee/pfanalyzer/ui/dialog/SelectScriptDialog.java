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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.ee.pfanalyzer.model.PowerFlowCase;
import net.ee.pfanalyzer.model.data.ModelData;

public class SelectScriptDialog extends BaseDialog {

	private ModelData selectedScript;
	
	public SelectScriptDialog(Frame frame, PowerFlowCase caze) {
		super(frame, "Select script");
		setText("<html><center><b>Select a script to be executed and press OK.");
		
		Box contentPane = Box.createVerticalBox();
		for (final ModelData script : caze.getModelDB().getScriptClass().getModel()) {
			String text = "<html>"  + "<b>" + script.getLabel() + "</b>";
			if(script.getDescription() != null)
				text += "<br>" + script.getDescription();
			JButton button = new JButton(text);
			button.setHorizontalAlignment(SwingConstants.LEFT);
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					selectedScript = script;
					okPressed = true;
					SelectScriptDialog.this.setVisible(false);
				}
			});
			if(caze.getModelDB().getScriptClass().getModel().size() == 1)
				button.doClick();
			contentPane.add(button);
		}
		addCancelButton();
		
		JPanel contentPaneResizer = new JPanel();
		contentPaneResizer.add(contentPane);
		
		getContentPane().add(contentPaneResizer, BorderLayout.CENTER);
	}

	@Override
	protected boolean checkInput() {
		return getSelectedScript() != null;
	}
	
	public ModelData getSelectedScript() {
		return selectedScript;
	}
}
