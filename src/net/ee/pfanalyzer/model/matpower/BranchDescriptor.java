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


public class BranchDescriptor implements IBranchDataConstants {

	public final static int DATA_FIELD_COUNT = 20;

	public static String getName(int dataField) {
		switch(dataField) {
		case FROM_BUS_NUMBER:
			return "From";
		case TO_BUS_NUMBER:
			return "To";
		case RESISTANCE:
			return "<html>Br<sub>R</sub></html>";
		case REACTANCE:
			return "<html>Br<sub>X</sub></html>";
		case TOTAL_LINE_CHARGING_SUSCEPTANCE:
			return "<html>Br<sub>B</sub></html>";
		case MVA_RATING_A_LONG_TERM_RATING:
			return "<html>Rate<sub>A</sub></html>";
		case MVA_RATING_B_SHORT_TERM_RATING:
			return "<html>Rate<sub>B</sub></html>";
		case MVA_RATING_C_EMERGENCY_RATING:
			return "<html>Rate<sub>C</sub></html>";
		case TRANSFORMER_OFF_NOMINAL_TURNS_RATIO:
			return "Tap";
		case TRANSFORMER_PHASE_SHIFT_ANGLE:
			return "Shift";
		case INITIAL_BRANCH_STATUS:
			return "<html>Br<sub>Status</sub></html>";
		case MINIMUM_ANGLE_DIFFERENCE:
			return "<html>Ang<sub>min</sub></html>";
		case MAXIMUM_ANGLE_DIFFERENCE:
			return "<html>Ang<sub>max</sub></html>";
		case REAL_POWER_INJECTED_AT_FROM_BUS_END:
			return "<html>P<sub>F</sub></html>";
		case REACTIVE_POWER_INJECTED_AT_FROM_BUS_END:
			return "<html>Q<sub>F</sub></html>";
		case REAL_POWER_INJECTED_AT_TO_BUS_END:
			return "<html>P<sub>T</sub></html>";
		case REACTIVE_POWER_INJECTED_AT_TO_BUS_END:
			return "<html>Q<sub>T</sub></html>";
		case KUHN_TUCKER_MULTIPLIER_ON_MVA_LIMIT_AT_FROM_BUS:
			return "<html>Mu<sub>SF</sub></html>";
		case KUHN_TUCKER_MULTIPLIER_ON_MVA_LIMIT_AT_TO_BUS:
			return "<html>Mu<sub>ST</sub></html>";
		case KUHN_TUCKER_MULTIPLIER_LOWER_ANGLE_DIFFERENCE_LIMIT:
			return "<html>Mu<sub>angmin</sub></html>";
		case KUHN_TUCKER_MULTIPLIER_UPPER_ANGLE_DIFFERENCE_LIMIT:
			return "<html>Mu<sub>angmax</sub></html>";
		}
		return null;
	}

	public static String getDescription(int dataField) {
		switch(dataField) {
		case FROM_BUS_NUMBER:
			return "\"From\" bus number";
		case TO_BUS_NUMBER:
			return "\"To\" bus number";
		case RESISTANCE:
			return "Resistance (p.u.)";
		case REACTANCE:
			return "Reactance (p.u.)";
		case TOTAL_LINE_CHARGING_SUSCEPTANCE:
			return "Total line charging susceptance (p.u.)";
		case MVA_RATING_A_LONG_TERM_RATING:
			return "MVA rating A (long term rating)";
		case MVA_RATING_B_SHORT_TERM_RATING:
			return "MVA rating B (short term rating)";
		case MVA_RATING_C_EMERGENCY_RATING:
			return "MVA rating C (emergency rating)";
		case TRANSFORMER_OFF_NOMINAL_TURNS_RATIO:
			return "<html>Transformer off nominal turns ratio, (taps at \"from\" bus, <br>impedance at \"to\" bus, i.e. if <i>r = x = 0</i>, <i>tap</i> = |V<sub>f</sub>| / |V<sub>t</sub>|)</html>";
		case TRANSFORMER_PHASE_SHIFT_ANGLE:
			return "Transformer phase shift angle (degrees), positive => delay";
		case INITIAL_BRANCH_STATUS:
			return "<html>Initial branch status:<br>1 = in-service<br>0 = out-of-service</html>";
		case MINIMUM_ANGLE_DIFFERENCE:
			return "<html>Minimum angle difference, \u0398<sub>f</sub> - \u0398<sub>t</sub> (degrees)</html>";
		case MAXIMUM_ANGLE_DIFFERENCE:
			return "<html>Maximum angle difference, \u0398<sub>f</sub> - \u0398<sub>t</sub> (degrees)</html>";
		case REAL_POWER_INJECTED_AT_FROM_BUS_END:
			return "Real power injected at \"from\" bus end (MW)";
		case REACTIVE_POWER_INJECTED_AT_FROM_BUS_END:
			return "Reactive power injected at \"from\" bus end (MVAr)";
		case REAL_POWER_INJECTED_AT_TO_BUS_END:
			return "Real power injected at \"to\" bus end (MW)";
		case REACTIVE_POWER_INJECTED_AT_TO_BUS_END:
			return "Reactive power injected at \"to\" bus end (MVAr)";
		case KUHN_TUCKER_MULTIPLIER_ON_MVA_LIMIT_AT_FROM_BUS:
			return "Kuhn-Tucker multiplier on MVA limit at \"from\" bus (<i>u</i>/MVA)";
		case KUHN_TUCKER_MULTIPLIER_ON_MVA_LIMIT_AT_TO_BUS:
			return "Kuhn-Tucker multiplier on MVA limit at \"to\" bus (<i>u</i>/MVA)";
		case KUHN_TUCKER_MULTIPLIER_LOWER_ANGLE_DIFFERENCE_LIMIT:
			return "Kuhn-Tucker multiplier lower angle difference limit (<i>u</i>/degree)";
		case KUHN_TUCKER_MULTIPLIER_UPPER_ANGLE_DIFFERENCE_LIMIT:
			return "Kuhn-Tucker multiplier upper angle difference limit (<i>u</i>/degree)";
		}
		return null;
	}
}
