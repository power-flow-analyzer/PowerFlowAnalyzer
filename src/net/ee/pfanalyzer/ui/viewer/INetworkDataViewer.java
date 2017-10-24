package net.ee.pfanalyzer.ui.viewer;

import java.awt.Graphics;

import javax.swing.JComponent;

import net.ee.pfanalyzer.model.INetworkChangeListener;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.ui.INetworkElementSelectionListener;
import net.ee.pfanalyzer.ui.timer.DisplayTimer;

public interface INetworkDataViewer extends INetworkElementSelectionListener, INetworkChangeListener {

	void refresh();
	
	void fireDisplayTimeChanged(DisplayTimer timer);
	
	void dispose();
	
	void setData(Network network);
	
	Network getNetwork();
	
	JComponent getComponent();
	
	DataViewerConfiguration getViewerConfiguration();
	
	void addViewerActions(DataViewerContainer container);
	
	void paintViewer(Graphics g);
}
