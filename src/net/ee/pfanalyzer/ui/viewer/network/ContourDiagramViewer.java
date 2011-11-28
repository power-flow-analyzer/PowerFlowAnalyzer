package net.ee.pfanalyzer.ui.viewer.network;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.data.ModelData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.model.util.ParameterUtils;
import net.ee.pfanalyzer.ui.viewer.DataViewerConfiguration;
import net.ee.pfanalyzer.ui.viewer.network.contour.ColorLegend;
import net.ee.pfanalyzer.ui.viewer.network.contour.ColorProvider;
import net.ee.pfanalyzer.ui.viewer.network.contour.ContourDiagramSettings;
import net.ee.pfanalyzer.ui.viewer.network.contour.ContourPainter;

public class ContourDiagramViewer extends NetworkMapViewer {
	
	public final static String VIEWER_ID = BASE_NETWORK_VIEWER_ID + ".contour";
	
	private final static String PROPERTY_ELEMENT_ID_PREFIX = "CONTOUR_ELEMENT_ID_PREFIX";
	private final static String PROPERTY_PARAMETER_NAME = "CONTOUR_PARAMETER_NAME";
	private final static String PROPERTY_MAX_VALUE = "CONTOUR_MAX_VALUE";
	private final static String PROPERTY_MIDDLE_VALUE = "CONTOUR_MIDDLE_VALUE";
	private final static String PROPERTY_MIN_VALUE = "CONTOUR_MIN_VALUE";
	private final static String PROPERTY_MAX_DISTANCE = "CONTOUR_MAX_DISTANCE";
	private final static String PROPERTY_MAX_REL_DISTANCE = "CONTOUR_MAX_REL_DISTANCE";
	private final static String PROPERTY_COLOR_STEPS = "CONTOUR_COLOR_STEPS";
	private final static String PROPERTY_MAX_COLOR = "CONTOUR_MAX_COLOR";
	private final static String PROPERTY_UPPER_HALF_COLOR = "CONTOUR_UPPER_HALF_COLOR";
	private final static String PROPERTY_MIDDLE_COLOR = "CONTOUR_MIDDLE_COLOR";
	private final static String PROPERTY_LOWER_HALF_COLOR = "CONTOUR_LOWER_HALF_COLOR";
	private final static String PROPERTY_MIN_COLOR = "CONTOUR_MIN_COLOR";
	private final static String PROPERTY_COLOR_PROVIDER = "COLOR_PROVIDER";
	private final static String PROPERTY_ACTION_OUT_OF_BOUNDS = "ACTION_OUT_OF_BOUNDS";
	private final static String PROPERTY_TRANSPARENCY = "TRANSPARENCY";
	
	private ContourDiagramSettings settings;
	private ColorLegend colorLegend;
	private JLabel contourLegend;
	
	public ContourDiagramViewer(Network network, DataViewerConfiguration configuration, Component parent) {
		super(network, configuration, parent);
		colorLegend = new ColorLegend(settings);
		getViewerController().add(colorLegend, BorderLayout.WEST);
		getViewerController().add(contourLegend, BorderLayout.NORTH);
		paintManager.addPaintListener(new ContourPainter(this, settings));
	}
	
	@Override
	protected void initializeSettings() {
		super.initializeSettings();
		settings = new ContourDiagramSettings();
		contourLegend = new JLabel();
		contourLegend.setOpaque(true);
		contourLegend.setFont(contourLegend.getFont().deriveFont(Font.BOLD));
		contourLegend.setHorizontalAlignment(SwingConstants.CENTER);
		setSetting(PROPERTY_ELEMENT_ID_PREFIX);
		setSetting(PROPERTY_PARAMETER_NAME);
		setSetting(PROPERTY_MAX_VALUE);
		setSetting(PROPERTY_MIN_VALUE);
		setSetting(PROPERTY_MIDDLE_VALUE);
		setSetting(PROPERTY_MAX_DISTANCE);
		setSetting(PROPERTY_MAX_REL_DISTANCE);
		setSetting(PROPERTY_COLOR_STEPS);
		setSetting(PROPERTY_MAX_COLOR);
		setSetting(PROPERTY_UPPER_HALF_COLOR);
		setSetting(PROPERTY_MIDDLE_COLOR);
		setSetting(PROPERTY_LOWER_HALF_COLOR);
		setSetting(PROPERTY_MIN_COLOR);
		setSetting(PROPERTY_COLOR_PROVIDER);
		setSetting(PROPERTY_TRANSPARENCY);
		setSetting(PROPERTY_ACTION_OUT_OF_BOUNDS);
		updateLabel();
	}
	
