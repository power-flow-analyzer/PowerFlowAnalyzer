package net.ee.pfanalyzer.ui.viewer.diagram;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.NetworkChangeEvent;
import net.ee.pfanalyzer.model.NetworkFlag;
import net.ee.pfanalyzer.model.data.DataViewerData;
import net.ee.pfanalyzer.ui.NetworkElementSelectionManager;
import net.ee.pfanalyzer.ui.viewer.DataViewerConfiguration;
import net.ee.pfanalyzer.ui.viewer.INetworkDataViewer;

public class PowerFlowDiagram extends JComponent implements INetworkDataViewer {

	private final static Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
	private final static Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
	
	private DataViewerData viewerData;
	private Network network;
	private String elementID, parameterID;
	private List<AbstractNetworkElement> elements;
	private double[] values;
//	private int dataField;
	private int xOffset = -1;
//	private int yOffset = 0;
	private int xMargin = 10;
	private int yMargin = 10;
	private int xSpace = 5;
	private int barWidth = 10;
	private double min, max;
	private String minLabel, maxLabel;
	private int[][] barCoordinates;
	private String title;
	
	private Object selection, hover;
	DecimalFormat format = new DecimalFormat("#.###");
	
	public PowerFlowDiagram(DataViewerData viewerData) {
		this.viewerData = viewerData;
//		this.elementID = viewerData.getElementFilter();
		if(true)
			throw new RuntimeException("TODO: Parameter-ID setzen");//TODO
//		this.parameterID = viewerData.get;
		MouseInputListener mouseListener = new MouseInputAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				try {
					AbstractNetworkElement obj = getObjectFromScreen(e.getX(), e.getY());
					if(obj != null)
						setCursor(HAND_CURSOR);
					else
						setCursor(DEFAULT_CURSOR);
					if(hover != obj) {
						hover = obj;
						repaint();
					}
					if(hover != null && hover instanceof AbstractNetworkElement) {
						setToolTipText(getTooltipText((AbstractNetworkElement) hover));
					}
					else
						setToolTipText(null);
				} catch(Exception except) {
					except.printStackTrace();
				}
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					AbstractNetworkElement obj = getObjectFromScreen(e.getX(), e.getY());
					if(obj != selection) {
						selectionChanged(obj);
						NetworkElementSelectionManager.selectionChanged(PowerFlowDiagram.this, obj);
					}
				} catch(Exception except) {
					except.printStackTrace();
				}
			}
		};
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
		revalidate();
	}
	
