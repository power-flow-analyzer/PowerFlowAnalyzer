package net.ee.pfanalyzer.ui.util;

import javax.swing.JComponent;

import net.ee.pfanalyzer.model.INetworkChangeListener;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.ui.INetworkElementSelectionListener;

public interface INetworkDataViewer extends INetworkElementSelectionListener, INetworkChangeListener {

	void refresh();
	
	void setData(Network network);
	
	Network getNetwork();
	
	JComponent getComponent();
}
