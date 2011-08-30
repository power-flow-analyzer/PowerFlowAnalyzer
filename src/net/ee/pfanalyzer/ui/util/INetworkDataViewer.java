package net.ee.pfanalyzer.ui.util;

import javax.swing.JComponent;

import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.ui.NetworkElementSelectionListener;

public interface INetworkDataViewer extends NetworkElementSelectionListener {

	void refresh();
	
	void setData(Network network);
	
	Network getNetwork();
	
	JComponent getComponent();
}
