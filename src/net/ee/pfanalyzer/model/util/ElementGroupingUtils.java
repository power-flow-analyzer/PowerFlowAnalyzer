package net.ee.pfanalyzer.model.util;

import java.util.ArrayList;
import java.util.List;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.Branch;
import net.ee.pfanalyzer.model.Bus;
import net.ee.pfanalyzer.model.CombinedBranch;
import net.ee.pfanalyzer.model.CombinedBus;
import net.ee.pfanalyzer.model.data.NetworkParameter;

public class ElementGroupingUtils {

//	private final static String paramName = "BUS_AREA";//TSO
	
	public static CombinedBus getCombinedBus(List<CombinedBus> combinedBusList, AbstractNetworkElement data) {
		for (CombinedBus cbus : combinedBusList) {
			if(cbus.contains(data))
				return cbus;
		}
		return null;
	}
	
	public static List<CombinedBus> getCombinedBussesByCoordinates(List<Bus> busses) {
		List<CombinedBus> combinedBusList = new ArrayList<CombinedBus>();
		for (int i = 0; i < busses.size(); i++) {
			Bus bus = busses.get(i);
			double longitude = bus.getLongitude();
			double latitude = bus.getLatitude();
			if(Double.isNaN(latitude) || Double.isNaN(longitude)) // no coords set
				continue;
			boolean found = false;
//			System.out.println("Bus " + i);
			for (CombinedBus cbus : combinedBusList) {
				if(cbus.hasLongitude(longitude) && cbus.hasLatitude(latitude)) {
//					System.out.println("  added to combined bus " + cbus.getIndex());
					cbus.addBus(bus);
					found = true;
					break;
				}
			}
			if(found == false) {
				CombinedBus cbus = new CombinedBus(bus, longitude, latitude);
				cbus.setIndex(combinedBusList.size());
				combinedBusList.add(cbus);
//				System.out.println("  created new combined bus " + cbus.getIndex());
			}
		}
//		int busCount = 0;
//		for (CombinedBus cbus : combinedBusList) {
//			busCount += cbus.getNetworkElementCount();
//		}
//		System.out.println(combinedBusList.size() + " combined bus nodes found with " + busCount + " elements");
		return combinedBusList;
	}
	
//	public static List<CombinedNetworkElement<AbstractNetworkElement>> getCombinedElementsByCoordinates(List<AbstractNetworkElement> busses) {
//		List<CombinedBus> combinedBusList = new ArrayList<CombinedBus>();
//		for (int i = 0; i < busses.size(); i++) {
//			AbstractNetworkElement bus = busses.get(i);
//			double longitude = bus.getLongitude();
//			double latitude = bus.getLatitude();
//			if(Double.isNaN(latitude) || Double.isNaN(longitude)) // no coords set
//				continue;
//			boolean found = false;
////			System.out.println("Bus " + i);
//			for (CombinedBus cbus : combinedBusList) {
//				if(cbus.hasLongitude(longitude) && cbus.hasLatitude(latitude)) {
////					System.out.println("  added to combined bus " + cbus.getIndex());
//					cbus.addBus(bus);
//					found = true;
//					break;
//				}
//			}
//			if(found == false) {
//				CombinedNetworkElement cbus = new CombinedNetworkElement();
//				cbus.setIndex(combinedBusList.size());
//				combinedBusList.add(cbus);
////				System.out.println("  created new combined bus " + cbus.getIndex());
//			}
//		}
////		int busCount = 0;
////		for (CombinedBus cbus : combinedBusList) {
////			busCount += cbus.getNetworkElementCount();
////		}
////		System.out.println(combinedBusList.size() + " combined bus nodes found with " + busCount + " elements");
//		return combinedBusList;
//	}
	
	public static List<CombinedBranch> getCombinedBranchesByCoordinates(
			List<Branch> branches, List<CombinedBus> combinedBusList) {
		List<CombinedBranch> combinedBranchList = new ArrayList<CombinedBranch>();
		for (int i = 0; i < branches.size(); i++) {
			Branch branch = branches.get(i);
			branch.setInverted(false);
			Bus fromBus = branch.getFromBus();
			Bus toBus = branch.getToBus();
			CombinedBus cFromBus = getCombinedBus(combinedBusList, fromBus);
			CombinedBus cToBus = getCombinedBus(combinedBusList, toBus);
			if(cFromBus == null || cToBus == null)
				continue;
//			if(cFromBus == cToBus) {// combined start and end busses are the same -> branch is a transformer!
//				cFromBus.addTransformer(new Transformer(branch));// TODO Transformers raus?
//				continue;
//			}
//			System.out.println("Branch " + i);
//			System.out.println("  from bus: " + fromBus.getIndex() + "(" + fromBusIndex + ")");
//			System.out.println("    combined bus: " + cFromBus.getIndex());
//			System.out.println("  to bus  : " + toBus.getIndex() + "(" + toBusIndex + ")");
//			System.out.println("    combined bus: " + cToBus.getIndex());
			boolean found = false;
			for (CombinedBranch cbranch : combinedBranchList) {
				// check if from and to bus are inverted
				if(cbranch.hasBusNodes(cToBus, cFromBus)) {
					branch.setInverted(true);
					CombinedBus tempBus = cFromBus;
					cFromBus = cToBus;
					cToBus = tempBus;
					// branch will be added in the following if block
				}
				if(cbranch.hasBusNodes(cFromBus, cToBus)) {
//					System.out.println("  added to combined branch " + cbranch.getIndex());
					cbranch.addBranch(branch);
					found = true;
					break;
				}
			}
			if(found == false) {
				CombinedBranch cbranch = new CombinedBranch(cFromBus, cToBus, branch);
				cbranch.setIndex(combinedBranchList.size());
				combinedBranchList.add(cbranch);
//				System.out.println("  created new combined branch " + cbranch.getIndex());
			}
		}
//		System.out.println(combinedBranchList.size() + " combined branches found");
		return combinedBranchList;
	}
	
