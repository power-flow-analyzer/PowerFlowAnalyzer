package net.ee.pfanalyzer.model.scenario;

import java.util.ArrayList;
import java.util.List;

public class ScenarioParameter {
	
	public final static String PARAMETER_TYPE_LIST = "list";
	public final static String PARAMETER_TYPE_CHECKBOX = "checkbox";

	private String ID, label;
	private List<String> optionLabels = new ArrayList<String>(), optionValues = new ArrayList<String>();
	private int selectedOption = -1;
	private String selectedValue;
	private String parameterType = PARAMETER_TYPE_LIST;
	private boolean hidden;
	
	public ScenarioParameter(String ID, String label, String type) {
		this(ID);
		setLabel(label);
		setParameterType(type);
	}
	
	public ScenarioParameter(String ID) {
		this(ID, false);
	}
	
	public ScenarioParameter(String ID, boolean hidden) {
		this.ID = ID;
		this.hidden = hidden;
	}
	
	public String getID() {
		return ID;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getParameterType() {
		return parameterType;
	}

	public void setParameterType(String parameterType) {
		if(parameterType == null)
			parameterType = PARAMETER_TYPE_LIST;
		this.parameterType = parameterType;
	}

	public void addOption(String label, String value) {
		if(label == null)
			label = value;
		optionLabels.add(label);
		optionValues.add(value);
	}
	
	public String getOptionValue(int index) {
		return optionValues.get(index);
	}
	
	public String getOptionLabel(int index) {
		return optionLabels.get(index);
	}
	
	public int getOptionCount() {
		return optionValues.size();
	}

	public int getSelectedOption() {
		return selectedOption;
	}

	public void setSelectedOption(int selectedOption) {
		this.selectedOption = selectedOption;
	}
	
	public void setSelectedValue(String value) {
		selectedValue = value;
	}
	
	public String getSelectedValue() {
		return getSelectedOptionValue();
	}
	
	public String getSelectedOptionValue() {
		if(selectedValue != null)
			return selectedValue;
		return getOptionValue(getSelectedOption());
	}
	
	public boolean isHidden() {
		return hidden;
	}
	
	public boolean isListType() {
		return PARAMETER_TYPE_LIST.equalsIgnoreCase(getParameterType());
	}
	
	public boolean isCheckBoxType() {
		return PARAMETER_TYPE_CHECKBOX.equalsIgnoreCase(getParameterType());
	}
}
