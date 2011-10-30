package net.ee.pfanalyzer.model;

import net.ee.pfanalyzer.model.data.AbstractNetworkElementData;
import net.ee.pfanalyzer.model.matpower.IBranchDataConstants;


public class Branch extends AbstractNetworkElement implements IBranchDataConstants {
	
	private Bus fromBus, toBus;
	private boolean isInverted = false;
	
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
	}
	
	public boolean isActive() {
		return getInitialBranchStatus() > 0;
	}
	
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
		return getIntParameter(PROPERTY_FROM_BUS_NUMBER, -1);
	}
	
	void setFromBusNumber(int number) {
		setParameter(PROPERTY_FROM_BUS_NUMBER, number);
	}

	public int getToBusNumber() {
		return getIntParameter(PROPERTY_TO_BUS_NUMBER, -1);
	}
	
	void setToBusNumber(int number) {
		setParameter(PROPERTY_TO_BUS_NUMBER, number);
	}
	
	public boolean isInverted() {
		return isInverted;
	}

	public void setInverted(boolean isInverted) {
		this.isInverted = isInverted;
	}

	@Override
	public String getDisplayName(int displayFlags) {
		Bus fromBus = getFromBus();
		String fromBusName = "??";
		if(fromBus != null) {
			fromBusName = fromBus.getName();
			if(fromBusName == null)
				fromBusName = fromBus.getDisplayName(displayFlags);
		}
		Bus toBus = getToBus();
		String toBusName = "??";
		if(toBus != null) {
			toBusName = toBus.getName();
			if(toBusName == null)
				toBusName = toBus.getDisplayName(displayFlags);
		}
		return "Branch " + (getIndex() + 1) + " (" + fromBusName + " - " + toBusName + ")";
	}
}
