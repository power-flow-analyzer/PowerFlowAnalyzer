package net.ee.pfanalyzer.model;

import java.util.List;

public class CombinedBranch extends CombinedNetworkElement<Branch> {

	private CombinedBus fromBus, toBus;
	private Boolean isCorrect = null;
	
	public CombinedBranch(CombinedBus fromBus, CombinedBus toBus, Branch branch) {
		addBranch(branch);
		this.fromBus = fromBus;
		this.toBus = toBus;
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

	@Override
	public String getLabel() {
		String text = "";
		String fromName = getFromBus().getLabel();
		String toName = getToBus().getLabel();
		if(fromName != null)
			text += fromName;
		else
			text += "Area " + (getIndex() + 1);
		text += " - ";
		if(toName != null)
			text += toName;
		else
			text += "Area " + (getIndex() + 1);
		return text;
	}
	
	@Override
	public boolean isCorrect() {
		if(isCorrect == null) {
			isCorrect = true;
			for (Branch branch : getBranches()) {
				if(branch.isCorrect() == false) {
					isCorrect = false;
					break;
				}
			}
		}
		return isCorrect;
	}
}
