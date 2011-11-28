package net.ee.pfanalyzer.ui.viewer.network;

import java.awt.BorderLayout;
import java.awt.Component;

import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.util.ParameterUtils;
import net.ee.pfanalyzer.ui.viewer.DataViewerConfiguration;
import net.ee.pfanalyzer.ui.viewer.network.contour.ColorProvider;
import net.ee.pfanalyzer.ui.viewer.network.contour.ContourDiagramSettings;
import net.ee.pfanalyzer.ui.viewer.network.contour.ContourPainter;
import net.ee.pfanalyzer.ui.viewer.network.contour.Legend;

public class ContourDiagramViewer extends NetworkMapViewer {
	
	public final static String VIEWER_ID = "viewer.network.contour";
	
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
	private Legend legend;
	
	public ContourDiagramViewer(Network network, DataViewerConfiguration configuration, Component parent) {
		super(network, configuration, parent);
		getViewerController().add(legend, BorderLayout.WEST);
		paintManager.addPaintListener(new ContourPainter(this, settings));
	}
	
	@Override
	protected void initializeSettings() {
		super.initializeSettings();
		settings = new ContourDiagramSettings();
		legend = new Legend(settings);
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
	}
	
	@Override
	protected void setViewerProperty(String property, String value) {
		if(property.equals(PROPERTY_MAX_VALUE)) {
			settings.setMaxValue(parseDouble(value));
			updateSettings();
		} else if(property.equals(PROPERTY_MIN_VALUE)) {
			settings.setMinValue(parseDouble(value));
			updateSettings();
		} else if(property.equals(PROPERTY_MIDDLE_VALUE)) {
			settings.setMiddleValue(parseDouble(value));
			updateSettings();
		} else if(property.equals(PROPERTY_MAX_DISTANCE)) {
			settings.setMaxDistance(parseDouble(value));
			updateSettings();
		} else if(property.equals(PROPERTY_MAX_REL_DISTANCE)) {
			settings.setMaxRelDistance(parseDouble(value));
			updateSettings();
		} else if(property.equals(PROPERTY_COLOR_STEPS)) {
			settings.getColorProvider().setColorSteps(parseDouble(value));
			updateSettings();
		} else if(property.equals(PROPERTY_MAX_COLOR)) {
			settings.getColorProvider().setMaxColor(ParameterUtils.parseColor(value));
			updateSettings();
		} else if(property.equals(PROPERTY_UPPER_HALF_COLOR)) {
			settings.getColorProvider().setUpperHalfColor(ParameterUtils.parseColor(value));
			updateSettings();
		} else if(property.equals(PROPERTY_MIDDLE_COLOR)) {
			settings.getColorProvider().setMiddleColor(ParameterUtils.parseColor(value));
			updateSettings();
		} else if(property.equals(PROPERTY_LOWER_HALF_COLOR)) {
			settings.getColorProvider().setLowerHalfColor(ParameterUtils.parseColor(value));
			updateSettings();
		} else if(property.equals(PROPERTY_MIN_COLOR)) {
			settings.getColorProvider().setMinColor(ParameterUtils.parseColor(value));
			updateSettings();
		} else if(property.equals(PROPERTY_COLOR_PROVIDER)) {
			int intValue = parseInt(value);
			ColorProvider provider = intValue == 5 ? 
					new ColorProvider.ComplexColorProvider(settings.getColorProvider()) : 
						new ColorProvider.SimpleColorProvider(settings.getColorProvider());
			settings.setColorProvider(provider);
			updateSettings();
		} else if(property.equals(PROPERTY_TRANSPARENCY)) {
			int transparency = parseInt(value);
			settings.getColorProvider().setTransparency(transparency);
			updateSettings();
		} else if(property.equals(PROPERTY_ACTION_OUT_OF_BOUNDS)) {
			int action = parseInt(value);
			settings.setOutOfBoundsAction(action);
			updateSettings();
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
	
	private void updateSettings() {
		paintManager.updateBackgroundImage();
		legend.repaint();
	}
}
