package net.ee.pfanalyzer.model.property;

public class TextProperty extends ElementProperty {

	private String value;

	public TextProperty(String propertyName, String label) {
		super(propertyName, label);
	}

	public TextProperty(String propertyName, String label, String description) {
		super(propertyName, label, description);
	}

	@Override
	public String getTextValue() {
		return getValue();
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
