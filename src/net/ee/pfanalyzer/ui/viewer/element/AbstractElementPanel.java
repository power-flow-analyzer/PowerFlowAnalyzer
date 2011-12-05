package net.ee.pfanalyzer.ui.viewer.element;

import java.util.List;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.Branch;
import net.ee.pfanalyzer.model.Bus;
import net.ee.pfanalyzer.model.CombinedBranch;
import net.ee.pfanalyzer.model.CombinedBus;
import net.ee.pfanalyzer.model.util.ElementGroupingUtils;


public abstract class AbstractElementPanel extends ModelElementPanel {

	public AbstractElementPanel(ElementViewer viewer) {
		super(viewer);
	}
	
	protected void addBusGroup(List<CombinedBus> combinedBusses, String title) {
		addElementGroup(title + " (" + combinedBusses.size() + " groups)");
		for (CombinedBus cbus : combinedBusses) {
			addElementLink(cbus);
		}
	}
	
	protected void addBranchGroup(List<CombinedBranch> combinedBranches, String title) {
		addElementGroup(title + " (" + combinedBranches.size() + " groups)");
		for (CombinedBranch cbranch : combinedBranches) {
			addElementLink(cbranch);
		}
	}
	
	protected void addBusElements(CombinedBus cbus) {
		addBusElements(cbus.getBusNodes());
	}
	
	protected void addBusElements(List<Bus> busList) {
		List<CombinedBus> combinedBusses = ElementGroupingUtils.getCombinedBussesByParameter(
				busList, "BUS_AREA");
		if(combinedBusses.size() > 1) {
			addBusGroup(combinedBusses, "Busses per Area");
		} else if( (combinedBusses = ElementGroupingUtils.getCombinedBussesByCoordinates(
				busList)).size() > 1) {
			addBusGroup(combinedBusses, "Busses per Location");
		} else if(busList.size() > 0) {
			addElementGroup("Bus Overview");
			for (Bus bus : busList) {
				addElementLink(bus, AbstractNetworkElement.DISPLAY_DEFAULT);
			}
		}
	}
	
	protected void addBranchElements(List<Branch> branchList, List<CombinedBus> combinedBusList) {
		List<CombinedBranch> combinedBranches = ElementGroupingUtils.getCombinedBranchesByParameter(
				branchList, combinedBusList, "BUS_AREA");
		if(combinedBranches.size() > 1) {
			addBranchGroup(combinedBranches, "Branches per Area");
			combinedBranches = ElementGroupingUtils.getCombinedTieLines(
					branchList, combinedBusList, "BUS_AREA");
			if(combinedBranches.size() > 0) {
				addBranchGroup(combinedBranches, "Tie lines");
			}
		} else if( (combinedBranches = ElementGroupingUtils.getCombinedBranchesByCoordinates(
				branchList, combinedBusList)).size() > 1 ) {
			addBranchGroup(combinedBranches, "Branches per Location");
		} else if( (combinedBranches = ElementGroupingUtils.getCombinedBranchesByParameter(
				branchList, combinedBusList, "BASE_KV")).size() > 1 ) {
			addBranchGroup(combinedBranches, "Branches per Voltage");
		} else if(branchList.size() > 0) {
			addElementGroup("Branch Overview");
			for (Branch branch : branchList) {
				addElementLink(branch, AbstractNetworkElement.DISPLAY_DEFAULT);
			}
		}
	}
}
