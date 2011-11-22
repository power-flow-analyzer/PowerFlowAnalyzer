package net.ee.pfanalyzer.ui.map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

import net.ee.pfanalyzer.math.coordinate.Mercator;
import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.Branch;
import net.ee.pfanalyzer.model.Bus;
import net.ee.pfanalyzer.model.CombinedBranch;
import net.ee.pfanalyzer.model.CombinedBus;
import net.ee.pfanalyzer.model.DatabaseChangeEvent;
import net.ee.pfanalyzer.model.Generator;
import net.ee.pfanalyzer.model.IDatabaseChangeListener;
import net.ee.pfanalyzer.model.IInternalParameters;
import net.ee.pfanalyzer.model.MarkerElement;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.NetworkChangeEvent;
import net.ee.pfanalyzer.model.NetworkElement;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.preferences.Preferences;
import net.ee.pfanalyzer.ui.NetworkContainer;
import net.ee.pfanalyzer.ui.NetworkElementSelectionManager;
import net.ee.pfanalyzer.ui.dataviewer.DataViewerConfiguration;
import net.ee.pfanalyzer.ui.dataviewer.INetworkDataViewer;

public class NetworkViewer extends JComponent implements INetworkDataViewer, IDatabaseChangeListener {

	public final static String BASE_NETWORK_VIEWER_ID = "viewer.network";
	public final static String VIEWER_ID = BASE_NETWORK_VIEWER_ID + ".map";
	
	protected final static double OVAL_HALF_HEIGHT = 8;
	protected final static int RECTANGLE_HALF_HEIGHT = 10;
	protected final static int HORIZONTAL_GAP = 20;
	protected final static int VERTICAL_GAP = 20;
	
	private final static double[][] GERMANY = {
		{54.899671,8.639831}, {53.892362,8.623352}, {53.233331,7.206116}, {51.958823,6.805115}, 
		{51.806237,5.953674}, {51.427642,6.211853}, {51.021703,5.882263}, {50.364036,6.39862},
		{50.005268,6.129455}, {49.796514,6.530456}, {49.519146,6.365662}, {49.161232,6.805115},
		{48.969941,8.236542}, {48.604402,7.816315}, {48.085969,7.569122}, {47.591902,7.593841},
		{47.718262,8.634338}, {47.585048,9.76593}, {47.310152,10.194397}, {47.581343,10.441589},
		{47.399465,11.012878}, {47.673895,12.182922}, {47.481202,12.990417}, {47.697378,13.067779},
		{47.726946,12.905731}, {47.945166,12.936344}, {47.939646,12.922268}, {47.955283,12.918148},
		{47.964709,12.872486}, {48.015948,12.851887}, {48.112612,12.752266}, {48.200239,12.868881},
		{48.291691,13.252029}, {48.390276,13.416824}, {48.565067,13.486862}, {48.515968,13.727188},
		{48.769134,13.832931}, {48.968769,13.554153}, {48.961556,13.45665}, {49.340604,12.956772},
		{49.386217,12.757645}, {49.683891,12.521439}, {49.752259,12.401962}, {49.921432,12.546158},
		{50.119345,12.197113}, {50.319688,12.133942}, {50.18622,12.323456}, {50.45628,12.817841},
		{50.917926,14.385681}, {51.055725,14.295502}, {50.825544,14.792633}, {51.279614,15.037079},
		{51.554191,14.715271}, {51.826612,14.599914}, {52.07039,14.74823}, {52.343058,14.550476},
		{52.590702,14.616394}, {52.883386,14.149475}, {53.279338,14.429626}, {53.746437,14.270325},
		{54.567603,13.588257}, {53.977413,11.347046}, {54.041974,10.742798}, {54.497482,11.204223},
		{54.420848,10.248413}, {54.693361,10.027313}, {54.837556,9.409332}, {54.899671,8.639831}
	};
	private int[][] internalGermany = new int[GERMANY.length][2];
	
	private final static Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
	private final static Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
	
	final static int VOLTAGE_LEVEL_UNKNOWN = -1;
	final static int VOLTAGE_LEVEL_380KV = 0;
	final static int VOLTAGE_LEVEL_220KV = 1;
	final static int VOLTAGE_LEVEL_110KV = 2;
	
	protected Stroke[] strokesNormal, strokesBold;
	private Stroke otherStrokeNormal, otherStrokeBold;
	private GeneralPath arrow_pos, arrow_neg, networkShape;
	private List<Integer> voltageLevels;
	
