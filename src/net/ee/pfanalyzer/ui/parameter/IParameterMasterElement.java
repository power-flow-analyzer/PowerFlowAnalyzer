package net.ee.pfanalyzer.ui.parameter;

import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ParameterSupport;

public interface IParameterMasterElement {
	
	NetworkParameter getParameter(String parameterID, boolean create);

	boolean hasParameterDefinition(String parameterID);
	
	NetworkParameter getParameterValue(String parameterID);
	
	NetworkParameter getOwnParameter(String parameterID);
	
	void removeOwnParameter(String parameterID);
	
	boolean isRequired(String parameterID);
	
	boolean showCheckBoxes(String parameterID);
	
	void fireValueChanged(String parameterID, String oldValue, String newValue);
	
	ParameterSupport getParameterSupport();
}
