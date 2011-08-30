package net.ee.pfanalyzer.model.matpower;

public interface IBranchDataConstants {

	public final static int FROM_BUS_NUMBER = 0;
	
	public final static int TO_BUS_NUMBER = 1;
	
	public final static int RESISTANCE = 2;
	
	public final static int REACTANCE = 3;
	
	public final static int TOTAL_LINE_CHARGING_SUSCEPTANCE = 4;
	
	public final static int MVA_RATING_A_LONG_TERM_RATING = 5;
	
	public final static int MVA_RATING_B_SHORT_TERM_RATING = 6;
	
	public final static int MVA_RATING_C_EMERGENCY_RATING = 7;
	
	public final static int TRANSFORMER_OFF_NOMINAL_TURNS_RATIO = 8;
	
	public final static int TRANSFORMER_PHASE_SHIFT_ANGLE = 9;
	
	public final static int INITIAL_BRANCH_STATUS = 10;
	
	public final static int MINIMUM_ANGLE_DIFFERENCE = 11;
	
	public final static int MAXIMUM_ANGLE_DIFFERENCE = 12;
	
	public final static int REAL_POWER_INJECTED_AT_FROM_BUS_END = 13;
	
	public final static int REACTIVE_POWER_INJECTED_AT_FROM_BUS_END = 14;
	
	public final static int REAL_POWER_INJECTED_AT_TO_BUS_END = 15;
	
	public final static int REACTIVE_POWER_INJECTED_AT_TO_BUS_END = 16;
	
	public final static int KUHN_TUCKER_MULTIPLIER_ON_MVA_LIMIT_AT_FROM_BUS = 17;
	
	public final static int KUHN_TUCKER_MULTIPLIER_ON_MVA_LIMIT_AT_TO_BUS = 18;
	
	public final static int KUHN_TUCKER_MULTIPLIER_LOWER_ANGLE_DIFFERENCE_LIMIT = 19;
	
	public final static int KUHN_TUCKER_MULTIPLIER_UPPER_ANGLE_DIFFERENCE_LIMIT = 20;

	
	public final static String PROPERTY_FROM_BUS_NUMBER = "F_BUS";
	
	public final static String PROPERTY_TO_BUS_NUMBER = "T_BUS";
	
	public final static String PROPERTY_RESISTANCE = "BR_R";
	
	public final static String PROPERTY_REACTANCE = "BR_X";
	
	public final static String PROPERTY_TOTAL_LINE_CHARGING_SUSCEPTANCE = "BR_B";
	
	public final static String PROPERTY_MVA_RATING_A_LONG_TERM_RATING = "RATE_A";
	
	public final static String PROPERTY_MVA_RATING_B_SHORT_TERM_RATING = "RATE_B";
	
	public final static String PROPERTY_MVA_RATING_C_EMERGENCY_RATING = "RATE_C";
	
	public final static String PROPERTY_TRANSFORMER_OFF_NOMINAL_TURNS_RATIO = "TAP";
	
	public final static String PROPERTY_TRANSFORMER_PHASE_SHIFT_ANGLE = "SHIFT";
	
	public final static String PROPERTY_INITIAL_BRANCH_STATUS = "BR_STATUS";
	
	public final static String PROPERTY_MINIMUM_ANGLE_DIFFERENCE = "ANGMIN";
	
	public final static String PROPERTY_MAXIMUM_ANGLE_DIFFERENCE = "ANGMAX";
	
	public final static String PROPERTY_REAL_POWER_INJECTED_AT_FROM_BUS_END = "PF";
	
	public final static String PROPERTY_REACTIVE_POWER_INJECTED_AT_FROM_BUS_END = "QF";
	
	public final static String PROPERTY_REAL_POWER_INJECTED_AT_TO_BUS_END = "PT";
	
	public final static String PROPERTY_REACTIVE_POWER_INJECTED_AT_TO_BUS_END = "QT";
	
	public final static String PROPERTY_KUHN_TUCKER_MULTIPLIER_ON_MVA_LIMIT_AT_FROM_BUS = "MU_SF";
	
	public final static String PROPERTY_KUHN_TUCKER_MULTIPLIER_ON_MVA_LIMIT_AT_TO_BUS = "MU_ST";
	
	public final static String PROPERTY_KUHN_TUCKER_MULTIPLIER_LOWER_ANGLE_DIFFERENCE_LIMIT = "MU_ANGMIN";
	
	public final static String PROPERTY_KUHN_TUCKER_MULTIPLIER_UPPER_ANGLE_DIFFERENCE_LIMIT = "MU_ANGMAX";
}
