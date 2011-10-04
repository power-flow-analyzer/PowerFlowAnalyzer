package net.ee.pfanalyzer.ui.parameter;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.ee.pfanalyzer.model.data.NetworkParameter;

public abstract class ParameterNumberSpinnerField extends ParameterValuePanel implements ChangeListener {

	private JSpinner spinner;
	private boolean isInteger;
	
	public ParameterNumberSpinnerField(IParameterMasterElement element, NetworkParameter property,	NetworkParameter propertyValue, boolean isInteger) {
		super(element, property, propertyValue);
		this.isInteger = isInteger;
		spinner.addChangeListener(this);
	}
	
	protected void createValuePanel() {
		spinner = new JSpinner();
		SpinnerNumberModel model = new SpinnerNumberModel();
		spinner.setModel(model);
		double min = getMin();
		double max = getMax();
		if(isInteger) {
			if(Double.isNaN(min) == false)
				model.setMinimum(new Integer((int) min));
			if(Double.isNaN(max) == false)
				model.setMaximum(new Integer((int) max));
			int inc = (int) getIncrement();
			model.setStepSize(new Integer(inc));
		} else {
			model.setValue(new Double(0));
			if(Double.isNaN(min) == false)
				model.setMinimum(new Double(min));
			if(Double.isNaN(max) == false)
				model.setMaximum(new Double(max));
			model.setStepSize(new Double(getIncrement()));
			spinner.setEditor(new JSpinner.NumberEditor(spinner, getNumberDecimalFormatPattern()));
		}
	}
	
	protected String getNumberDecimalFormatPattern() {
		String pattern = "";
		for (int i = 0; i < getDecimalFormatPattern().length(); i++) {
			char c = getDecimalFormatPattern().charAt(i);
			if(c == '%') {
				pattern += "##";
			} else
				pattern += c;
		}
		return pattern;
	}
	
	protected JComponent getValuePanel() {
		return spinner;
	}
	
	protected void setSpinnerValue(Object value) {
		spinner.setValue(value);
	}
	
	protected abstract String getTextValue(Object value);

	@Override
	public void stateChanged(ChangeEvent e) {
		if(ignoreAction())
			return;
		NetworkParameter property = getMasterElement().getParameter(getPropertyID(), true);
		String newValue = getTextValue(spinner.getValue());
		String oldValue = property.getValue();
		property.setValue(newValue);
		fireValueChanged(oldValue, newValue);
		refresh();
	}
}