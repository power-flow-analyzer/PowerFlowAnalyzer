package net.ee.pfanalyzer.ui.viewer.element;

import java.util.List;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.Branch;
import net.ee.pfanalyzer.model.Bus;
import net.ee.pfanalyzer.model.CombinedBranch;
import net.ee.pfanalyzer.model.CombinedBus;
import net.ee.pfanalyzer.model.ElementList;
import net.ee.pfanalyzer.model.util.ElementGroupingUtils;


public abstract class AbstractElementPanel extends ModelElementPanel {

	public AbstractElementPanel(ElementViewer viewer) {
		super(viewer);
	}
	
	protected void addBusGroup(List<CombinedBus> combinedBusses, String title) {
		addElementGroup(title + " (" + combinedBusses.size() + " groups)");
		for (CombinedBus cbus : combinedBusses) {
			addElementLink(cbus, AbstractNetworkElement.DISPLAY_DEFAULT);
		}
	}
	
	protected void addBranchGroup(List<CombinedBranch> combinedBranches, String title) {
		addElementGroup(title + " (" + combinedBranches.size() + " groups)");
		for (CombinedBranch cbranch : combinedBranches) {
			addElementLink(cbranch, AbstractNetworkElement.DISPLAY_DEFAULT);
		}
	}
	
	protected void addElementGroup(List<ElementList> combinedElements, String title) {
		addElementGroup(title + " (" + combinedElements.size() + " groups)");
		for (ElementList list : combinedElements) {
			addElementLink(list, AbstractNetworkElement.DISPLAY_DEFAULT);
		}
	}
	
	protected void addBusElements(CombinedBus cbus) {
		addBusElements(cbus.getBusNodes());
	}
	
	protected void addBusElements(List<Bus> busList) {
		List<CombinedBus> combinedBusses;
		if( getElementViewer().groupBusByArea && 
				(combinedBusses = ElementGroupingUtils.getCombinedBussesByParameter(
				busList, getElementViewer().viewerAreaParameter)).size() > 1) {
			addBusGroup(combinedBusses, "Busses per " + getElementViewer().viewerAreaLabel);
		} else if( getElementViewer().groupBusByLocation &&
				(combinedBusses = ElementGroupingUtils.getCombinedBussesByCoordinates(
						busList)).size() > 1) {
			addBusGroup(combinedBusses, "Busses per Location");
		} else if(busList.size() > 0) {
			addElementGroup("Bus Overview (" + busList.size() + " busses)");
			for (Bus bus : busList) {
				addElementLink(bus, AbstractNetworkElement.DISPLAY_DEFAULT);
			}
		}
	}
	
	protected void addBranchElements(List<Branch> branchList, List<CombinedBus> combinedBusList) {
		List<CombinedBranch> combinedBranches;
		if( getElementViewer().groupBranchByArea &&
				(combinedBranches = ElementGroupingUtils.getCombinedBranchesByParameter(
				branchList, combinedBusList, getElementViewer().viewerAreaParameter)).size() > 1) {
			addBranchGroup(combinedBranches, "Branches per " + getElementViewer().viewerAreaLabel);
			combinedBranches = ElementGroupingUtils.getCombinedTieLines(
					branchList, combinedBusList, getElementViewer().viewerAreaParameter);
			if(combinedBranches.size() > 0) {
				addBranchGroup(combinedBranches, "Tie lines");
			}
		} else if( getElementViewer().groupBranchByLocation &&
				(combinedBranches = ElementGroupingUtils.getCombinedBranchesByCoordinates(
				branchList, combinedBusList)).size() > 1 ) {
			addBranchGroup(combinedBranches, "Branches per Location");
		} else if( getElementViewer().groupBranchByVoltage &&
				(combinedBranches = ElementGroupingUtils.getCombinedBranchesByParameter(
				branchList, combinedBusList, "BASE_KV")).size() > 1 ) {
			addBranchGroup(combinedBranches, "Branches per Voltage");
		} else if(branchList.size() > 0) {
			addElementGroup("Branch Overview (" + branchList.size() + " branches)");
			for (Branch branch : branchList) {
				addElementLink(branch, AbstractNetworkElement.DISPLAY_DEFAULT);
			}
		}
	}
	
	protected void addElements(List<AbstractNetworkElement> elements, String typeLabel) {
		List<ElementList> list;
		if( getElementViewer().groupElementByArea &&
				(list = ElementGroupingUtils.getCombinedElementsByParameter(
				elements, getElementViewer().viewerAreaParameter, typeLabel)).size() > 1) {
			addElementGroup(list, typeLabel + " per " + getElementViewer().viewerAreaLabel);
		} else if( getElementViewer().groupElementByLocation &&
				(list = ElementGroupingUtils.getCombinedElementsByCoordinates(
				elements, typeLabel)).size() > 1 ) {
			addElementGroup(list, typeLabel + " per Location");
		} else if(elements.size() > 0) {
			addElementGroup(typeLabel + " (" + elements.size() + " elements)");
			for (AbstractNetworkElement element : elements) {
				addElementLink(element, AbstractNetworkElement.DISPLAY_DEFAULT);
			}
		}
	}
}
