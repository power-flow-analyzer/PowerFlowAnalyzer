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
