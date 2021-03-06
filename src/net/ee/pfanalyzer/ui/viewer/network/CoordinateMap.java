/*******************************************************************************
 * Copyright 2019 Markus Gronau
 * 
 * This file is part of PowerFlowAnalyzer.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
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
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.event.MouseInputAdapter;

import net.ee.pfanalyzer.PowerFlowAnalyzer;
import net.ee.pfanalyzer.math.coordinate.Mercator;
import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.Branch;
import net.ee.pfanalyzer.model.Bus;
import net.ee.pfanalyzer.model.CombinedNetworkElement;
import net.ee.pfanalyzer.model.MarkerElement;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.ui.NetworkElementSelectionManager;
import net.ee.pfanalyzer.ui.dialog.ElementSelectionDialog;
import net.ee.pfanalyzer.ui.util.MapBoundingBox;
import net.ee.pfanalyzer.ui.viewer.DataViewerConfiguration;
import net.ee.pfanalyzer.ui.viewer.INetworkDataViewer;
import net.ee.pfanalyzer.ui.viewer.IPaintListener;

public abstract class CoordinateMap extends JComponent implements INetworkDataViewer, INetworkMapParameters {

	public final static String BASE_NETWORK_VIEWER_ID = "viewer.network";
	
	private final static Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
	private final static Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
	private final static Cursor MOVE_CURSOR = new Cursor(Cursor.MOVE_CURSOR);
	
	protected int horizontal_margin = 20;
	protected int vertical_margin = 20;

	protected double minLatitude = ZOOM_GERMANY_COORDINATES[0];
	protected double maxLatitude = ZOOM_GERMANY_COORDINATES[1];
	protected double minLongitude = ZOOM_GERMANY_COORDINATES[2];
	protected double maxLongitude = ZOOM_GERMANY_COORDINATES[3];
	
	private DataViewerConfiguration viewerConfiguration;
	private Network data;
	
	protected Mercator converter;
	protected Map<Integer, int[]> internalBusCoords = new HashMap<Integer, int[]>();
	protected Map<Integer, int[]> internalMarkerCoords = new HashMap<Integer, int[]>();
	protected int internalMinX = 0;
	protected int internalMaxX = 0;
	protected int internalMinY = 0;
	protected int internalMaxY = 0;
	
	protected boolean respectAspectRatio = true;
	protected boolean perfectFit = true;
	protected boolean drawAreas = true;
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
	protected boolean editingMode = false;
	protected DraggingObject draggingObject;
	protected PaintManager paintManager;
	
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
	public void setData(Network network) {
		data = network;
		selection = null;
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
					updateInternalCoordinates(latitude, longitude, x, y);
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
			if(perfectFit)
				updateInternalCoordinates(latitude, longitude, x, y);
		}
		// update min/max coords of background painters
		if(perfectFit && paintManager != null) {
			for (IPaintListener painter : paintManager.getActivePaintListeners()) {
				if(painter.getBoundingBox() == null || painter.getBoundingBox().isIncomplete())
					continue;
				MapBoundingBox box = painter.getBoundingBox();
				// determine minimum coords
				double longitudeMin = box.getLongitudeMin();
				double latitudeMin = box.getLatitudeMin();
				if(Double.isNaN(longitudeMin)// no coords set
						|| Double.isNaN(latitudeMin))
					continue;
				// determine maximum coords
				double longitudeMax = box.getLongitudeMax();
				double latitudeMax = box.getLatitudeMax();
				if(Double.isNaN(longitudeMax)// no coords set
						|| Double.isNaN(latitudeMax))
					continue;
				// calculate min/max screen coords
				int xmin = converter.getX(longitudeMin);
				int ymin = converter.getY(latitudeMin);
				int xmax = converter.getX(longitudeMax);
				int ymax = converter.getY(latitudeMax);
				// update min/max coords
				updateInternalCoordinates(latitudeMin, longitudeMin, xmin, ymin);
				updateInternalCoordinates(latitudeMax, longitudeMax, xmax, ymax);
			}
		}
	}
	
	private void updateInternalCoordinates(double latitude, double longitude, int x, int y) {
		internalMinX = Math.min(internalMinX, x);
		internalMaxX = Math.max(internalMaxX, x);
		internalMinY = Math.min(internalMinY, y);
		internalMaxY = Math.max(internalMaxY, y);
		minLatitude = Math.min(minLatitude, latitude);
		maxLatitude = Math.max(maxLatitude, latitude);
		minLongitude = Math.min(minLongitude, longitude);
		maxLongitude = Math.max(maxLongitude, longitude);
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
				horizontalScale = ((double) getWidth() - 2 * horizontal_margin) / width;
				double height = internalMaxY - internalMinY;
				verticalScale = ((double) getHeight() - 2 * vertical_margin) / height;
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
	
	protected double getLatitude(double y) {
		double coord = (getHeight() - y - vertical_margin) / verticalScale + internalMinY;
		return converter.getLatitude((int) coord);
	}
	
	protected double getLongitude(double x) {
		double coord = (x - horizontal_margin) / horizontalScale + internalMinX;
		return converter.getLongitude((int) coord);
	}
	
	public int[] getBusXY(int i) {
		double[] coords = getBusXYDouble(i);
		return new int[] { (int) coords[0], (int) coords[1] };
	}
	
	public double[] getBusXYDouble(int i) {
		int[] coords = internalBusCoords.get(i);
		if(coords == null)
			return new double[] { -1, -1 };
		if(coords[0] == -1 || coords[1] == -1)
			return new double[] { -1, -1 };
		double x = ((coords[0] - internalMinX) * horizontalScale) + horizontal_margin;
		double y = getHeight() - ((coords[1] - internalMinY) * verticalScale) - vertical_margin;
		return new double[] { x, y };
	}
	
	public double[] getMarkerXYDouble(int i) {
		int[] coords = internalMarkerCoords.get(i);
		if(coords == null)
			return new double[] { -1, -1 };
		if(coords[0] == -1 || coords[1] == -1)
			return new double[] { -1, -1 };
		double x = ((coords[0] - internalMinX) * horizontalScale) + horizontal_margin;
		double y = getHeight() - ((coords[1] - internalMinY) * verticalScale) - vertical_margin;
		return new double[] { x, y };
	}
	
	public double getX(int[] coords) {
		return ((coords[0] - internalMinX) * horizontalScale) + horizontal_margin;
	}
	
	public double getY(int[] coords) {
		return getHeight() - ((coords[1] - internalMinY) * verticalScale) - vertical_margin;
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
	
	protected void setPerfectFit() {
		perfectFit = true;
		getViewerConfiguration().setParameter("ZOOM", 0);
		initializeInternalCoordinates();
		repaint();
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
	
	public boolean isHovered(Object object) {
		return hover != null && hover == object;
	}
	
	public boolean isSelection(Object object) {
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
		return false; // TODO is this really equivalent?
//		return selection != null && selection == object;
	}
	
	public boolean hasSelection() {
		return selection != null;
	}
	
	public boolean isDrawElementNames() {
		return drawBusNames;
	}
	
	public boolean isDrawBranches() {
		return drawBranches;
	}
	
	public boolean isDrawAreas() {
		return drawAreas;
	}
	
	public boolean isDrawBusNodes() {
		return drawBusNodes;
	}

	public boolean isDrawGenerators() {
		return drawGenerators;
	}

	public boolean isDrawMarkers() {
		return drawMarkers;
	}

	public boolean isDrawFlags() {
		return drawFlags;
	}

	public boolean isDrawPowerDirection() {
		return drawPowerDirection;
	}
	
	public boolean isFadeOutUnselected() {
		return fadeOutUnselected;
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
		} else if(property.equals(PROPERTY_DRAW_AREAS)) {
			drawAreas = Boolean.valueOf(value);
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
				if(obj != null) {
					if(editingMode && ! (obj instanceof Branch))
						setCursor(MOVE_CURSOR);
					else
						setCursor(HAND_CURSOR);
				} else
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
			try {
				if(editingMode) {
					double longitude = getLongitude(e.getX());
					double latitude = getLatitude(e.getY());
					if(draggingObject == null) {
						AbstractNetworkElement element = getObjectFromScreen(e.getX(), e.getY());
						if(element != null && ! (element instanceof Branch))
						draggingObject = new DraggingObject(element, longitude, latitude);
					} else {
						draggingObject.longitude = longitude;
						draggingObject.latitude = latitude;
					}
					repaint();
				} else {
					if(allowDragging == false)
						return;
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
		
		@Override
		public void mouseReleased(MouseEvent e) {
			if(draggingObject != null) {
				if(draggingObject.draggingElement != null) {
					// check if the target coordinates belong to another element
					AbstractNetworkElement targetObject = getObjectFromScreen(e.getX(), e.getY());
					if(targetObject instanceof Branch)
						targetObject = null;
					if(targetObject == draggingObject.draggingElement)
						targetObject = null;
					if(targetObject != null) {
						int action = JOptionPane.showConfirmDialog(PowerFlowAnalyzer.getInstance(), 
								"Do you want to combine the elements?", "Combine?", JOptionPane.YES_NO_CANCEL_OPTION);
						if(action == JOptionPane.YES_OPTION) {
							if(targetObject instanceof Bus) {
								draggingObject.longitude = ((Bus) targetObject).getLongitude();
								draggingObject.latitude  = ((Bus) targetObject).getLatitude();
							} else if(targetObject instanceof MarkerElement) {
								draggingObject.longitude = ((MarkerElement) targetObject).getLongitude();
								draggingObject.latitude  = ((MarkerElement) targetObject).getLatitude();
							}
						} else if(action == JOptionPane.CANCEL_OPTION) {
							draggingObject = null;
							repaint();
							return;
						}
					}
					// find a combined bus for the dragged element
					Object obj = getNetwork().getCombinedBus(draggingObject.draggingElement);
					// take the element itself instead
					if(obj == null)
						obj = draggingObject.draggingElement;
					// gather all elements belonging to this element
					Vector<AbstractNetworkElement> elements2move = new Vector<AbstractNetworkElement>();
					if(obj instanceof AbstractNetworkElement) {
						elements2move.add((AbstractNetworkElement) obj);
					} else if(obj instanceof CombinedNetworkElement<?>) {
						for (AbstractNetworkElement element : ((CombinedNetworkElement<?>) obj).getNetworkElements()) {
							elements2move.add(element);
						}
					}
					// show a dialog for selecting the elements to be moved
					if(elements2move.size() > 1) {
						ElementSelectionDialog dialog = new ElementSelectionDialog(
								PowerFlowAnalyzer.getInstance(), elements2move, 
								"Move Network Elements", "Select the network elements you want to move");
						if(dialog.isCancelPressed() || dialog.getSelectedElements().isEmpty()) {
							draggingObject = null;
							repaint();
							return;
						}
						elements2move = new Vector<AbstractNetworkElement>(dialog.getSelectedElements());
					}
					// move all elements
					for (AbstractNetworkElement element : elements2move) {
						if(element instanceof Bus) {
							((Bus) element).setLongitude(draggingObject.longitude);
							((Bus) element).setLatitude(draggingObject.latitude);
						} else if(element instanceof MarkerElement) {
							((MarkerElement) element).setLongitude(draggingObject.longitude);
							((MarkerElement) element).setLatitude(draggingObject.latitude);
						}
					}
					// propagate the changes
					getNetwork().fireNetworkChanged(true);
				}
				draggingObject = null;
				repaint();
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
	
	class DraggingObject {
		
		AbstractNetworkElement draggingElement;
		double longitude, latitude;
		
		public DraggingObject(AbstractNetworkElement draggingElement, double longitude, double latitude) {
			this.draggingElement = draggingElement;
			this.longitude = longitude;
			this.latitude = latitude;
		}
		
	}
}
