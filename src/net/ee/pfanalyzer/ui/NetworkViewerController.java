package net.ee.pfanalyzer.ui;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JPanel;

import net.ee.pfanalyzer.ui.dialog.MapPropertiesDialog;

public class NetworkViewerController extends JPanel {

	public final static double[] ZOOM_GERMANY_COORDINATES = { 5.8, 15.1, 47, 55 };
	public final static int ZOOM_PERFECT_FIT = 0;
	public final static int ZOOM_CUSTOM = 1;
	public final static int ZOOM_GERMANY = 2;
	
	public final static String PROPERTY_ZOOM_CHOICE = "property.viewer.zoomChoice";
	public final static String PROPERTY_RESPECT_ASPECT_RATIO = "property.viewer.respectAspectRatio";
	public final static String PROPERTY_DRAW_BUSSES = "property.viewer.draw.bus";
	public final static String PROPERTY_DRAW_BRANCHES = "property.viewer.draw.branch";
	public final static String PROPERTY_DRAW_POWER_DIRECTION = "property.viewer.draw.power.direction";
	public final static String PROPERTY_DRAW_GENERATORS = "property.viewer.draw.generator";
	public final static String PROPERTY_DRAW_OUTLINE = "property.viewer.draw.outline";
	public final static String PROPERTY_DRAW_LEGEND = "property.viewer.draw.legend";

	public final static String PROPERTY_INTERACTION_ZOOM = "property.viewer.interaction.zoom";
	public final static String PROPERTY_INTERACTION_MOVE = "property.viewer.interaction.move";
	public final static String PROPERTY_SHOW_TOOLTIPS = "property.viewer.tooltips.show";
	
	private NetworkViewer viewer;
	private NetworkViewerLegend legend;
	private MapPropertiesDialog mapPropertiesDialog;
	
	public NetworkViewerController(final NetworkViewer viewer) {
		super(new BorderLayout());
		this.viewer = viewer;
		legend = new NetworkViewerLegend(viewer);
		add(legend, BorderLayout.SOUTH);
		add(viewer, BorderLayout.CENTER);
	}
	
	public void showMapPropertiesDialog(Frame frame) {
		if(mapPropertiesDialog != null) {
			mapPropertiesDialog.setVisible(true);
			mapPropertiesDialog.toFront();
		} else
			mapPropertiesDialog = new MapPropertiesDialog(frame, this);
	}
	
	public void zoomChanged(int zoomID) {
		if(mapPropertiesDialog != null)
			mapPropertiesDialog.setZoomChoice(zoomID);
	}
	
	public void setViewerProperty(String property, boolean value) {
		if(property.equals(PROPERTY_RESPECT_ASPECT_RATIO))
			viewer.setRespectAspectRation(value);
		else if(property.equals(PROPERTY_DRAW_BUSSES))
			viewer.setDrawBusNodes(value);
		else if(property.equals(PROPERTY_DRAW_BRANCHES))
			viewer.setDrawBranches(value);
		else if(property.equals(PROPERTY_DRAW_POWER_DIRECTION))
			viewer.setDrawPowerDirection(value);
		else if(property.equals(PROPERTY_DRAW_GENERATORS))
			viewer.setDrawGeneratorsNodes(value);
		else if(property.equals(PROPERTY_DRAW_OUTLINE))
			viewer.setDrawOutline(value);
		else if(property.equals(PROPERTY_DRAW_LEGEND)) {
			legend.setVisible(value);
			revalidate();
		}
		else if(property.equals(PROPERTY_INTERACTION_ZOOM))
			viewer.setAllowZooming(value);
		else if(property.equals(PROPERTY_INTERACTION_MOVE))
			viewer.setAllowDragging(value);
		else if(property.equals(PROPERTY_SHOW_TOOLTIPS))
			viewer.setShowTooltips(value);
	}
	
	public void setViewerProperty(String property, int value) {
		if(property.equals(PROPERTY_ZOOM_CHOICE)) {
			if(value == 2)
				viewer.setView(ZOOM_GERMANY_COORDINATES);
			viewer.setPerfectFit(value == 0);
		}
	}
}
