package net.ee.pfanalyzer.ui.viewer.element;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import net.ee.pfanalyzer.PowerFlowAnalyzer;
import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.Bus;
import net.ee.pfanalyzer.model.NetworkFlag;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.data.NetworkParameterType;
import net.ee.pfanalyzer.model.data.NetworkParameterValueRestriction;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.preferences.Preferences;
import net.ee.pfanalyzer.ui.parameter.IParameterMasterElement;
import net.ee.pfanalyzer.ui.parameter.ParameterContainer;
import net.ee.pfanalyzer.ui.parameter.ParameterMasterNetworkElement;
import net.ee.pfanalyzer.ui.util.Group;
import net.ee.pfanalyzer.ui.util.ProgressBar;

public class ModelElementPanel extends ParameterContainer {

	private ElementViewer viewer;
	private AbstractNetworkElement element;
	private JLabel titleLabel;
	private Font titleFont = new Font(null, Font.BOLD, 16);
//	private Font groupFont = new Font(null, Font.BOLD, 12);
	
	public ModelElementPanel(ElementViewer viewer) {
		super(null, true);
		this.viewer = viewer;
		setEditable(false);// default setting
		
		titleLabel = new JLabel("", SwingConstants.CENTER);
		titleLabel.setFont(titleFont);
		titleLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
		
		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.setOpaque(false);
		titlePanel.add(titleLabel, BorderLayout.CENTER);
		if(getElementViewer() != null) {
			final JToggleButton editButton = PowerFlowAnalyzer.createToggleButton("Toggle editing mode", 
					"pencil.png", "Toggle editing mode", false);
			editButton.setMargin(new Insets(2, 2, 1, 1));
			editButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setEditable( editButton.isSelected());
					getElementViewer().reloadCard();
				}
			});
			JPanel buttonPanel = new JPanel();
			buttonPanel.setOpaque(false);
			buttonPanel.add(editButton);
			titlePanel.add(buttonPanel, BorderLayout.EAST);
		}
		
		add(titlePanel, BorderLayout.NORTH);
	}
	
	public ElementViewer getElementViewer() {
		return viewer;
	}

	protected void setTitle(String title) {
		titleLabel.setText(title);
	}
	
	private void setCurrentElement(AbstractNetworkElement element) {
		this.element = element;
		IParameterMasterElement parameterMaster = new ParameterMasterNetworkElement(element);
		setParameterMaster(parameterMaster);
	}
	
	public void setNetworkElement(AbstractNetworkElement element) {
		// remove old elements
		removeAllElements();
		// set title
		setTitle(element.getDisplayName(AbstractNetworkElement.DISPLAY_DEFAULT));
		// set element
		setCurrentElement(element);
		Group modelGroup = addElementGroup("");
		modelGroup.add(new JLabel("Model: "));
		addModelLink(element);
		// show flags
		addFlags(element);
		// show properties
		addParameters(element);
		finishLayout();
	}
	
	private void addParameters(AbstractNetworkElement element) {
		// add parameters from model
		if(element.getModel() != null)
			addParameters(element.getModel(), element.getModel(), getElementContainer());
		Group propGroup = new Group("Unknown Parameters");
		for (NetworkParameter parameter : element.getParameterList()) {
			NetworkParameter paramDef = element.getParameterDefinition(parameter.getID());
			String value = element.getParameterDisplayValue(parameter.getID());
			if(paramDef != null) {
				addParameter(paramDef, parameter, propGroup);
			} else {
				String tooltipText = "<html>Parameter ID: " + getLabel(parameter);
				tooltipText += "<br>Value: " + value;
				tooltipText += "<br><br><i>This is an unknown parameter, i.e. " +
						"it has no definition in the parameter database.</i>";
				JLabel label = new JLabel(getLabel(parameter) + ": ");
				label.setToolTipText(tooltipText);
				propGroup.add(label);
				label = new JLabel(value);
				label.setToolTipText(tooltipText);
				propGroup.add(label);
			}
		}
		if(propGroup.getComponentCount() > 0)
			addElementGroup(propGroup);
	}
	
	public void addParameter(NetworkParameter paramDef, NetworkParameter propertyValue, Group panel) {
		if(isParameterAdded(paramDef))
			return;
		if(isEditable() == false) {
			NetworkParameterType type = paramDef.getType();
			NetworkParameterValueRestriction restriction = paramDef.getRestriction();
			if(restriction != null 
					&& NetworkParameterType.INTEGER.equals(type)
					&& NetworkParameterValueRestriction.EXISTING_PARAMETER_VALUE.equals(restriction)) {
				int busNumber = element.getIntParameter(propertyValue.getID(), -1);
				Bus bus = null;
				if(busNumber != -1)
					bus = element.getNetwork().getBus(busNumber);// TODO verallgemeinern
				if(bus != null && bus != element) {
					panel.add(new JLabel(getLabel(paramDef) + ": "));
					panel.addElementLink(bus, AbstractNetworkElement.DISPLAY_DEFAULT);
					parameterAdded(paramDef);
					return;
				}
			}
		}
		// hide empty values
		String displayValue = ModelDBUtils.getParameterDisplayValue(element, paramDef);
		if(isEditable() || element == null || displayValue != null && displayValue.length() > 0)
			super.addParameter(paramDef, propertyValue, panel);
	}
	
	protected void addFlags(AbstractNetworkElement childData) {
		if(isEditable())
			return; // do not show flags in editing mode
		Group flagGroup = new Group("Flags");
		for (NetworkFlag flag : childData.getFlags()) {
			JLabel label = new JLabel(flag.getLabel());
			if(flag.isFailure())
				label.setForeground(Preferences.getFlagFailureColor());
			else if(flag.isWarning())
				label.setForeground(Preferences.getFlagWarningColor());
			flagGroup.add(label);
			double percentage = flag.getPercentage();
			if(percentage > -1) {
				ProgressBar progressBar = new ProgressBar((int) Math.floor(percentage), 
						flag.isFailure(), flag.isWarning());
				JPanel resizer = new JPanel(new BorderLayout());
				resizer.add(progressBar, BorderLayout.CENTER);
//				resizer.add(new JLabel(" " + (int) Math.floor(percentage) + "% "), BorderLayout.EAST);
				flagGroup.add(resizer);
			} else
				flagGroup.add(new JLabel("unknown (" + percentage + "%)"));
		}
		if(flagGroup.getComponentCount() > 0)
			addElementGroup(flagGroup);
	}
}