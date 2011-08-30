package net.ee.pfanalyzer.ui.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

public class ProgressBar extends JComponent {
	final int BAR_HEIGHT = 20;
	double value;
	boolean isCorrect;
	
	public ProgressBar(double value, boolean isCorrect) {
		this.value = value;
		this.isCorrect = isCorrect;
	}
	
	protected void paintComponent(Graphics g) {
		int y = getHeight() / 2 - BAR_HEIGHT / 2;
		g.setColor(Color.WHITE);
		g.fillRect(0, y, getWidth() - 1, BAR_HEIGHT);
		int barWidth = Math.min((int) (getWidth() * value / 100), getWidth());
		if(isCorrect)
			g.setColor(Color.GREEN.darker());
		else
			g.setColor(Color.RED);
		g.fillRect(0, y, barWidth - 1, BAR_HEIGHT);
		g.setColor(Color.BLACK);
		g.drawRect(0, y, getWidth() - 1, BAR_HEIGHT);
		String text = (int) value + "%";
		Rectangle2D rect = g.getFontMetrics(g.getFont()).getStringBounds(text, g);
		int x = (int) (getWidth() / 2 - rect.getWidth() / 2);
		g.drawString(text, x, (int) (y + rect.getHeight() + 1));
	}
}