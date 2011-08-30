package net.ee.pfanalyzer.ui.parameter;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ModelDBUtils;

public class ParameterMasterNetworkElement implements IParameterMasterElement {

	private AbstractNetworkElement master;
	
	public ParameterMasterNetworkElement(AbstractNetworkElement element) {
		this.master = element;
	}
	
	protected AbstractNetworkElement getMasterElement() {
		return master;
	}
	
	@Override
	public boolean isRequired(String parameterID) {
		if(getMasterElement().getModel() == null)
			return false;
		NetworkParameter parameter = ModelDBUtils.getParameterValue(getMasterElement().getModel(), parameterID);
		return parameter == null || parameter.getValue() == null;
	}
	
	@Override
	public NetworkParameter getParameter(String parameterID, boolean create) {
		return getMasterElement().getParameter(parameterID, create);
	}
	
	@Override
	public boolean hasParameterDefinition(String parameterID) {
		return false;// network elements never contain parameter definitions
	}

	@Override
	public NetworkParameter getParameterValue(String parameterID) {
		return getMasterElement().getParameterValue(parameterID);
	}
	
	@Override
	public NetworkParameter getOwnParameter(String parameterID) {
		return getMasterElement().getOwnParameter(parameterID);
	}
	
	@Override
	public void removeOwnParameter(String parameterID) {
		getMasterElement().removeOwnParameter(parameterID);
	}
}