	public static List<CombinedBus> getCombinedBussesByParameter(List<Bus> busList, final String paramName) {
		List<CombinedBus> list = new ArrayList<CombinedBus>();
		for (final Bus bus : busList) {
			boolean added = false;
			String paramValue = getParameterValue(bus, paramName);
			if(paramValue != null && paramValue.length() > 0) {
				for (CombinedBus cbus : list) {
					String addedValue = getParameterValue(cbus.getFirstBus(), paramName);
					if(paramValue.equals(addedValue)) {
						cbus.addBus(bus);
						added = true;
						break;
					}
				}
				if(added == false) {
					CombinedBus cbus = new CombinedBus(bus, bus.getLongitude(), bus.getLatitude()) {
						@Override
						public String getLabel() {
							String count = getBusNodes().size() + "";
							String displayValue = bus.getParameterDisplayValue(paramName);
							if(isNumber(displayValue))
								return "Area " + displayValue + " (" + count + " busses)";
							else
								return displayValue + " (" + count + " busses)";
						}
					};
					list.add(cbus);
				}
			}
		}
//		Collections.sort(list, new LabelSorter());
		return list;
	}
		
	private static boolean isNumber(String text) {
		try {
			Integer.parseInt(text);
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	public static List<CombinedBranch> getCombinedBranchesByParameter(
			List<Branch> branchList, List<CombinedBus> combinedBusList, final String paramName) {
		return getCombinedBranches(branchList, combinedBusList, true, paramName);
	}
	
	public static List<CombinedBranch> getCombinedTieLines(
			List<Branch> branchList, List<CombinedBus> combinedBusList, final String paramName) {
		return getCombinedBranches(branchList, combinedBusList, false, paramName);
	}
	
	private static List<CombinedBranch> getCombinedBranches(
			List<Branch> branchList, List<CombinedBus> combinedBusList, final boolean sameArea, final String paramName) {
		List<CombinedBranch> list = new ArrayList<CombinedBranch>();
		for (final Branch branch : branchList) {
			boolean added = false;
			final Bus fromBus = branch.getFromBus();
			final Bus toBus = branch.getToBus();
//			if(fromBus == toBus)
//				continue;
//			System.out.println("checking branch " + branch.getFromBusNumber());
//			System.out.println("     " + branch.getToBusNumber());
			CombinedBus cFromBus = getCombinedBus(combinedBusList, fromBus);
			CombinedBus cToBus = getCombinedBus(combinedBusList, toBus);
			if(cFromBus == null || cToBus == null)
				continue;
			String fromParamValue = getParameterValue(fromBus, paramName);
			String toParamValue = getParameterValue(toBus, paramName);
//			System.out.println("combined busses: " + fromParamValue + ", " + toParamValue);
			if(fromParamValue != null && fromParamValue.length() > 0
					&& toParamValue != null && toParamValue.length() > 0) {
				if(sameArea) {
					if(fromParamValue.equals(toParamValue) == false)
						continue;
				} else { // not the same area -> tie line
					if(fromParamValue.equals(toParamValue))
						continue;
				}
				for (CombinedBranch cbranch : list) {
					Bus addedFromBus = cbranch.getFromBus().getFirstBus();
					Bus addedToBus = cbranch.getToBus().getFirstBus();
					String addedFromParamValue = getParameterValue(addedFromBus, paramName);
					String addedToParamValue = getParameterValue(addedToBus, paramName);
//					System.out.println("checking");
//					System.out.println("   " + addedFromParamValue + " with " + fromParamValue);
//					System.out.println("   " + addedToParamValue + " with " + toParamValue);
					if((fromParamValue.equals(addedFromParamValue) 
							&& toParamValue.equals(addedToParamValue)) ||
							(fromParamValue.equals(addedToParamValue) 
									&& toParamValue.equals(addedFromParamValue))) {
						cbranch.addBranch(branch);
						added = true;
						break;
					}
				}
				if(added == false) {
					CombinedBranch cbranch = new CombinedBranch(cFromBus, cToBus, branch) {
						@Override
						public String getLabel() {
							String count = " (" + getBranchCount() + " branches)";
							String label = "";
							String fromDisplayValue = fromBus.getParameterDisplayValue(paramName);
							if(isNumber(fromDisplayValue))
								label += fromDisplayValue + " kV";
							else
								label += fromDisplayValue;
							if(sameArea == false) {
								label += " - ";
								String toDisplayValue = toBus.getParameterDisplayValue(paramName);
								if(isNumber(toDisplayValue))
									label += toDisplayValue + " kV";
								else
									label += toDisplayValue;
							}
							label += count;
							return label;
						}
					};
					list.add(cbranch);
				}
			}
		}
//		System.out.println(list.size() + " combined branches found");
//		Collections.sort(list, new LabelSorter());
		return list;
	}
	
	private static String getParameterValue(Bus bus, String paramName) {
		NetworkParameter paramValue = bus.getParameterValue(paramName);
		if(paramValue != null && paramValue.getValue() != null) {
			NetworkParameter paramDef = bus.getParameterDefinition(paramName);
			if(paramDef != null)
				return ParameterUtils.getNormalizedParameterValue(paramDef, paramValue.getValue());
			else
				return paramValue.getValue();
		} else
			return null;
	}
}
