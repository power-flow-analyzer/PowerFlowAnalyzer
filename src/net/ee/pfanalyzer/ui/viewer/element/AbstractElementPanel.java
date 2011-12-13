package net.ee.pfanalyzer.ui.viewer.element;

import java.text.DecimalFormat;
import java.util.List;

import javax.swing.Box;
import javax.swing.JLabel;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.Branch;
import net.ee.pfanalyzer.model.Bus;
import net.ee.pfanalyzer.model.CombinedBranch;
import net.ee.pfanalyzer.model.CombinedBus;
import net.ee.pfanalyzer.model.CombinedNetworkElement;
import net.ee.pfanalyzer.model.ElementList;
import net.ee.pfanalyzer.model.util.ElementGroupingUtils;
import net.ee.pfanalyzer.ui.util.Group;


public abstract class AbstractElementPanel extends ModelElementPanel {

	private final static DecimalFormat format = new DecimalFormat("#,###.#");
	
	protected final static ElementAttributes BUS_ATTRIBUTES = new ElementAttributes(
			new String[] { "PD", "QD" }, 
			new String[] { "\u2211 Real Power Demand:", "\u2211 Reactive Power Demand:" }, 
			new String[] { "MW", "MVAr" });
	protected final static ElementAttributes GENERATOR_ATTRIBUTES = new ElementAttributes(
			new String[] { "PG", "QG" }, 
			new String[] { "\u2211 Real Power Output:", "\u2211 Reactive Power Output:" }, 
			new String[] { "MW", "MVAr" });
	protected final static ElementAttributes LOAD_ATTRIBUTES = new ElementAttributes(
			new String[] { "PD", "QD" }, 
			new String[] { "\u2211 Real Power Demand:", "\u2211 Reactive Power Demand:" }, 
			new String[] { "MW", "MVAr" });
	
	private boolean showSumsOfValues;
	
	public AbstractElementPanel(ElementViewer viewer) {
		super(viewer);
	}
	
