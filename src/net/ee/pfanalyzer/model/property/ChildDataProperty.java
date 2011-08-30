package net.ee.pfanalyzer.model.property;

public class ChildDataProperty extends ElementProperty {

	public ChildDataProperty(String propertyName, String label) {
		super(propertyName, label);
	}

	public ChildDataProperty(String propertyName, String label, String description) {
		super(propertyName, label, description);
	}

	@Override
	public String getTextValue() {
		return null;
	}
}
