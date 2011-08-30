package net.ee.pfanalyzer.model.property;

import java.util.ArrayList;
import java.util.List;

public class ElementPropertyList {

	private List<ElementProperty> properties = new ArrayList<ElementProperty>();
	
	public void addProperty(ElementProperty p) {
		properties.add(p);
	}
	
	public int getPropertyCount() {
		return properties.size();
	}
	
	public ElementProperty getProperty(int index) {
		return getProperties().get(index);
	}
	
	public ElementProperty getProperty(String name) {
		for (ElementProperty prop : getProperties()) {
			if(prop.getPropertyName().equals(name))
				return prop;
		}
		return null;
	}
	
	public TextProperty getTextProperty(String name) {
		return (TextProperty) getProperty(name);
	}
	
	public TextProperty getTextProperty(int index) {
		return (TextProperty) getProperty(index);
	}
	
	public IntegerProperty getIntegerProperty(String name) {
		return (IntegerProperty) getProperty(name);
	}
	
	public IntegerProperty getIntegerProperty(int index) {
		return (IntegerProperty) getProperty(index);
	}
	
	public DoubleProperty getDoubleProperty(int index) {
		return (DoubleProperty) getProperty(index);
	}
	
	public List<ElementProperty> getProperties() {
		return properties;
	}
}
