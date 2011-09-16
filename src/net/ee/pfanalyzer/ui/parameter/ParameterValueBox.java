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
		String[] options = getOptionLabels();
		String[] newLabels = new String[options.length + 1];
		newLabels[0] = "";
		System.arraycopy(options, 0, newLabels, 1, options.length);
		box.setModel(new DefaultComboBoxModel(newLabels));
	}
	
	protected JComponent getValuePanel() {
		return box;
	}
	
	protected void setValue(String value) {
		int optionIndex = getOptionIndexForValue(value);
		if(optionIndex > -1) {
			box.setSelectedIndex(optionIndex + 1);// first is empty
			oldValue = value;
		} else {
			box.setSelectedIndex(0);
			oldValue = null;
		}
	}
	
	private int getOptionIndexForLabel(String value) {
		for (int i = 0; i < getOptions().size(); i++) {
			if(getOptions().get(i).getLabel().equals(value)) {
				return i;
			}
		}
		return -1;
	}
	
	private int getOptionIndexForValue(String value) {
		for (int i = 0; i < getOptions().size(); i++) {
			if(getOptions().get(i).getValue().equals(value)) {
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(ignoreAction())
			return;
		NetworkParameter property = getMasterElement().getParameter(getPropertyID(), true);
		int optionIndex = getOptionIndexForLabel((String) box.getSelectedItem());
		if(optionIndex > -1) {
			String value = getOptions().get(optionIndex).getValue();
			property.setValue(value);
			fireValueChanged(oldValue, value);
			oldValue = value;
			refresh();
		} else {
		}
	}
}