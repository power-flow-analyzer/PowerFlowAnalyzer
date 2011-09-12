package net.ee.pfanalyzer.ui.model;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.Branch;

public class BranchPanel extends ModelElementPanel {

	public BranchPanel(ElementPanelController controller) {
		super(controller);
	}

	public void setBranch(Branch data) {
		// remove old elements
		removeAllElements();
		// set title
		setTitle(data.getDisplayName(AbstractNetworkElement.DISPLAY_DEFAULT));
		// show properties
		addParameters(data);
		// show flags
		addFlags(data);
		finishLayout();
	}
}
