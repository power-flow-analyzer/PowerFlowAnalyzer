package net.ee.pfanalyzer.ui.viewer;

import java.awt.Graphics2D;

import net.ee.pfanalyzer.model.DatabaseChangeEvent;
import net.ee.pfanalyzer.ui.util.MapBoundingBox;

public interface IPaintListener {
	
	public final static int LAYER_BUS_NODES = 10;
	public final static int LAYER_MARKERS = 9;
	public final static int LAYER_BRANCHES = 8;
	public final static int LAYER_AREAS = 7;
	public final static int LAYER_OUTLINES = 6;
	public final static int LAYER_CONTOUR = 5;

	void paint(Graphics2D g2d);
	
	INetworkDataViewer getViewer();
	
	boolean needsUpdate(DatabaseChangeEvent event);
	
	void update();
	
//	void updateDisplayTime(DisplayTimer timer);
	
	void setActive(boolean flag);
	
	boolean isActive();
	
	String getPaintID();
	
	int getLayer();
	
	MapBoundingBox getBoundingBox();
}
