package net.ee.pfanalyzer.model;

import net.ee.pfanalyzer.model.data.AbstractNetworkElementData;
import net.ee.pfanalyzer.model.matpower.ICoordinateDataConstants;

public class Bus extends AbstractNetworkElement implements IExtendedBusParameters {

	public final static int NAME = 17;
	public final static int DESCRIPTION = 18;
	public final static int LONGITUDE = 19;
	public final static int LATTITUDE = 20;

	public Bus(Network data, int index) {
		super(data, index);
	}
	
	public Bus(Network data, AbstractNetworkElementData elementData, int index) {
		super(data, elementData, index);
		
		// define Matpower data
//		addIntProp(matpowerData, PROPERTY_BUS_NUMBER, BUS_NUMBER);
//		addProperty(new IntegerOptionProperty(PROPERTY_BUS_TYPE, "Bus Type", 
//				new String[] {"PQ", "PV", "reference", "isolated"}, 
//				new int[] { 1, 2, 3, 4 }));
//		addDoubleProp(matpowerData, PROPERTY_REAL_POWER_DEMAND, REAL_POWER_DEMAND);
//		addDoubleProp(matpowerData, PROPERTY_REACTIVE_POWER_DEMAND, REACTIVE_POWER_DEMAND);
//		addDoubleProp(matpowerData, PROPERTY_SHUNT_CONDUCTANCE, SHUNT_CONDUCTANCE);
//		addDoubleProp(matpowerData, PROPERTY_SHUNT_SUSCEPTANCE, SHUNT_SUSCEPTANCE);
//		addIntProp(matpowerData, PROPERTY_AREA_NUMBER, AREA_NUMBER);
//		addDoubleProp(matpowerData, PROPERTY_VOLTAGE_MAGNITUDE, VOLTAGE_MAGNITUDE);
//		addDoubleProp(matpowerData, PROPERTY_VOLTAGE_ANGLE, VOLTAGE_ANGLE);
//		addIntProp(matpowerData, PROPERTY_BASE_VOLTAGE, BASE_VOLTAGE);
//		addIntProp(matpowerData, PROPERTY_LOSS_ZONE, LOSS_ZONE);
//		addDoubleProp(matpowerData, PROPERTY_MAXIMUM_VOLTAGE_MAGNITUDE, MAXIMUM_VOLTAGE_MAGNITUDE);
//		addDoubleProp(matpowerData, PROPERTY_MINIMUM_VOLTAGE_MAGNITUDE, MINIMUM_VOLTAGE_MAGNITUDE);
//		addDoubleProp(matpowerData, PROPERTY_LAGRANGE_MULTIPLIER_ON_REAL_POWER_MISMATCH, LAGRANGE_MULTIPLIER_ON_REAL_POWER_MISMATCH);
//		addDoubleProp(matpowerData, PROPERTY_LAGRANGE_MULTIPLIER_ON_REACTIVE_POWER_MISMATCH, LAGRANGE_MULTIPLIER_ON_REACTIVE_POWER_MISMATCH);
//		addDoubleProp(matpowerData, PROPERTY_KUHN_TUCKER_MULTIPLIER_ON_UPPER_VOLTAGE_LIMIT, KUHN_TUCKER_MULTIPLIER_ON_UPPER_VOLTAGE_LIMIT);
//		addDoubleProp(matpowerData, PROPERTY_KUHN_TUCKER_MULTIPLIER_ON_LOWER_VOLTAGE_LIMIT, KUHN_TUCKER_MULTIPLIER_ON_LOWER_VOLTAGE_LIMIT);
//		// define additional data
//		addProperty(new TextProperty(PROPERTY_NAME, "Name"));
//		addProperty(new TextProperty(PROPERTY_DESCRIPTION, "Description"));
//		addProperty(new DoubleProperty(PROPERTY_LONGITUDE, "Longitude"));
//		addProperty(new DoubleProperty(PROPERTY_LATTITUDE, "Lattitude"));
		
//		setBusType((int) Math.round(matpowerData[BUS_TYPE]));
	}
	
	@Override
	public String getDefaultModelID() {
		return ModelDB.DEFAULT_BUS_MODEL;
	}
	
	public int getBusNumber() {
		return getIntParameter(PROPERTY_BUS_NUMBER, -1);
	}
	
	public void setBusNumber(int value) {
		setParameter(PROPERTY_BUS_NUMBER, value);
	}
	
	public int getBusType() {
		return getIntParameter(PROPERTY_BUS_TYPE, -1);
	}
	
	public void setBusType(int value) {
		setParameter(PROPERTY_BUS_TYPE, value);
	}
	
	public boolean isSlackNode() {
		return getBusType() == BUS_TYPE_REFERENCE;
	}
	
	public int getBaseVoltage() {
		return getIntParameter(PROPERTY_BASE_VOLTAGE);
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
	
	public double getLattitude() {
		return getDoubleParameter(PROPERTY_LATTITUDE, Double.NaN);
	}
	
	public void setLattitude(double value) {
		setParameter(PROPERTY_LATTITUDE, value);
	}
	
	public void setCoordinates(double[] values) {
		setLongitude(values[ICoordinateDataConstants.LONGITUDE]);
		setLattitude(values[ICoordinateDataConstants.LATITUDE]);
	}
	
	public double[] getCoordinates() {
		return new double[] { getLongitude(), getLattitude() };
	}
	
	public String getDisplayName() {
		String text = "Bus " + (getBusNumber()) + " (" + getBaseVoltage() + " kV)";
		if(isSlackNode())
			text += " (Slack Bus)";
		return text;
	}
}
