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
package net.ee.pfanalyzer.ui.viewer.network.contour;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.DecimalFormat;

import javax.swing.JComponent;
import javax.swing.border.LineBorder;

public class ColorLegend extends JComponent {
	
	private static DecimalFormat FORMAT = new DecimalFormat("#.###");
	private static int VALUE_OFFSET = 2;

	private ContourDiagramSettings settings;

	public ColorLegend(ContourDiagramSettings settings) {
		this.settings = settings;
		setBorder(new LineBorder(Color.BLACK));
	}
	
	public Dimension getPreferredSize() {
		int minwidth = 30;
		if(getGraphics() != null) {
			String maxText = FORMAT.format(settings.getMaxValue());
			minwidth = (int) Math.max(minwidth, getGraphics().getFontMetrics().getStringBounds(
					maxText, getGraphics()).getWidth() + 2 * VALUE_OFFSET);
			String minText = FORMAT.format(settings.getMinValue());
			minwidth = (int) Math.max(minwidth, getGraphics().getFontMetrics().getStringBounds(
					minText, getGraphics()).getWidth() + 2 * VALUE_OFFSET);
			String middleText = FORMAT.format(settings.getMiddleValue());
			minwidth = (int) Math.max(minwidth, getGraphics().getFontMetrics().getStringBounds(
					middleText, getGraphics()).getWidth() + 2 * VALUE_OFFSET);
		}
		return new Dimension(minwidth, 200);
	}
	
	protected void paintComponent(Graphics g) {
		if(settings.isIncomplete())
			return;
		Graphics2D g2d = (Graphics2D) g;
		double maxValue = settings.getMaxValue();
		double middleValue = settings.getMiddleValue();
		double minValue = settings.getMinValue();
		boolean showMax = maxValue != middleValue;
		boolean showMin = minValue != middleValue;
		int width = getWidth();
		if(showMax) {
			int height = showMin ? (int) (getHeight() / 2.0) : getHeight();
			for (int i = 0; i < height; i++) {
				if(i == height/2.0)
					g2d.setColor(Color.BLACK);
				else
					g2d.setColor(settings.getColorProvider().getColor((double) (height - i) / (double) height));
				g2d.drawLine(0, i, width, i);
			}
		}
		if(showMin) {
			int heightOffset = showMax ? (int) (getHeight() / 2.0) : 0;
			int height = showMax ? (int) (getHeight() / 2.0) : getHeight();
			for (int i = 0; i < height; i++) {
				if(i == 0 || i == height/2.0)
					g2d.setColor(Color.BLACK);
				else
					g2d.setColor(settings.getColorProvider().getColor((double) (-i) / (double) height));
				g2d.drawLine(0, i + heightOffset, width, i + heightOffset);
			}
		}
		g2d.setColor(Color.BLACK);
		// draw max value
		if(showMax) {
			String maxText = FORMAT.format(maxValue);
			g2d.drawString(maxText, VALUE_OFFSET, g2d.getFontMetrics().getAscent());
		} else {
			// draw middle value
			String middleText = FORMAT.format(middleValue);
			g2d.drawString(middleText, VALUE_OFFSET, g2d.getFontMetrics().getAscent());
		}
		// draw min value
		if(showMin) {
			String minText = FORMAT.format(minValue);
			g2d.drawString(minText, VALUE_OFFSET, getHeight() - g2d.getFontMetrics().getDescent());
		} else {
			// draw middle value
			String middleText = FORMAT.format(middleValue);
			g2d.drawString(middleText, VALUE_OFFSET, getHeight() - g2d.getFontMetrics().getDescent());
		}
		// draw middle value
		if(showMax && showMin) {
			g2d.setColor(Color.BLACK);
			String middleText = FORMAT.format(middleValue);
			g2d.drawString(middleText, VALUE_OFFSET, getHeight() / 2 + g2d.getFontMetrics().getAscent());
		}
	}
}
