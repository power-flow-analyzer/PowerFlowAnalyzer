/*******************************************************************************
 * Copyright 2019 Markus Gronau
 * 
 * This file is part of PowerFlowAnalyzer.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.ee.pfanalyzer.model.matpower;


public class GeneratorDescriptor implements IGeneratorDataConstants {

	public final static int DATA_FIELD_COUNT = 24;

	public static String getName(int dataField) {
		switch(dataField) {
		case BUS_NUMBER:
			return "Bus";
		case REAL_POWER_OUTPUT:
			return "<html>P<sub>G</sub></html>";
		case REACTIVE_POWER_OUTPUT:
			return "<html>Q<sub>G</sub></html>";
		case MAXIMUM_REACTIVE_POWER_OUTPUT:
			return "<html>Q<sub>MAX</sub></html>";
		case MINIMUM_REACTIVE_POWER_OUTPUT:
			return "<html>Q<sub>MIN</sub></html>";
		case VOLTAGE_MAGNITUDE_SETPOINT:
			return "<html>V<sub>G</sub></html>";
		case TOTAL_MVA_BASE_OF_MACHINE:
			return "<html>M<sub>base</sub></html>";
		case MACHINE_STATUS:
			return "Status";
		case MAXIMUM_REAL_POWER_OUTPUT:
			return "<html>P<sub>max</sub></html>";
		case MINIMUM_REAL_POWER_OUTPUT:
			return "<html>P<sub>min</sub></html>";
		case LOWER_REAL_POWER_OUTPUT_OF_PQ_CAPABILITY_CURVE:
			return "<html>PC<sub>1</sub></html>";
		case UPPER_REAL_POWER_OUTPUT_OF_PQ_CAPABILITY_CURVE:
			return "<html>PC<sub>2</sub></html>";
		case MINIMUM_REACTIVE_POWER_OUTPUT_AT_PC1:
			return "<html>QC1<sub>min</sub></html>";
		case MAXIMUM_REACTIVE_POWER_OUTPUT_AT_PC1:
			return "<html>QC1<sub>max</sub></html>";
		case MINIMUM_REACTIVE_POWER_OUTPUT_AT_PC2:
			return "<html>QC2<sub>min</sub></html>";
		case MAXIMUM_REACTIVE_POWER_OUTPUT_AT_PC2:
			return "<html>QC2<sub>max</sub></html>";
		case RAMP_RATE_FOR_LOADING_FOLLOWING_AGC:
			return "<html>Ramp<sub>AGC</sub></html>";
		case RAMP_RATE_FOR_10_MINUTE_RESERVES:
			return "<html>Ramp<sub>10</sub></html>";
		case RAMP_RATE_FOR_30_MINUTE_RESERVES:
			return "<html>Ramp<sub>30</sub></html>";
		case RAMP_RATE_FOR_REACTIVE_POWER:
			return "<html>Ramp<sub>Q</sub></html>";
		case AREA_PARTICIPATION_FACTOR:
			return "APF";
		case KUHN_TUCKER_MULTIPLIER_ON_UPPER_PG_LIMIT:
			return "<html>Mu<sub>Pmax</sub></html>";
		case KUHN_TUCKER_MULTIPLIER_ON_LOWER_PG_LIMIT:
			return "<html>Mu<sub>Pmin</sub></html>";
		case KUHN_TUCKER_MULTIPLIER_ON_UPPER_QG_LIMIT:
			return "<html>Mu<sub>Qmax</sub></html>";
		case KUHN_TUCKER_MULTIPLIER_ON_LOWER_QG_LIMIT:
			return "<html>Mu<sub>Qmin</sub></html>";
		}
		return null;
	}

	public static String getDescription(int dataField) {
		switch(dataField) {
		case BUS_NUMBER:
			return "Bus Number";
		case REAL_POWER_OUTPUT:
			return "Real Power Output (MW)";
		case REACTIVE_POWER_OUTPUT:
			return "Reactive Power Output (MVAr)";
		case MAXIMUM_REACTIVE_POWER_OUTPUT:
			return "Maximum reactive power output (MVAr)";
		case MINIMUM_REACTIVE_POWER_OUTPUT:
			return "Minimum reactive power output (MVAr)";
		case VOLTAGE_MAGNITUDE_SETPOINT:
			return "Voltage magnitude setpoint (p.u.)";
		case TOTAL_MVA_BASE_OF_MACHINE:
			return "Total MVA base of machine, defaults to baseMVA";
		case MACHINE_STATUS:
			return "<html>Machine status:<br>  &gt;  0 = machine in-service<br>  &lt;= 0 = machine out-of-service";
		case MAXIMUM_REAL_POWER_OUTPUT:
			return "Maximum real power output (MW)";
		case MINIMUM_REAL_POWER_OUTPUT:
			return "Minimum real power output (MW)";
		case LOWER_REAL_POWER_OUTPUT_OF_PQ_CAPABILITY_CURVE:
			return "Lower real power output of PQ capability curve (MW)";
		case UPPER_REAL_POWER_OUTPUT_OF_PQ_CAPABILITY_CURVE:
			return "Upper real power output of PQ capability curve (MW)";
		case MINIMUM_REACTIVE_POWER_OUTPUT_AT_PC1:
			return "Minimum reactive power output at PC1 (MVAr)";
		case MAXIMUM_REACTIVE_POWER_OUTPUT_AT_PC1:
			return "Maximum reactive power output at PC1 (MVAr)";
		case MINIMUM_REACTIVE_POWER_OUTPUT_AT_PC2:
			return "Minimum reactive power output at PC2 (MVAr)";
		case MAXIMUM_REACTIVE_POWER_OUTPUT_AT_PC2:
			return "Maximum reactive power output at PC2 (MVAr)";
		case RAMP_RATE_FOR_LOADING_FOLLOWING_AGC:
			return "Ramp rate for loading following/AGC (MW/min)";
		case RAMP_RATE_FOR_10_MINUTE_RESERVES:
			return "Ramp rate for 10 minute reserves (MW)";
		case RAMP_RATE_FOR_30_MINUTE_RESERVES:
			return "Ramp rate for 30 minute reserves (MW)";
		case RAMP_RATE_FOR_REACTIVE_POWER:
			return "Ramp rate for reactive power (2 sec timescale) (MVAr/min)";
		case AREA_PARTICIPATION_FACTOR:
			return "Area participation factor";
		case KUHN_TUCKER_MULTIPLIER_ON_UPPER_PG_LIMIT:
			return "<html>Kuhn-Tucker multiplier on upper <i>P<sub>g</sub></i> limit (<i>u</i>/MW)</html>";
		case KUHN_TUCKER_MULTIPLIER_ON_LOWER_PG_LIMIT:
			return "<html>Kuhn-Tucker multiplier on lower <i>P<sub>g</sub></i> limit (<i>u</i>/MW)</html>";
		case KUHN_TUCKER_MULTIPLIER_ON_UPPER_QG_LIMIT:
			return "<html>Kuhn-Tucker multiplier on upper <i>Q<sub>g</sub></i> limit (<i>u</i>/MW)</html>";
		case KUHN_TUCKER_MULTIPLIER_ON_LOWER_QG_LIMIT:
			return "<html>Kuhn-Tucker multiplier on lower <i>Q<sub>g</sub></i> limit (<i>u</i>/MW)</html>";
		}
		return null;
	}
}
