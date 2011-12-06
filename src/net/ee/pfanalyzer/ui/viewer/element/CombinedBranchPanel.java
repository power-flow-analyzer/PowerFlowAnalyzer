package net.ee.pfanalyzer.ui.viewer.element;

import java.util.List;

import net.ee.pfanalyzer.model.CombinedBranch;
import net.ee.pfanalyzer.model.CombinedBus;
import net.ee.pfanalyzer.model.Network;

public class CombinedBranchPanel extends AbstractElementPanel {

	public CombinedBranchPanel(ElementViewer viewer, Network data) {
		super(viewer);
		
	}

	public void setCombinedBranch(CombinedBranch cbranch, List<CombinedBus> combinedBusList) {
		removeAllElements();
		// set title
		setTitle(cbranch.getLabel());
		addBranchElements(cbranch.getBranches(), combinedBusList);
		finishLayout();
	}
}
