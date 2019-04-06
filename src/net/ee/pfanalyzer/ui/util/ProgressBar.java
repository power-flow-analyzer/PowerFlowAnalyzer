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
package net.ee.pfanalyzer.ui.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import net.ee.pfanalyzer.preferences.Preferences;

public class ProgressBar extends JComponent {
	static final int BAR_HEIGHT = 16;
	double value;
	boolean isError, isWarning;
	static Dimension DIMENSION = new Dimension(150, BAR_HEIGHT);
	
	public ProgressBar(double value, boolean isError, boolean isWarning) {
		this.value = value;
		this.isError = isError;
		this.isWarning = isWarning;
	}
	
	public Dimension getPreferredSize() {
		return DIMENSION;
	}
	
	protected void paintComponent(Graphics g) {
		int y = getHeight() / 2 - BAR_HEIGHT / 2;
		g.setColor(Color.WHITE);
		g.fillRect(0, y, getWidth() - 1, BAR_HEIGHT);
		int barWidth = Math.min((int) (getWidth() * value / 100), getWidth());
		if(isError)
			g.setColor(Preferences.getFlagFailureColor());
		else if(isWarning)
			g.setColor(Preferences.getFlagWarningColor());
		else
			g.setColor(Preferences.getFlagCorrectColor());
		g.fillRect(0, y, barWidth - 1, BAR_HEIGHT - 1);
		g.setColor(Color.BLACK);
		g.drawRect(0, y, getWidth() - 1, BAR_HEIGHT - 1);
		String text = (int) value + "%";
		Rectangle2D rect = g.getFontMetrics(g.getFont()).getStringBounds(text, g);
		int x = (int) (getWidth() / 2 - rect.getWidth() / 2);
		g.drawString(text, x, (int) (y + rect.getHeight()));
	}
}
