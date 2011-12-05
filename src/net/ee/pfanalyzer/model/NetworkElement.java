package net.ee.pfanalyzer.model;

import net.ee.pfanalyzer.model.data.AbstractNetworkElementData;

public class NetworkElement extends AbstractNetworkElement implements ICommonParameters {

	private Bus parentBus;
	
	public NetworkElement(Network data) {
		this(data, 0);
	}

	public NetworkElement(Network data, int index) {
		super(data, index);
	}

	NetworkElement(Network data, AbstractNetworkElementData elementData, int index) {
		super(data, elementData, index);
	}
	
	@Override
	public String getDefaultModelID() {
		return ModelDB.DEFAULT_MODEL;
	}

	@Override
	public String getDisplayName(int displayFlags) {
		String name = getName();
		if(name == null || name.isEmpty()) {
			if(getModel() != null)
				name = getModel().getLabel();
			else
				name = "Element " + getIndex();
		}
		return name;
	}
	
	public void setName(String name) {
		setParameter(PROPERTY_NAME, name);
	}
	
	public String getName() {
		return getTextParameter(PROPERTY_NAME);
	}
	
	public int getParentBusNumber() {
		return getIntParameter(PROPERTY_PARENT_BUS, -1);
	}

	public Bus getParentBus() {
		return parentBus;
	}

	public void setParentBus(Bus parentBus) {
		this.parentBus = parentBus;
	}
}
