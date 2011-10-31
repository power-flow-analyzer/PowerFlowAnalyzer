package net.ee.pfanalyzer.model;

import net.ee.pfanalyzer.model.data.AbstractNetworkElementData;

public class MarkerElement extends AbstractNetworkElement implements ICommonParameters, ICoordinatesParameters {

	private Bus parentBus;
	
	public MarkerElement(Network data) {
		this(data, 0);
	}

	public MarkerElement(Network data, int index) {
		super(data, index);
	}

	MarkerElement(Network data, AbstractNetworkElementData elementData, int index) {
		super(data, elementData, index);
	}
	
	@Override
	public String getDefaultModelID() {
		return ModelDB.DEFAULT_MARKER_MODEL;
	}

	@Override
	public String getDisplayName(int displayFlags) {
		String name = getName();
		if(name == null || name.isEmpty())
			name = "Marker " + getIndex();
		return name;
	}
	
	public void setName(String name) {
		setParameter(PROPERTY_NAME, name);
	}
	
	public String getName() {
		return getTextParameter(PROPERTY_NAME);
	}
	
	public double getLongitude() {
		return getDoubleParameter(PROPERTY_LONGITUDE, Double.NaN);
	}
	
	public void setLongitude(double value) {
		setParameter(PROPERTY_LONGITUDE, value);
	}
	
	public double getLatitude() {
		return getDoubleParameter(PROPERTY_LATITUDE, Double.NaN);
	}
	
	public void setLatitude(double value) {
		setParameter(PROPERTY_LATITUDE, value);
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
