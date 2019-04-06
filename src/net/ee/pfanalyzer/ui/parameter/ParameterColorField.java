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
package net.ee.pfanalyzer.ui.parameter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;

import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ParameterUtils;

public class ParameterColorField extends ParameterValuePanel implements ActionListener {
	
	private final static int BORDER = 5;
	
	private JButton button;
	private Color color;
	
	public ParameterColorField(IParameterMasterElement element, NetworkParameter property, NetworkParameter propertyValue) {
		super(element, property, propertyValue);
		button.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(ignoreAction())
			return;
		Color newColor = JColorChooser.showDialog(this, "Select a color", color);
		if(newColor != null)
			color = newColor;
		repaint();
		NetworkParameter property = getMasterElement().getParameter(getPropertyID(), true);
		String value = getColorValue();
		String oldValue = property.getValue();
		property.setValue(value);
		fireValueChanged(oldValue, value);
		refresh();
	}
	
	@Override
	protected void createValuePanel() {
		button = new JButton() {
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if(color != null) {
					g.setColor(color);
					g.fillRect(BORDER, BORDER, getWidth() - 2 * BORDER, getHeight() - 2 * BORDER);
					g.setColor(Color.BLACK);
					g.drawRect(BORDER, BORDER, getWidth() - 2 * BORDER, getHeight() - 2 * BORDER);
				}
			}
		};
	}
	
	public JButton getButton() {
		return button;
	}

	@Override
	protected JComponent getValuePanel() {
		return button;
	}
	
	private String getColorValue() {
		if(color != null)
			return color.getRed() + "," + color.getGreen() + "," + color.getBlue();
		return null;
	}

	@Override
	protected void setValue(String value) {
		color = ParameterUtils.parseColor(value);
	}
}
