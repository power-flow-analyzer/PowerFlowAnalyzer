package net.ee.pfanalyzer.ui.viewer.network;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;

import net.ee.pfanalyzer.math.coordinate.Mercator;
import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.Bus;
import net.ee.pfanalyzer.model.CombinedNetworkElement;
import net.ee.pfanalyzer.model.MarkerElement;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.ui.NetworkElementSelectionManager;
import net.ee.pfanalyzer.ui.viewer.DataViewerConfiguration;
import net.ee.pfanalyzer.ui.viewer.INetworkDataViewer;

public abstract class CoordinateMap extends JComponent implements INetworkDataViewer, INetworkMapParameters {

	public final static String BASE_NETWORK_VIEWER_ID = "viewer.network";
	
	private final static Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
	private final static Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
	
	protected final static int HORIZONTAL_GAP = 20;
	protected final static int VERTICAL_GAP = 20;

	protected double minLatitude = ZOOM_GERMANY_COORDINATES[0];
	protected double maxLatitude = ZOOM_GERMANY_COORDINATES[1];
	protected double minLongitude = ZOOM_GERMANY_COORDINATES[2];
	protected double maxLongitude = ZOOM_GERMANY_COORDINATES[3];
	
	private DataViewerConfiguration viewerConfiguration;
	private Network data;
	
	private Mercator converter;
	protected Map<Integer, int[]> internalBusCoords = new HashMap<Integer, int[]>();
	protected Map<Integer, int[]> internalMarkerCoords = new HashMap<Integer, int[]>();
	protected int internalMinX = 0;
	protected int internalMaxX = 0;
	protected int internalMinY = 0;
	protected int internalMaxY = 0;
	
	protected boolean respectAspectRatio = true;
	protected boolean perfectFit = true;
	protected boolean drawBusNodes = true;
	protected boolean drawBusNames = true;
	protected boolean drawBranches = true;
	protected boolean drawGenerators = true;
	protected boolean drawOutline = true;
	protected boolean drawPowerDirection = true;
	protected boolean drawMarkers = true;
	protected boolean drawFlags = true;
	protected boolean fadeOutUnselected = false;
	
	protected boolean allowZooming = true;
	protected boolean allowDragging = true;
	protected boolean showTooltips = true;
	
	protected double horizontalScale, verticalScale;
	private int dragFactor = 300;
	protected Object selection, hover;
	
	protected abstract AbstractNetworkElement getObjectFromScreen(int x, int y);
	
	protected abstract String getTooltipText(AbstractNetworkElement dataElement);
	
	protected abstract void paintNetwork(Graphics2D g2d);
	
	protected CoordinateMap(Network data, DataViewerConfiguration viewerConfiguration) {
		this.data = data;
		this.viewerConfiguration = viewerConfiguration;
		converter = new Mercator();
		NetworkMouseListener mouseListener = new NetworkMouseListener();
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
		addMouseWheelListener(new NetworkWheelListener());
	}

	@Override
	public Network getNetwork() {
		return data;
	}

	@Override
	public DataViewerConfiguration getViewerConfiguration() {
		return viewerConfiguration;
	}
	
