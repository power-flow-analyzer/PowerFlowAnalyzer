package net.ee.pfanalyzer.ui.parameter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import net.ee.pfanalyzer.model.data.NetworkParameter;

public class ParameterDoubleField extends ParameterNumberSpinnerField {
	
	public ParameterDoubleField(IParameterMasterElement element, NetworkParameter property, NetworkParameter propertyValue) {
		super(element, property, propertyValue, false);
	}
	
	protected void setValue(String value) {
		setSpinnerValue(new Double(value));
	}

	@Override
	protected String getTextValue(Object value) {
		return new DecimalFormat(getNumberDecimalFormatPattern(), 
				new DecimalFormatSymbols(Locale.ENGLISH)).format(((Double) value).doubleValue());
	}
}
