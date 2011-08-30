package net.ee.pfanalyzer.model;

import net.ee.pfanalyzer.model.data.AbstractNetworkElementData;
import net.ee.pfanalyzer.model.matpower.IBranchDataConstants;


public class Branch extends AbstractNetworkElement implements IBranchDataConstants {
	
	private Bus fromBus, toBus;
	
	public Branch(Network data, int index) {
		super(data, index);
	}
	
	public Branch(Network data, AbstractNetworkElementData elementData, int index) {
		super(data, elementData, index);
	}
	
	@Override
	public String getDefaultModelID() {
		return ModelDB.DEFAULT_BRANCH_MODEL;
	}
	
	public int getInitialBranchStatus() {
		return getIntParameter(PROPERTY_INITIAL_BRANCH_STATUS, -1);
//		return (int) Math.round(getData()[INITIAL_BRANCH_STATUS]);
	}
	
	public boolean isActive() {
		return getInitialBranchStatus() > 0;
	}
	
//	public double getRealPowerInjectedAtFromBusEnd() {
//		return getData()[REAL_POWER_INJECTED_AT_FROM_BUS_END];
//	}
//	
//	public double getRealPowerInjectedAtToBusEnd() {
//		return getData()[REAL_POWER_INJECTED_AT_TO_BUS_END];
//	}
	
//	public double getMVARationA() {
//		return getData()[MVA_RATING_A_LONG_TERM_RATING];
//	}
	
	
	public Bus getFromBus() {
		return fromBus;
	}

	public void setFromBus(Bus fromBus) {
		this.fromBus = fromBus;
	}

	public Bus getToBus() {
		return toBus;
	}

	public void setToBus(Bus toBus) {
		this.toBus = toBus;
	}
	
	public int getFromBusNumber() {
		return getIntParameter(PROPERTY_FROM_BUS_NUMBER);
	}

//	public int getFromBusIndex() {
//		return getFromBusNumber() - 1;
//	}
	
	public int getToBusNumber() {
		return getIntParameter(PROPERTY_TO_BUS_NUMBER);
	}
	
//	public int getToBusIndex() {
//		return getToBusNumber() - 1;
//	}
	
	public String getDisplayName() {
		Bus fromBus = getFromBus();
		String fromBusName = "??";
		if(fromBus != null) {
			fromBusName = fromBus.getName();
			if(fromBusName == null)
				fromBusName = fromBus.getDisplayName();
		}
		Bus toBus = getToBus();
		String toBusName = "??";
		if(toBus != null) {
			toBusName = toBus.getName();
			if(toBusName == null)
				toBusName = toBus.getDisplayName();
		}
		return "Branch " + (getIndex() + 1) + " (" + fromBusName + " - " + toBusName + ")";
	}
}