	protected void initializeInternalCoordinates() {
		if(perfectFit == false) {
			internalMinX = converter.getX(minLongitude);
			internalMaxX = converter.getX(maxLongitude);
			internalMinY = converter.getY(minLatitude);
			internalMaxY = converter.getY(maxLatitude);
		}
		for (int i = 0; i < data.getBusses().size(); i++) {
			Bus bus = data.getBusses().get(i);
			double longitude = bus.getLongitude();
			double latitude = bus.getLatitude();
			if(Double.isNaN(longitude)// no coords set
					|| Double.isNaN(latitude)) {
				internalBusCoords.put(bus.getBusNumber(), new int[]{-1, -1});
				continue;
			}
			int x = converter.getX(longitude);
			int y = converter.getY(latitude);
			internalBusCoords.put(bus.getBusNumber(), new int[]{x, y});
			if(perfectFit) {
				if(i == 0) {// initialize min/max values
					internalMinX = x;
					internalMaxX = x;
					internalMinY = y;
					internalMaxY = y;
					minLatitude = latitude;
					maxLatitude = latitude;
					minLongitude = longitude;
					maxLongitude = longitude;
				} else {
					internalMinX = Math.min(internalMinX, x);
					internalMaxX = Math.max(internalMaxX, x);
					internalMinY = Math.min(internalMinY, y);
					internalMaxY = Math.max(internalMaxY, y);
					minLatitude = Math.min(minLatitude, latitude);
					maxLatitude = Math.max(maxLatitude, latitude);
					minLongitude = Math.min(minLongitude, longitude);
					maxLongitude = Math.max(maxLongitude, longitude);
				}
			}
		}
		for (MarkerElement marker : data.getMarkers()) {
			double longitude = marker.getLongitude();
			double latitude = marker.getLatitude();
			if(Double.isNaN(longitude)// no coords set
					|| Double.isNaN(latitude)) {
				internalMarkerCoords.put(marker.getIndex(), new int[]{-1, -1});
				continue;
			}
			int x = converter.getX(longitude);
			int y = converter.getY(latitude);
			internalMarkerCoords.put(marker.getIndex(), new int[]{x, y});
		}
	}

	public void paintViewer(Graphics g) {
		paintComponent(g);
	}

	protected void paintComponent(Graphics g) {
		try {
			Graphics2D g2d = (Graphics2D) g;
			// enable anti aliasing
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//			g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
//			g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
//			g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
			// fill background
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, getWidth(), getHeight());
			// real painting
			if(internalBusCoords != null) {
				double width = internalMaxX - internalMinX;
				horizontalScale = ((double) getWidth() - 2 * HORIZONTAL_GAP) / width;
				double height = internalMaxY - internalMinY;
				verticalScale = ((double) getHeight() - 2 * VERTICAL_GAP) / height;
				if(respectAspectRatio) {
					if(verticalScale < horizontalScale) {
						horizontalScale = verticalScale;
					} else {
						verticalScale = horizontalScale;
					}
				}
				paintNetwork(g2d);
			} else {
				g.setColor(Color.BLACK);
				g.drawString("No coordinate data provided.", 0, getHeight() / 2);
			}
			if(isFocusOwner()) {
				g.setColor(Color.BLUE);
				g2d.setStroke(new BasicStroke(3));
				g.drawRect(0, 0, getWidth()-1, getHeight()-1);
			}
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}
	
	private double getLatitudeDifference(int y1, int y2) {
		int diffY = y1 - y2;
		double latitudeDiff = maxLatitude - minLatitude;
		return diffY * latitudeDiff / dragFactor;
	}
	
	private double getLongitudeDifference(int x1, int x2) {
		int diffX = x1 - x2;
		double longitudeDiff = maxLongitude - minLongitude;
		return diffX * longitudeDiff / dragFactor;
	}
	
	protected int[] getBusXY(int i) {
		double[] coords = getBusXYDouble(i);
		return new int[] { (int) coords[0], (int) coords[1] };
	}
	
	public double[] getBusXYDouble(int i) {
		int[] coords = internalBusCoords.get(i);
		if(coords == null)
			return new double[] { -1, -1 };
		if(coords[0] == -1 || coords[1] == -1)
			return new double[] { -1, -1 };
		double x = ((coords[0] - internalMinX) * horizontalScale) + HORIZONTAL_GAP;
		double y = getHeight() - ((coords[1] - internalMinY) * verticalScale) - VERTICAL_GAP;
		return new double[] { x, y };
	}
	
	protected double[] getMarkerXYDouble(int i) {
		int[] coords = internalMarkerCoords.get(i);
		if(coords == null)
			return new double[] { -1, -1 };
		if(coords[0] == -1 || coords[1] == -1)
			return new double[] { -1, -1 };
		double x = ((coords[0] - internalMinX) * horizontalScale) + HORIZONTAL_GAP;
		double y = getHeight() - ((coords[1] - internalMinY) * verticalScale) - VERTICAL_GAP;
		return new double[] { x, y };
	}
	
