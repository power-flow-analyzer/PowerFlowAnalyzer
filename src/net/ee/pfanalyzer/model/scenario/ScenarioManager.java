package net.ee.pfanalyzer.model.scenario;

import java.io.FileReader;
import java.util.Properties;

public class ScenarioManager {

	public static Scenario loadScenarioFile(String path) throws Exception {
		Properties props = new Properties();
		props.load(new FileReader(path));
		Scenario scenario = new Scenario();
		int scenarioCounter = 1;
		while(props.containsKey(getScenarioIDPath(props, scenarioCounter))) {
			ScenarioParameter parameter = new ScenarioParameter(
					props.getProperty(getScenarioIDPath(props, scenarioCounter)));
			scenario.addParameter(parameter);
			parameter.setLabel(props.getProperty(
					getScenarioLabelPath(props, scenarioCounter), "Parameter " + scenarioCounter));
			int optionCounter = 1;
			while(props.containsKey(getScenarioOptionValuePath(props, scenarioCounter, optionCounter))) {
				String value = props.getProperty(
						getScenarioOptionValuePath(props, scenarioCounter, optionCounter));
				String label = props.getProperty(
						getScenarioOptionLabelPath(props, scenarioCounter, optionCounter), value);
				parameter.addOption(label, value);
				optionCounter++;
			}
			scenarioCounter++;
		}
		return scenario;
	}
	
	private static String getScenarioIDPath(Properties props, int scenarioCounter) {
		return "parameter" + scenarioCounter + ".ID";
	}
	
	private static String getScenarioLabelPath(Properties props, int scenarioCounter) {
		return "parameter" + scenarioCounter + ".label";
	}
	
	private static String getScenarioOptionLabelPath(Properties props, int scenarioCounter, int optionCounter) {
		return "parameter" + scenarioCounter + ".option" + optionCounter + ".label";
	}
	
	private static String getScenarioOptionValuePath(Properties props, int scenarioCounter, int optionCounter) {
		return "parameter" + scenarioCounter + ".option" + optionCounter + ".value";
	}
}
