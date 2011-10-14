package net.ee.pfanalyzer.ui.parameter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import net.ee.pfanalyzer.model.data.NetworkParameter;

public class ParameterCheckBox extends ParameterValuePanel implements ActionListener {
	
	private JCheckBox checkBox;
	
	public ParameterCheckBox(IParameterMasterElement element, NetworkParameter property, NetworkParameter propertyValue) {
		super(element, property, propertyValue);
		checkBox.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(ignoreAction())
			return;
		NetworkParameter property = getMasterElement().getParameter(getPropertyID(), true);
		String value = Boolean.toString(checkBox.isSelected());
		String oldValue = property.getValue();
		property.setValue(value);
		fireValueChanged(oldValue, value);
		refresh();
	}
	
	@Override
	protected void createValuePanel() {
		checkBox = new JCheckBox();
	}

	@Override
	protected JComponent getValuePanel() {
		return checkBox;
	}

	@Override
	protected void setValue(String value) {
		checkBox.setSelected(Boolean.parseBoolean(value));
	}
}