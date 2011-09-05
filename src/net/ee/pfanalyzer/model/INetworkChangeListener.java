package net.ee.pfanalyzer.model;

public interface INetworkChangeListener {

	void networkElementAdded(NetworkChangeEvent event);

	void networkElementRemoved(NetworkChangeEvent event);

	void networkElementChanged(NetworkChangeEvent event);

	void networkChanged(NetworkChangeEvent event);
}
