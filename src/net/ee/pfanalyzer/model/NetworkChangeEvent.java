package net.ee.pfanalyzer.model;


public class NetworkChangeEvent {
	
	private Network network;

	private AbstractNetworkElement networkElement;
	
//	private List<NetworkParameter> parameters = new ArrayList<NetworkParameter>();
	
	private String parameterID, oldValue, newValue;
	
	public NetworkChangeEvent() {
		
	}
	
	public NetworkChangeEvent(Network network, String parameterID, String oldValue, String newValue) {
		setNetwork(network);
		setParameterID(parameterID);
		setOldValue(oldValue);
		setNewValue(newValue);
	}
	
	public NetworkChangeEvent(AbstractNetworkElement networkElement, String oldValue, String newValue) {
		setNetworkElement(networkElement);
		setOldValue(oldValue);
		setNewValue(newValue);
	}
	
	public NetworkChangeEvent(AbstractNetworkElement networkElement, String parameterID, String oldValue, String newValue) {
		setNetworkElement(networkElement);
		setParameterID(parameterID);
		setOldValue(oldValue);
		setNewValue(newValue);
	}
	
	public NetworkChangeEvent(Network network) {
		setNetwork(network);
	}

	public Network getNetwork() {
		return network;
	}

	public void setNetwork(Network network) {
		this.network = network;
	}

	public AbstractNetworkElement getNetworkElement() {
		return networkElement;
	}

	public void setNetworkElement(AbstractNetworkElement networkElement) {
		this.networkElement = networkElement;
	}

	public String getParameterID() {
		return parameterID;
	}

	public void setParameterID(String parameterID) {
		this.parameterID = parameterID;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
}
