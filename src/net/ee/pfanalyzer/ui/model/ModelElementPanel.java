package net.ee.pfanalyzer.ui.model;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.Bus;
import net.ee.pfanalyzer.model.NetworkFlag;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.data.NetworkParameterType;
import net.ee.pfanalyzer.model.data.NetworkParameterValueRestriction;
import net.ee.pfanalyzer.ui.parameter.IParameterMasterElement;
import net.ee.pfanalyzer.ui.parameter.ParameterContainer;
import net.ee.pfanalyzer.ui.parameter.ParameterMasterNetworkElement;
import net.ee.pfanalyzer.ui.util.Group;
import net.ee.pfanalyzer.ui.util.ProgressBar;

public class ModelElementPanel extends ParameterContainer {

	private ElementPanelController controller;
	private AbstractNetworkElement element;
	private JLabel titleLabel;
	private Font titleFont = new Font(null, Font.BOLD, 16);
//	private Font groupFont = new Font(null, Font.BOLD, 12);
	
	protected ModelElementPanel(ElementPanelController controller) {
		super(null, true);
		this.controller = controller;
		
		titleLabel = new JLabel("", SwingConstants.CENTER);
		titleLabel.setFont(titleFont);
		titleLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
		add(titleLabel, BorderLayout.NORTH);
		
	}
	
	public ElementPanelController getController() {
		return controller;
	}

	protected void setTitle(String title) {
		titleLabel.setText(title);
	}
	
	private void setCurrentElement(AbstractNetworkElement element) {
		this.element = element;
		IParameterMasterElement parameterMaster = new ParameterMasterNetworkElement(element);
		setParameterMaster(parameterMaster);
	}
	
	protected void addParameters(AbstractNetworkElement element) {
		setCurrentElement(element);
		Group modelGroup = addElementGroup("");
		modelGroup.add(new JLabel("Model: "));
		addModelLink(element);
		// add parameters from model
		if(element.getModel() != null)
			addParameters(element.getModel(), element.getModel(), getElementContainer());
		Group propGroup = addElementGroup("Unknown Parameters");
		for (NetworkParameter parameter : element.getParameterList()) {
//			if(showProperty(prop) == false)
//				continue;
			NetworkParameter paramDef = element.getParameterDefinition(parameter.getID());
			String value = element.getParameterDisplayValue(parameter.getID());
			if(paramDef != null) {
				addParameter(paramDef, parameter, propGroup);
			} else {
				propGroup.add(new JLabel(getLabel(parameter) + ": "));
				propGroup.add(new JLabel(value));
			}
		}
	}
	
	protected void addParameter(NetworkParameter paramDef, NetworkParameter propertyValue, Group panel) {
		if(isParameterAdded(paramDef))
			return;
		NetworkParameterType type = paramDef.getType();
		NetworkParameterValueRestriction restriction = paramDef.getRestriction();
		Bus bus = null;
		if(restriction != null 
				&& NetworkParameterType.INTEGER.equals(type)
				&& NetworkParameterValueRestriction.BUS_NUMBER.equals(restriction)) {
			int busNumber = element.getIntParameter(propertyValue.getID(), -1);
			if(busNumber != -1)
				bus = element.getNetwork().getBus(busNumber);
			if(bus != null && bus != element) {
				panel.add(new JLabel(getLabel(paramDef) + ": "));
				panel.addElementLink(bus);
				parameterAdded(paramDef);
				return;
			}
		}
		super.addParameter(paramDef, propertyValue, panel);
	}
	
//	private boolean showProperty(ElementProperty p) {
//		return getController().getViewerProperty(
//				IPreferenceConstants.PROPERTY_UI_SHOW_MODEL_PREFIX + p.getPropertyName(), true);
//	}
	
	protected void addFlags(AbstractNetworkElement childData) {
		Group flagGroup = addElementGroup("Flags");
		for (NetworkFlag flag : childData.getFlags()) {
			boolean isCorrect = flag.isCorrect();
			JLabel label = new JLabel(flag.getLabel());
			if(isCorrect == false)
				label.setForeground(Color.RED);
			flagGroup.add(label);
			double percentage = flag.getPercentage();
			if(percentage > -1) {
				ProgressBar progressBar = new ProgressBar((int) Math.floor(percentage), isCorrect);
				JPanel resizer = new JPanel(new BorderLayout());
				resizer.add(progressBar, BorderLayout.CENTER);
				resizer.add(new JLabel(" " + (int) Math.floor(percentage) + "% "), BorderLayout.EAST);
				flagGroup.add(resizer);
			} else
				flagGroup.add(new JLabel("unknown"));
		}
	}
}