package net.ee.pfanalyzer.ui.viewer;

import java.awt.Graphics2D;

import net.ee.pfanalyzer.model.DatabaseChangeEvent;

public interface IPaintListener {

	void paint(Graphics2D g2d);
	
	INetworkDataViewer getViewer();
	
	boolean needsUpdate(DatabaseChangeEvent event);
	
	void update();
	
	void setActive(boolean flag);
	
	boolean isActive();
	
	String getPaintID();
}
