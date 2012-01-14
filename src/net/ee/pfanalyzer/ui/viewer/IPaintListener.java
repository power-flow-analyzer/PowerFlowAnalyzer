package net.ee.pfanalyzer.ui.viewer;

import java.awt.Graphics2D;

import net.ee.pfanalyzer.model.DatabaseChangeEvent;
import net.ee.pfanalyzer.ui.util.MapBoundingBox;

public interface IPaintListener {
	
	public final static int LAYER_CONTOUR = 0;
	public final static int LAYER_OUTLINES = 10;

	void paint(Graphics2D g2d);
	
	INetworkDataViewer getViewer();
	
	boolean needsUpdate(DatabaseChangeEvent event);
	
	void update();
	
	void setActive(boolean flag);
	
	boolean isActive();
	
	String getPaintID();
	
	int getLayer();
	
	MapBoundingBox getBoundingBox();
}
