package net.ee.pfanalyzer.model;

import java.util.List;

public class CombinedBranch extends CombinedNetworkElement<Branch> {

	private CombinedBus fromBus, toBus;
	
	public CombinedBranch(CombinedBus fromBus, CombinedBus toBus, Branch branch) {
		addBranch(branch);
		this.fromBus = fromBus;
		this.toBus = toBus;
	}
	
	public String getTypeLabel() {
		return "Branches";
	}
	
	public void addBranch(Branch branch) {
		addNetworkElement(branch);
	}
	
	public int getBranchCount() {
		return getNetworkElementCount();
	}
	
	public List<Branch> getBranches() {
		return getNetworkElements();
	}
	
	public Branch getBranch(int index) {
		return getNetworkElement(index);
	}
	
	public Branch getFirstBranch() {
		return getFirstNetworkElement();
	}
	
	public CombinedBus getFromBus() {
		return fromBus;
	}
	
	public CombinedBus getToBus() {
		return toBus;
	}
	
	public boolean hasBusNodes(CombinedBus fromBus, CombinedBus toBus) {
		return (this.fromBus == fromBus && this.toBus == toBus) ;
//				|| (this.fromBus == toBus && this.toBus == fromBus);
	}
	
	public double getFromBusRealInjectionSum() {
		double result = 0;
		for (Branch branch : this.getBranches()) {
			if(branch.isInverted())
				result += branch.getDoubleParameter("PT", 0);
			else
				result += branch.getDoubleParameter("PF", 0);
		}
		return result;
	}
	
	public double getToBusRealInjectionSum() {
		double result = 0;
		for (Branch branch : this.getBranches()) {
			if(branch.isInverted())
				result += branch.getDoubleParameter("PF", 0);
			else
				result += branch.getDoubleParameter("PT", 0);
		}
		return result;
	}

	@Override
	public String getLabel() {
		String text = "";
		String fromName = getFromBus().getLabel();
		String toName = getToBus().getLabel();
		if(fromName != null)
			text += fromName;
		else
			text += "Combined bus " + (getIndex() + 1);
		text += " - ";
		if(toName != null)
			text += toName;
		else
			text += "Combined bus " + (getIndex() + 1);
		return text;
	}
}
