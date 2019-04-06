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

public interface IBusDataConstants {

	public final static int BUS_NUMBER = 0;
	
	public final static int BUS_TYPE = 1;
	
	public final static int BUS_TYPE_PQ = 1;
	
	public final static int BUS_TYPE_PV = 2;
	
	public final static int BUS_TYPE_REFERENCE = 3;
	
	public final static int BUS_TYPE_ISOLATED = 4;
	
	public final static int REAL_POWER_DEMAND = 2;
	
	public final static int REACTIVE_POWER_DEMAND = 3;
	
	public final static int SHUNT_CONDUCTANCE = 4;
	
	public final static int SHUNT_SUSCEPTANCE = 5;
	
	public final static int AREA_NUMBER = 6;
	
	public final static int VOLTAGE_MAGNITUDE = 7;
	
	public final static int VOLTAGE_ANGLE = 8;
	
	public final static int BASE_VOLTAGE = 9;
	
	public final static int LOSS_ZONE = 10;
	
	public final static int MAXIMUM_VOLTAGE_MAGNITUDE = 11;
	
	public final static int MINIMUM_VOLTAGE_MAGNITUDE = 12;
	
	public final static int LAGRANGE_MULTIPLIER_ON_REAL_POWER_MISMATCH = 13;
	
	public final static int LAGRANGE_MULTIPLIER_ON_REACTIVE_POWER_MISMATCH = 14;
	
	public final static int KUHN_TUCKER_MULTIPLIER_ON_UPPER_VOLTAGE_LIMIT = 15;
	
	public final static int KUHN_TUCKER_MULTIPLIER_ON_LOWER_VOLTAGE_LIMIT = 16;
	
	
	public final static String PROPERTY_BUS_NUMBER = "BUS_I";
	
	public final static String PROPERTY_BUS_TYPE = "BUS_TYPE";
	
	public final static String PROPERTY_REAL_POWER_DEMAND = "PD";
	
	public final static String PROPERTY_REACTIVE_POWER_DEMAND = "QD";
	
	public final static String PROPERTY_SHUNT_CONDUCTANCE = "GS";
	
	public final static String PROPERTY_SHUNT_SUSCEPTANCE = "BS";
	
	public final static String PROPERTY_AREA_NUMBER = "BUS_AREA";
	
	public final static String PROPERTY_VOLTAGE_MAGNITUDE = "VM";
	
	public final static String PROPERTY_VOLTAGE_ANGLE = "VA";
	
	public final static String PROPERTY_BASE_VOLTAGE = "BASE_KV";
	
	public final static String PROPERTY_LOSS_ZONE = "ZONE";
	
	public final static String PROPERTY_MAXIMUM_VOLTAGE_MAGNITUDE = "VMAX";
	
	public final static String PROPERTY_MINIMUM_VOLTAGE_MAGNITUDE = "VMIN";
	
	public final static String PROPERTY_LAGRANGE_MULTIPLIER_ON_REAL_POWER_MISMATCH = "LAM_P";
	
	public final static String PROPERTY_LAGRANGE_MULTIPLIER_ON_REACTIVE_POWER_MISMATCH = "LAM_Q";
	
	public final static String PROPERTY_KUHN_TUCKER_MULTIPLIER_ON_UPPER_VOLTAGE_LIMIT = "MU_VMAX";
	
	public final static String PROPERTY_KUHN_TUCKER_MULTIPLIER_ON_LOWER_VOLTAGE_LIMIT = "MU_VMIN";
}
