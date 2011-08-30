package net.ee.pfanalyzer.ui.model;

import net.ee.pfanalyzer.model.Network;

public class NetworkPanel extends ModelElementPanel {

	public NetworkPanel(ElementPanelController controller, Network data) {
		super(controller);
		
		setTitle("Network Overview");
		addElementGroup("Areas");
		for (int i = 0; i < data.getCombinedBusCount(); i++) {
			addElementLink(data.getCombinedBus(i));
		}
		
		addElementGroup("Power Lines");
		for (int i = 0; i < data.getCombinedBranchCount(); i++) {
			addElementLink(data.getCombinedBranch(i));
		}
		finishLayout();
	}
}
