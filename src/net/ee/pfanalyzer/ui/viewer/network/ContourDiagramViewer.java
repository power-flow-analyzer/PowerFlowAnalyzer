package net.ee.pfanalyzer.ui.viewer.network;

import java.awt.BorderLayout;
import java.awt.Component;

import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.ui.viewer.DataViewerConfiguration;
import net.ee.pfanalyzer.ui.viewer.network.contour.ColorProvider;
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
	
	private ColorProvider colorProvider = new ColorProvider.SimpleColorProvider();
	
	public ContourDiagramViewer(Network network, DataViewerConfiguration configuration, Component parent) {
		super(network, configuration, parent);
		getViewerController().add(new Legend(colorProvider), BorderLayout.WEST);
		paintManager.addPaintListener(new ContourPainter(this, colorProvider));
	}
	
	@Override
	protected void initializeSettings() {
		super.initializeSettings();
		setSetting(PROPERTY_MAX_VALUE);
		setSetting(PROPERTY_MIN_VALUE);
	}
	
	@Override
	protected void setViewerProperty(String property, String value) {
		if(property.equals(PROPERTY_MAX_VALUE)) {
			
		} else
			super.setViewerProperty(property, value);
	}
}
