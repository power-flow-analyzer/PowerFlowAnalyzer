package net.ee.pfanalyzer.ui.viewer.network.painter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.Bus;
import net.ee.pfanalyzer.model.CombinedBus;
import net.ee.pfanalyzer.model.Generator;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.preferences.Preferences;
import net.ee.pfanalyzer.ui.viewer.network.NetworkMapViewer;

public class BusPainter extends AbstractShapePainter {
	
	public final static String PAINT_ID = "paint.network.bus";

	private final static double OVAL_HALF_HEIGHT = 8;
	
	protected List<CombinedBus> visibleBusses = new ArrayList<CombinedBus>();
	
	public BusPainter(Network network, NetworkMapViewer viewer) {
		super(network, viewer);
	}

	@Override
	public String getPaintID() {
		return PAINT_ID;
	}

	@Override
	public int getLayer() {
		return LAYER_BUS_NODES;
	}

	@Override
	public void paint(Graphics2D g2d) {
		if(viewer.isDrawBusNodes()) {
			synchronized (visibleBusses) {
				for (CombinedBus cbus : visibleBusses) {
					boolean hasFailures = cbus.hasFailures();
					boolean hasWarnings = cbus.hasWarnings();
					boolean isSlack = false;
					boolean isSelected = viewer.isSelection(cbus);
					boolean isHovered = viewer.isHovered(cbus);
					for (int b = 0; b < cbus.getBusNodes().size(); b++) {
						Bus bus = cbus.getBusNodes().get(b);
						if( ! isSlack && bus.isSlackNode())
							isSlack = true;
						if( ! isSelected && viewer.isSelection(bus))
							isSelected = true;
						if( ! isHovered && viewer.isHovered(bus))
							isHovered = true;
					}
					for (int b = 0; b < cbus.getGenerators().size(); b++) {
						Generator generator = cbus.getGenerators().get(b);
						if( ! isSelected && viewer.isSelection(generator))
							isSelected = true;
						if( ! isHovered && viewer.isHovered(generator))
							isHovered = true;
					}
					boolean highlighted = isSelected || isHovered;
					boolean fadeOut = viewer.isFadeOutUnselected() && viewer.hasSelection() && highlighted == false;
					if(viewer.isDrawFlags() && hasFailures)
						g2d.setColor(Preferences.getFlagFailureColor());
					else if(viewer.isDrawFlags() && hasWarnings)
						g2d.setColor(Preferences.getFlagWarningColor());
					else {
						if(isSlack)
							g2d.setColor(Color.BLUE);
						else if(fadeOut)
							g2d.setColor(Color.GRAY);
						else
							g2d.setColor(Color.BLACK);
					}
					double[] coords = viewer.getBusXYDouble(cbus.getFirstBus().getBusNumber());
					double x = coords[0];
					double y = coords[1];
					String locationName = viewer.isDrawElementNames() ? cbus.getLabel() : null;
					drawShape(cbus.getFirstBus(), g2d, x, y, Double.NaN, Double.NaN, highlighted, locationName, null);
	//				// draw generators
	//				if(drawGenerators && cbus.getGenerators().size() > 0) {
	//					for (int gen = 0; gen < cbus.getGenerators().size(); gen++) {
	//						Generator generator = cbus.getGenerators().get(gen);
	//						if( ! isSelected && isSelection(generator))
	//							isSelected = true;
	//						if( ! isHovered && isHovered(generator))
	//							isHovered = true;
	//						
	//					}
	//					if(hasFailures)
	//						g.setColor(Preferences.getFlagFailureColor());
	//					else if(hasWarnings)
	//						g.setColor(Preferences.getFlagWarningColor());
	//					else {
	//						if(isSlack)
	//							g.setColor(Color.BLUE);
	//						else
	//							g.setColor(Color.BLACK);
	//					}
	//					int x_gen = (int) x;//getBusX(busIndex, horizontalScale);
	//					int y_gen = (int) y;//getBusY(busIndex, verticalScale);
	//					if(x == -1 || y == -1)
	//						continue;
	//					g2d.drawRect(
	//							x_gen - RECTANGLE_HALF_HEIGHT - 1, 
	//							y_gen - RECTANGLE_HALF_HEIGHT - 1, 
	//							2 * RECTANGLE_HALF_HEIGHT + 2, 
	//							2 * RECTANGLE_HALF_HEIGHT + 2);
	//					if(isSelected || isHovered) {
	//						g2d.drawRect(
	//								x_gen - RECTANGLE_HALF_HEIGHT - 2, 
	//								y_gen - RECTANGLE_HALF_HEIGHT - 2, 
	//								2 * RECTANGLE_HALF_HEIGHT + 4, 
	//								2 * RECTANGLE_HALF_HEIGHT + 4);
	//						g2d.drawRect(
	//								x_gen - RECTANGLE_HALF_HEIGHT - 3, 
	//								y_gen - RECTANGLE_HALF_HEIGHT - 3, 
	//								2 * RECTANGLE_HALF_HEIGHT + 6, 
	//								2 * RECTANGLE_HALF_HEIGHT + 6);
	//					}
	//				}
				}
			}
		}
	}

	@Override
	public void update() {
		updateVisibleBusNodes();
		// determine factor for size of element shapes
		Rectangle2D.Double r = visibleRect;
		double minDim = Math.max(r.getMaxX() - r.getMinX(), r.getMaxY() - r.getMinY());
		int busCount = getVisibleBusNodes().size();
		double shapeSizeFactor;
		if(busCount > 0 && minDim > 0)
			shapeSizeFactor = ((r.getMaxX() - r.getMinX()) * (r.getMaxY() - r.getMinY())) / (5000 * busCount);//minDim / (10.0 * busCount);
		else
			shapeSizeFactor = 1;
		shapeProvider.setShapeSizeFactor(shapeSizeFactor);
//		System.out.println("shapeSizeFactor="+shapeSizeFactor);

	}
	
	@Override
	public AbstractNetworkElement getObjectFromScreen(int x, int y) {
		if(viewer.isDrawBusNodes()) {
			for (CombinedBus cbus : visibleBusses) {
				Bus bus = cbus.getFirstBus();
				int busNumber = bus.getBusNumber();
				int[] coords = viewer.getBusXY(busNumber);
				if(x == -1 || y == -1)
					continue;
				if(Math.abs(coords[0] - x) <= OVAL_HALF_HEIGHT
						&& Math.abs(coords[1] - y) <= OVAL_HALF_HEIGHT)
					return getNetwork().getBus(busNumber);
			}
		}
		return null;
	}
	
	private void updateVisibleBusNodes() {
		synchronized (visibleBusses) {
			visibleBusses.clear();
			double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY, maxX = 0, maxY = 0;
			for (int i = 0; i < getNetwork().getCombinedBusCount(); i++) {
				CombinedBus cbus = getNetwork().getCombinedBus(i);
				double[] coords = viewer.getBusXYDouble(cbus.getFirstBus().getBusNumber());
				double x = coords[0];
				double y = coords[1];
				if(isOutsideView(x, y))
					continue;
				minX = Math.min(minX, x);
				maxX = Math.max(maxX, x);
				minY = Math.min(minY, y);
				maxY = Math.max(maxY, y);
				visibleBusses.add(cbus);
			}
			visibleRect.setFrame(minX, minY, maxX - minX, maxY - minY);
	//		System.out.println("Visible combined busses: " + visibleBusses.size());
		}
	}
	
	List<CombinedBus> getVisibleBusNodes() {
		return visibleBusses;
	}
}
