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
package net.ee.pfanalyzer.io;

public interface IConnectionConstants {

	public final static String SERVER_MESSAGE_OK = "OK";
	
	public final static String DATA_FIELD_NOT_FOUND_MESSAGE = "Data field not found";
	
	public final static String POWER_FLOW_DATA_CONNECTION = "transfer-data";
	
	public final static String NETWORK_DATA_CONNECTION = "set-network-data";
	
	public final static String NETWORK_DATA_FIELD = "network-data";
	
	public final static String IMPORT_TYPE_DATA_FIELD = "import-type";
	
	public final static String IMPORT_TYPE_REPLACE_CASE_DATA_VALUE = "replace-case";
	
	public final static String IMPORT_TYPE_NEW_CASE_DATA_VALUE = "new-case";
	
	public final static String SERVER_STATUS_CONNECTION = "server-status";
	
	public final static String SET_WORKING_DIR_CONNECTION = "set-working-directory";
	
	public final static String POWER_FLOW_CANCELED_CONNECTION = "power-flow-canceled";
	
	public final static String SHOW_VIEWER_CONNECTION = "show-viewer";
	
	public final static String CLOSE_VIEWER_CONNECTION = "close-viewer";
	
	public final static String BUS_DATA_FIELD = "bus";
	
	public final static String BRANCH_DATA_FIELD = "branch";
	
	public final static String GENERATOR_DATA_FIELD = "generator";
	
	public final static String BUS_NAMES_DATA_FIELD = "bus-names";
	
	public final static String BUS_COORDINATES_DATA_FIELD = "bus-coordinates";
	
	public final static String FLAGS_DATA_FIELD_PREFIX = "flags";
	
	public final static String FLAG_LABELS_DATA_FIELD = "labels";
	
	public final static String FLAG_FAILURES_DATA_FIELD = "failures";
	
	public final static String FLAG_PERCENTAGES_DATA_FIELD = "percentages";
	
	public final static String FLAG_DATA_INDICES_DATA_FIELD = "data-indices";
	
	public final static String WORKING_DIR_DATA_FIELD = "working-directory";
	
	public final static String SIGNAL_TO_USER_DATA_FIELD = "signal-to-user";
	
	public final static int DATA_TYPE_DOUBLE = 0;
	
	public final static int DATA_TYPE_DOUBLE_ARRAY = 1;
	
	public final static int DATA_TYPE_2DIM_DOUBLE_ARRAY = 2;
	
	public final static int DATA_TYPE_STRING = 3;
	
	public final static int DATA_TYPE_STRING_ARRAY = 4;
	
	public final static int DATA_TYPE_BOOLEAN = 5;
	
	public final static int DATA_TYPE_BOOLEAN_ARRAY = 6;
	
	public final static int DATA_TYPE_2DIM_BOOLEAN_ARRAY = 7;
}
