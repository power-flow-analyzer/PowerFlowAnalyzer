package net.ee.pfanalyzer.ui.parameter;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import net.ee.pfanalyzer.PowerFlowAnalyzer;
import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.CombinedNetworkElement;
import net.ee.pfanalyzer.model.data.AbstractModelElementData;
import net.ee.pfanalyzer.model.data.ModelData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.data.NetworkParameterType;
import net.ee.pfanalyzer.model.data.NetworkParameterValueRestriction;
import net.ee.pfanalyzer.ui.util.Group;

public class ParameterContainer extends JPanel {

	private IParameterMasterElement parameterMaster;
	private Map<String, NetworkParameter> properties = new HashMap<String, NetworkParameter>();
	private Box elementContainer;
	private Group currentGroupContainer;

	public ParameterContainer(IParameterMasterElement parameterMaster, boolean scrollable) {
		super(new BorderLayout());
		this.parameterMaster = parameterMaster;
		
		elementContainer = Box.createVerticalBox();
		elementContainer.setBorder(new EmptyBorder(0, 10, 10, 10));
		if(scrollable)
			add(new JScrollPane(elementContainer), BorderLayout.CENTER);
		else
			add(elementContainer, BorderLayout.CENTER);
		
		setOpaque(false);
		elementContainer.setOpaque(false);
	}

	public void setParameterMaster(IParameterMasterElement parameterMaster) {
		this.parameterMaster = parameterMaster;
	}

	protected void addParameters(AbstractModelElementData element, AbstractModelElementData master, JComponent parent) {
		if(element.getParent() != null)
			addParameters(element.getParent(), master, parent);
		Group resizer = new Group("Parameters of \"" + element.getLabel() + "\"");new JPanel(new GridLayout(0, 2));
		for (NetworkParameter property : element.getParameter()) {
			NetworkParameter propertyValue = parameterMaster.getParameterValue(property.getID());//ModelDBUtils.getParameterValue(master, property.getID());
			if(propertyValue != null)
				addParameter(property, propertyValue, resizer);
			else
				addParameter(property, property, resizer);
		}
		if(resizer.getComponentCount() > 0)
			addElementGroup(resizer);
	}
	
	protected void addParameter(NetworkParameter propertyDefinition, NetworkParameter propertyValue, Group panel) {
		if(isParameterAdded(propertyDefinition))
			return;
		if(NetworkParameterValueRestriction.LIST.equals(propertyDefinition.getRestriction()) == false) {
			if(NetworkParameterType.BOOLEAN.equals(propertyDefinition.getType())) {
				panel.add(new JLabel(getLabel(propertyDefinition) + ": "));
				panel.add(new ParameterCheckBox(parameterMaster, propertyDefinition, propertyValue));
			} else if(NetworkParameterType.DOUBLE.equals(propertyDefinition.getType())) {
				ParameterDoubleField box = new ParameterDoubleField(parameterMaster, propertyDefinition, propertyValue);
				panel.add(new JLabel(getLabel(propertyDefinition) + ": "));
				panel.add(box);
			} else if(NetworkParameterType.INTEGER.equals(propertyDefinition.getType())) {
				ParameterIntField box = new ParameterIntField(parameterMaster, propertyDefinition, propertyValue);
				panel.add(new JLabel(getLabel(propertyDefinition) + ": "));
				panel.add(box);
			} else { //if(NetworkParameterType.TEXT.equals(propertyDefinition.getType())) {
				ParameterTextField box = new ParameterTextField(parameterMaster, propertyDefinition, propertyValue);
				panel.add(new JLabel(getLabel(propertyDefinition) + ": "));
				panel.add(box);
			}
		} else { // list
			ParameterValueBox box = new ParameterValueBox(parameterMaster, propertyDefinition, propertyValue);
			panel.add(new JLabel(getLabel(propertyDefinition) + ": "));
			panel.add(box);
		}
		parameterAdded(propertyDefinition);
	}
	
	protected void parameterAdded(NetworkParameter parameter) {
		properties.put(parameter.getID(), parameter);
	}
	
	protected boolean isParameterAdded(NetworkParameter parameter) {
		return properties.get(parameter.getID()) != null;
	}
	
	protected String getLabel(NetworkParameter parameter) {
		String label = parameter.getLabel();
		if(label == null || label.isEmpty())
			return parameter.getID();
		return label;
	}
	
	protected void addElementLink(CombinedNetworkElement<?> element) {
		currentGroupContainer.addElementLink(element);
	}
	
	protected void addElementLink(AbstractNetworkElement element, int displayFlags) {
		currentGroupContainer.addElementLink(element, displayFlags);
	}
	
	protected void addModelLink(final AbstractNetworkElement element) {
		String modelName = element.getModelID();
		if(element.getModel() != null)
			modelName = element.getModel().getLabel() + " (\"" + modelName + "\")";
		JButton changeModelButton = PowerFlowAnalyzer.createButton("Select a model from the database", "database_go.png", "Change Model", false);
		changeModelButton.setMargin(new Insets(2, 2, 1, 1));
		changeModelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showModelDB(element.getModel());
			}
		});
		JPanel linkPanel = new JPanel(new BorderLayout());
		linkPanel.add(new JLabel(modelName), BorderLayout.CENTER);
		linkPanel.add(changeModelButton, BorderLayout.EAST);
		currentGroupContainer.add(linkPanel);
	}
	
	protected void removeAllElements() {
		elementContainer.removeAll();
		properties.clear();
	}
	
	protected void addElementGroup(Group group) {
		elementContainer.add(Box.createVerticalStrut(10));
		currentGroupContainer = group;
		elementContainer.add(currentGroupContainer);
	}
	
	protected Group addElementGroup(String name) {
		addElementGroup(new Group(name));
		return currentGroupContainer;
	}
	
	protected JComponent getElementContainer() {
		return elementContainer;
	}
	
	protected void finishLayout() {
//		elementContainer.add(Box.createVerticalGlue());
//		elementContainer.add(Box.createVerticalGlue());
//		elementContainer.add(Box.createVerticalGlue());
	}
	
	private void showModelDB(ModelData model) {
		PowerFlowAnalyzer.getInstance().showModelDBDialog();
	}
}
