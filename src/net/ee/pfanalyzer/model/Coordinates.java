package net.ee.pfanalyzer.model;

import net.ee.pfanalyzer.model.matpower.ICoordinateDataConstants;

public class Coordinates extends AbstractNetworkElement implements ICoordinateDataConstants {

	Coordinates(Network data, int index) {
		super(data, index);
	}
	
	public String getDisplayName() {
		throw new RuntimeException("Must not be called");
	}
}