	protected void addBusGroup(List<CombinedBus> combinedBusses, String title) {
		Group group = addElementGroup(title + " (" + combinedBusses.size() + " groups)");
		CombinedBus ungrouped = null;
		ElementSumCalculator calculator = new ElementSumCalculator(group, 
				isShowSumsOfValues() ? BUS_ATTRIBUTES : null);
		for (CombinedBus cbus : combinedBusses) {
			if(cbus.isUngrouped())
				ungrouped = cbus;
			else {
				addElementLink(cbus, AbstractNetworkElement.DISPLAY_DEFAULT);
				calculator.addElement(cbus);
			}
		}
		calculator.finishCalculation();
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
	
	protected void addElementGroup(List<ElementList> combinedElements, String title, ElementAttributes attributes) {
		Group group = addElementGroup(title + " (" + combinedElements.size() + " groups)");
		ElementList ungrouped = null;
		ElementSumCalculator calculator = new ElementSumCalculator(group, 
				isShowSumsOfValues() ? attributes : null);
		for (ElementList list : combinedElements) {
			if(list.isUngrouped())
				ungrouped = list;
			else {
				addElementLink(list, AbstractNetworkElement.DISPLAY_DEFAULT);
				calculator.addElement(list);
			}
		}
		calculator.finishCalculation();
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
				busList, getElementViewer().viewerAreaParameter, "Area ", "")).size() > 1) {
			addBusGroup(combinedBusses, "Busses per " + getElementViewer().viewerAreaLabel);
		} else if( getElementViewer().groupBusByLocation &&
				(combinedBusses = ElementGroupingUtils.getCombinedBussesByCoordinates(
						busList, true)).size() > 1) {
			addBusGroup(combinedBusses, "Busses per Location");
		} else if(busList.size() > 0) {
			Group group = addElementGroup("Bus Overview (" + busList.size() + " busses)");
			ElementSumCalculator calculator = new ElementSumCalculator(group, 
					isShowSumsOfValues() ? BUS_ATTRIBUTES : null);
			for (Bus bus : busList) {
				addElementLink(bus, AbstractNetworkElement.DISPLAY_DEFAULT);
				calculator.addElement(bus);
			}
			calculator.finishCalculation();
		}
	}
	
	protected void addBranchElements(List<Branch> branchList, List<CombinedBus> combinedBusList) {
		List<CombinedBranch> combinedBranches;
		if( getElementViewer().groupBranchByArea &&
				(combinedBranches = ElementGroupingUtils.getCombinedBranchesByParameter(
				branchList, getElementViewer().viewerAreaParameter, "Area ", "")).size() > 1) {
			CombinedBranch ungrouped = removeUngrouped(combinedBranches);
			addBranchGroup(combinedBranches, "Branches per " + getElementViewer().viewerAreaLabel);
			addUngrouped(ungrouped);
			combinedBranches = ElementGroupingUtils.getCombinedTieLines(
					branchList, getElementViewer().viewerAreaParameter, "Area ", "");
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
				branchList, true, "BASE_KV", "", " kV")).size() > 1 ) {
			CombinedBranch ungrouped = removeUngrouped(combinedBranches);
			addBranchGroup(combinedBranches, "Branches per Voltage");
			addUngrouped(ungrouped);
			// add branches with differing voltage -> transformers
			combinedBranches = ElementGroupingUtils.getCombinedBranches(branchList, false, "BASE_KV", "", " kV");
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
	
	protected void addElements(List<AbstractNetworkElement> elements, String typeLabel, 
			ElementAttributes attributes) {
		List<ElementList> list;
		if( getElementViewer().groupElementByArea &&
				(list = ElementGroupingUtils.getCombinedElementsByParameter(
				elements, getElementViewer().viewerAreaParameter, typeLabel, attributes, "Area ", "")).size() > 1) {
			addElementGroup(list, typeLabel + " per " + getElementViewer().viewerAreaLabel, attributes);
		} else if( getElementViewer().groupElementByLocation &&
				(list = ElementGroupingUtils.getCombinedElementsByCoordinates(
				elements, typeLabel, attributes)).size() > 1 ) {
			addElementGroup(list, typeLabel + " per Location", attributes);
		} else if(elements.size() > 0) {
			Group group = addElementGroup(typeLabel + " (" + elements.size() + " elements)");
			ElementSumCalculator calculator = new ElementSumCalculator(group, 
					isShowSumsOfValues() ? attributes : null);
			for (AbstractNetworkElement element : elements) {
				addElementLink(element, AbstractNetworkElement.DISPLAY_DEFAULT);
				calculator.addElement(element);
			}
			calculator.finishCalculation();
		}
	}
	
	public boolean isShowSumsOfValues() {
		return showSumsOfValues;
	}

	public void setShowSumsOfValues(boolean showSumsOfValues) {
		this.showSumsOfValues = showSumsOfValues;
	}

	class ElementSumCalculator {
		
		private Group parent;
		private ElementAttributes attributes;
		private double[] values;
		
		ElementSumCalculator(Group parent, ElementAttributes attributes) {
			this.parent = parent;
			this.attributes = attributes;
			if(attributes != null)
				values = new double[attributes.getAttributesCount()];
		}
		
		public void finishCalculation() {
			if(attributes == null)
				return;
			JLabel label = null;
			int visibleParams = 0;
			for (int i = 0; i < attributes.getAttributesCount(); i++) {
				if(values[i] == 0)
					continue;
				else {
					label = new JLabel(attributes.getLabels()[i] + " " 
							+ format.format(values[i]) + " " + attributes.getUnits()[i]);
					parent.add(label, visibleParams);
					visibleParams++;
				}
			}
			if(visibleParams > 0) {
				if(visibleParams % 2 == 0) {
					parent.add(Box.createVerticalStrut(5), visibleParams);
					parent.add(Box.createVerticalStrut(5), visibleParams);
				} else {
					parent.add(Box.createVerticalStrut(5), visibleParams);
					parent.add(Box.createVerticalStrut(5), visibleParams);
					parent.add(Box.createVerticalStrut(5), visibleParams);
				}
			}
		}
		
		public void addElement(AbstractNetworkElement element) {
			if(attributes == null)
				return;
			for (int i = 0; i < attributes.getAttributesCount(); i++) {
				values[i] += element.getDoubleParameter(attributes.getParameterIDs()[i], 0);
			}
		}
		
		public void addElement(CombinedNetworkElement<?> element) {
			if(attributes == null)
				return;
			for (int i = 0; i < attributes.getAttributesCount(); i++) {
				values[i] += element.getSumOfParameters(attributes.getParameterIDs()[i]);
			}
		}
	}
}
