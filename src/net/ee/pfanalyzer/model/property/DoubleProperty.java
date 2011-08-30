package net.ee.pfanalyzer.model.property;

public class DoubleProperty extends ElementProperty {

	private double value;
	
	public DoubleProperty(String propertyName, String label) {
		super(propertyName, label);
	}

	public DoubleProperty(String propertyName, String label, String description) {
		super(propertyName, label, description);
	}

	@Override
	public String getTextValue() {
		return Double.toString(getValue());
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
}
