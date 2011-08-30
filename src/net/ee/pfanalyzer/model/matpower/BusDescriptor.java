package net.ee.pfanalyzer.model.matpower;


public class BusDescriptor implements IBusDataConstants {
	
	public final static int DATA_FIELD_COUNT = 16;
	
//	public static String[] DATA_NAMES = new String[17];
//	
//	static {
//		
//	}
	
	public static String getName(int dataField) {
		switch (dataField) {
		case BUS_NUMBER:
			return "Bus";
		case BUS_TYPE:
			return "Type";
		case REAL_POWER_DEMAND:
			return "<html>P<sub>D</sub></html>";
		case REACTIVE_POWER_DEMAND:
			return "<html>Q<sub>D</sub></html>";
		case SHUNT_CONDUCTANCE:
			return "<html>G<sub>S</sub></html>";
		case SHUNT_SUSCEPTANCE:
			return "<html>B<sub>S</sub></html>";
		case AREA_NUMBER:
			return "Area";
		case VOLTAGE_MAGNITUDE:
			return "<html>V<sub>M</sub></html>";
		case VOLTAGE_ANGLE:
			return "<html>V<sub>A</sub></html>";
		case BASE_VOLTAGE:
			return "<html>Base<sub>kV</sub></html>";
		case LOSS_ZONE:
			return "Zone";
		case MAXIMUM_VOLTAGE_MAGNITUDE:
			return "<html>V<sub>MAX</sub></html>";
		case MINIMUM_VOLTAGE_MAGNITUDE:
			return "<html>V<sub>MIN</sub></html>";
		case LAGRANGE_MULTIPLIER_ON_REAL_POWER_MISMATCH:
			return "<html>LAM<sub>P</sub></html>";
		case LAGRANGE_MULTIPLIER_ON_REACTIVE_POWER_MISMATCH:
			return "<html>LAM<sub>Q</sub></html>";
		case KUHN_TUCKER_MULTIPLIER_ON_UPPER_VOLTAGE_LIMIT:
			return "<html>MU<sub>VMAX</sub></html>";
		case KUHN_TUCKER_MULTIPLIER_ON_LOWER_VOLTAGE_LIMIT:
			return "<html>MU<sub>VMIN</sub></html>";
		}
		return null;
	}

	public static String getDescription(int dataField) {
		switch (dataField) {
		case BUS_NUMBER:
			return "Bus number";
		case BUS_TYPE:
			return "Bus type (1 = PQ, 2 = PV, 3 = ref, 4 = isolated)";
		case REAL_POWER_DEMAND:
			return "Real power demand (MW)";
		case REACTIVE_POWER_DEMAND:
			return "Reactive power demand (MVAr)";
		case SHUNT_CONDUCTANCE:
			return "Shunt conductance (MW demanded at V = 1.0 p.u.)";
		case SHUNT_SUSCEPTANCE:
			return "Shunt susceptance (MVAr injected at V = 1.0 p.u.)";
		case AREA_NUMBER:
			return "Area number";
		case VOLTAGE_MAGNITUDE:
			return "Voltage magnitude (p.u.)";
		case VOLTAGE_ANGLE:
			return "Voltage angle (degrees)";
		case BASE_VOLTAGE:
			return "Base voltage (kV)";
		case LOSS_ZONE:
			return "Loss zone";
		case MAXIMUM_VOLTAGE_MAGNITUDE:
			return "Maximum voltage magnitude (p.u.)";
		case MINIMUM_VOLTAGE_MAGNITUDE:
			return "Minimum voltage magnitude (p.u.)";
		case LAGRANGE_MULTIPLIER_ON_REAL_POWER_MISMATCH:
			return "<html>Lagrange multiplier on real power mismatch (<i>u</i>/MW)";
		case LAGRANGE_MULTIPLIER_ON_REACTIVE_POWER_MISMATCH:
			return "<html>Lagrange multiplier on reactive power mismatch (<i>u</i>/MVAr)";
		case KUHN_TUCKER_MULTIPLIER_ON_UPPER_VOLTAGE_LIMIT:
			return "<html>Kuhn-Tucker multiplier on upper voltage limit (<i>u</i>/p.u.)";
		case KUHN_TUCKER_MULTIPLIER_ON_LOWER_VOLTAGE_LIMIT:
			return "<html>Kuhn-Tucker multiplier on lower voltage limit (<i>u</i>/p.u.)";
		}
		return null;
	}
}