	@Override
	protected void setViewerProperty(String property, String value) {
		if(property.equals(PROPERTY_ELEMENT_ID_PREFIX)) {
			settings.setElementIDPrefix(value);
			updateLabel();
			updateBackground();
		} else if(property.equals(PROPERTY_PARAMETER_NAME)) {
			settings.setParameterName(value);
			updateLabel();
			updateBackground();
		} else if(property.equals(PROPERTY_MAX_VALUE)) {
			settings.setMaxValue(parseDouble(value));
			updateBackground();
		} else if(property.equals(PROPERTY_MIN_VALUE)) {
			settings.setMinValue(parseDouble(value));
			updateBackground();
		} else if(property.equals(PROPERTY_MIDDLE_VALUE)) {
			settings.setMiddleValue(parseDouble(value));
			updateBackground();
		} else if(property.equals(PROPERTY_MAX_DISTANCE)) {
			settings.setMaxDistance(parseDouble(value));
			updateBackground();
		} else if(property.equals(PROPERTY_MAX_REL_DISTANCE)) {
			settings.setMaxRelDistance(parseDouble(value));
			updateBackground();
		} else if(property.equals(PROPERTY_COLOR_STEPS)) {
			settings.getColorProvider().setColorSteps(parseDouble(value));
			updateBackground();
		} else if(property.equals(PROPERTY_MAX_COLOR)) {
			settings.getColorProvider().setMaxColor(ParameterUtils.parseColor(value));
			updateBackground();
		} else if(property.equals(PROPERTY_UPPER_HALF_COLOR)) {
			settings.getColorProvider().setUpperHalfColor(ParameterUtils.parseColor(value));
			updateBackground();
		} else if(property.equals(PROPERTY_MIDDLE_COLOR)) {
			settings.getColorProvider().setMiddleColor(ParameterUtils.parseColor(value));
			updateBackground();
		} else if(property.equals(PROPERTY_LOWER_HALF_COLOR)) {
			settings.getColorProvider().setLowerHalfColor(ParameterUtils.parseColor(value));
			updateBackground();
		} else if(property.equals(PROPERTY_MIN_COLOR)) {
			settings.getColorProvider().setMinColor(ParameterUtils.parseColor(value));
			updateBackground();
		} else if(property.equals(PROPERTY_COLOR_PROVIDER)) {
			int intValue = parseInt(value);
			ColorProvider provider = intValue == 3 ? 
					new ColorProvider.SimpleColorProvider(settings.getColorProvider()) :
					new ColorProvider.ComplexColorProvider(settings.getColorProvider());
			settings.setColorProvider(provider);
			updateBackground();
		} else if(property.equals(PROPERTY_TRANSPARENCY)) {
			int transparency = parseInt(value);
			settings.getColorProvider().setTransparency(transparency);
			updateBackground();
		} else if(property.equals(PROPERTY_ACTION_OUT_OF_BOUNDS)) {
			int action = parseInt(value);
			settings.setOutOfBoundsAction(action);
			updateBackground();
		} else
			super.setViewerProperty(property, value);
	}
	
	private double parseDouble(String value) {
		if(value == null || value.isEmpty())
			return Double.NaN;
		else
			return Double.valueOf(value);
	}
	
	private int parseInt(String value) {
		int intValue = -1;
		try {
			if(value == null || value.isEmpty())
				return -1;
			intValue = (int) Double.valueOf(value).doubleValue();
		} catch(Exception e) {
		}
		if(intValue < 0 || intValue > 255)
			intValue = -1;
		return intValue;
	}
	
	private void updateLabel() {
		String text = "";
		if(settings.getElementIDPrefix() != null && settings.getParameterName() != null) {
			text = "Parameter \"" + settings.getParameterName() + "\"";
			List<ModelData> possibleModels = getNetwork().getPowerFlowCase().getModelDB().getModels(
					settings.getElementIDPrefix());
			if(possibleModels.size() > 0) {
				NetworkParameter paramDef = ModelDBUtils.getParameterDefinition(
						possibleModels.get(0), settings.getParameterName());
				if(paramDef != null && paramDef.getLabel() != null)
					text = paramDef.getLabel();
			}
		}
		contourLegend.setText(text);
	}
	
	@Override
	protected void updateBackground() {
		super.updateBackground();
		if(colorLegend != null) {
			colorLegend.revalidate();
			colorLegend.repaint();
		}
	}
}
