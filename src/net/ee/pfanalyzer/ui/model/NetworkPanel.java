package net.ee.pfanalyzer.ui.model;

import net.ee.pfanalyzer.model.Branch;
import net.ee.pfanalyzer.model.Bus;
import net.ee.pfanalyzer.model.Generator;
import net.ee.pfanalyzer.model.Network;

public class NetworkPanel extends ModelElementPanel {

	public NetworkPanel(ElementPanelController controller, Network data) {
		super(controller);
		
		setTitle("Network Overview");
		
		if(data.getCombinedBusCount() > 0) { // show combined elements
			addElementGroup("Areas");
			for (int i = 0; i < data.getCombinedBusCount(); i++) {
				addElementLink(data.getCombinedBus(i));
			}
			
			addElementGroup("Power Lines");
			for (int i = 0; i < data.getCombinedBranchCount(); i++) {
				addElementLink(data.getCombinedBranch(i));
			}
		} else { // show single elements
			addElementGroup("Bus Nodes");
			for (Bus bus: data.getBusses()) {
				addElementLink(bus);
			}
			
			addElementGroup("Branches");
			for (Branch branch: data.getBranches()) {
				addElementLink(branch);
			}
			
			addElementGroup("Generators");
			for (Generator generator: data.getGenerators()) {
				addElementLink(generator);
			}
		}
		finishLayout();
	}
}
