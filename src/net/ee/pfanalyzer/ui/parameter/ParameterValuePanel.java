package net.ee.pfanalyzer.ui.parameter;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.data.NetworkParameterType;
import net.ee.pfanalyzer.model.data.NetworkParameterValueDisplay;
import net.ee.pfanalyzer.model.data.NetworkParameterValueOption;

public abstract class ParameterValuePanel extends JPanel {
	
	private IParameterMasterElement master;
	private NetworkParameter propertyDefinition, propertyValue;
	private JCheckBox inheritBox, emptyBox;
	private boolean userAction = true;

	public ParameterValuePanel(IParameterMasterElement element, NetworkParameter property, NetworkParameter propertyValue) {
		super(new BorderLayout());
		this.master = element;
		this.propertyDefinition = property;
		this.propertyValue = propertyValue;
		
		createValuePanel();
		boolean isDefinition = getMasterElement().hasParameterDefinition(property.getID());
		if(isDefinition) {
			emptyBox = new JCheckBox("empty");
			emptyBox.setToolTipText("Leave this value empty.");
			emptyBox.setSelected(propertyValue.isEmpty());
			getValuePanel().setEnabled( ! propertyValue.isEmpty());
			add(emptyBox, BorderLayout.EAST);
			
			emptyBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					ParameterValuePanel.this.propertyValue.setEmpty(emptyBox.isSelected());
					if(emptyBox.isSelected()) {
						ParameterValuePanel.this.propertyValue.setValue(null);
						getValuePanel().setEnabled(false);
					} else {
						getValuePanel().setEnabled(true);
					}
				}
			});
		} else {
			boolean required = getMasterElement().isRequired(property.getID());
			boolean inherited = required ? false : getMasterElement().getOwnParameter(property.getID()) == null;
			inheritBox = new JCheckBox("inherit");
			if(required)
				inheritBox.setToolTipText("<html>This value cannot be inherited from the super type(s)<br>since no value is specified there.");
			else
				inheritBox.setToolTipText("<html>Inherit this value from the super type(s),<br>i.e. do not define a particular value for this type.");
			inheritBox.setSelected(inherited);
			inheritBox.setEnabled( ! required);
			getValuePanel().setEnabled( ! inherited);
			add(inheritBox, BorderLayout.EAST);
			
			inheritBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(inheritBox.isSelected()) {
						inheritValue();
					} else {
						getValuePanel().setEnabled(true);
					}
				}
			});
		}
		setValue(propertyValue);
		add(getValuePanel(), BorderLayout.CENTER);
	}
	
	protected IParameterMasterElement getMasterElement() {
		return master;
	}
	
	protected String[] getOptionLabels() {
		String[] optionLabels = new String[getOptions().size()];
		for (int i = 0; i < optionLabels.length; i++) {
			optionLabels[i] = getOptions().get(i).getLabel();
		}
		return optionLabels;
	}
	
	protected abstract void setValue(String value);
	
	protected abstract JComponent getValuePanel();
	
	protected abstract void createValuePanel();
	
	private void setValue(NetworkParameter p) {
		String value = null;
		if(p.getValue() != null)
			value = p.getValue();
		else if(p.getDefaultValue() != null)
			value = p.getDefaultValue();
		if(value != null) {
			// convert double values to integers if necessary
			// (otherwise option cannot be found by using equals)
			if(NetworkParameterType.INTEGER.equals(propertyDefinition.getType()) && value.endsWith(".0"))
				value = value.substring(0, value.length() - 2);
			// set data in panel
			setValue(value);
		}
	}
	
	private void inheritValue() {
		userAction = false;
		getMasterElement().removeOwnParameter(getPropertyID());
		propertyValue = getMasterElement().getParameterValue(getPropertyID());
		setValue(propertyValue);
		getValuePanel().setEnabled(false);
		userAction = true;
	}
	
	protected boolean ignoreAction() {
		return ! userAction;
	}
	
	public String getPropertyID() {
		return propertyDefinition.getID();
	}
	
	protected List<NetworkParameterValueOption> getOptions() {
		return propertyDefinition.getOption();
	}
	
	protected NetworkParameterValueDisplay getDisplayOptions() {
		return propertyDefinition.getDisplay();
	}
	
	protected double getMin() {
		NetworkParameterValueDisplay options = getDisplayOptions();
		if(options == null || options.getMin() == null)
			return Double.NaN;
		return options.getMin();
	}
	
	protected double getMax() {
		NetworkParameterValueDisplay options = getDisplayOptions();
		if(options == null || options.getMax() == null)
			return Double.NaN;
		return options.getMax();
	}
	
	protected double getIncrement() {
		NetworkParameterValueDisplay options = getDisplayOptions();
		if(options != null)
			return options.getIncrement();
		return 1;
	}
	
//	protected double getPrecision() {
//		ModelPropertyDisplay options = getDisplayOptions();
//		if(options != null && options.getPrecision() != null)
//			return options.getPrecision().doubleValue();
//		return 1;
//	}
	
	protected String getDecimalFormatPattern() {
		NetworkParameterValueDisplay options = getDisplayOptions();
		if(options != null && options.getDecimalFormatPattern() != null)
			return options.getDecimalFormatPattern();
		return "#.########";
	}
	
	protected void refresh() {
		
	}
}