//	@Override
//	public DataViewerData getViewerData() {
//		return viewerData;
//	}
	
	@Override
	public DataViewerConfiguration getViewerConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JComponent getComponent() {
		return this;
	}

	@Override
	public void refresh() {
		revalidate();
	}

	@Override
	public void setData(Network network) {
		this.network = network;
		elements = network.getElements(elementID);
		values = new double[elements.size()];
		// determine min and max values
		for (int i = 0; i < values.length; i++) {
			values[i] = elements.get(i).getDoubleParameter(parameterID, Double.NaN);
			if(Double.isNaN(values[i]))
				continue;
			if(i == 0) {
				min = values[i];
				max = values[i];
			} else {
				min = Math.min(min, values[i]);
				max = Math.max(max, values[i]);
			}
		}
		if(max > 0 && min > 0) {
			min = 0;
		} else if(max < 0 && min < 0) {
			max = 0;
		}
//		System.out.println("Min: " + min);
//		System.out.println("Max: " + max);
		if(max > 0) {
			double factor = 1;
			while(max / factor > 10) {
				factor *= 10;
			}
			max = Math.ceil(max / factor) * factor;
		}
		if(min < 0) {
			double factor = 1;
			while(Math.abs(min / factor) > 10) {
				factor *= 10;
			}
			min = Math.floor(min / factor) * factor;
		}
		minLabel = format.format(min);
		maxLabel = format.format(max);
//		System.out.println("Min: " + minLabel);
//		System.out.println("Max: " + maxLabel);
	}

	private AbstractNetworkElement getObjectFromScreen(int x, int y) {
		for (int i = 0; i < barCoordinates.length; i++) {
			if(barCoordinates[i][0] < x && barCoordinates[i][2] > x) { // x coordinates matches mouse cursor
				if(barCoordinates[i][1] < y && barCoordinates[i][3] > y)
					return getElement(i);
			}
		}
		return null;
	}
	
	private String getTooltipText(AbstractNetworkElement element) {
		return element.getDisplayName(AbstractNetworkElement.DISPLAY_DEFAULT) 
				+ " (" + element.getTextParameter(parameterID) + ")";
	}
	
    public Dimension getPreferredSize() {
    	Dimension superDim = super.getPreferredSize();
    	superDim.width = xOffset + 2 * xMargin + getElementsCount() * barWidth + getElementsCount() * xSpace + xSpace;
    	return superDim;
    }

	protected void paintComponent(Graphics g) {
		try {
			paintDiagram(g);
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}

	protected void paintDiagram(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Font boldFont = g2d.getFont().deriveFont(Font.BOLD);
		int boldFontHeight = g2d.getFontMetrics(boldFont).getHeight();
		// draw the background
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		if(xOffset == -1) {
			xOffset = (int)Math.max( 
					getStringWidth(maxLabel, g2d),
					getStringWidth(minLabel, g2d)) + 1;
			xOffset += Math.max(boldFontHeight, xMargin) - xMargin + 3;
			barCoordinates = new int[getElementsCount()][4];
			revalidate();
			return;
		}
		int innerWidth = getWidth() - 2 * xMargin - xOffset;
		int innerHeight = getHeight() - 2 * yMargin;
		// draw diagram outline border
		g2d.setColor(Color.BLACK);
		int xCounter = xOffset + xMargin;
		g2d.drawRect(xCounter - 1, yMargin - 1, innerWidth + 1, innerHeight + 1);
		double heightFactor = ((double) innerHeight) / (Math.abs(min) + Math.abs(max));
		int xAxisY = (int) (Math.abs(max) * heightFactor) + yMargin;
		// draw X axis
		g.drawLine(xCounter, xAxisY, xCounter + innerWidth, xAxisY);
		xCounter += xSpace;
		// draw x axis labels
		int fontAscent = g2d.getFontMetrics().getAscent();
		int lineHeightHalf = fontAscent / 2;
		g2d.drawString(maxLabel, xMargin + xOffset - getStringWidth(maxLabel, g2d) - 1, yMargin + lineHeightHalf);
		g2d.drawString(minLabel, xMargin + xOffset - getStringWidth(minLabel, g2d) - 1, getHeight() - yMargin + lineHeightHalf);
		g2d.drawString("0", xMargin + xOffset - getStringWidth("0", g2d) - 1, xAxisY + lineHeightHalf);
		// draw bars
		for (int i = 0; i < getElementsCount(); i++) {
			double value = values[i];
			int barHeight = (int) (value  * heightFactor);
			int y = xAxisY;
			if(barHeight > 0) {
				y -= barHeight;
			}
			barCoordinates[i][0] = xCounter;
			barCoordinates[i][1] = y;
			barCoordinates[i][2] = xCounter + barWidth;
			barCoordinates[i][3] = y + Math.abs(barHeight);
			if(Math.abs(barHeight) < 2) {
				barCoordinates[i][1] = y - 2;
				barCoordinates[i][3] = y + 2;
			}
			g2d.setColor(Color.BLUE);
			for (NetworkFlag flag : getElement(i).getFlags()) {
				if(flag.isFailure() && flag.containsParameter(parameterID)) {
					g2d.setColor(Color.RED);
					break;
				}
			}
			g2d.fillRect(xCounter, y, barWidth, Math.abs(barHeight));
			g2d.setColor(Color.BLACK);
			g2d.drawRect(xCounter, y, barWidth, Math.abs(barHeight));
			AbstractNetworkElement childData = getElement(i);
			boolean isHovered = hover != null && hover == childData;
			boolean isSelected = selection != null && selection == childData;
			if(isHovered || isSelected)
				g2d.drawRect(xCounter + 1, y + 1, barWidth - 2, Math.abs(barHeight) - 2);
			xCounter += barWidth + xSpace;
		}
		if(getTitle() != null) {
			g.setFont(getFont().deriveFont(Font.BOLD));
			int titleWidth = getStringWidth(getTitle(), g2d);
			int textY = (getHeight() + titleWidth) / 2;
			int textX = boldFontHeight;
			g2d.rotate(3.0 * Math.PI / 2.0, textX, textY);
	//		TextLayout yAxisLabel = new TextLayout(getTitle(), getFont(), g2d.getFontRenderContext());
	//		AffineTransform textTransform = new AffineTransform();
			g2d.drawString(getTitle(), textX, textY);
	//		g2d.draw(yAxisLabel.getOutline(textTransform));
		}
	}
	
	private int getStringWidth(String text, Graphics g) {
		return (int) g.getFontMetrics().getStringBounds(text, g).getWidth();
	}
	
	@Override
	public Network getNetwork() {
		return network;
	}
	
//	protected int getDataField() {
//		return dataField;
//	}
	
//	protected abstract double[][] getChildData();
	
//	protected abstract AbstractNetworkElement getChild(int index);
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	protected int getElementsCount() {
		return elements.size();
	}
	
	protected AbstractNetworkElement getElement(int index) {
		return elements.get(index);
	}

	@Override
	public void selectionChanged(Object data) {
		selection = data;
		repaint();
	}

	@Override
	public void networkChanged(NetworkChangeEvent event) {
//		System.out.println("viewer: networkChanged");
		refresh();
	}

	@Override
	public void networkElementAdded(NetworkChangeEvent event) {
//		System.out.println("viewer: networkElementAdded");
		refresh();
	}

	@Override
	public void networkElementChanged(NetworkChangeEvent event) {
//		System.out.println("viewer: networkElementChanged");
		refresh();
	}

	@Override
	public void networkElementRemoved(NetworkChangeEvent event) {
		refresh();
	}
	
	@Override
	public void dispose() {
		
	}
}
