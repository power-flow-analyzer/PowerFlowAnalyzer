package net.ee.pfanalyzer.model;

import net.ee.pfanalyzer.model.data.AbstractNetworkElementData;
import net.ee.pfanalyzer.model.matpower.IGeneratorDataConstants;

public class Generator extends AbstractNetworkElement implements IGeneratorDataConstants {

	private Bus bus;
	
	public Generator(Network data, int index) {
		super(data, index);
	}
	
	public Generator(Network data, AbstractNetworkElementData elementData, int index) {
		super(data, elementData, index);
	}
	
	@Override
	public String getDefaultModelID() {
		return ModelDB.DEFAULT_GENERATOR_MODEL;
	}
	
	public int getBusNumber() {
		return getIntParameter(PROPERTY_BUS_NUMBER, -1);
	}
	
//	public int getRealBusIndex() {
//		return BusIndexConverter.getRealBusIndex(getBusNumber());
//	}
	
	public Bus getBus() {
		return bus;
	}

	public void setBus(Bus bus) {
		this.bus = bus;
	}

	public int getMachineStatus() {
		return getIntParameter(PROPERTY_MACHINE_STATUS, -1);
	}
	
	public boolean isActive() {
		return getMachineStatus() > 0;
	}
	
	public double getRealPowerOutput() {
		return getDoubleParameter(PROPERTY_REAL_POWER_OUTPUT, 0);
	}
	
	public double getReactivePowerOutput() {
		return getDoubleParameter(PROPERTY_REACTIVE_POWER_OUTPUT, 0);
	}
	
	@Override
	public String getDisplayName(int displayFlags) {
		int MW = (int) Math.round(getRealPowerOutput());
		int MVAr = (int) Math.round(getReactivePowerOutput());
		String text = "Generator " + (getIndex() + 1);
		if(isActive())
			text += " (" + MW + " MW, " + MVAr + " MVAr)";
		else
			text += " (out-of-service)";
		return text;
	}
}
