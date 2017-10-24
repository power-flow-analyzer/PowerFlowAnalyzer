package net.ee.pfanalyzer.ui.viewer.network.painter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.MarkerElement;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.NetworkElement;
import net.ee.pfanalyzer.ui.viewer.network.NetworkMapViewer;

public class MarkerPainter extends AbstractShapePainter {
	
	public final static String PAINT_ID = "paint.network.marker";

	private List<MarkerElement> cachedMarkers = new ArrayList<MarkerElement>();
	private List<MarkerElement> visibleMarkers = new ArrayList<MarkerElement>();
	
	private int networkMarkerSize = 20;
	
	public MarkerPainter(Network network, NetworkMapViewer viewer) {
		super(network, viewer);
	}

	@Override
	public String getPaintID() {
		return PAINT_ID;
	}

	@Override
	public int getLayer() {
		return LAYER_MARKERS;
	}

	@Override
	public void paint(Graphics2D g2d) {
		if(viewer.isDrawMarkers()) {
			synchronized (visibleMarkers) {
				g2d.setColor(Color.GRAY);
				for (MarkerElement marker : visibleMarkers) {
					double[] markerCoords = viewer.getMarkerXYDouble(marker.getIndex());
					double markerX = markerCoords[0];
					double markerY = markerCoords[1];
					if(markerX == -1 || markerY == -1)
						continue;
					double[] busCoords = viewer.getBusXYDouble(marker.getParentBusNumber());
					double busX = busCoords[0];
					double busY = busCoords[1];
					// set stroke
					boolean highlighted = viewer.isSelection(marker) || viewer.isHovered(marker);
					if(highlighted)
						g2d.setStroke(strokesBold[0]);
					else
						g2d.setStroke(strokesNormal[0]);
					String locationName = viewer.isDrawElementNames() ? marker.getDisplayName(NetworkElement.DISPLAY_NAME) : null;
					// draw the marker
					drawShape(marker, g2d, markerX, markerY, busX, busY, highlighted, locationName, null);
				}
			}
		}
	}

	@Override
	public void update() {
		cachedMarkers = network.getMarkers();
		updateVisibleMarkers();
	}
	
	@Override
	public AbstractNetworkElement getObjectFromScreen(int x, int y) {
		if(viewer.isDrawMarkers()) {
		for (MarkerElement marker : cachedMarkers) {
			int markerIndex = marker.getIndex();
			double[] coords = viewer.getMarkerXYDouble(markerIndex);
			if(x == -1 || y == -1)
				continue;
			if(Math.abs(coords[0] - x) <= networkMarkerSize / 2.0
					&& Math.abs(coords[1] - y) <= networkMarkerSize / 2.0)
				return getNetwork().getMarkers().get(markerIndex);
		}
	}
		return null;
	}
	
	private void updateVisibleMarkers() {
		synchronized (visibleMarkers) {
			visibleMarkers.clear();
			double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY, maxX = 0, maxY = 0;
			for (MarkerElement marker : cachedMarkers) {
				double[] coords = viewer.getBusXYDouble(marker.getIndex());
				double x = coords[0];
				double y = coords[1];
				if(isOutsideView(x, y))
					continue;
				minX = Math.min(minX, x);
				maxX = Math.max(maxX, x);
				minY = Math.min(minY, y);
				maxY = Math.max(maxY, y);
				visibleMarkers.add(marker);
			}
			visibleRect.setFrame(minX, minY, maxX - minX, maxY - minY);
	//		System.out.println("Visible markers: " + visibleBusses.size());
		}
	}
}
