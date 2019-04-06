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
package net.ee.pfanalyzer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlagList {
	
	public final static String FLAGS_BUS = "bus";
	public final static String FLAGS_BRANCH = "branch";
	public final static String FLAGS_GENERATOR = "generator";

	private Map<String, List<PowerFlowFlag>> flags = new HashMap<String, List<PowerFlowFlag>>(3);
	
	public void addFlag(String component, PowerFlowFlag flag) {
		getFlags(component).add(flag);
	}
	
	public List<PowerFlowFlag> getBusFlags() {
		return getFlags(FLAGS_BUS);
	}
	
	public List<PowerFlowFlag> getBranchFlags() {
		return getFlags(FLAGS_BRANCH);
	}
	
	public List<PowerFlowFlag> getGeneratorFlags() {
		return getFlags(FLAGS_GENERATOR);
	}
	
	public List<PowerFlowFlag> getFlags(String component) {
		List<PowerFlowFlag> list = flags.get(component.toLowerCase());
		if(list == null) {
			list = new ArrayList<PowerFlowFlag>();
			flags.put(component.toLowerCase(), list);
		}
		return list;
	}
	
	public List<PowerFlowFlag> getFlagsForData(String component, int dataIndex) {
		List<PowerFlowFlag> flags = new ArrayList<PowerFlowFlag>();
		for (PowerFlowFlag flag : getFlags(component)) {
			if(flag.getDataIndex() == dataIndex)
				flags.add(flag);
		}
		return flags;
	}
}
