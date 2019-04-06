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

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import net.ee.pfanalyzer.PowerFlowAnalyzer;

public abstract class ProgressDialog extends JDialog {
	
	private JProgressBar progressBar;
	
	public ProgressDialog() {
		super(PowerFlowAnalyzer.getInstance(), "Please wait...", false);
		PowerFlowAnalyzer frame = PowerFlowAnalyzer.getInstance();
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setBorder(new EmptyBorder(10, 50, 20, 50));
		JLabel l = new JLabel("<html><b>Please wait while the requested operations are executed...");
		l.setBorder(new EmptyBorder(20, 10, 30, 10));
		contentPane.add(l, BorderLayout.CENTER);
		contentPane.add(progressBar, BorderLayout.SOUTH);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(contentPane, BorderLayout.CENTER);
		pack();
		int centerX = frame.getX() + frame.getWidth() / 2;
		int centerY = frame.getY() + frame.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					setVisible(true);
					Thread.sleep(500);
					while(showProgressDialog()) {
						Thread.sleep(500);
					}
				} catch(InterruptedException e) {
				} finally {
					setVisible(false);
					dispose();
				}
			}
		}).start();
	}
	
	protected abstract boolean showProgressDialog();
}
