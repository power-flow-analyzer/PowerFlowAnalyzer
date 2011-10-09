package net.ee.pfanalyzer.ui.parameter;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.ee.pfanalyzer.model.data.NetworkParameter;

public class ParameterNumberSpinnerField extends ParameterValuePanel {

	private JSpinner spinner;
	private NumberEditor editor;
	private boolean isInteger;
	private Double value;
	
	public ParameterNumberSpinnerField(IParameterMasterElement element, NetworkParameter property,	NetworkParameter propertyValue, boolean isInteger) {
		super(element, property, propertyValue);
		this.isInteger = isInteger;// TODO is not TRUE during call to createValuePanel() through super constructor; is always double
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
			editor = new NumberEditor(getNumberDecimalFormatPattern());
			spinner.setEditor(editor);
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
	
	protected void setValue(String value) {
		editor.setValue(value);
	}
	
	protected String getTextValue(Object value) {
		if(isInteger)
			return ((Double) value).toString();
		else
			return new DecimalFormat(getNumberDecimalFormatPattern(), 
					new DecimalFormatSymbols(Locale.ENGLISH)).format(((Double) value).doubleValue());
	}

	private void valueChanged() {
		NetworkParameter property = getMasterElement().getParameter(getPropertyID(), true);
		String newValue = value == null ? null : getTextValue(value);
		String oldValue = property.getValue();
		property.setValue(newValue);
		fireValueChanged(oldValue, newValue);
		refresh();
	}
	
	class NumberEditor extends JTextField implements KeyListener, ChangeListener, FocusListener {
		
		DecimalFormat format;
		boolean selfSelection = false;
		boolean hasErrors = false;
		
		NumberEditor(String decimalPattern) {
			setHorizontalAlignment(JTextField.RIGHT);
			format = new DecimalFormat(decimalPattern);
			spinner.addChangeListener(this);
			addKeyListener(this);
			addFocusListener(this);
		}
		
		protected void setValue(String value) {
			selfSelection = true;
			spinner.setValue(new Double(value));
			setText(format.format(spinner.getValue()));
			selfSelection = false;
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			if( ! selfSelection) {
				setText(format.format(spinner.getValue()));
				commit();
			}
		}
		
		private void commit() {
			selfSelection = true;
			String text = getText();
			if(text.isEmpty()) {
				value = null;
				spinner.setValue(new Double(0));
				setBackground(Color.YELLOW);
				hasErrors = false;
			} else {
				char separator = format.getDecimalFormatSymbols().getDecimalSeparator();
				if(separator == ',' && text.contains(".")) {
					text = text.replace('.', ',');
				} else if(separator == '.' && text.contains(",")) {
					text = text.replace(',', '.');
				}
				try {
					Number number = format.parse(text);
					value = number.doubleValue();
					spinner.setValue(value);
					setBackground(Color.YELLOW);
					hasErrors = false;
				} catch (ParseException e) {
					setBackground(Color.RED);
					hasErrors = true;
				}
			}
			valueChanged();
			selfSelection = false;
		}

		@Override
		public void keyReleased(KeyEvent e) {
			commit();
		}

		@Override
		public void keyPressed(KeyEvent e) {
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void focusGained(FocusEvent e) {
			setBackground(Color.YELLOW);
		}

		@Override
		public void focusLost(FocusEvent e) {
			if(hasErrors) {
				if(value == null)
					setText("");
				else
					setText(format.format(spinner.getValue()));
			}
			setBackground(Color.WHITE);
		}
	}
}