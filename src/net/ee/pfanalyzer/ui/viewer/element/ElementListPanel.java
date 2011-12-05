package net.ee.pfanalyzer.ui.viewer.element;

import net.ee.pfanalyzer.model.ElementList;
import net.ee.pfanalyzer.model.Network;

public class ElementListPanel extends AbstractElementPanel {

	public ElementListPanel(ElementViewer viewer, Network data) {
		super(viewer);
	}

	public void setElementList(ElementList list) {
		removeAllElements();
		// set title
		setTitle(list.getLabel());
		addElements(list.getNetworkElements(), list.getTypeLabel());
		finishLayout();
	}
}
