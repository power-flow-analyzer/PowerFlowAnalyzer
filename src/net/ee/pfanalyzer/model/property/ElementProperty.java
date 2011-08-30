package net.ee.pfanalyzer.model.property;

public abstract class ElementProperty {

	private String propertyName, label, description;

	public abstract String getTextValue();

	public ElementProperty(String propertyName, String label) {
		this(propertyName, label, null);
	}

	public ElementProperty(String propertyName, String label, String description) {
		this.propertyName = propertyName;
		this.label = label;
		this.description = description;
	}

	public String getPropertyName() {
		return propertyName;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