	public double getX(int[] coords) {
		return ((coords[0] - internalMinX) * horizontalScale) + HORIZONTAL_GAP;
	}
	
	public double getY(int[] coords) {
		return getHeight() - ((coords[1] - internalMinY) * verticalScale) - VERTICAL_GAP;
	}

	public void setView(double[] coordinates) {
		this.minLatitude = coordinates[0];
		this.maxLatitude = coordinates[1];
		this.minLongitude = coordinates[2];
		this.maxLongitude = coordinates[3];
	}
	
	private void changeZoom() {
		perfectFit = false;
		getViewerConfiguration().setParameter("ZOOM", 1);
	}
	
	public void setPerfectFit(boolean flag) {
		perfectFit = flag;
		initializeInternalCoordinates();
		repaint();
	}

	@Override
	public void selectionChanged(Object data) {
		selection = data;
		repaint();
	}
	
	protected boolean isHovered(Object object) {
		return hover != null && hover == object;
	}
	
	protected boolean isSelection(Object object) {
		if(object == null || selection == null)
			return false;
		if(selection == object)
			return true;
		if(object instanceof CombinedNetworkElement<?> 
				&& selection instanceof AbstractNetworkElement) {
			return ((CombinedNetworkElement<?>) object).contains((AbstractNetworkElement) selection);
		}
		if(selection instanceof CombinedNetworkElement<?> 
				&& object instanceof AbstractNetworkElement) {
			return ((CombinedNetworkElement<?>) selection).contains((AbstractNetworkElement) object);
}
		return selection != null && selection == object;
	}
	
	protected void setViewerProperty(String property, String value) {
		if(property.equals(PROPERTY_ZOOM_CHOICE)) {
			if(value == null)
				value = "0";
			int intvalue = Integer.valueOf(value);
			if(intvalue == 2)
				setView(ZOOM_GERMANY_COORDINATES);
			setPerfectFit(intvalue == 0);
		} else if(property.equals(PROPERTY_RESPECT_ASPECT_RATIO)) {
			respectAspectRatio = Boolean.valueOf(value);
			initializeInternalCoordinates();
			repaint();
		} else if(property.equals(PROPERTY_DRAW_BUSSES)) {
			drawBusNodes = Boolean.valueOf(value);
			repaint();
		} else if(property.equals(PROPERTY_DRAW_BUS_NAMES)) {
			drawBusNames = Boolean.valueOf(value);
			repaint();
		} else if(property.equals(PROPERTY_DRAW_BRANCHES)) {
			drawBranches = Boolean.valueOf(value);
			repaint();
		} else if(property.equals(PROPERTY_DRAW_POWER_DIRECTION)) {
			drawPowerDirection = Boolean.valueOf(value);
			repaint();
		} else if(property.equals(PROPERTY_DRAW_GENERATORS)) {
			drawGenerators = Boolean.valueOf(value);
			repaint();
		} else if(property.equals(PROPERTY_DRAW_OUTLINE)) {
			drawOutline = Boolean.valueOf(value);
			updateBackground();
		} else if(property.equals(PROPERTY_DRAW_LEGEND)) {
			setVisible(Boolean.valueOf(value));
			revalidate();
		} else if(property.equals(PROPERTY_FADE_OUT_UNSELECTED)) {
			fadeOutUnselected = Boolean.valueOf(value);
			repaint();
		} else if(property.equals(PROPERTY_INTERACTION_ZOOM)) {
			allowZooming = Boolean.valueOf(value);
			repaint();
		} else if(property.equals(PROPERTY_INTERACTION_MOVE)) {
			allowDragging = Boolean.valueOf(value);
			repaint();
		} else if(property.equals(PROPERTY_SHOW_TOOLTIPS)) {
			showTooltips = Boolean.valueOf(value);
			repaint();
		}
	}
	