	private void createStrokes(float widthNormal, float widthBold) {
		strokesNormal = new Stroke[] {
				// 380kV
				new BasicStroke(widthNormal),
				// 220kV
				new BasicStroke(widthNormal, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                        10.0f, new float[] { 10.0f }, 0.0f), 
                // 110kV
				new BasicStroke(widthNormal, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                        1.0f, new float[] { 2.0f, 7.0f }, 0.0f)
		};
		strokesBold = new Stroke[] {
				// 380kV
				new BasicStroke(widthBold),
				// 220kV
				new BasicStroke(widthBold, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                        10.0f, new float[] { 10.0f }, 0.0f), 
                // 110kV
				new BasicStroke(widthBold, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                        1.0f, new float[] { 2.0f, 7.0f }, 0.0f)
		};
		otherStrokeNormal = new BasicStroke(widthNormal, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                1.0f, new float[] { 3.0f, 2.0f, 5.0f }, 0.0f);
		otherStrokeBold = new BasicStroke(widthBold, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                1.0f, new float[] { 3.0f, 2.0f, 3.0f }, 0.0f);
	}
	
	private Network data;
	private Mercator converter;
	protected Map<Integer, int[]> internalBusCoords = new HashMap<Integer, int[]>();
	protected Map<Integer, int[]> internalMarkerCoords = new HashMap<Integer, int[]>();
	int internalMinX = 0;
	int internalMaxX = 0;
	int internalMinY = 0;
	int internalMaxY = 0;
	private List<Outline> outlines;
	private Collection<Outline> lastOutlineList;
	
	protected boolean respectAspectRatio = true;
	protected boolean perfectFit = true;
	protected boolean drawBusNodes = true;
	protected boolean drawBusNames = true;
	protected boolean drawBranches = true;
	protected boolean drawGenerators = true;
	protected boolean drawOutline = true;
	protected boolean drawPowerDirection = true;
	protected boolean drawMarkers = true;
	
	protected boolean allowZooming = true;
	protected boolean allowDragging = true;
	protected boolean showTooltips = true;
	
	private int dragFactor = 300;
	private int arrowSize = 10;
	private int networkMarkerSize = 20;
	
	private double minLatitude = NetworkViewerController.ZOOM_GERMANY_COORDINATES[0];
	private double maxLatitude = NetworkViewerController.ZOOM_GERMANY_COORDINATES[1];
	private double minLongitude = NetworkViewerController.ZOOM_GERMANY_COORDINATES[2];
	private double maxLongitude = NetworkViewerController.ZOOM_GERMANY_COORDINATES[3];
	
	protected double horizontalScale, verticalScale;
	
	private Object selection, hover;
	private DataViewerConfiguration viewerConfiguration;
	private NetworkViewerController controller;
	private Component parentContainer;

