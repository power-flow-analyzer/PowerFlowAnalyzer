package net.ee.pfanalyzer.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

public class NetworkViewerLegend extends JComponent {

	private final static int branchLength = 50;
	private final static int X_OFFSET = 10;
	private final static int Y_OFFSET = 5;
	private final static int X_MARGIN = 30;
	private final static int X_PADDING = 5;
	
	private NetworkViewer viewer;
	
	NetworkViewerLegend(NetworkViewer viewer) {
		this.viewer = viewer;
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		try {
			paintLegend(g);
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}
	
	public Dimension getPreferredSize() {
		if(getGraphics() == null)
			return new Dimension(0, 0);
		int textHeight = getGraphics().getFontMetrics().getHeight();
		return new Dimension(0, textHeight + Y_OFFSET);
	}

	protected void paintLegend(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		// enable anti aliasing
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//		g2d.setColor(Color.WHITE);
//		g2d.fillRect(0, 0, getWidth(), getHeight());// draw background
		g2d.setColor(Color.BLACK);
		g2d.drawLine(0, 0, getWidth(), 0);// draw horizontal bar
		int textHeight = g2d.getFontMetrics().getHeight();
		int linePosY = g2d.getFontMetrics().getAscent() / 2;
		int x = X_OFFSET;
		for (Integer voltageLevel : viewer.getVoltageLevels()) {
			String text = voltageLevel + " kV";
			g2d.drawString(text, x, textHeight);
			x += g2d.getFontMetrics().getStringBounds(text, g2d).getWidth() + X_PADDING;
			g2d.setStroke(viewer.getBranchStroke(voltageLevel, false));
			g2d.drawLine(x, textHeight - linePosY, 
					x + branchLength, textHeight - linePosY);
			x += branchLength + X_MARGIN;
		}
	}
}
