package net.ee.pfanalyzer.ui.model;

import net.ee.pfanalyzer.model.Bus;

public class BusPanel extends ModelElementPanel {

	private Group propGroup, flagGroup;

	public BusPanel(ElementPanelController controller) {
		super(controller);
		
		propGroup = addElementGroup("");
		flagGroup = addElementGroup("Flags");
	}

	public void setBus(Bus data) {
		// remove old elements
		propGroup.removeAll();
		flagGroup.removeFlags();
		// set title
		String title = data.getDisplayName();
		if(data.getName() != null)
			title += " (" + data.getName() + ")";
		setTitle(title);
		// show properties
//		addProperties(data, propGroup);
		// show flags
		addFlags(data, flagGroup);
		finishLayout();
	}
}
