package net.ee.pfanalyzer.ui.viewer.element;

import java.util.List;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.Branch;
import net.ee.pfanalyzer.model.Bus;
import net.ee.pfanalyzer.model.CombinedBranch;
import net.ee.pfanalyzer.model.CombinedBus;
import net.ee.pfanalyzer.model.CombinedNetworkElement;
import net.ee.pfanalyzer.model.ElementList;
import net.ee.pfanalyzer.model.util.ElementGroupingUtils;


public abstract class AbstractElementPanel extends ModelElementPanel {

	public AbstractElementPanel(ElementViewer viewer) {
		super(viewer);
	}
	
	protected void addBusGroup(List<CombinedBus> combinedBusses, String title) {
		addElementGroup(title + " (" + combinedBusses.size() + " groups)");
		CombinedBus ungrouped = null;
		for (CombinedBus cbus : combinedBusses) {
			if(cbus.isUngrouped())
				ungrouped = cbus;
			else
				addElementLink(cbus, AbstractNetworkElement.DISPLAY_DEFAULT);
		}
		if(ungrouped != null) {
			addElementGroup("Ungrouped busses");
			addElementLink(ungrouped, AbstractNetworkElement.DISPLAY_DEFAULT);
		}
	}
	
	protected void addBranchGroup(List<CombinedBranch> combinedBranches, String title) {
		addElementGroup(title + " (" + combinedBranches.size() + " groups)");
//		CombinedBranch ungrouped = null;
		for (CombinedBranch cbranch : combinedBranches) {
//			if(cbranch.isUngrouped())
//				ungrouped = cbranch;
//			else
				addElementLink(cbranch, AbstractNetworkElement.DISPLAY_DEFAULT);
		}
//		if(ungrouped != null) {
//			addElementGroup("Ungrouped branches");
//			addElementLink(ungrouped, AbstractNetworkElement.DISPLAY_DEFAULT);
//		}
	}
	
	protected void addElementGroup(List<ElementList> combinedElements, String title) {
		addElementGroup(title + " (" + combinedElements.size() + " groups)");
		ElementList ungrouped = null;
		for (ElementList list : combinedElements) {
			if(list.isUngrouped())
				ungrouped = list;
			else
				addElementLink(list, AbstractNetworkElement.DISPLAY_DEFAULT);
		}
		if(ungrouped != null) {
			addElementGroup("Ungrouped elements");
			addElementLink(ungrouped, AbstractNetworkElement.DISPLAY_DEFAULT);
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
						busList, true)).size() > 1) {
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
				branchList, getElementViewer().viewerAreaParameter)).size() > 1) {
			CombinedBranch ungrouped = removeUngrouped(combinedBranches);
			addBranchGroup(combinedBranches, "Branches per " + getElementViewer().viewerAreaLabel);
			addUngrouped(ungrouped);
			combinedBranches = ElementGroupingUtils.getCombinedTieLines(
					branchList, getElementViewer().viewerAreaParameter);
			if(combinedBranches.size() > 0) {
				// should be the same ungrouped branches as before
				removeUngrouped(combinedBranches);
				addBranchGroup(combinedBranches, "Tie lines");
			}
		} else if( getElementViewer().groupBranchByLocation &&
				(combinedBranches = ElementGroupingUtils.getCombinedBranchesByCoordinates(
				branchList, combinedBusList)).size() > 1 ) {
			CombinedBranch ungrouped = removeUngrouped(combinedBranches);
			addBranchGroup(combinedBranches, "Branches per Location");
			addUngrouped(ungrouped);
		} else if( getElementViewer().groupBranchByVoltage &&
				(combinedBranches = ElementGroupingUtils.getCombinedBranches(
				branchList, true, "BASE_KV")).size() > 1 ) {
			CombinedBranch ungrouped = removeUngrouped(combinedBranches);
			addBranchGroup(combinedBranches, "Branches per Voltage");
			addUngrouped(ungrouped);
			// add branches with differing voltage -> transformers
			combinedBranches = ElementGroupingUtils.getCombinedBranches(branchList, false, "BASE_KV");
			if(combinedBranches.size() > 0) {
				// should be the same ungrouped branches as before
				removeUngrouped(combinedBranches);
				addBranchGroup(combinedBranches, "Transformers");
			}
		} else if(branchList.size() > 0) {
			addElementGroup("Branch Overview (" + branchList.size() + " branches)");
			for (Branch branch : branchList) {
				addElementLink(branch, AbstractNetworkElement.DISPLAY_DEFAULT);
			}
		}
	}
	
	private CombinedBranch removeUngrouped(List<CombinedBranch> combinedBranches) {
		for (int i = 0; i < combinedBranches.size(); i++) {
			if(combinedBranches.get(i).isUngrouped())
				return combinedBranches.remove(i);
		}
		return null;
	}
	
	private void addUngrouped(CombinedNetworkElement<?> ungrouped) {
		if(ungrouped != null) {
			addElementGroup("Ungrouped elements");
			addElementLink(ungrouped, AbstractNetworkElement.DISPLAY_DEFAULT);
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
