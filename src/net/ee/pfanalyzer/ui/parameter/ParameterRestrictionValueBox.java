package net.ee.pfanalyzer.ui.parameter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.data.NetworkParameter;

public class ParameterRestrictionValueBox extends ParameterValuePanel implements ActionListener {
	
	private JComboBox box;
	private String oldValue;
	Vector<String> values;
	
	public ParameterRestrictionValueBox(ParameterMasterNetworkElement element, NetworkParameter property, NetworkParameter propertyValue) {
		super(element, property, propertyValue);
		box.addActionListener(this);
	}
	
	public Network getNetwork() {
		return ((ParameterMasterNetworkElement) getMasterElement()).getMasterElement().getNetwork();
	}
	
	protected void createValuePanel() {
		values = new Vector<String>();
		box = new JComboBox();
		String parameterID = getDisplayOptions().getParameterID();
		if(parameterID == null || parameterID.isEmpty())
			return;
		Vector<String> labels = new Vector<String>();
		String elementRestriction = getDisplayOptions().getElementRestriction();
		List<AbstractNetworkElement> elements = getNetwork().getElements(elementRestriction);
		for (AbstractNetworkElement element : elements) {
			NetworkParameter parameter = element.getParameterValue(parameterID);
			if(parameter != null && parameter.getValue() != null) {
				values.add(parameter.getValue());
				labels.add(element.getParameterDisplayValue(parameterID));
			}
		}
		box.setModel(new DefaultComboBoxModel(labels));
	}
	
	protected JComponent getValuePanel() {
		return box;
	}
	
	protected void setValue(String value) {
		box.setSelectedItem(value);
		
//		int optionIndex = getOptionIndexForValue(value);
//		if(optionIndex > -1) {
//			box.setSelectedIndex(optionIndex + 1);// first is empty
//			oldValue = value;
//		} else {
//			box.setSelectedIndex(0);
//			oldValue = null;
//		}
	}
	
//	private int getOptionIndexForLabel(String value) {
//		for (int i = 0; i < getOptions().size(); i++) {
//			if(getOptions().get(i).getLabel().equals(value)) {
//				return i;
//			}
//		}
//		return -1;
//	}
	
//	private int getOptionIndexForValue(String value) {
//		for (int i = 0; i < getOptions().size(); i++) {
//			if(getOptions().get(i).getValue().equals(value)) {
//				return i;
//			}
//		}
//		return -1;
//	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(ignoreAction())
			return;
		NetworkParameter property = getMasterElement().getParameter(getPropertyID(), true);
//		int optionIndex = getOptionIndexForLabel((String) box.getSelectedItem());
//		String value = null;
//		if(optionIndex > -1) {
//			value = getOptions().get(optionIndex).getValue();
//		} else {
//		}
		String value = values.get(box.getSelectedIndex());
		property.setValue(value);
		fireValueChanged(oldValue, value);
		oldValue = value;
		refresh();
	}
}