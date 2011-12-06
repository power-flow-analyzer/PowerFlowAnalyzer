package net.ee.pfanalyzer.ui.viewer.network;

public interface INetworkMapParameters {

	final static double[] ZOOM_GERMANY_COORDINATES = { 5.8, 15.1, 47, 55 };
	final static int ZOOM_PERFECT_FIT = 0;
	final static int ZOOM_CUSTOM = 1;
	final static int ZOOM_GERMANY = 2;
	
	final static String PROPERTY_ZOOM_CHOICE = "ZOOM";
	final static String PROPERTY_RESPECT_ASPECT_RATIO = "KEEP_ASPECT_RATIO";
	final static String PROPERTY_DRAW_BUSSES = "DRAW_BUSSES";
	final static String PROPERTY_DRAW_BUS_NAMES = "DRAW_BUS_NAMES";
	final static String PROPERTY_DRAW_BRANCHES = "DRAW_BRANCHES";
	final static String PROPERTY_DRAW_POWER_DIRECTION = "DRAW_POWER_FLOW_DIRECTION";
	final static String PROPERTY_DRAW_GENERATORS = "DRAW_GENERATORS";
	final static String PROPERTY_DRAW_OUTLINE = "DRAW_OUTLINE";
	final static String PROPERTY_DRAW_LEGEND = "SHOW_LEGEND";
	final static String PROPERTY_FADE_OUT_UNSELECTED = "FADE_OUT_UNSELECTED";

	final static String PROPERTY_INTERACTION_ZOOM = "ALLOW_ZOOMING";
	final static String PROPERTY_INTERACTION_MOVE = "ALLOW_DRAGGING";
	final static String PROPERTY_SHOW_TOOLTIPS = "SHOW_TOOLTIPS";
	
}
