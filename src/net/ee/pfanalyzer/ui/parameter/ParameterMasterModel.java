package net.ee.pfanalyzer.ui.parameter;

import net.ee.pfanalyzer.model.data.AbstractModelElementData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ModelDBUtils;

public class ParameterMasterModel implements IParameterMasterElement {

	private AbstractModelElementData master;
	
	public ParameterMasterModel(AbstractModelElementData element) {
		this.master = element;
	}
	
	protected AbstractModelElementData getMasterElement() {
		return master;
	}
	
	@Override
	public boolean isRequired(String parameterID) {
		return false;
	}

	@Override
	public NetworkParameter getParameter(String parameterID, boolean create) {
		return ModelDBUtils.getParameter(getMasterElement(), parameterID, create);
	}
	
	@Override
	public boolean hasParameterDefinition(String parameterID) {
		return ModelDBUtils.hasParameterDefinition(getMasterElement(), parameterID);
	}

	@Override
	public NetworkParameter getParameterValue(String parameterID) {
		return ModelDBUtils.getParameterValue(getMasterElement(), parameterID);
	}
	
	@Override
	public NetworkParameter getOwnParameter(String parameterID) {
		return ModelDBUtils.getOwnParameter(getMasterElement(), parameterID);
	}
	
	@Override
	public void removeOwnParameter(String parameterID) {
		ModelDBUtils.removeOwnParameter(getMasterElement(), parameterID);
	}
}
