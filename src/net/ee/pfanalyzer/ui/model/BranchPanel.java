package net.ee.pfanalyzer.ui.model;

import net.ee.pfanalyzer.model.Branch;

public class BranchPanel extends ModelElementPanel {

	private Group flagGroup;

	public BranchPanel(ElementPanelController controller) {
		super(controller);
		
		flagGroup = addElementGroup("Flags");
	}

	public void setBranch(Branch data) {
		// remove old elements
		flagGroup.removeAll();
		// set title
		setTitle(data.getDisplayName());
		// show flags
		addFlags(data, flagGroup);
		finishLayout();
	}
}
