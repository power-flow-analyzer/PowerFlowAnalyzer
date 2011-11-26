package net.ee.pfanalyzer.ui.viewer.network.contour;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.border.LineBorder;

public class Legend extends JComponent {
	
	private ColorProvider colorProvider;

	public Legend(ColorProvider colorProvider) {
		this.colorProvider = colorProvider;
		setPreferredSize(new Dimension(30, 200));
		setBorder(new LineBorder(Color.BLACK));
	}
	
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		int height = getHeight();
		int half = (int) (height / 2.0);
		int width = getWidth();
		for (int i = 0; i < half; i++) {
			if(i == half/2.0)
				g2d.setColor(Color.BLACK);
			else
				g2d.setColor(colorProvider.getColor((double) (half - i) / (double) half));
			g2d.drawLine(0, i, width, i);
		}
		for (int i = 0; i < half; i++) {
			if(i == 0 || i == half/2.0)
				g2d.setColor(Color.BLACK);
			else
				g2d.setColor(colorProvider.getColor((double) (-i) / (double) half));
			g2d.drawLine(0, i + half, width, i + half);
		}
	}
}