	protected void initializeSettings() {
		setSetting(PROPERTY_ZOOM_CHOICE);
		setSetting(PROPERTY_RESPECT_ASPECT_RATIO);
		setSetting(PROPERTY_DRAW_BUSSES);
		setSetting(PROPERTY_DRAW_BUS_NAMES);
		setSetting(PROPERTY_DRAW_BRANCHES);
		setSetting(PROPERTY_DRAW_POWER_DIRECTION);
		setSetting(PROPERTY_DRAW_GENERATORS);
		setSetting(PROPERTY_DRAW_OUTLINE);
		setSetting(PROPERTY_DRAW_LEGEND);
		setSetting(PROPERTY_INTERACTION_ZOOM);
		setSetting(PROPERTY_INTERACTION_MOVE);
		setSetting(PROPERTY_SHOW_TOOLTIPS);
		setSetting(PROPERTY_FADE_OUT_UNSELECTED);
	}
	
	protected void updateBackground() {
		repaint();
	}
	
	protected void setSetting(String parameterID) {
		NetworkParameter value = getViewerConfiguration().getParameterValue(parameterID);
		if(value != null && value.getValue() != null)
			setViewerProperty(parameterID, value.getValue());
	}

	class NetworkMouseListener extends MouseInputAdapter {
		int lastX = -1, lastY = -1;
		
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
				if(showTooltips) {
					if(hover != null && hover instanceof AbstractNetworkElement)
						setToolTipText(getTooltipText((AbstractNetworkElement) hover));
					else
						setToolTipText(null);
				}
			} catch(Exception except) {
				except.printStackTrace();
			}
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			if(allowDragging == false)
				return;
			try {
				changeZoom();
				if(lastX > -1 && lastY > -1) {
					double diffLat = getLatitudeDifference(lastY, e.getY());
					minLatitude -= diffLat;
					maxLatitude -= diffLat;
					double diffLong = getLongitudeDifference(lastX, e.getX());
					minLongitude += diffLong;
					maxLongitude += diffLong;
					initializeInternalCoordinates();
					repaint();
				}
				lastX = e.getX();
				lastY = e.getY();
			} catch(Exception except) {
				except.printStackTrace();
			}
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			lastX = e.getX();
			lastY = e.getY();
			requestFocusInWindow();// needed on windows
			repaint();// needed on windows
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			try {
				lastX = -1;
				lastY = -1;
				Object obj = getObjectFromScreen(e.getX(), e.getY());
				if(obj != null) {
					Object combinedObj = getNetwork().getCombinedBus((AbstractNetworkElement) obj);
					if(combinedObj == null)
						combinedObj = getNetwork().getCombinedBranch((AbstractNetworkElement) obj);
					if(combinedObj != null)
						obj = combinedObj;
				}
				if(obj != selection) {
					selectionChanged(obj);
					NetworkElementSelectionManager.selectionChanged(CoordinateMap.this, obj);
				}
			} catch(Exception except) {
				except.printStackTrace();
			}
		}
	}
	
	class NetworkWheelListener implements MouseWheelListener {
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			if(allowZooming == false)
				return;
			// don't allow scrolling if mouse cursor is not above this component
			if(e.getX() < 0 || e.getX() > getWidth() || e.getY() < 0 || e.getY() > getHeight())
				return;
			try {
				changeZoom();
				// scale
				double wheelRot = -e.getWheelRotation();
				double scaleLatitude = wheelRot * (maxLatitude - minLatitude) / 10.0; 
				double scaleLongitude = wheelRot * (maxLongitude - minLongitude) / 10.0;
				minLatitude += scaleLatitude;
				maxLatitude -= scaleLatitude;
				minLongitude += scaleLongitude;
				maxLongitude -= scaleLongitude;
				// translate
				
				if(wheelRot > 0) {
					double factor = 0.5;
					int x = e.getX();
					int y = e.getY();
					int centerX = getWidth()/2;
					int centerY = getHeight()/2;
					double diffLat = getLatitudeDifference(y, centerY) * factor;
					double diffLong = getLongitudeDifference(x, centerX) * factor;
					minLatitude -= diffLat;
					maxLatitude -= diffLat;
					minLongitude += diffLong;
					maxLongitude += diffLong;
				}
				initializeInternalCoordinates();
				repaint();
			} catch(Exception except) {
				except.printStackTrace();
			}
		}
	}
}
