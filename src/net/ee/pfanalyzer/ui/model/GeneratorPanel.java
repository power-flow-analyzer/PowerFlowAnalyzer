package net.ee.pfanalyzer.ui.model;

import net.ee.pfanalyzer.model.Generator;

public class GeneratorPanel extends ModelElementPanel {

	private Group flagGroup;

	public GeneratorPanel(ElementPanelController controller) {
		super(controller);
		
		flagGroup = addElementGroup("Flags");
	}

	public void setGenerator(Generator data) {
		// remove old elements
		flagGroup.removeAll();
		// set title
		setTitle(data.getDisplayName());
		// show flags
		addFlags(data, flagGroup);
		finishLayout();
	}
}
