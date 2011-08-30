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
		property.setValue(Boolean.toString(checkBox.isSelected()));
		refresh();
//		int option = -1;
//		for (int i = 0; i < labels.length; i++) {
//			if(isSelected()) {
//				if(isSelectedText(labels[i])) {
//					option = i;
//					break;
//				}
//			} else {
//				if(isDeselectedText(labels[i])) {
//					option = i;
//					break;
//				}
//			}
//		}
//		if(option > -1)
//			getScenario().getParameter(parameterID).setSelectedOption(option);
	}
//	
//	private boolean isSelectedText(String text) {
//		return text.equalsIgnoreCase("yes") 
//				|| text.equalsIgnoreCase("true")
//					|| text.equalsIgnoreCase("1");
//	}
//	
//	private boolean isDeselectedText(String text) {
//		return text.equalsIgnoreCase("no") 
//				|| text.equalsIgnoreCase("false")
//					|| text.equalsIgnoreCase("0");
//	}
	
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