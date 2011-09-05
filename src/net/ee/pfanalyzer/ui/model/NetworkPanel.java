package net.ee.pfanalyzer.ui.model;

import net.ee.pfanalyzer.model.Branch;
import net.ee.pfanalyzer.model.Bus;
import net.ee.pfanalyzer.model.Generator;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.ui.util.Group;

public class NetworkPanel extends ModelElementPanel {

	private Network data;
	
	public NetworkPanel(ElementPanelController controller, Network data) {
		super(controller);
		this.data = data;
		setTitle("Network Overview");
		updateNetwork();
	}
	
	public void updateNetwork() {
		removeAllElements();
		if(data.getCombinedBusCount() > 0) { // show combined elements
			addElementGroup("Areas");
			for (int i = 0; i < data.getCombinedBusCount(); i++) {
				if(data.getCombinedBus(i).getBusNodes().size() == 1)
					addElementLink(data.getCombinedBus(i).getFirstBus());
				else
					addElementLink(data.getCombinedBus(i));
			}
			
			addElementGroup("Power Lines");
			for (int i = 0; i < data.getCombinedBranchCount(); i++) {
				if(data.getCombinedBranch(i).getBranches().size() == 1)
					addElementLink(data.getCombinedBranch(i).getFirstBranch());
				else
					addElementLink(data.getCombinedBranch(i));
			}
		} 
//		else { // show single elements
			Group group = new Group("Bus Nodes (no location data available)");
			for (Bus bus: data.getBusses()) {
				boolean added = false;
				for (int i = 0; i < data.getCombinedBusCount(); i++) {
					if(data.getCombinedBus(i).contains(bus)) {
						added = true;
						break;
					}
				}
				if(added == false)
					group.addElementLink(bus);
			}
			if(group.getComponentCount() > 0)
				addElementGroup(group);
			
			group = new Group("Branches (no location data available)");
			for (Branch branch: data.getBranches()) {
				boolean added = false;
				for (int i = 0; i < data.getCombinedBranchCount(); i++) {
					if(data.getCombinedBranch(i).contains(branch)) {
						added = true;
						break;
					}
				}
				if(added == false)
					group.addElementLink(branch);
			}
			if(group.getComponentCount() > 0)
				addElementGroup(group);
			
			addElementGroup("Generators");
			for (Generator generator: data.getGenerators()) {
				addElementLink(generator);
			}
//		}
		finishLayout();
	}
}
