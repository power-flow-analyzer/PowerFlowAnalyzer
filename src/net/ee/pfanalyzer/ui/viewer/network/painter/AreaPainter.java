package net.ee.pfanalyzer.ui.viewer.network.painter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.util.ParameterUtils;
import net.ee.pfanalyzer.preferences.Preferences;
import net.ee.pfanalyzer.ui.CaseViewer;
import net.ee.pfanalyzer.ui.util.MapBoundingBox;
import net.ee.pfanalyzer.ui.viewer.network.NetworkMapViewer;
import net.ee.pfanalyzer.ui.viewer.network.Outline;

public class AreaPainter extends AbstractShapePainter {
	
	public final static String PAINT_ID = "paint.network.area";
	
	public final static String SETTING_ELEMENT_FILTER = "AREA_ELEMENT_FILTER";
	public final static String SETTING_PARAMETER_NAME = "AREA_PARAMETER_NAME";
	public final static String SETTING_MIN_VALUE = "AREA_MIN_VALUE";
	public final static String SETTING_MAX_VALUE = "AREA_MAX_VALUE";
	public final static String SETTING_MIN_COLOR = "AREA_MIN_COLOR";
	public final static String SETTING_MAX_COLOR = "AREA_MAX_COLOR";
	public final static String SETTING_DISPLAY_VALUE = "AREA_DISPLAY_VALUE";
	public final static String SETTING_ITEM_FIXED_VALUE = "FIXED_VALUE";
	public final static String SETTING_ITEM_TIME_SERIES = "TIME_SERIES";
	public final static String SETTING_ITEM_FLAG = "FLAG";
	public final static String SETTING_ITEM_OUTLINE_BACKGROUND = "OUTLINE_BACKGROUND";
	
	private final static int MODE_FIXED_VALUE = 0;
	private final static int MODE_TIME_SERIES = 1;
	private final static int MODE_FLAG = 2;
	private final static int MODE_OUTLINE_BACKGROUND = 3;
	
	private String elementFilter, parameterName;
	private double minValue, maxValue;
	private Color minColor, maxColor;
	private int displayValue;

	private List<Area> visibleAreas = new ArrayList<Area>();
	
//	private List<Outline> outlines;
//	private Collection<Outline> lastOutlineList;
	private MapBoundingBox boundingBox = new MapBoundingBox();

	public AreaPainter(Network network, NetworkMapViewer viewer) {
		super(network, viewer);
	}

	@Override
	public String getPaintID() {
		return PAINT_ID;
	}

	@Override
	public int getLayer() {
		return LAYER_AREAS;
	}

