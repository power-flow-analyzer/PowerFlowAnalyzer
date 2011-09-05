package net.ee.pfanalyzer.ui.parameter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import net.ee.pfanalyzer.model.data.NetworkParameter;

public class ParameterValueBox extends ParameterValuePanel implements ActionListener {
	
	private JComboBox box;
	private String oldValue;
	
	public ParameterValueBox(IParameterMasterElement element, NetworkParameter property, NetworkParameter propertyValue) {
		super(element, property, propertyValue);
		box.addActionListener(this);
	}
	
	protected void createValuePanel() {
		box = new JComboBox();
		box.setModel(new DefaultComboBoxModel(getOptionLabels()));
	}
	
	protected JComponent getValuePanel() {
		return box;
	}
	
	protected void setValue(String value) {
		for (int i = 0; i < getOptions().size(); i++) {
			if(getOptions().get(i).getValue().equals(value)) {
				box.setSelectedIndex(i);
				oldValue = value;
				break;
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(ignoreAction())
			return;
		NetworkParameter property = getMasterElement().getParameter(getPropertyID(), true);
		String value = getOptions().get(box.getSelectedIndex()).getValue();
		property.setValue(value);
		fireValueChanged(oldValue, value);
		oldValue = value;
		refresh();
	}
}