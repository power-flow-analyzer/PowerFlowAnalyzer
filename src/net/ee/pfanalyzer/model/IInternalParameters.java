package net.ee.pfanalyzer.model;

import net.ee.pfanalyzer.model.matpower.IBranchDataConstants;
import net.ee.pfanalyzer.model.matpower.IGeneratorDataConstants;

public interface IInternalParameters {

	public final static String LONGITUDE = ICoordinatesParameters.PROPERTY_LONGITUDE;
	public final static String LATITUDE = ICoordinatesParameters.PROPERTY_LATITUDE;
	
	public final static String FROM_BUS = IBranchDataConstants.PROPERTY_FROM_BUS_NUMBER;
	public final static String TO_BUS = IBranchDataConstants.PROPERTY_TO_BUS_NUMBER;
	
	public final static String GEN_BUS = IGeneratorDataConstants.PROPERTY_BUS_NUMBER;
}
