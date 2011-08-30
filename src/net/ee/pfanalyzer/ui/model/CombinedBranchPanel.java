package net.ee.pfanalyzer.ui.model;

import net.ee.pfanalyzer.model.CombinedBranch;
import net.ee.pfanalyzer.model.Network;

public class CombinedBranchPanel extends ModelElementPanel {

	public CombinedBranchPanel(ElementPanelController controller, Network data) {
		super(controller);
		
	}

	public void setCombinedBranch(CombinedBranch data) {
		// remove old elements
		removeAllElements();
		// set title
		setTitle("Power Lines " + data.getLabel());
		// add branches
		addElementGroup("Branches");
		for (int i = 0; i < data.getNetworkElementCount(); i++) {
			addElementLink(data.getNetworkElement(i));
		}
		finishLayout();
	}
}
