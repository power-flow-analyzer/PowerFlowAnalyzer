package net.ee.pfanalyzer.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.Iterator;
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
import net.ee.pfanalyzer.model.Generator;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.matpower.IBranchDataConstants;
import net.ee.pfanalyzer.model.matpower.IBusDataConstants;
import net.ee.pfanalyzer.model.matpower.ICoordinateDataConstants;

public class NetworkViewer extends JComponent implements NetworkElementSelectionListener,
		IBusDataConstants, IBranchDataConstants, ICoordinateDataConstants{

	private final static double OVAL_HALF_HEIGHT = 8;
	private final static int RECTANGLE_HALF_HEIGHT = 10;
	private final static int HORIZONTAL_GAP = 20;
	private final static int VERTICAL_GAP = 20;
	
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
	
	private Stroke[] strokesNormal, strokesBold;
	private Stroke otherStrokeNormal, otherStrokeBold;
	
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
	private Map<Integer, int[]> internalBusCoords = new HashMap<Integer, int[]>();
//	private int[] internalBusNumbers;
	int internalMinX = 0;
	int internalMaxX = 0;
	int internalMinY = 0;
	int internalMaxY = 0;
	
	private boolean respectAspectRatio = true;
	private boolean perfectFit = true;
	private boolean drawBusNodes = true;
	private boolean drawBranches = true;
	private boolean drawGenerators = true;
	private boolean drawOutline = true;
	
	private boolean allowZooming = true;
	private boolean allowDragging = true;
	private boolean showTooltips = true;
	
	private double minLatitude = NetworkViewerController.ZOOM_GERMANY_COORDINATES[0];
	private double maxLatitude = NetworkViewerController.ZOOM_GERMANY_COORDINATES[1];
	private double minLongitude = NetworkViewerController.ZOOM_GERMANY_COORDINATES[2];
	private double maxLongitude = NetworkViewerController.ZOOM_GERMANY_COORDINATES[3];
	
	private double horizontalScale, verticalScale;
	
	private Object selection, hover;
//	private List<NetworkElementSelectionListener> networkElementListeners = new ArrayList<NetworkElementSelectionListener>();
	private NetworkViewerController controller;

	public NetworkViewer(Network data) {
		this.data = data;
		createStrokes(1.0f, 2.0f);
		converter = new Mercator();
		initializeInternalCoordinates();
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
			
			int lastX = -1, lastY = -1;
			@Override
			public void mouseDragged(MouseEvent e) {
				if(allowDragging == false)
					return;
				try {
					changeZoom();
					if(lastX > -1 && lastY > -1) {
						double diffLat = getLatitudeDifference(lastX, e.getX());
						minLatitude += diffLat;
						maxLatitude += diffLat;
						double diffLong = getLongitudeDifference(lastY, e.getY());
						minLongitude -= diffLong;
						maxLongitude -= diffLong;
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
						NetworkElementSelectionManager.getInstance().selectionChanged(obj);
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
					double scale = wheelRot * 0.3;
					minLatitude += scale;
					maxLatitude -= scale;
					minLongitude += scale;
					maxLongitude -= scale;
					// translate
					if(wheelRot > 0) {
						double factor = 0.3;
						int x = e.getX();
						int y = e.getY();
						int centerX = getWidth()/2;
						int centerY = getHeight()/2;
						double diffLat = getLatitudeDifference(x, centerX) * factor;
						double diffLong = getLongitudeDifference(y, centerY) * factor;
						minLatitude += diffLat;
						maxLatitude += diffLat;
						minLongitude -= diffLong;
						maxLongitude -= diffLong;
					}
					initializeInternalCoordinates();
					repaint();
				} catch(Exception except) {
					except.printStackTrace();
				}
			}
		};
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
		addMouseWheelListener(wheelListener);
		setFocusable(true);
	}
	
	public void setController(NetworkViewerController controller) {
		this.controller = controller;
	}
	
	private double getLatitudeDifference(int x1, int x2) {
		int diffX = x1 - x2;
		double latitudeDiff = maxLatitude - minLatitude;
		return diffX * latitudeDiff / getWidth();
	}
	
	private double getLongitudeDifference(int y1, int y2) {
		int diffY = y1 - y2;
		double longitudeDiff = maxLongitude - minLongitude;
		return diffY * longitudeDiff / getHeight();
	}
	
	private void initializeInternalCoordinates() {
//		internalBusCoords = new int[data.getBusses().size()][2];
//		internalBusNumbers = new int[data.getBusses().size()];
		if(perfectFit == false) {
			internalMinX = converter.getX(minLatitude);
			internalMaxX = converter.getX(maxLatitude);
			internalMinY = converter.getY(minLongitude);
			internalMaxY = converter.getY(maxLongitude);
		}
		for (int i = 0; i < data.getBusses().size(); i++) {
			Bus bus = data.getBusses().get(i);
//			internalBusNumbers[i] = bus.getBusNumber();
			double longitude = bus.getLongitude();
			double lattitude = bus.getLattitude();
			if(Double.isNaN(longitude)// no coords set
					|| Double.isNaN(lattitude)) {
				internalBusCoords.put(bus.getBusNumber(), new int[]{-1, -1});
//				internalBusCoords[i][1] = -1;
				continue;
			}
			int x = converter.getX(lattitude);
			int y = converter.getY(longitude);
			internalBusCoords.put(bus.getBusNumber(), new int[]{x, y});
//			internalBusCoords[i][0] = x;
//			internalBusCoords[i][1] = y;
			if(perfectFit) {
				if(i == 0) {// initialize min/max values
					internalMinX = x;
					internalMaxX = x;
					internalMinY = y;
					internalMaxY = y;
					minLatitude = lattitude;
					maxLatitude = lattitude;
					minLongitude = longitude;
					maxLongitude = longitude;
				} else {
					internalMinX = Math.min(internalMinX, x);
					internalMaxX = Math.max(internalMaxX, x);
					internalMinY = Math.min(internalMinY, y);
					internalMaxY = Math.max(internalMaxY, y);
					minLatitude = Math.min(minLatitude, lattitude);
					maxLatitude = Math.max(maxLatitude, lattitude);
					minLongitude = Math.min(minLongitude, longitude);
					maxLongitude = Math.max(maxLongitude, longitude);
				}
			}
		}
//		double[][] coords = data.getCoordinateData();
//		if(coords != null && coords.length > 0) {// no coords at all set
//			internalBusCoords = new int[coords.length][2];
//			if(perfectFit == false) {
//				internalMinX = converter.getX(minLatitude);
//				internalMaxX = converter.getX(maxLatitude);
//				internalMinY = converter.getY(minLongitude);
//				internalMaxY = converter.getY(maxLongitude);
//			}
//			for(int i = 0; i < internalBusCoords.length; i++) {
//				if(Double.isNaN(coords[i][LATITUDE])// no coords set
//						|| Double.isNaN(coords[i][LONGITUDE])) {
//					internalBusCoords[i][0] = -1;
//					internalBusCoords[i][1] = -1;
//					continue;
//				}
//				int x = converter.getX(coords[i][LATITUDE]);
//				int y = converter.getY(coords[i][LONGITUDE]);
//				internalBusCoords[i][0] = x;
//				internalBusCoords[i][1] = y;
//				if(perfectFit) {
//					if(i == 0) {// initialize min/max values
//						internalMinX = x;
//						internalMaxX = x;
//						internalMinY = y;
//						internalMaxY = y;
//						minLatitude = coords[i][LATITUDE];
//						maxLatitude = coords[i][LATITUDE];
//						minLongitude = coords[i][LONGITUDE];
//						maxLongitude = coords[i][LONGITUDE];
//					} else {
//						internalMinX = Math.min(internalMinX, x);
//						internalMaxX = Math.max(internalMaxX, x);
//						internalMinY = Math.min(internalMinY, y);
//						internalMaxY = Math.max(internalMaxY, y);
//						minLatitude = Math.min(minLatitude, coords[i][LATITUDE]);
//						maxLatitude = Math.max(maxLatitude, coords[i][LATITUDE]);
//						minLongitude = Math.min(minLongitude, coords[i][LONGITUDE]);
//						maxLongitude = Math.max(maxLongitude, coords[i][LONGITUDE]);
//					}
//				}
//			}
			for (int i = 0; i < GERMANY.length; i++) {
				int x = converter.getX(GERMANY[i][1]);
				int y = converter.getY(GERMANY[i][0]);
				internalGermany[i] = new int[] { x, y };
			}
//		}
	}
	
	private AbstractNetworkElement getObjectFromScreen(int x, int y) {
		// check busses
		if(drawBusNodes) {
			Iterator<Integer> busNumbers = internalBusCoords.keySet().iterator();
			while(busNumbers.hasNext()) {
				int busNumber = busNumbers.next();
				int[] coords = getBusXY(busNumber, horizontalScale, verticalScale);
//				int busX = getBusX(busNumber, horizontalScale);
//				int busY = getBusY(busNumber, verticalScale);
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
		// check branches
		if(drawBranches) {
			for (int i = 0; i < data.getBranches().size(); i++) {
				Branch branch = data.getBranches().get(i);
				int fromBus = branch.getFromBusNumber();
				int toBus = branch.getToBusNumber();
				double[] coords1 = getBusXYDouble(fromBus, horizontalScale, verticalScale);
				double x1 = coords1[0];//getBusX(fromBus, horizontalScale);
				double y1 = coords1[1];//getBusY(fromBus, verticalScale);
				if(x1 == -1 || y1 == -1)
					continue;
				double[] coords2 = getBusXYDouble(toBus, horizontalScale, verticalScale);
				double x2 = coords2[0];//getBusX(toBus, horizontalScale);
				double y2 = coords2[1];//getBusY(toBus, verticalScale);
				if(x2 == -1 || y2 == -1)
					continue;
				double minX = Math.min(x1, x2);
				double maxX = Math.max(x1, x2);
				if(x < minX || x > maxX)
					continue;
				double minY = Math.min(y1, y2);
				double maxY = Math.max(y1, y2);
				if(y < minY || y > maxY)
					continue;
				double m = (y2 - y1) / (x2 - x1);
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
//		double[][] coords = data.getCoordinateData();
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
			// draw outline
			if(drawOutline) {
				g.setColor(Color.GRAY);
				double lastX = getOutlineX(0, horizontalScale);
				double lastY = getOutlineY(0, horizontalScale);
				for (int i = 1; i < GERMANY.length; i++) {
					double x = getOutlineX(i, horizontalScale);
					double y = getOutlineY(i, verticalScale);
					g2d.draw(new Line2D.Double(lastX, lastY, x, y));
					lastX = x;
					lastY = y;
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
					double[] coords1 = getBusXYDouble(fromBus, horizontalScale, verticalScale);
					double x1 = coords1[0];//getBusXDouble(fromBus, horizontalScale);
					double y1 = coords1[1];//getBusYDouble(fromBus, verticalScale);
					if(x1 == -1 || y1 == -1)
						continue;
					double[] coords2 = getBusXYDouble(toBus, horizontalScale, verticalScale);
					double x2 = coords2[0];//getBusXDouble(toBus, horizontalScale);
					double y2 = coords2[1];//getBusYDouble(toBus, verticalScale);
					if(x2 == -1 || y2 == -1)
						continue;
					
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
						if(branch.isCorrect())
							g.setColor(Color.BLUE);
						else
							g.setColor(Color.RED);
						g2d.setStroke(getBranchStroke(branch, isSelected || isHovered));
						g2d.draw(new Line2D.Double(x1l, y1l, x2l, y2l));
					}
				}
			}
//			for (int i = 0; i < data.getBranchesCount(); i++) {
//				Branch branch = data.getBranch(i);
//				if(branch.isCorrect())
//					g.setColor(Color.BLUE);
//				else
//					g.setColor(Color.RED);
//				int fromBus = branch.getFromBusIndex();
//				int toBus = branch.getToBusIndex();
//				// check if coordinates for "from" and "to" are available
//				if(fromBus >= internalBusCoords.length || toBus >= internalBusCoords.length)
//					continue;
//				double x1 = getBusXDouble(fromBus, horizontalScale);
//				double y1 = getBusYDouble(fromBus, verticalScale);
//				if(x1 == -1 || y1 == -1)
//					continue;
//				double x2 = getBusXDouble(toBus, horizontalScale);
//				double y2 = getBusYDouble(toBus, verticalScale);
//				if(x2 == -1 || y2 == -1)
//					continue;
//				float dash1[] = { 10.0f, 5.0f, 1.0f };
//				if(isSelection(branch) || isHovered(branch)) {
//					BasicStroke dashed = new BasicStroke(3.0f,
//	                        BasicStroke.CAP_BUTT,
//	                        BasicStroke.JOIN_MITER,
//	                        10.0f, dash1, 0.0f);
////					g2d.setStroke(dashed);
////					int xDiff = x1 > x2 ? 1 : -1;
////					int yDiff = y1 < y2 ? 1 : -1;
////					g2d.drawPolygon(new int[]{
////							x1 + xDiff, x1 - xDiff, x2 - xDiff, x2 + xDiff
////					}, new int[]{
////							y1 + yDiff, y1 - yDiff, y2 - yDiff, y2 + yDiff
////					}, 4);
////					g2d.drawLine(x1, y1, x2, y2);
//				} else {
//					BasicStroke dashed = new BasicStroke(1.5f,
//	                        BasicStroke.CAP_BUTT,
//	                        BasicStroke.JOIN_MITER,
//	                        10.0f, dash1, 0.0f);
////					g2d.setStroke(dashed);
////					g2d.drawLine(x1, y1, x2, y2);
//				}
//				g2d.draw(new Line2D.Double(x1, y1, x2, y2));
//			}
			g2d.setStroke(defaultStroke);
			// draw bus nodes
			if(drawBusNodes) {
				for (int i = 0; i < data.getCombinedBusCount(); i++) {
					CombinedBus cbus = data.getCombinedBus(i);
					boolean isCorrect = cbus.isCorrect();
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
					if(isCorrect) {
						if(isSlack)
							g.setColor(Color.BLUE);
						else
							g.setColor(Color.BLACK);
					} else
						g.setColor(Color.RED);
					double[] coords = getBusXYDouble(cbus.getFirstBus().getBusNumber(), horizontalScale, verticalScale);
					double x = coords[0];//getBusXDouble(cbus.getFirstBus().getBusNumber(), horizontalScale);
					double y = coords[1];//getBusYDouble(cbus.getFirstBus().getBusNumber(), verticalScale);
					g2d.fill(new Ellipse2D.Double(
							(int) x - OVAL_HALF_HEIGHT, 
							(int) y - OVAL_HALF_HEIGHT, 
							(int) 2.0 * OVAL_HALF_HEIGHT, 
							(int) 2.0 * OVAL_HALF_HEIGHT));
					String locationName = cbus.getLabel();
					if(locationName != null)
						g2d.drawString(locationName, (int) x+15, (int) y+15);
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
						if(isCorrect)
							g.setColor(Color.BLACK);
						else
							g.setColor(Color.RED);
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
	
//	private int getBusX(int i, double horizontalScale) {
//		return (int) getBusXDouble(i, horizontalScale);
//	}
	
	private int[] getBusXY(int i, double horizontalScale, double verticalScale) {
		double[] coords = getBusXYDouble(i, horizontalScale, verticalScale);
		return new int[] { (int) coords[0], (int) coords[1] };
	}
	
	private double[] getBusXYDouble(int i, double horizontalScale, double verticalScale) {
		int[] coords = internalBusCoords.get(i);
		if(coords == null)
			return new double[] { -1, -1 };
		double x = ((coords[0] - internalMinX) * horizontalScale) + HORIZONTAL_GAP;
		double y = getHeight() - ((coords[1] - internalMinY) * verticalScale) - VERTICAL_GAP;
		return new double[] { x, y };
	}
	
//	private double getBusXDouble(int i, double horizontalScale) {
//		int[] coords = internalBusCoords.get(i);
//		if(coords == null)
//			return -1;
//		return ((coords[0] - internalMinX) * horizontalScale) + HORIZONTAL_GAP;
//	}
	
//	private int getBusY(int i, double verticalScale) {
//		return (int) getBusYDouble(i, verticalScale);
//	}
	
//	private double getBusYDouble(int i, double verticalScale) {
//		int[] coords = internalBusCoords.get(i);
//		if(coords == null)
//			return -1;
//		return getHeight() - ((coords[1] - internalMinY) * verticalScale) - VERTICAL_GAP;
//	}
	
	private double getOutlineX(int i, double horizontalScale) {
		return ((internalGermany[i][0] - internalMinX) * horizontalScale) + HORIZONTAL_GAP;
	}
	
	private double getOutlineY(int i, double verticalScale) {
		return getHeight() - ((internalGermany[i][1] - internalMinY) * verticalScale) - VERTICAL_GAP;
	}
	
	private boolean isHovered(Object object) {
		return hover != null && hover == object;
	}
	
	private boolean isSelection(Object object) {
		return selection != null && selection == object;
	}
	
	private String getTooltipText(AbstractNetworkElement dataElement) {
		if(dataElement instanceof Branch) {
			CombinedBranch cbranch = data.getCombinedBranch(dataElement);
			if(cbranch != null) {
				String tooltipText = "<html>";
				tooltipText += cbranch.getLabel();
				tooltipText += "<ul>";
				for (Branch branch : cbranch.getBranches()) {
					tooltipText += "<li>" + branch.getDisplayName();
					int baseVoltage = getBaseVoltage(branch);
					if(baseVoltage != VOLTAGE_LEVEL_UNKNOWN)
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
					tooltipText += "<li>" + bus.getDisplayName() + "</li>";
				}
				if(cbus.getGenerators().size() > 0) {
					tooltipText += "</ul>Generators:<ul>";
					for (Generator gen : cbus.getGenerators()) {
						tooltipText += "<li>" + gen.getDisplayName() + "</li>";
					}
				}
				return tooltipText;
			} 
		}
		return dataElement.getDisplayName();
	}
	
    public Point getToolTipLocation(MouseEvent event) {
    	AbstractNetworkElement element = getObjectFromScreen(event.getX(), event.getY());
    	if(element != null && element instanceof Bus) {
    		int[] coords = getBusXY(((Bus) element).getBusNumber(), horizontalScale, verticalScale);
    		return new Point(coords[0] + 15, coords[1]);
    	}
		return super.getToolTipLocation(event);
    }
	
	private Stroke getBranchStroke(Branch branch, boolean bold) {
		return getBranchStroke(getBaseVoltage(branch), bold);
	}
	
	Stroke getBranchStroke(int baseVoltage, boolean bold) {
		switch(baseVoltage) {
		case 380:
		case 400:
		case 420:
			return bold ? strokesBold[VOLTAGE_LEVEL_380KV] : strokesNormal[VOLTAGE_LEVEL_380KV];
		case 220:
		case 245:
			return bold ? strokesBold[VOLTAGE_LEVEL_220KV] : strokesNormal[VOLTAGE_LEVEL_220KV];
		case 110:
		case 115:
			return bold ? strokesBold[VOLTAGE_LEVEL_110KV] : strokesNormal[VOLTAGE_LEVEL_110KV];
		}
		return bold ? otherStrokeBold : otherStrokeNormal;
	}
	
	private int getBaseVoltage(Branch branch) {
		// from and to bus should have the same voltage level!
		return data.getBus(branch.getFromBusNumber()).getBaseVoltage();
	}
	
	public void selectionChanged(Object data) {
		selection = data;
		repaint();
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
		controller.zoomChanged(NetworkViewerController.ZOOM_CUSTOM);
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
	
	public void setDrawBranches(boolean flag) {
		drawBranches = flag;
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
