package net.ee.pfanalyzer.model.matpower;

public interface IGeneratorDataConstants {

	public final static int BUS_NUMBER = 0;
	
	public final static int REAL_POWER_OUTPUT = 1;
	
	public final static int REACTIVE_POWER_OUTPUT = 2;
	
	public final static int MAXIMUM_REACTIVE_POWER_OUTPUT = 3;
	
	public final static int MINIMUM_REACTIVE_POWER_OUTPUT = 4;
	
	public final static int VOLTAGE_MAGNITUDE_SETPOINT = 5;
	
	public final static int TOTAL_MVA_BASE_OF_MACHINE = 6;
	
	public final static int MACHINE_STATUS = 7;
	
	public final static int MAXIMUM_REAL_POWER_OUTPUT = 8;
	
	public final static int MINIMUM_REAL_POWER_OUTPUT = 9;
	
	public final static int LOWER_REAL_POWER_OUTPUT_OF_PQ_CAPABILITY_CURVE = 10;
	
	public final static int UPPER_REAL_POWER_OUTPUT_OF_PQ_CAPABILITY_CURVE = 11;
	
	public final static int MINIMUM_REACTIVE_POWER_OUTPUT_AT_PC1 = 12;
	
	public final static int MAXIMUM_REACTIVE_POWER_OUTPUT_AT_PC1 = 13;
	
	public final static int MINIMUM_REACTIVE_POWER_OUTPUT_AT_PC2 = 14;
	
	public final static int MAXIMUM_REACTIVE_POWER_OUTPUT_AT_PC2 = 15;
	
	public final static int RAMP_RATE_FOR_LOADING_FOLLOWING_AGC = 16;
	
	public final static int RAMP_RATE_FOR_10_MINUTE_RESERVES = 17;
	
	public final static int RAMP_RATE_FOR_30_MINUTE_RESERVES = 18;
	
	public final static int RAMP_RATE_FOR_REACTIVE_POWER = 19;
	
	public final static int AREA_PARTICIPATION_FACTOR = 20;
	
	public final static int KUHN_TUCKER_MULTIPLIER_ON_UPPER_PG_LIMIT = 21;
	
	public final static int KUHN_TUCKER_MULTIPLIER_ON_LOWER_PG_LIMIT = 22;
	
	public final static int KUHN_TUCKER_MULTIPLIER_ON_UPPER_QG_LIMIT = 23;
	
	public final static int KUHN_TUCKER_MULTIPLIER_ON_LOWER_QG_LIMIT = 24;

	
	public final static String PROPERTY_BUS_NUMBER = "GEN_BUS";
	
	public final static String PROPERTY_REAL_POWER_OUTPUT = "PG";
	
	public final static String PROPERTY_REACTIVE_POWER_OUTPUT = "QG";
	
	public final static String PROPERTY_MAXIMUM_REACTIVE_POWER_OUTPUT = "QMAX";
	
	public final static String PROPERTY_MINIMUM_REACTIVE_POWER_OUTPUT = "QMIN";
	
	public final static String PROPERTY_VOLTAGE_MAGNITUDE_SETPOINT = "VG";
	
	public final static String PROPERTY_TOTAL_MVA_BASE_OF_MACHINE = "MBASE";
	
	public final static String PROPERTY_MACHINE_STATUS = "GEN_STATUS";
	
	public final static String PROPERTY_MAXIMUM_REAL_POWER_OUTPUT = "PMAX";
	
	public final static String PROPERTY_MINIMUM_REAL_POWER_OUTPUT = "PMIN";
	
	public final static String PROPERTY_LOWER_REAL_POWER_OUTPUT_OF_PQ_CAPABILITY_CURVE = "PC1";
	
	public final static String PROPERTY_UPPER_REAL_POWER_OUTPUT_OF_PQ_CAPABILITY_CURVE = "PC2";
	
	public final static String PROPERTY_MINIMUM_REACTIVE_POWER_OUTPUT_AT_PC1 = "QC1MIN";
	
	public final static String PROPERTY_MAXIMUM_REACTIVE_POWER_OUTPUT_AT_PC1 = "QC1MAX";
	
	public final static String PROPERTY_MINIMUM_REACTIVE_POWER_OUTPUT_AT_PC2 = "QC2MIN";
	
	public final static String PROPERTY_MAXIMUM_REACTIVE_POWER_OUTPUT_AT_PC2 = "QC2MAX";
	
	public final static String PROPERTY_RAMP_RATE_FOR_LOADING_FOLLOWING_AGC = "RAMP_AGC";
	
	public final static String PROPERTY_RAMP_RATE_FOR_10_MINUTE_RESERVES = "RAMP_10";
	
	public final static String PROPERTY_RAMP_RATE_FOR_30_MINUTE_RESERVES = "RAMP_30";
	
	public final static String PROPERTY_RAMP_RATE_FOR_REACTIVE_POWER = "RAMP_Q";
	
	public final static String PROPERTY_AREA_PARTICIPATION_FACTOR = "APF";
	
	public final static String PROPERTY_KUHN_TUCKER_MULTIPLIER_ON_UPPER_PG_LIMIT = "MU_PMAX";
	
	public final static String PROPERTY_KUHN_TUCKER_MULTIPLIER_ON_LOWER_PG_LIMIT = "MU_PMIN";
	
	public final static String PROPERTY_KUHN_TUCKER_MULTIPLIER_ON_UPPER_QG_LIMIT = "MU_QMAX";
	
	public final static String PROPERTY_KUHN_TUCKER_MULTIPLIER_ON_LOWER_QG_LIMIT = "MU_QMIN";
}
