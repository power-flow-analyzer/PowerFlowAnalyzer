package net.ee.pfanalyzer.ui.viewer.element;

public class ElementAttributes {

	private String[] parameterIDs, labels, units;

	public ElementAttributes(String[] parameterIDs, String[] labels, String[] units) {
		this.parameterIDs = parameterIDs;
		this.labels = labels;
		this.units = units;
	}

	public String[] getParameterIDs() {
		return parameterIDs;
	}

	public String[] getLabels() {
		return labels;
	}

	public String[] getUnits() {
		return units;
	}
	
	public int getAttributesCount() {
		return getParameterIDs().length;
	}
}
