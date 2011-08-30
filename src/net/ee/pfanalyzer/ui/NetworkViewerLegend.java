package net.ee.pfanalyzer.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

public class NetworkViewerLegend extends JComponent {

	private NetworkViewer viewer;
	
	NetworkViewerLegend(NetworkViewer viewer) {
		this.viewer = viewer;
		setPreferredSize(new Dimension(300, 20));
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		try {
			paintLegend(g);
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}

	protected void paintLegend(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		// enable anti aliasing
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//		g2d.setColor(Color.WHITE);
//		g2d.fillRect(0, 0, getWidth(), getHeight());// draw background
		g2d.setColor(Color.BLACK);
		g2d.drawLine(0, 0, getWidth(), 0);// draw horizontal bar
		int branchLength = 50;
		int textLength = 55;
		int levelLength = textLength + branchLength + 30;
		int textHeight = g2d.getFontMetrics().getHeight();
		int linePosY = g2d.getFontMetrics().getAscent() / 2;
		// 380 kV
		g2d.drawString("380 kV", 10, textHeight);
		g2d.setStroke(viewer.getBranchStroke(380, false));
		g2d.drawLine(textLength, textHeight - linePosY, 
				textLength + branchLength, textHeight - linePosY);
		// 220 kV
		g2d.drawString("220 kV", levelLength, textHeight);
		g2d.setStroke(viewer.getBranchStroke(220, false));
		g2d.drawLine(levelLength + textLength, textHeight - linePosY, 
				levelLength + textLength + branchLength, textHeight - linePosY);
	}
}
