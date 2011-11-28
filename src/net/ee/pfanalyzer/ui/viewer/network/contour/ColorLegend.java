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
		int height = getHeight();
		int half = (int) (height / 2.0);
		int width = getWidth();
		for (int i = 0; i < half; i++) {
			if(i == half/2.0)
				g2d.setColor(Color.BLACK);
			else
				g2d.setColor(settings.getColorProvider().getColor((double) (half - i) / (double) half));
			g2d.drawLine(0, i, width, i);
		}
		for (int i = 0; i < half; i++) {
			if(i == 0 || i == half/2.0)
				g2d.setColor(Color.BLACK);
			else
				g2d.setColor(settings.getColorProvider().getColor((double) (-i) / (double) half));
			g2d.drawLine(0, i + half, width, i + half);
		}
		g2d.setColor(Color.BLACK);
		// draw max value
		String maxText = FORMAT.format(settings.getMaxValue());
		g2d.drawString(maxText, VALUE_OFFSET, g2d.getFontMetrics().getAscent());
		// draw min value
		String minText = FORMAT.format(settings.getMinValue());
		g2d.drawString(minText, VALUE_OFFSET, getHeight() - g2d.getFontMetrics().getDescent());
		// draw middle value
		String middleText = FORMAT.format(settings.getMiddleValue());
		g2d.drawString(middleText, VALUE_OFFSET, getHeight() / 2 + g2d.getFontMetrics().getAscent());
	}
}
