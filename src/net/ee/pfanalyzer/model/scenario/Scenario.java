package net.ee.pfanalyzer.model.scenario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scenario {

	public final static String PARAMETER_INPUT_SOURCE = "INPUT_SOURCE";
	public final static String VALUE_INPUT_SOURCE_EXCEL = "EXCEL";
	public final static String VALUE_INPUT_SOURCE_MATLAB = "MATLAB";
	public final static String PARAMETER_INPUT_FILE = "INPUT_FILE";
	public final static String PARAMETER_SCENARIO_DATA_FILE = "SCENARIO_INPUT";
	public final static String PARAMETER_SCENARIO_OUTPUT_FILE = "SCENARIO_OUTPUT";
	public final static String PARAMETER_POWER_FLOW = "POWER_FLOW";
	public final static String VALUE_POWER_FLOW_PF = "PF";
	public final static String VALUE_POWER_FLOW_OPF = "OPF";
	public final static String VALUE_POWER_FLOW_UOPF = "UOPF";
	
	private Map<String, ScenarioParameter> parameterMap = new HashMap<String, ScenarioParameter>();
	
	private List<SubScenario> subScenarios = new ArrayList<SubScenario>();
	
	private SubScenario currentSubScenario;
	
	public Scenario() {
//		// input Source Param
//		ScenarioParameter inputSourceParam = new ScenarioParameter(PARAMETER_INPUT_SOURCE, true);
//		inputSourceParam.addOption(null, VALUE_INPUT_SOURCE_EXCEL);
//		inputSourceParam.addOption(null, VALUE_INPUT_SOURCE_MATLAB);
//		addParameter(inputSourceParam);
//		// input other Params
//		addParameter(new ScenarioParameter(PARAMETER_INPUT_FILE, true));
//		addParameter(new ScenarioParameter(PARAMETER_SCENARIO_DATA_FILE, true));
//		addParameter(new ScenarioParameter(PARAMETER_SCENARIO_OUTPUT_FILE, true));
//		// power flow Param
//		ScenarioParameter powerFlowParam = new ScenarioParameter(PARAMETER_POWER_FLOW, true);
//		powerFlowParam.addOption(null, VALUE_POWER_FLOW_PF);
//		powerFlowParam.addOption(null, VALUE_POWER_FLOW_OPF);
//		powerFlowParam.addOption(null, VALUE_POWER_FLOW_UOPF);
//		addParameter(powerFlowParam);
	}
	
	public void addSubScenario(String label) {
		SubScenario subScenario = new SubScenario(label);
		currentSubScenario = subScenario;
		subScenarios.add(subScenario);
	}
	
	public void addParameter(ScenarioParameter parameter) {
		parameterMap.put(parameter.getID(), parameter);
		if(currentSubScenario == null)
			addSubScenario("");
		currentSubScenario.addParameter(parameter);
	}
	
	public int getSubScenarioCount() {
		return subScenarios.size();
	}
	
	public String getSubScenarioLabel(int index) {
		return subScenarios.get(index).getLabel();
	}
	
	public List<ScenarioParameter> getSubScenarioParameters(int index) {
		return subScenarios.get(index).getParameters();
	}
	
	public Collection<ScenarioParameter> getParameters() {
		return parameterMap.values();
	}
	
	public int getParameterCount() {
		return parameterMap.size();
	}
	
	public ScenarioParameter getParameter(String ID) {
		return parameterMap.get(ID);
	}
	
	public void setParameterValue(String paramName, String value) {
		ScenarioParameter param = getParameter(paramName);
		if(param == null)
			throw new IllegalArgumentException("No parameter defined with ID \"" + paramName + "\"");
		param.setSelectedValue(value);
	}
	
	public String getParameterValue(String ID) {
		return getParameter(ID).getSelectedOptionValue();
	}
	
	public String getTextParameter(String ID) {
		return getParameter(ID).getSelectedOptionValue();
	}
	
	public int getParameterValueInt(String ID) {
		return Integer.parseInt(getParameterValue(ID));
	}
	
	public double getParameterValueDouble(String ID) {
		return Double.parseDouble(getParameterValue(ID));
	}
	
	public double getNumberParameter(String ID) {
		return getParameterValueDouble(ID);
	}
	
	class SubScenario {
		
		String label;
		List<ScenarioParameter> parameters = new ArrayList<ScenarioParameter>();
		
		SubScenario(String label) {
			this.label = label;
		}
		
		public void addParameter(ScenarioParameter parameter) {
			parameters.add(parameter);
		}
		
		public String getLabel() {
			return label;
		}
		
		public List<ScenarioParameter> getParameters() {
			return parameters;
		}
	}
}
