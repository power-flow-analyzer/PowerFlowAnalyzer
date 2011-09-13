package net.ee.pfanalyzer.ui.parameter;

import javax.swing.JComponent;
import javax.swing.JLabel;

import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ModelDBUtils;

public class ParameterTextLabel extends ParameterValuePanel {
	
	private NetworkParameter propertyDefinition;
	private JLabel label;
	
	public ParameterTextLabel(IParameterMasterElement element, 
			NetworkParameter property, NetworkParameter propertyValue) {
		super(element, property, propertyValue, false);
		this.propertyDefinition = property;
		setValue(propertyValue);
	}
	
	protected void createValuePanel() {
		label = new JLabel("<no value>");
	}
	
	protected JComponent getValuePanel() {
		return label;
	}
	
	protected void setValue(String value) {
		String text;
		if(propertyDefinition != null && getMasterElement().getParameterSupport() != null)
			text = ModelDBUtils.getParameterDisplayValue(
					getMasterElement().getParameterSupport(), propertyDefinition);
		else
			text = value;
		if(text == null || text.isEmpty())
			text = "<no value>";
		label.setText(text);
	}
}