	public NetworkViewer(Network data, DataViewerConfiguration viewerConfiguration, Component parent) {
		this.data = data;
		this.viewerConfiguration = viewerConfiguration;
		this.parentContainer = parent;
		createStrokes(1.0f, 2.0f);
		converter = new Mercator();
		initializeInternalCoordinates();
		MouseInputListener mouseListener = new MouseInputAdapter() {
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
						Object combinedObj = NetworkViewer.this.data.getCombinedBus((AbstractNetworkElement) obj);
						if(combinedObj == null)
							combinedObj = NetworkViewer.this.data.getCombinedBranch((AbstractNetworkElement) obj);
						if(combinedObj != null)
							obj = combinedObj;
					}
					if(obj != selection) {
						selectionChanged(obj);
						NetworkElementSelectionManager.selectionChanged(NetworkViewer.this, obj);
					}
				} catch(Exception except) {
					except.printStackTrace();
				}
			}
		};
		MouseWheelListener wheelListener = new MouseWheelListener() {
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
//					if(horizontalScale > verticalScale)
//						scaleLatitude = getLatitudeDifference(internalMinY, internalMaxY);
//					else
//						scaleLatitude = getLongitudeDifference(internalMinX, internalMaxX);
//					int diffX = (int) ((internalMaxX - internalMinX) * horizontalScale + HORIZONTAL_GAP);
//					double diffLongitude = converter.getLongitude(diffX);
//					System.out.println("scaleLongitude="+scaleLongitude);
//					System.out.println("diffLongitude="+diffLongitude);
//					double centerLong = minLongitude + (maxLongitude - minLongitude) / 2.0;
//					double centerLat = minLatitude + (maxLatitude - minLatitude) / 2.0;
//					System.out.println("  minLatitude1="+minLatitude);
//					System.out.println("  maxLatitude1="+maxLatitude);
//					System.out.println("  minLongitude1="+minLongitude);
//					System.out.println("  maxLongitude1="+maxLongitude);
					minLatitude += scaleLatitude;
					maxLatitude -= scaleLatitude;
					minLongitude += scaleLongitude;
					maxLongitude -= scaleLongitude;
					// translate
//					centerLong = minLongitude + (maxLongitude - minLongitude) / 2.0;
//					centerLat = minLatitude + (maxLatitude - minLatitude) / 2.0;
//					System.out.println("  minLatitude2="+minLatitude);
//					System.out.println("  maxLatitude2="+maxLatitude);
//					System.out.println("  minLongitude2="+minLongitude);
//					System.out.println("  maxLongitude2="+maxLongitude);
					
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
		};
		controller = new NetworkViewerController(this);
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
		addMouseWheelListener(wheelListener);
		setFocusable(true);
		updateArrowSize(arrowSize);
		updateNetworkShape(networkMarkerSize);
		getViewerConfiguration().addDatabaseChangeListener(this);
	}
	
	public void dispose() {
		getViewerController().dispose();
		getViewerConfiguration().removeDatabaseChangeListener(this);
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
	
	private void initializeInternalCoordinates() {
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
		for (int i = 0; i < GERMANY.length; i++) {
			int x = converter.getX(GERMANY[i][1]);
			int y = converter.getY(GERMANY[i][0]);
			internalGermany[i] = new int[] { x, y };
		}
	}
	
	private AbstractNetworkElement getObjectFromScreen(int x, int y) {
		// check busses
		if(drawBusNodes) {
			Iterator<Integer> busNumbers = internalBusCoords.keySet().iterator();
			while(busNumbers.hasNext()) {
				int busNumber = busNumbers.next();
				int[] coords = getBusXY(busNumber);
				if(x == -1 || y == -1)
					continue;
				if(Math.abs(coords[0] - x) <= OVAL_HALF_HEIGHT
						&& Math.abs(coords[1] - y) <= OVAL_HALF_HEIGHT)
					return data.getBus(busNumber);
			}
		}
		// check generators
//		if(drawGenerators) {
//			for (int i = 0; i < data.getGenerators().size(); i++) {
//				Generator generator = data.getGenerators().get(i);
//				int busIndex = generator.getBusNumber();
//				int busX = getBusX(busIndex, horizontalScale);
//				int busY = getBusY(busIndex, verticalScale);
//				if(x == -1 || y == -1)
//					continue;
//				if(Math.abs(busX - x) <= RECTANGLE_HALF_HEIGHT + 2
//						&& Math.abs(busY - y) <= RECTANGLE_HALF_HEIGHT + 2)
//					return generator;
//			}
//		}
		// check markers
		if(drawMarkers) {
			Iterator<Integer> busNumbers = internalMarkerCoords.keySet().iterator();
			while(busNumbers.hasNext()) {
				int markerIndex = busNumbers.next();
				double[] coords = getMarkerXYDouble(markerIndex);
				if(x == -1 || y == -1)
					continue;
				if(Math.abs(coords[0] - x) <= networkMarkerSize / 2.0
						&& Math.abs(coords[1] - y) <= networkMarkerSize / 2.0)
					return data.getMarkers().get(markerIndex);
			}
		}
		// check branches
		if(drawBranches) {
			for (int i = 0; i < data.getBranches().size(); i++) {
				Branch branch = data.getBranches().get(i);
				int fromBus = branch.getFromBusNumber();
				int toBus = branch.getToBusNumber();
				double[] coords1 = getBusXYDouble(fromBus);
				double x1 = coords1[0];//getBusX(fromBus, horizontalScale);
				double y1 = coords1[1];//getBusY(fromBus, verticalScale);
				if(x1 == -1 || y1 == -1)
					continue;
				double[] coords2 = getBusXYDouble(toBus);
				double x2 = coords2[0];//getBusX(toBus, horizontalScale);
				double y2 = coords2[1];//getBusY(toBus, verticalScale);
				if(x2 == -1 || y2 == -1)
					continue;
				double minX = Math.min(x1, x2);
				double maxX = Math.max(x1, x2);
				if(x < minX - 2 || x > maxX + 2)
					continue;
				double minY = Math.min(y1, y2);
				double maxY = Math.max(y1, y2);
				if(y < minY - 2 || y > maxY + 2)
					continue;
				double m = (y2 - y1) / (x2 - x1);
				// check if m is infinity -> x2 = x1 -> both bus points on a vertical line
				if(Double.isInfinite(m) && Math.abs(x1 - x) <= 5)
					return branch;
				double n = y1 - m * x1;
				if(Math.abs((m * x + n - y)/(Math.sqrt(m * m + 1))) <= 5)
					return branch;
			}
		}
		return null;
	}

	protected void paintComponent(Graphics g) {
		try {
			paintNetwork(g);
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}

	protected void paintNetwork(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		// enable anti aliasing
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
//		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
//		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		Stroke defaultStroke = g2d.getStroke();
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
			// draw outlines
			if(drawOutline) {
				drawOutlines(g2d);
			}
			// draw markers
			if(drawMarkers) {
				g.setColor(Color.GRAY);
				List<MarkerElement> markers = data.getMarkers();
				for (MarkerElement marker : markers) {
					double[] markerCoords = getMarkerXYDouble(marker.getIndex());
					double markerX = markerCoords[0];
					double markerY = markerCoords[1];
					if(markerX == -1 || markerY == -1)
						continue;
					double[] busCoords = getBusXYDouble(marker.getParentBusNumber());
					double busX = busCoords[0];
					double busY = busCoords[1];
					// set stroke
					if(isSelection(marker) || isHovered(marker))
						g2d.setStroke(strokesBold[0]);
					else
						g2d.setStroke(strokesNormal[0]);
					// draw line between marker and bus
					if(busX != -1 && busY != -1) {
						// determine where the line should be connected to the marker
						double markerOutlineX = markerX;
						double markerOutlineY = markerY;
						double xDiff = busX - markerX;
						double yDiff = busY - markerY;
						if(xDiff < yDiff || yDiff == 0)
							markerOutlineX += (Math.signum(xDiff) == 1 ? 1 : -1) * networkMarkerSize / 2.0;
						else
							markerOutlineY += (Math.signum(yDiff) == 1 ? 1 : -1) * networkMarkerSize / 2.0;
						// draw the line
						g2d.draw(new Line2D.Double(markerOutlineX, markerOutlineY, busX, busY));
					}
					// draw the marker
					g2d.draw(getNetworkShape(markerX, markerY));
					// draw the marker's name
					String locationName = marker.getDisplayName(NetworkElement.DISPLAY_NAME);
					if(locationName != null)
						g2d.drawString(locationName, (int) markerX+15, (int) markerY+15);
				}
			}
			// draw branches
			if(drawBranches) {
				for (int i = 0; i < data.getCombinedBranchCount(); i++) {
					CombinedBranch cbranch = data.getCombinedBranch(i);
					boolean isSelected = isSelection(cbranch);
					boolean isHovered = isHovered(cbranch);
					for (int b = 0; b < cbranch.getBranchCount(); b++) {
						Branch branch = cbranch.getBranch(b);
						if( ! isSelected && isSelection(branch))
							isSelected = true;
						if( ! isHovered && isHovered(branch))
							isHovered = true;
					}
					int fromBus = cbranch.getFirstBranch().getFromBusNumber();
					int toBus = cbranch.getFirstBranch().getToBusNumber();
					double[] coords1 = getBusXYDouble(fromBus);
					double x1 = coords1[0];//getBusXDouble(fromBus, horizontalScale);
					double y1 = coords1[1];//getBusYDouble(fromBus, verticalScale);
					if(x1 == -1 || y1 == -1)
						continue;
					double[] coords2 = getBusXYDouble(toBus);
					double x2 = coords2[0];//getBusXDouble(toBus, horizontalScale);
					double y2 = coords2[1];//getBusYDouble(toBus, verticalScale);
					if(x2 == -1 || y2 == -1)
						continue;
					if(drawPowerDirection) {
						// draw first branch
						Branch branch = cbranch.getFirstBranch();
						if(branch.isCorrect()) {
							if(branch.isActive())
								g.setColor(Color.BLUE);
							else
								g.setColor(Color.GRAY);
						} else
							g.setColor(Color.RED);
						g2d.setStroke(getBranchStroke(branch, isSelected || isHovered));
						g2d.draw(new Line2D.Double(x1, y1, x2, y2));
						double realInjectionSumFrom = cbranch.getFromBusRealInjectionSum();
						double realInjectionSumTo = cbranch.getToBusRealInjectionSum();
						if(realInjectionSumFrom != realInjectionSumTo)
							g2d.fill(getArrowShape(g2d, x1, y1, x2, y2, realInjectionSumFrom > realInjectionSumTo));
					} else {
						double branch_space = 5;
						double alpha = Math.atan((y2-y1)/(x2-x1));
						double beta1 = alpha + Math.PI / 2.0;
						double xFactor = branch_space * Math.cos(beta1);
						double yFactor = branch_space * Math.sin(beta1);
						double nrbranches = cbranch.getBranchCount() - 1;
						double xOffset = xFactor * nrbranches / 2.0;
						double yOffset = yFactor * nrbranches / 2.0;
						for (int b = 0; b < cbranch.getBranchCount(); b++) {
							double bd = b;
							double x1l = x1 - xOffset + xFactor * bd;
							double y1l = y1 - yOffset + yFactor * bd;
							double x2l = x2 - xOffset + xFactor * bd;
							double y2l = y2 - yOffset + yFactor * bd;
							Branch branch = cbranch.getBranch(b);
							if(branch.isCorrect()) {
								if(branch.isActive())
									g.setColor(Color.BLUE);
								else
									g.setColor(Color.GRAY);
							} else
								g.setColor(Color.RED);
							g2d.setStroke(getBranchStroke(branch, isSelected || isHovered));
							g2d.draw(new Line2D.Double(x1l, y1l, x2l, y2l));
						}
					}
				}
			}
			g2d.setStroke(defaultStroke);
			// draw bus nodes
			if(drawBusNodes) {
				for (int i = 0; i < data.getCombinedBusCount(); i++) {
					CombinedBus cbus = data.getCombinedBus(i);
					boolean hasFailures = cbus.hasFailures();
					boolean hasWarnings = cbus.hasWarnings();
					boolean isSlack = false;
					boolean isSelected = isSelection(cbus);
					boolean isHovered = isHovered(cbus);
					for (int b = 0; b < cbus.getBusNodes().size(); b++) {
						Bus bus = cbus.getBusNodes().get(b);
						if( ! isSlack && bus.isSlackNode())
							isSlack = true;
						if( ! isSelected && isSelection(bus))
							isSelected = true;
						if( ! isHovered && isHovered(bus))
							isHovered = true;
					}
					if(hasFailures)
						g.setColor(Preferences.getFlagFailureColor());
					else if(hasWarnings)
						g.setColor(Preferences.getFlagWarningColor());
					else {
						if(isSlack)
							g.setColor(Color.BLUE);
						else
							g.setColor(Color.BLACK);
					}
					double[] coords = getBusXYDouble(cbus.getFirstBus().getBusNumber());
					double x = coords[0];
					double y = coords[1];
					g2d.fill(new Ellipse2D.Double(
							(int) x - OVAL_HALF_HEIGHT, 
							(int) y - OVAL_HALF_HEIGHT, 
							(int) 2.0 * OVAL_HALF_HEIGHT, 
							(int) 2.0 * OVAL_HALF_HEIGHT));
					if(drawBusNames) {
						String locationName = cbus.getLabel();
						if(locationName != null)
							g2d.drawString(locationName, (int) x+15, (int) y+15);
					}
					if(isSelected || isHovered) {
						g2d.draw(new Ellipse2D.Double(
								(int) x - OVAL_HALF_HEIGHT - 2.0, 
								(int) y - OVAL_HALF_HEIGHT - 2.0, 
								(int) 2.0 * OVAL_HALF_HEIGHT + 3.0, 
								(int) 2.0 * OVAL_HALF_HEIGHT + 3.0));
						g2d.draw(new Ellipse2D.Double(
								(int) x - OVAL_HALF_HEIGHT - 3.0, 
								(int) y - OVAL_HALF_HEIGHT - 3.0, 
								(int) 2.0 * OVAL_HALF_HEIGHT + 5.0, 
								(int) 2.0 * OVAL_HALF_HEIGHT + 5.0));
					}
					// draw generators
					if(drawGenerators && cbus.getGenerators().size() > 0) {
						for (int gen = 0; gen < cbus.getGenerators().size(); gen++) {
							Generator generator = cbus.getGenerators().get(gen);
							if( ! isSelected && isSelection(generator))
								isSelected = true;
							if( ! isHovered && isHovered(generator))
								isHovered = true;
							
						}
						if(hasFailures)
							g.setColor(Preferences.getFlagFailureColor());
						else if(hasWarnings)
							g.setColor(Preferences.getFlagWarningColor());
						else {
							if(isSlack)
								g.setColor(Color.BLUE);
							else
								g.setColor(Color.BLACK);
						}
						int x_gen = (int) x;//getBusX(busIndex, horizontalScale);
						int y_gen = (int) y;//getBusY(busIndex, verticalScale);
						if(x == -1 || y == -1)
							continue;
						g2d.drawRect(
								x_gen - RECTANGLE_HALF_HEIGHT - 1, 
								y_gen - RECTANGLE_HALF_HEIGHT - 1, 
								2 * RECTANGLE_HALF_HEIGHT + 2, 
								2 * RECTANGLE_HALF_HEIGHT + 2);
						if(isSelected || isHovered) {
							g2d.drawRect(
									x_gen - RECTANGLE_HALF_HEIGHT - 2, 
									y_gen - RECTANGLE_HALF_HEIGHT - 2, 
									2 * RECTANGLE_HALF_HEIGHT + 4, 
									2 * RECTANGLE_HALF_HEIGHT + 4);
							g2d.drawRect(
									x_gen - RECTANGLE_HALF_HEIGHT - 3, 
									y_gen - RECTANGLE_HALF_HEIGHT - 3, 
									2 * RECTANGLE_HALF_HEIGHT + 6, 
									2 * RECTANGLE_HALF_HEIGHT + 6);
						}
					}
				}
			}
		} else {
			g.setColor(Color.BLACK);
			g.drawString("No coordinate data provided.", 0, getHeight() / 2);
		}
		if(hasFocus()) {
			g.setColor(Color.BLUE);
			g2d.setStroke(new BasicStroke(3));
			g.drawRect(0, 0, getWidth()-1, getHeight()-1);
		}
	}
	
	private void drawOutlines(Graphics2D g2d) {
		for (Outline outline : getOutlines()) {
			Color outlineColor = outline.getBorderColor();
			Color backgroundColor = outline.getBackgroundColor();
			int[][] coords = outline.getScreenPoints();
			if(coords.length == 0)
				continue;
			double lastX = getX(coords[0]);
			double lastY = getY(coords[0]);
			GeneralPath polygon = new GeneralPath();
			polygon.moveTo(lastX, lastY);
			for (int i = 1; i < coords.length; i++) {
				// check if polygon must be closed
				if(coords[i][0] == -1 && coords[i][1] == -1) {
					if(polygon != null) {
						polygon.closePath();
						drawOutline(g2d, polygon, outlineColor, backgroundColor);
						// new polygon will be created next round
						polygon = null;
					}
					continue;
				}
				double x = getX(coords[i]);
				double y = getY(coords[i]);
				if(polygon == null) {
					polygon = new GeneralPath();
					polygon.moveTo(x, y);
					
					continue;
				}
//				g2d.draw(new Line2D.Double(lastX, lastY, x, y));
//				lastX = x;
//				lastY = y;
				polygon.lineTo(x, y);
			}
			if(polygon != null) {
				polygon.closePath();
				drawOutline(g2d, polygon, outlineColor, backgroundColor);
			}
		}
	}
	
	private void drawOutline(Graphics2D g2d, GeneralPath polygon, Color outlineColor, Color backgroundColor) {
		if(backgroundColor != null) {
			g2d.setColor(backgroundColor);
			g2d.fill(polygon);
		}
		if(outlineColor != null)
			g2d.setColor(outlineColor);
		else
			g2d.setColor(Color.LIGHT_GRAY);
		g2d.draw(polygon);
	}
	
	private void updateArrowSize(int size) {
		arrow_pos = new GeneralPath();
		arrow_pos.moveTo(-size / 2, -size / 2);
		arrow_pos.lineTo(-size / 2, size / 2);
		arrow_pos.lineTo(size / 2, 0);
		arrow_pos.lineTo(-size / 2, -size / 2);

		arrow_neg = new GeneralPath();
		arrow_neg.moveTo(-size / 2, 0);
		arrow_neg.lineTo(size / 2, size / 2);
		arrow_neg.lineTo(size / 2, -size / 2);
		arrow_neg.lineTo(-size / 2, 0);
	}

	private Shape getArrowShape(Graphics2D g2d, double x1, double y1, double x2, double y2, boolean directionOneToTwo) {
		double x = x1 + (x2 - x1) / 2.0;
		double y = y1 + (y2 - y1) / 2.0;
		double angle = Math.atan((y2 - y1) / (x2 - x1));
		if(x1 > x2) {
			angle += Math.PI;
		}
		AffineTransform transformation = AffineTransform
				.getTranslateInstance(x, y);
		transformation
				.concatenate(AffineTransform.getRotateInstance(angle));
		return directionOneToTwo ? 
				arrow_pos.createTransformedShape(transformation) : 
				arrow_neg.createTransformedShape(transformation);
	}
	
	private void updateNetworkShape(int size) {
		networkShape = new GeneralPath();
		int stepSize = size / 3;
		
		double x = -size / 2.0;
		double y = -size / 2.0;
		int counter = 0;
		for (int step = 1; step * stepSize < 2 * size; step++) {
			boolean upperHalf = counter >= size;
			if(upperHalf) {
				networkShape.append(new Line2D.Double(x + counter - size, y, x + size, y + size - counter + size), false);
				networkShape.append(new Line2D.Double(x + size, y + counter - size, x - size + counter, y + size), false);
			} else {
				networkShape.append(new Line2D.Double(x, y + size - counter, x + counter, y + size), false);
				networkShape.append(new Line2D.Double(x, y + counter, x + counter, y), false);
			}
			counter += stepSize;
		}
		networkShape.append(new Line2D.Double(x, y, x + size, y), false);// oben
		networkShape.append(new Line2D.Double(x, y, x, y + size), false);// links
		networkShape.append(new Line2D.Double(x, y + size, x + size, y + size), false);// unten
		networkShape.append(new Line2D.Double(x + size, y, x + size, y + size), false);// rechts
	}
	
	private Shape getNetworkShape(double x, double y) {
		AffineTransform transformation = AffineTransform.getTranslateInstance(x, y);
		return networkShape.createTransformedShape(transformation);
	}
	
	protected int[] getBusXY(int i) {
		double[] coords = getBusXYDouble(i);
		return new int[] { (int) coords[0], (int) coords[1] };
	}
	
	protected double[] getBusXYDouble(int i) {
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
	
	protected double getOutlineX(int i) {
		return ((internalGermany[i][0] - internalMinX) * horizontalScale) + HORIZONTAL_GAP;
	}
	
	protected double getOutlineY(int i, double verticalScale) {
		return getHeight() - ((internalGermany[i][1] - internalMinY) * verticalScale) - VERTICAL_GAP;
	}
	
	protected double getX(int[] coords) {
		return ((coords[0] - internalMinX) * horizontalScale) + HORIZONTAL_GAP;
	}
	
	protected double getY(int[] coords) {
		return getHeight() - ((coords[1] - internalMinY) * verticalScale) - VERTICAL_GAP;
	}
	
	protected boolean isHovered(Object object) {
		return hover != null && hover == object;
	}
	
	protected boolean isSelection(Object object) {
		return selection != null && selection == object;
	}
	
	List<Integer> getVoltageLevels() {
		if(voltageLevels == null)
			updateVoltageLevels();
		return voltageLevels;
	}
	
	private void updateVoltageLevels() {
		voltageLevels = new ArrayList<Integer>();
		for (int i = 0; i < data.getCombinedBranchCount(); i++) {
			CombinedBranch cbranch = data.getCombinedBranch(i);
			int voltage = getBaseVoltage(cbranch.getFirstBranch());
			if(voltageLevels.contains(voltage) == false)
				voltageLevels.add(voltage);
		}
		// sort voltage levels in descending order
		Collections.sort(voltageLevels, new Comparator<Integer>() {
			@Override
			public int compare(Integer int1, Integer int2) {
				return int2.compareTo(int1);
			}
		});
	}
	
	private String getTooltipText(AbstractNetworkElement dataElement) {
		if(dataElement instanceof Branch) {
			CombinedBranch cbranch = data.getCombinedBranch(dataElement);
			if(cbranch != null) {
				String tooltipText = "<html>";
				tooltipText += cbranch.getLabel();
				tooltipText += "<ul>";
				for (Branch branch : cbranch.getBranches()) {
					tooltipText += "<li>" + branch.getDisplayName(AbstractNetworkElement.DISPLAY_NAME);
					int baseVoltage = getBaseVoltage(branch);
					if(baseVoltage != 0)
						tooltipText += " (" + baseVoltage + " kV)";
					tooltipText += "</li>";
				}
				return tooltipText;
			}
		} else {
			CombinedBus cbus = data.getCombinedBus(dataElement);
			if(cbus != null) {
				String tooltipText = "<html>";
				tooltipText += cbus.getLabel();
				tooltipText += "<ul>";
				for (Bus bus : cbus.getBusNodes()) {
					tooltipText += "<li>" + bus.getDisplayName(AbstractNetworkElement.DISPLAY_DEFAULT) + "</li>";
				}
				if(cbus.getGenerators().size() > 0) {
					tooltipText += "</ul>Generators:<ul>";
					for (Generator gen : cbus.getGenerators()) {
						tooltipText += "<li>" + gen.getDisplayName(AbstractNetworkElement.DISPLAY_DEFAULT) + "</li>";
					}
				}
				return tooltipText;
			} 
		}
		return dataElement.getDisplayName(AbstractNetworkElement.DISPLAY_DEFAULT);
	}
	
    public Point getToolTipLocation(MouseEvent event) {
    	AbstractNetworkElement element = getObjectFromScreen(event.getX(), event.getY());
    	if(element != null && element instanceof Bus) {
    		int[] coords = getBusXY(((Bus) element).getBusNumber());
    		return new Point(coords[0] + 15, coords[1]);
    	}
		return super.getToolTipLocation(event);
    }
	
	protected Stroke getBranchStroke(Branch branch, boolean bold) {
		return getBranchStroke(getBaseVoltage(branch), bold);
	}
	
	Stroke getBranchStroke(int baseVoltage, boolean bold) {
		if(drawPowerDirection)
			return bold ? strokesBold[0] : strokesNormal[0];
		for (int i = 0; i < getVoltageLevels().size(); i++) {
			Integer voltageLevel = getVoltageLevels().get(i);
			if(voltageLevel.intValue() == baseVoltage)
				return bold ? strokesBold[i] : strokesNormal[i];
		}
		return bold ? otherStrokeBold : otherStrokeNormal;
	}
	
	private int getBaseVoltage(Branch branch) {
		// from and to bus should have the same voltage level!
		Bus bus = data.getBus(branch.getFromBusNumber());
		if(bus == null)
			return 0;
		return bus.getBaseVoltage();
	}
	
	@Override
	public JComponent getComponent() {
		return getViewerController();
	}

	@Override
	public Network getNetwork() {
		return data;
	}

	@Override
	public DataViewerConfiguration getViewerConfiguration() {
		return viewerConfiguration;
	}

	@Override
	public void refresh() {
	}

	@Override
	public void setData(Network network) {
	}

	public NetworkViewerController getViewerController() {
		return controller;
	}

	public void selectionChanged(Object data) {
		selection = data;
		repaint();
	}

	@Override
	public void networkChanged(NetworkChangeEvent event) {
//		System.out.println("viewer: networkChanged");
		initializeInternalCoordinates();
		updateVoltageLevels();
		repaint();
	}

	@Override
	public void networkElementAdded(NetworkChangeEvent event) {
//		System.out.println("viewer: networkElementAdded");
		initializeInternalCoordinates();
		updateVoltageLevels();
		repaint();
	}

	@Override
	public void networkElementChanged(NetworkChangeEvent event) {
//		System.out.println("viewer: networkElementChanged");
		if(IInternalParameters.LONGITUDE.equals(event.getParameterID())
				|| IInternalParameters.LATITUDE.equals(event.getParameterID())
				|| IInternalParameters.FROM_BUS.equals(event.getParameterID())
				|| IInternalParameters.TO_BUS.equals(event.getParameterID())
				|| IInternalParameters.GEN_BUS.equals(event.getParameterID()))
			initializeInternalCoordinates();
		repaint();
	}

	@Override
	public void networkElementRemoved(NetworkChangeEvent event) {
		initializeInternalCoordinates();
		updateVoltageLevels();
		repaint();
	}

	@Override
	public void elementChanged(DatabaseChangeEvent event) {
		if(event.getParameterID().startsWith("OUTLINE.")) {
			updateOutlines();
			repaint();
		}
	}

	@Override
	public void parameterChanged(DatabaseChangeEvent event) {
		if(event.getParameterID().startsWith("OUTLINE.")) {
			updateOutlines();
			repaint();
		}
	}
	
	private void updateOutlines() {
//		System.out.println("updateOutlines");
		outlines = new ArrayList<Outline>();
		NetworkContainer container = getNetworkContainer(parentContainer);
		lastOutlineList = container.getOutlines();
		for (Outline outline : lastOutlineList) {
			String paramID = "OUTLINE." + outline.getOutlineID();
			NetworkParameter param = getViewerConfiguration().getParameterValue(paramID);
			if(param == null)
				param = ModelDBUtils.getParameterValue(outline.getOutlineData(), "ENABLED");
			boolean defaultEnabled = param == null ? false : Boolean.parseBoolean(param.getValue());
			if(getViewerConfiguration().getBooleanParameter(paramID, defaultEnabled))
				outlines.add(outline);
		}
//		repaint();
	}
	
	private NetworkContainer getNetworkContainer(Component c) {
		if(c instanceof NetworkContainer)
			return (NetworkContainer) c;
		if(c.getParent() != null)
			return getNetworkContainer(c.getParent());
		return null;
	}
	
	private List<Outline> getOutlines() {
		if(outlines == null)
			updateOutlines();
		NetworkContainer container = getNetworkContainer(parentContainer);
		if(container != null && container.getOutlines() != lastOutlineList)
			updateOutlines();
		return outlines;
	}

	public void setView(double[] coordinates) {
		this.minLatitude = coordinates[0];
		this.maxLatitude = coordinates[1];
		this.minLongitude = coordinates[2];
		this.maxLongitude = coordinates[3];
	}
	
	public void setPerfectFit(boolean flag) {
		perfectFit = flag;
		initializeInternalCoordinates();
		repaint();
	}
	
	private void changeZoom() {
		perfectFit = false;
		getViewerConfiguration().setParameter("ZOOM", 1);
	}
	
	public void setRespectAspectRation(boolean flag) {
		respectAspectRatio = flag;
		initializeInternalCoordinates();
		repaint();
	}
	
	public void setDrawBusNodes(boolean flag) {
		drawBusNodes = flag;
		repaint();
	}
	
	public void setDrawBusNames(boolean flag) {
		drawBusNames = flag;
		repaint();
	}
	
	public void setDrawBranches(boolean flag) {
		drawBranches = flag;
		repaint();
	}
	
	public void setDrawPowerDirection(boolean flag) {
		drawPowerDirection = flag;
		repaint();
	}

	public void setDrawGeneratorsNodes(boolean flag) {
		drawGenerators = flag;
		repaint();
	}
	
	public void setDrawOutline(boolean flag) {
		drawOutline = flag;
		repaint();
	}
	
	public void setAllowZooming(boolean flag) {
		allowZooming = flag;
		repaint();
	}
	
	public void setAllowDragging(boolean flag) {
		allowDragging = flag;
		repaint();
	}
	
	public void setShowTooltips(boolean flag) {
		showTooltips = flag;
		repaint();
	}
}
