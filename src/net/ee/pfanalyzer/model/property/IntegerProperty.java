package net.ee.pfanalyzer.model.property;

public class IntegerProperty extends ElementProperty {

	private int value;
	
	public IntegerProperty(String propertyName, String label) {
		super(propertyName, label);
	}

	public IntegerProperty(String propertyName, String label, String description) {
		super(propertyName, label, description);
	}

	@Override
	public String getTextValue() {
		return Integer.toString(getValue());
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
