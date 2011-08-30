package net.ee.pfanalyzer.model;

public class Transformer extends Branch implements IDerivedElement<Branch> {

	private Branch parentElement;
	
	public Transformer(Branch branch) {
		super(branch.getNetwork(), branch.getElementData(), branch.getIndex());
		parentElement = branch;
	}
	
	public String getDisplayName() {
		Bus fromBus = getFromBus();
		String fromBusVoltage = "??";
		if(fromBus != null)
			fromBusVoltage = fromBus.getBaseVoltage() + " kV";
		Bus toBus = getToBus();
		String toBusVoltage = "??";
		if(toBus != null)
			toBusVoltage = toBus.getBaseVoltage() + " kV";
		return "Transformer " + (getIndex() + 1) + " (" + fromBusVoltage + " - " + toBusVoltage + ")";
	}

	@Override
	public Branch getRealElement() {
		return parentElement;
	}
	
	@Override
	public Bus getFromBus() {
		return parentElement.getFromBus();
	}

	@Override
	public Bus getToBus() {
		return parentElement.getToBus();
	}
}
