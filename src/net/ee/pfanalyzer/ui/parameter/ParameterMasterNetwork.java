package net.ee.pfanalyzer.ui.parameter;

import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.NetworkChangeEvent;
import net.ee.pfanalyzer.model.data.NetworkParameter;

public class ParameterMasterNetwork implements IParameterMasterElement {

	private Network network;
	
	public ParameterMasterNetwork(Network network) {
		this.network = network;
	}
	
	protected Network getNetwork() {
		return network;
	}
	
	@Override
	public boolean isRequired(String parameterID) {
		return false;
	}
	
	@Override
	public NetworkParameter getParameter(String parameterID, boolean create) {
		return network.getParameter(parameterID, create);
	}
	
	@Override
	public boolean hasParameterDefinition(String parameterID) {
		return false;// networks never contain parameter definitions
	}

	@Override
	public NetworkParameter getParameterValue(String parameterID) {
		return network.getParameterValue(parameterID);
	}
	
	@Override
	public NetworkParameter getOwnParameter(String parameterID) {
		return network.getOwnParameter(parameterID);
	}
	
	@Override
	public void removeOwnParameter(String parameterID) {
//		network.removeOwnParameter(parameterID);
	}
	
	@Override
	public void fireValueChanged(String parameterID, String oldValue, String newValue) {
		NetworkChangeEvent event = new NetworkChangeEvent(network, parameterID, oldValue, newValue);
		network.fireNetworkElementChanged(event);
	}
}
