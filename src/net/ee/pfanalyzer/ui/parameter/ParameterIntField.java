package net.ee.pfanalyzer.ui.parameter;

import net.ee.pfanalyzer.model.data.NetworkParameter;

public class ParameterIntField extends ParameterNumberSpinnerField {
	
	public ParameterIntField(IParameterMasterElement element, NetworkParameter property, NetworkParameter propertyValue) {
		super(element, property, propertyValue, true);
	}
	
	protected void setValue(String value) {
		setSpinnerValue(new Double(value));
	}

	@Override
	protected String getTextValue(Object value) {
		return ((Double) value).toString();
	}
}