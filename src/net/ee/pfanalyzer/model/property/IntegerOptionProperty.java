package net.ee.pfanalyzer.model.property;

public class IntegerOptionProperty extends IntegerProperty {

	private String[] optionLabels;
	private int[] optionValues;
	
	public IntegerOptionProperty(String propertyName, String label, String[] optionLabels, int[] optionValues) {
		super(propertyName, label);
		this.optionLabels = optionLabels;
		this.optionValues = optionValues;
	}

	@Override
	public String getTextValue() {
		for (int i = 0; i < optionValues.length; i++) {
			if(optionValues[i] == getValue())
				return optionLabels[i] + " (" + getValue() + ")";
		}
		return super.getTextValue();
	}
}
