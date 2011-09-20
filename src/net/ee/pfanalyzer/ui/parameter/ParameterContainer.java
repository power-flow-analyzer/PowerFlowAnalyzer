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
import net.ee.pfanalyzer.model.data.NetworkParameterPurposeRestriction;
import net.ee.pfanalyzer.model.data.NetworkParameterType;
import net.ee.pfanalyzer.model.data.NetworkParameterValueRestriction;
import net.ee.pfanalyzer.ui.util.Group;

public class ParameterContainer extends JPanel {

	private IParameterMasterElement parameterMaster;
	private Map<String, NetworkParameter> properties = new HashMap<String, NetworkParameter>();
	private Box elementContainer;
	private Group currentGroupContainer;
	private boolean editable = true;
	private boolean showNetworkParameters = false;
	private boolean showResultsWhenEditing = true;

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

	public void addParameters(AbstractModelElementData element, AbstractModelElementData master, JComponent parent) {
		if(element.getParent() != null)
			addParameters(element.getParent(), master, parent);
		Group paramPanel = new Group("Parameters of \"" + element.getLabel() + "\"");new JPanel(new GridLayout(0, 2));
		Group resultPanel = new Group("Results of \"" + element.getLabel() + "\"");new JPanel(new GridLayout(0, 2));
		for (NetworkParameter property : element.getParameter()) {
			if(isShowNetworkParameters() == false && element.getParent() == null)
				continue;// do not show network/global parameters
			NetworkParameter propertyValue = parameterMaster.getParameterValue(property.getID());//ModelDBUtils.getParameterValue(master, property.getID());
			boolean isResult = NetworkParameterPurposeRestriction.RESULT.equals(property.getPurpose());
			boolean isScenarioParam = NetworkParameterPurposeRestriction.SCENARIO.equals(property.getPurpose());
			if(isScenarioParam && element != master) {
				parameterAdded(property);
				continue;
			}
			if(isShowResultsWhenEditing() == false && isResult && isEditable()) {
				parameterAdded(property);
				continue;// do not show results in editing mode
			}
			Group panel = isResult ? 
					resultPanel : paramPanel;
			if(propertyValue != null)
				addParameter(property, propertyValue, panel);
			else
				addParameter(property, property, panel);
		}
		if(resultPanel.getComponentCount() > 0)
			addElementGroup(resultPanel);
		if(paramPanel.getComponentCount() > 0)
			addElementGroup(paramPanel);
	}
	
	protected void addParameter(NetworkParameter propertyDefinition, NetworkParameter propertyValue, Group panel) {
		if(isParameterAdded(propertyDefinition))
			return;
		panel.add(createLabel(propertyDefinition));
		boolean editParameter = NetworkParameterPurposeRestriction.PARAMETER.equals(
						propertyDefinition.getPurpose())
				|| NetworkParameterPurposeRestriction.SCENARIO.equals(propertyDefinition.getPurpose());
		if(isEditable() && editParameter) {
			if(NetworkParameterValueRestriction.LIST.equals(propertyDefinition.getRestriction())) {// list
				ParameterValueBox box = new ParameterValueBox(
						parameterMaster, propertyDefinition, propertyValue);
				panel.add(box);
			} else if(parameterMaster instanceof ParameterMasterNetworkElement 
					&& NetworkParameterValueRestriction.EXISTING_PARAMETER_VALUE.equals(propertyDefinition.getRestriction())) {
				ParameterRestrictionValueBox box = new ParameterRestrictionValueBox(
						(ParameterMasterNetworkElement) parameterMaster, propertyDefinition, propertyValue);
				panel.add(box);
			} else {
				if(NetworkParameterType.BOOLEAN.equals(propertyDefinition.getType())) {
					ParameterCheckBox box = new ParameterCheckBox(parameterMaster, propertyDefinition, propertyValue);
					panel.add(box);
				} else if(NetworkParameterType.DOUBLE.equals(propertyDefinition.getType())) {
					ParameterDoubleField box = new ParameterDoubleField(parameterMaster, propertyDefinition, propertyValue);
					panel.add(box);
				} else if(NetworkParameterType.INTEGER.equals(propertyDefinition.getType())) {
					ParameterIntField box = new ParameterIntField(parameterMaster, propertyDefinition, propertyValue);
					panel.add(box);
				} else { //if(NetworkParameterType.TEXT.equals(propertyDefinition.getType())) {
					ParameterTextField box = new ParameterTextField(parameterMaster, propertyDefinition, propertyValue);
					panel.add(box);
				}
			}
		} else { //if(NetworkParameterPurposeRestriction.RESULT.equals(propertyDefinition.getPurpose())) {
			panel.add(new ParameterTextLabel(parameterMaster, propertyDefinition, propertyValue));
		}
		parameterAdded(propertyDefinition);
	}
	
	protected void parameterAdded(NetworkParameter parameter) {
		properties.put(parameter.getID(), parameter);
	}
	
	protected boolean isParameterAdded(NetworkParameter parameter) {
		return properties.get(parameter.getID()) != null;
	}
	
	protected JLabel createLabel(NetworkParameter parameter) {
		JLabel label = new JLabel(getLabel(parameter) + ": ");
		if(parameter.getDescription() != null && parameter.getDescription().length() > 0)
			label.setToolTipText(parameter.getDescription());
		return label;
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
		currentGroupContainer.add(createModelLink(element));
	}
	
	protected JComponent createModelLink(final AbstractNetworkElement element) {
		String modelName = element.getModelID();
		if(element.getModel() != null)
			modelName = element.getModel().getLabel() + " (\"" + modelName + "\")";
		JPanel linkPanel = new JPanel(new BorderLayout());
		linkPanel.add(new JLabel(modelName), BorderLayout.CENTER);
		if(isEditable()) {
			JButton changeModelButton = PowerFlowAnalyzer.createButton("Select a model from the database", "database_go.png", "Change Model", false);
			changeModelButton.setMargin(new Insets(2, 2, 1, 1));
			changeModelButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					showModelDB(element.getModel());
				}
			});
			linkPanel.add(changeModelButton, BorderLayout.EAST);
		}
		return linkPanel;
	}
	
	protected void removeAllElements() {
		elementContainer.removeAll();
		properties.clear();
	}
	
	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
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
	
	public JComponent getElementContainer() {
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
	
	public boolean isShowNetworkParameters() {
		return showNetworkParameters;
	}

	public void setShowNetworkParameters(boolean showNetworkParameters) {
		this.showNetworkParameters = showNetworkParameters;
	}

	public boolean isShowResultsWhenEditing() {
		return showResultsWhenEditing;
	}

	public void setShowResultsWhenEditing(boolean showResultsWhenEditing) {
		this.showResultsWhenEditing = showResultsWhenEditing;
	}
}