	@Override
	public void paint(Graphics2D g2d) {
		if(viewer.isDrawAreas()) {
//			System.out.println("AreaPainter::paint");
			int timeStep = getNetwork().getPowerFlowCase().getTimer().getTimeStep();
			synchronized (visibleAreas) {
				for (Area area : visibleAreas) {
					Color borderColor = area.getOutline().getBorderColor();
					Color backgroundColor = null;
					switch (displayValue) {
						case MODE_FIXED_VALUE:
							Double fixedValue = area.getData().getDoubleParameter(parameterName);
							if(fixedValue != null)
								backgroundColor = getColorFromValue(fixedValue.doubleValue());
							break;
						case MODE_TIME_SERIES:
							double value = area.getData().getDoubleValue(parameterName, timeStep);
							if(Double.isNaN(value) == false)
								backgroundColor = getColorFromValue(value);
							break;
						case MODE_FLAG:
							if(area.getData().hasFailures())
								backgroundColor = Preferences.getFlagFailureColor();
							else if(area.getData().hasWarnings())
								backgroundColor = Preferences.getFlagWarningColor();
							break;
						case MODE_OUTLINE_BACKGROUND:
							backgroundColor = area.getOutline().getBackgroundColor();
							break;
					}
					// draw selection
					// TODO area selection is not rendered properly due to asynchronous area painting
//					boolean isSelected = viewer.isSelection(area.getData());
//					boolean isHovered = viewer.isHovered(area.getData());
//					boolean highlighted = isSelected || isHovered;
////					if(highlighted)
//						g2d.setStroke(new BasicStroke(5.0f));
//					else
//						g2d.setStroke(new BasicStroke(1.0f));
					// draw all related shapes
					for (GeneralPath polygon : area.getPolygons()) {
						// background color may be null
						drawOutline(g2d, polygon, borderColor, backgroundColor);
					}
				}
			}
//			List<Outline> outlines = getOutlines();
//			if(outlines == null)
//				return;
//			for (Outline outline : getOutlines()) {
//				Color outlineColor = outline.getBorderColor();
//				Color backgroundColor = outline.getBackgroundColor();
//				int[][] coords = outline.getScreenPoints();
//				if(coords.length == 0)
//					continue;
//				double lastX = viewer.getX(coords[0]);
//				double lastY = viewer.getY(coords[0]);
//				GeneralPath polygon = new GeneralPath();
//				polygon.moveTo(lastX, lastY);
//				for (int i = 1; i < coords.length; i++) {
//					// check if polygon must be closed
//					if(coords[i][0] == -1 && coords[i][1] == -1) {
//						if(polygon != null) {
//							polygon.closePath();
//							drawOutline(g2d, polygon, outlineColor, backgroundColor);
//							// new polygon will be created next round
//							polygon = null;
//						}
//						continue;
//					}
//					double x = viewer.getX(coords[i]);
//					double y = viewer.getY(coords[i]);
//					if(polygon == null) {
//						polygon = new GeneralPath();
//						polygon.moveTo(x, y);
//						
//						continue;
//					}
////					g2d.draw(new Line2D.Double(lastX, lastY, x, y));
////					lastX = x;
////					lastY = y;
//					polygon.lineTo(x, y);
//				}
//				if(polygon != null) {
//					polygon.closePath();
//					drawOutline(g2d, polygon, outlineColor, backgroundColor);
//				}
//			}
			
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
	
	private Color getColorFromValue(double rawValue) {
		double value = rawValue;
		if(rawValue > maxValue)
			value = maxValue;
		else if(rawValue < minValue)
			value = minValue;
		double ratio = Math.abs((value - minValue) / (maxValue - minValue));
		int red = (int) (minColor.getRed() + ratio * (maxColor.getRed() - minColor.getRed()));
		int green = (int) (minColor.getGreen() + ratio * (maxColor.getGreen() - minColor.getGreen()));
		int blue = (int) (minColor.getBlue() + ratio * (maxColor.getBlue() - minColor.getBlue()));
		int alpha = (int) (minColor.getAlpha() + ratio * (maxColor.getAlpha() - minColor.getAlpha()));
//		if(red < 0 || red > 255)
//			System.err.println("Wrong color; red: " + red + " for value " + value);
//		if(green < 0 || green > 255)
//			System.err.println("Wrong color; green: " + green + " for value " + value);
//		if(blue < 0 || blue > 255)
//			System.err.println("Wrong color; blue: " + blue + " for value " + value);
		return new Color(red, green, blue, alpha);
	}

	@Override
	public void update() {
		if(viewer.isDrawAreas()) {
//			System.out.println("AreaPainter::update");
			
			String displayValueSetting = viewer.getViewerConfiguration().getTextParameter(
					SETTING_DISPLAY_VALUE, SETTING_ITEM_FIXED_VALUE);
			if(SETTING_ITEM_FIXED_VALUE.equals(displayValueSetting))
				displayValue = MODE_FIXED_VALUE;
			else if(SETTING_ITEM_TIME_SERIES.equals(displayValueSetting))
				displayValue = MODE_TIME_SERIES;
			else if(SETTING_ITEM_FLAG.equals(displayValueSetting))
				displayValue = MODE_FLAG;
			else if(SETTING_ITEM_OUTLINE_BACKGROUND.equals(displayValueSetting))
				displayValue = MODE_OUTLINE_BACKGROUND;
			elementFilter = viewer.getViewerConfiguration().getTextParameter(SETTING_ELEMENT_FILTER, "");
			parameterName = viewer.getViewerConfiguration().getTextParameter(SETTING_PARAMETER_NAME, "");
			minValue = viewer.getViewerConfiguration().getDoubleParameter(SETTING_MIN_VALUE, Double.NaN);
			maxValue = viewer.getViewerConfiguration().getDoubleParameter(SETTING_MAX_VALUE, Double.NaN);
			minColor = ParameterUtils.parseColor(
					viewer.getViewerConfiguration().getTextParameter(SETTING_MIN_COLOR));
			maxColor = ParameterUtils.parseColor(
					viewer.getViewerConfiguration().getTextParameter(SETTING_MAX_COLOR));
			
			updateOutlines();
		}
		
//		outlines = new ArrayList<Outline>();
//		CaseViewer container = viewer.getNetworkContainer();
//		if(container == null) // may be called before container is initialized
//			return;
//		lastOutlineList = container.getOutlines();
//		for (Outline outline : lastOutlineList) {
//			String paramID = "OUTLINE." + outline.getOutlineID();
//			NetworkParameter param = viewer.getViewerConfiguration().getParameterValue(paramID);
//			if(param == null)
//				param = ModelDBUtils.getParameterValue(outline.getOutlineData(), "ENABLED");
//			boolean defaultEnabled = param == null ? false : Boolean.parseBoolean(param.getValue());
//			if(viewer.getViewerConfiguration().getBooleanParameter(paramID, defaultEnabled))
//				addOutlineInternal(outline);
//		}
////		repaint();
	}

	@Override
	public AbstractNetworkElement getObjectFromScreen(int x, int y) {
		return null;
	}
	
	private void updateOutlines() {
		CaseViewer container = viewer.getNetworkContainer();
		if(container == null) // may be called before container is initialized
			return;
		synchronized (visibleAreas) {
	//		System.out.println("updateOutlines");
			visibleAreas.clear();
			boundingBox.reset();
			List<AbstractNetworkElement> areaElements = getNetwork().getElements("area");
			for (AbstractNetworkElement areaElement : areaElements) {
				String outlineID = areaElement.getTextParameter("OUTLINE");
				Outline outline = container.getOutline(outlineID);
				if(outline == null)
					continue;
				Area area = new Area(areaElement, outline);
				if(area.getData().getModelID().startsWith(elementFilter)) {
					visibleAreas.add(area);
					boundingBox.add(outline.getBoundingBox());
					updatePolygons(area);
				}
			}
		}
	}
	
	private void updatePolygons(Area area) {
		area.clearPolygons();
		Outline outline = area.getOutline();
		int[][] coords = outline.getScreenPoints();
		if(coords.length == 0)
			return;
		double lastX = viewer.getX(coords[0]);
		double lastY = viewer.getY(coords[0]);
		GeneralPath polygon = new GeneralPath();
		polygon.moveTo(lastX, lastY);
		for (int i = 1; i < coords.length; i++) {
			// check if polygon must be closed
			if(coords[i][0] == -1 && coords[i][1] == -1) {
				if(polygon != null) {
					polygon.closePath();
					area.addPolygon(polygon);
					// new polygon will be created next round
					polygon = null;
				}
				continue;
			}
			double x = viewer.getX(coords[i]);
			double y = viewer.getY(coords[i]);
			if(polygon == null) {
				polygon = new GeneralPath();
				polygon.moveTo(x, y);
				
				continue;
			}
//			g2d.draw(new Line2D.Double(lastX, lastY, x, y));
//			lastX = x;
//			lastY = y;
			polygon.lineTo(x, y);
		}
		if(polygon != null) {
			polygon.closePath();
			area.addPolygon(polygon);
		}
	}
	
//	private void addOutlineInternal(Outline outline) {
//		outlines.add(outline);
//		if(outline.getBoundingBox() != null)
//			boundingBox.add(outline.getBoundingBox());
//	}
//	
//	private List<Outline> getOutlines() {
//		CaseViewer container = viewer.getNetworkContainer();
//		if(outlines == null && container != null)
//			update();
//		if(container != null && container.getOutlines() != lastOutlineList)
//			update();
//		return outlines;
//	}
	
	class Area {
		
		AbstractNetworkElement data;
		Outline outline;
		List<GeneralPath> polygons = new ArrayList<GeneralPath>();
		
		Area(AbstractNetworkElement data, Outline outline) {
			this.data = data;
			this.outline = outline;
		}
		
		AbstractNetworkElement getData() {
			return data;
		}
		
		Outline getOutline() {
			return outline;
		}
		
		void addPolygon(GeneralPath polygon) {
			polygons.add(polygon);
		}
		
		List<GeneralPath> getPolygons() {
			return polygons;
		}
		
		void clearPolygons() {
			for (GeneralPath polygon : polygons) {
				polygon.reset();
			}
			polygons.clear();
		}
	}
}
