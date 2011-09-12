package net.ee.pfanalyzer.ui.model;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.Generator;

public class GeneratorPanel extends ModelElementPanel {

	public GeneratorPanel(ElementPanelController controller) {
		super(controller);
	}

	public void setGenerator(Generator data) {
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
