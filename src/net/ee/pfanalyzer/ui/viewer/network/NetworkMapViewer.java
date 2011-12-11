package net.ee.pfanalyzer.ui.viewer.network;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import net.ee.pfanalyzer.PowerFlowAnalyzer;
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
import net.ee.pfanalyzer.preferences.Preferences;
import net.ee.pfanalyzer.ui.CaseViewer;
import net.ee.pfanalyzer.ui.shape.ElementShapeProvider;
import net.ee.pfanalyzer.ui.shape.IElementShape;
import net.ee.pfanalyzer.ui.viewer.DataViewerConfiguration;
import net.ee.pfanalyzer.ui.viewer.DataViewerContainer;
import net.ee.pfanalyzer.ui.viewer.DataViewerDialog;
import net.ee.pfanalyzer.ui.viewer.DataViewerImageExport;
import net.ee.pfanalyzer.ui.viewer.INetworkDataViewer;

public class NetworkMapViewer extends CoordinateMap implements INetworkDataViewer, IDatabaseChangeListener, 
		INetworkMapParameters {

	public final static String VIEWER_ID = BASE_NETWORK_VIEWER_ID + ".map";
	
	protected final static double OVAL_HALF_HEIGHT = 8;
	protected final static int RECTANGLE_HALF_HEIGHT = 10;
	
	final static int VOLTAGE_LEVEL_UNKNOWN = -1;
	final static int VOLTAGE_LEVEL_380KV = 0;
	final static int VOLTAGE_LEVEL_220KV = 1;
	final static int VOLTAGE_LEVEL_110KV = 2;
	
	protected Stroke[] strokesNormal, strokesBold;
	private Stroke otherStrokeNormal, otherStrokeBold;
	private GeneralPath arrow_pos, arrow_neg;
	private ElementShapeProvider shapeProvider;
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
	
	private int arrowSize = 10;
	private int networkMarkerSize = 20;
	private float fontSize = -1;
	
	protected List<CombinedBus> visibleBusses;
	protected List<CombinedBranch> visibleBranches;
	protected PaintManager paintManager;
	
	private NetworkViewerController controller;
	private Component parentContainer;

	public NetworkMapViewer(Network data, DataViewerConfiguration viewerConfiguration, Component parent) {
		super(data, viewerConfiguration);
		this.parentContainer = parent;
		setDoubleBuffered(false);// uses own double buffering
		createStrokes(1.0f, 2.5f);
		paintManager = new PaintManager(this);
		initializeInternalCoordinates();
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				updateVisibleElements();
				paintManager.updateBackgroundImage();
			}
		});
		controller = new NetworkViewerController(this);
		initializeSettings();
		setFocusable(true);
		shapeProvider = new ElementShapeProvider();
		updateArrowSize(arrowSize);
		getViewerConfiguration().addDatabaseChangeListener(this);
		paintManager.addPaintListener(new OutlinePainter(this));
	}
	
	@Override
	public void addViewerActions(DataViewerContainer container) {
		container.addAction("Repaint map", "arrow_refresh.png", "Repaint", false, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateBackground();
			}
		});
		container.addAction("Export as an image", "camera.png", "Export", false, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportImage();
			}
		});
	}
	
	private void exportImage() {
		ExportImageDialog dialog = new ExportImageDialog(PowerFlowAnalyzer.getInstance(), 
				getViewerConfiguration());
		dialog.showDialog(-1, -1);
		if(dialog.isCancelPressed())// cancel pressed
			return;
		DataViewerDialog dialogData = dialog.getDialogData();
		Integer imageWidth = dialogData.getIntParameter("IMAGE_WIDTH");
		Integer imageHeight = dialogData.getIntParameter("IMAGE_HEIGHT");
		Double quality = dialogData.getDoubleParameter("IMAGE_COMPRESSION_QUALITY");
		String imagePath = dialogData.getTextParameter("IMAGE_FILE");
		if(imageWidth == null || imageHeight == null || quality == null || imagePath == null)
			return;
		File imageFile = new File(imagePath);
		if(imageFile.isAbsolute() == false)
			imageFile = new File(PowerFlowAnalyzer.getInstance().getWorkingDirectory(), imagePath);
		if(imageFile.exists()) {
			int action = JOptionPane.showConfirmDialog(this, 
					"The file\n\t\t" + imageFile.getAbsolutePath() + "\nalready exists. " +
							"Do you want to overwrite it?", "Overwrite?", JOptionPane.YES_NO_OPTION);
			if(action != JOptionPane.YES_OPTION) {
				exportImage();
				return;
			}
		}
		DataViewerImageExport export = new DataViewerImageExport(this, 
				imageWidth, imageHeight, quality.floatValue(), imageFile);
		export.exportImage();
	}
	
	public INetworkDataViewer createOffscreenViewer() {
		NetworkMapViewer viewer = new NetworkMapViewer(getNetwork(), getViewerConfiguration(), 
				getNetworkContainer());
		return viewer;
	}
	
	public void initializeOffscreenViewer(INetworkDataViewer aViewer, int width, int height) {
		NetworkMapViewer viewer = (NetworkMapViewer) aViewer;
		viewer.paintManager.setOffscreenPainting(true);
		viewer.setSize(width, height);
		// set same view as for this map
		viewer.perfectFit = false;
		viewer.minLatitude = this.minLatitude;
		viewer.maxLatitude = this.maxLatitude;
		viewer.minLongitude = this.minLongitude;
		viewer.maxLongitude = this.maxLongitude;
		viewer.initializeInternalCoordinates();
		double widthFactor = (double) width / (double) getWidth();
		double heightFactor = (double) height / (double) getHeight();
		double factor = Math.min(widthFactor, heightFactor);
		viewer.horizontal_margin = (int) (this.horizontal_margin * factor);
		viewer.vertical_margin = (int) (this.vertical_margin * factor);
		double weightFactor = 0.75;
		viewer.createStrokes((float) (1.0 * weightFactor * factor), (float) (2.5 * weightFactor * factor));
		viewer.shapeProvider.setLimitSize(false);
		viewer.fontSize = (float) (this.getFont().getSize2D() * factor * weightFactor);
//		viewer.shapeProvider.setShapeSizeFactor(factor);
		viewer.updateArrowSize((int) (this.arrowSize * factor * weightFactor));
		viewer.networkMarkerSize = (int) (this.networkMarkerSize * factor);
	}

	public void dispose() {
		getViewerController().dispose();
		getViewerConfiguration().removeDatabaseChangeListener(this);
	}
	
	protected void initializeInternalCoordinates() {
		super.initializeInternalCoordinates();
		updateVisibleElements();
		paintManager.updateBackgroundImage();
	}
	
	private void updateVisibleElements() {
		visibleBusses = null;
		visibleBranches = null;
	}
	
	private void checkVisibleElements() {
		if(visibleBusses == null)
			initializeVisibleElements();
	}
	
	private void initializeVisibleElements() {
		visibleBusses = new ArrayList<CombinedBus>();
		double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY, maxX = 0, maxY = 0;
		for (int i = 0; i < getNetwork().getCombinedBusCount(); i++) {
			CombinedBus cbus = getNetwork().getCombinedBus(i);
			double[] coords = getBusXYDouble(cbus.getFirstBus().getBusNumber());
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
		visibleBranches = new ArrayList<CombinedBranch>();
		for (int i = 0; i < getNetwork().getCombinedBranchCount(); i++) {
			CombinedBranch cbranch = getNetwork().getCombinedBranch(i);
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
			if(isOutsideView(x1, y1) && isOutsideView(x2, y2) && isOutsideView(x1, y1, x2, y2, 6))
				continue;
			visibleBranches.add(cbranch);
		}
//		System.out.println("Visible combined busses: " + visibleBusses.size());
//		System.out.println("Visible combined branches: " + visibleBranches.size());
		// determine factor for size of element shapes
		double minDim = Math.max(maxX - minX, maxY - minY);
		int busCount = visibleBusses.size();
		double shapeSizeFactor;
		if(busCount > 0 && minDim > 0)
			shapeSizeFactor = ((maxX - minX) * (maxY - minY)) / (5000 * busCount);//minDim / (10.0 * busCount);
		else
			shapeSizeFactor = 1;
//		System.out.println("shapeSizeFactor="+shapeSizeFactor);
		shapeProvider.setShapeSizeFactor(shapeSizeFactor);
	}
	
	@Override
	protected AbstractNetworkElement getObjectFromScreen(int x, int y) {
		checkVisibleElements();
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
					return getNetwork().getBus(busNumber);
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
					return getNetwork().getMarkers().get(markerIndex);
			}
		}
		// check branches
		if(drawBranches && visibleBranches != null) {
			for (int i = 0; i < visibleBranches.size(); i++) {
				Branch branch = visibleBranches.get(i).getFirstBranch();
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

	@Override
	protected void paintNetwork(Graphics2D g2d) {
		Stroke defaultStroke = g2d.getStroke();
		checkVisibleElements();
		// enable/disable outlines
		paintManager.setActive(OutlinePainter.PAINT_ID, drawOutline);
		// draw background
		paintManager.drawBackground(g2d);
		// draw markers
		if(drawMarkers) {
			g2d.setColor(Color.GRAY);
			List<MarkerElement> markers = getNetwork().getMarkers();
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
				boolean highlighted = isSelection(marker) || isHovered(marker);
				if(highlighted)
					g2d.setStroke(strokesBold[0]);
				else
					g2d.setStroke(strokesNormal[0]);
				String locationName = drawBusNames ? marker.getDisplayName(NetworkElement.DISPLAY_NAME) : null;
				// draw the marker
				drawShape(marker, g2d, markerX, markerY, busX, busY, highlighted, locationName, null);
			}
		}
		// draw branches
		if(drawBranches) {
			for (int i = 0; i < visibleBranches.size(); i++) {
				CombinedBranch cbranch = visibleBranches.get(i);
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
				double[] coords2 = getBusXYDouble(toBus);
				double x2 = coords2[0];//getBusXDouble(toBus, horizontalScale);
				double y2 = coords2[1];//getBusYDouble(toBus, verticalScale);
				if(drawPowerDirection) {
					boolean highlighted = isSelected || isHovered;
					// draw first branch
					Branch branch = cbranch.getFirstBranch();
					if(drawFlags == false || branch.isCorrect()) {
						if(fadeOutUnselected && selection != null && highlighted == false)
							g2d.setColor(Color.LIGHT_GRAY);
						else if(branch.isActive())
							g2d.setColor(Color.BLUE);
						else
							g2d.setColor(Color.GRAY);
					} else
						g2d.setColor(Color.RED);
					IElementShape branchShape = drawShape(branch, g2d, x1, y1, x2, y2, highlighted, null, 
							getBranchStroke(branch, highlighted));
					double realInjectionSumFrom = cbranch.getFromBusRealInjectionSum();
					double realInjectionSumTo = cbranch.getToBusRealInjectionSum();
					if(realInjectionSumFrom != realInjectionSumTo && branchShape != null) {
						double[][] decorationPlaces = branchShape.getAdditionalDecorationsPlace();
						if(decorationPlaces != null && decorationPlaces.length >= 1 && decorationPlaces[0].length >= 4) {
							g2d.fill(getArrowShape(g2d, decorationPlaces[0][0], decorationPlaces[0][1], 
									decorationPlaces[0][2], decorationPlaces[0][3], 
									realInjectionSumFrom > realInjectionSumTo));
						}
					}
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
						if(drawFlags == false || branch.isCorrect()) {
							if(branch.isActive())
								g2d.setColor(Color.BLUE);
							else
								g2d.setColor(Color.GRAY);
						} else
							g2d.setColor(Color.RED);
						boolean highlighted = isSelected || isHovered;
						drawShape(branch, g2d, x1l, y1l, x2l, y2l, false, null, getBranchStroke(branch, highlighted));
					}
				}
			}
		}
		g2d.setStroke(defaultStroke);
		// draw bus nodes
		if(drawBusNodes) {
			for (int i = 0; i < visibleBusses.size(); i++) {
				CombinedBus cbus = visibleBusses.get(i);
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
				for (int b = 0; b < cbus.getGenerators().size(); b++) {
					Generator generator = cbus.getGenerators().get(b);
					if( ! isSelected && isSelection(generator))
						isSelected = true;
					if( ! isHovered && isHovered(generator))
						isHovered = true;
				}
				boolean highlighted = isSelected || isHovered;
				if(drawFlags && hasFailures)
					g2d.setColor(Preferences.getFlagFailureColor());
				else if(drawFlags && hasWarnings)
					g2d.setColor(Preferences.getFlagWarningColor());
				else {
					if(isSlack)
						g2d.setColor(Color.BLUE);
					else if(fadeOutUnselected && selection != null && highlighted == false)
						g2d.setColor(Color.GRAY);
					else
						g2d.setColor(Color.BLACK);
				}
				double[] coords = getBusXYDouble(cbus.getFirstBus().getBusNumber());
				double x = coords[0];
				double y = coords[1];
				String locationName = drawBusNames ? cbus.getLabel() : null;
				drawShape(cbus.getFirstBus(), g2d, x, y, Double.NaN, Double.NaN, highlighted, locationName, null);
//					// draw generators
//					if(drawGenerators && cbus.getGenerators().size() > 0) {
//						for (int gen = 0; gen < cbus.getGenerators().size(); gen++) {
//							Generator generator = cbus.getGenerators().get(gen);
//							if( ! isSelected && isSelection(generator))
//								isSelected = true;
//							if( ! isHovered && isHovered(generator))
//								isHovered = true;
//							
//						}
//						if(hasFailures)
//							g.setColor(Preferences.getFlagFailureColor());
//						else if(hasWarnings)
//							g.setColor(Preferences.getFlagWarningColor());
//						else {
//							if(isSlack)
//								g.setColor(Color.BLUE);
//							else
//								g.setColor(Color.BLACK);
//						}
//						int x_gen = (int) x;//getBusX(busIndex, horizontalScale);
//						int y_gen = (int) y;//getBusY(busIndex, verticalScale);
//						if(x == -1 || y == -1)
//							continue;
//						g2d.drawRect(
//								x_gen - RECTANGLE_HALF_HEIGHT - 1, 
//								y_gen - RECTANGLE_HALF_HEIGHT - 1, 
//								2 * RECTANGLE_HALF_HEIGHT + 2, 
//								2 * RECTANGLE_HALF_HEIGHT + 2);
//						if(isSelected || isHovered) {
//							g2d.drawRect(
//									x_gen - RECTANGLE_HALF_HEIGHT - 2, 
//									y_gen - RECTANGLE_HALF_HEIGHT - 2, 
//									2 * RECTANGLE_HALF_HEIGHT + 4, 
//									2 * RECTANGLE_HALF_HEIGHT + 4);
//							g2d.drawRect(
//									x_gen - RECTANGLE_HALF_HEIGHT - 3, 
//									y_gen - RECTANGLE_HALF_HEIGHT - 3, 
//									2 * RECTANGLE_HALF_HEIGHT + 6, 
//									2 * RECTANGLE_HALF_HEIGHT + 6);
//						}
//					}
			}
		}
	}
	
	protected boolean isOutsideView(double x1, double y1, double x2, double y2, double count) {
		double xn = (x2 - x1) / count;
		double yn = (y2 - y1) / count;
		for (double i = 1; i < count; i++) {
			if(isOutsideView(x1 + xn * i, y1 + yn * i) == false)
				return false;
		}
		return true;
	}
	
	protected boolean isOutsideView(double x, double y) {
		int space = 10;
		return x < -space || x > getWidth() + space || y < -space || y > getHeight() + space;
	}
	
	protected IElementShape drawShape(AbstractNetworkElement element, Graphics2D g2d, 
			double x1, double y1, double x2, double y2, boolean highlighted, String label, Stroke customStroke) {
		IElementShape elementShape = shapeProvider.getShape(element.getShapeID());
		if(elementShape != null) {
			Shape[] shapes = elementShape.getTranslatedShapes(x1, y1, x2, y2, highlighted);
			if(shapes == null)
				return elementShape;
			boolean[] fillShapes = elementShape.fillShape();
			boolean[] useCustomStrokes = elementShape.useCustomStroke();
			for (int shapeIndex = 0; shapeIndex < shapes.length; shapeIndex++) {
				Shape shape = shapes[shapeIndex];
				if(shape == null)
					continue;
				if(customStroke != null && useCustomStrokes.length > shapeIndex && useCustomStrokes[shapeIndex]) {
					g2d.setStroke(customStroke);
				} else {
					if(highlighted)
						g2d.setStroke(strokesBold[0]);
					else
						g2d.setStroke(strokesNormal[0]);
				}
				if(fillShapes.length > shapeIndex && fillShapes[shapeIndex])
					g2d.fill(shape);
				else
					g2d.draw(shape);
			}
		}
		// draw the marker's name
		if(label != null) {
			if(fontSize > -1)
				g2d.setFont(g2d.getFont().deriveFont(fontSize));
			double offset = elementShape != null ? elementShape.getSize() / 1.5 : 15;
			g2d.drawString(label, (int) (x1+offset), (int) (y1+offset+g2d.getFont().getSize()/2));
		}
		return elementShape;
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
	
	@Override
	protected void updateBackground() {
		paintManager.updateBackgroundImage();
		repaint();
	}
	
	List<Integer> getVoltageLevels() {
		if(voltageLevels == null)
			updateVoltageLevels();
		return voltageLevels;
	}
	
	private void updateVoltageLevels() {
		voltageLevels = new ArrayList<Integer>();
		for (int i = 0; i < getNetwork().getCombinedBranchCount(); i++) {
			CombinedBranch cbranch = getNetwork().getCombinedBranch(i);
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
	
	@Override
	protected String getTooltipText(AbstractNetworkElement dataElement) {
		if(dataElement instanceof Branch) {
			CombinedBranch cbranch = getNetwork().getCombinedBranch(dataElement);
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
			CombinedBus cbus = getNetwork().getCombinedBus(dataElement);
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
		Bus bus = getNetwork().getBus(branch.getFromBusNumber());
		if(bus == null)
			return 0;
		return bus.getBaseVoltage();
	}
	
	@Override
	public JComponent getComponent() {
		return getViewerController();
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
		paintManager.updateListeners(event);
	}

	@Override
	public void parameterChanged(DatabaseChangeEvent event) {
		String property = event.getParameterID();
		setViewerProperty(property, event.getNewValue());
		paintManager.updateListeners(event);
	}
	
	CaseViewer getNetworkContainer() {
		return getNetworkContainer(parentContainer);
	}
	
	private CaseViewer getNetworkContainer(Component c) {
		if(c instanceof CaseViewer)
			return (CaseViewer) c;
		if(c.getParent() != null)
			return getNetworkContainer(c.getParent());
		return null;
	}
}
