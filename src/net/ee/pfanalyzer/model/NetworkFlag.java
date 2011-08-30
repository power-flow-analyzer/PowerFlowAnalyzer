package net.ee.pfanalyzer.model;

import java.util.List;

import net.ee.pfanalyzer.model.data.NetworkFlagData;

public class NetworkFlag {

	private NetworkFlagData data;
	
	public NetworkFlag(NetworkFlagData data) {
		this.data = data;
	}
	
	public NetworkFlag(String label) {
		data = new NetworkFlagData();
		setLabel(label);
	}
	
	public NetworkFlagData getData() {
		return data;
	}
	
	public String getLabel() {
		return getData().getLabel();
	}

	public void setLabel(String label) {
		getData().setLabel(label);
	}
	
	public void addParameter(String id) {
		getData().getParameter().add(id);
	}
	
	public List<String> getParameters() {
		return getData().getParameter();
	}
	
	public boolean containsParameter(String parameterID) {
		return getData().getParameter().contains(parameterID);
	}

	public boolean isCorrect() {
		return ! getData().isFailure();
	}

	public boolean isWarning() {
		return getData().isWarning();
	}

	public void setWarning(boolean warning) {
		getData().setWarning(warning);
	}

	public boolean isFailure() {
		return getData().isFailure();
	}

	public void setFailure(boolean failure) {
		getData().setFailure(failure);
	}

	public double getPercentage() {
		return getData().getPercentage();
	}

	public void setPercentage(double percentage) {
		getData().setPercentage(percentage);
	}
}
