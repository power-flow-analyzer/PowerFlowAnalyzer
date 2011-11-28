package net.ee.pfanalyzer.ui.viewer.network.contour;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.border.LineBorder;

public class Legend extends JComponent {
	
	private ContourDiagramSettings settings;

	public Legend(ContourDiagramSettings settings) {
		this.settings = settings;
		setPreferredSize(new Dimension(30, 200));
		setBorder(new LineBorder(Color.BLACK));
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
	}
}
