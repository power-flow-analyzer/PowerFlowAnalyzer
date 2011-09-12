package net.ee.pfanalyzer.ui.model;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.Bus;

public class BusPanel extends ModelElementPanel {

	public BusPanel(ElementPanelController controller) {
		super(controller);
	}

	public void setBus(Bus data) {
		// remove old elements
		removeAllElements();
		// set title
		String title = data.getDisplayName(AbstractNetworkElement.DISPLAY_DEFAULT);
		if(data.getName() != null)
			title += " (" + data.getName() + ")";
		setTitle(title);
		// show properties
		addParameters(data);
		// show flags
		addFlags(data);
		finishLayout();
	}
}
