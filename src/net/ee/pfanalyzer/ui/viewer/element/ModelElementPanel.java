package net.ee.pfanalyzer.ui.viewer.element;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.Branch;
import net.ee.pfanalyzer.model.Bus;
import net.ee.pfanalyzer.model.IDisplayConstants;
import net.ee.pfanalyzer.model.NetworkFlag;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.data.NetworkParameterType;
import net.ee.pfanalyzer.model.data.NetworkParameterValueRestriction;
import net.ee.pfanalyzer.model.util.ElementGroupingUtils;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.preferences.Preferences;
import net.ee.pfanalyzer.ui.parameter.IParameterMasterElement;
import net.ee.pfanalyzer.ui.parameter.ParameterContainer;
import net.ee.pfanalyzer.ui.parameter.ParameterMasterNetworkElement;
import net.ee.pfanalyzer.ui.util.Group;
import net.ee.pfanalyzer.ui.util.ProgressBar;
import net.miginfocom.swing.MigLayout;

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
		
		add(titleLabel, BorderLayout.NORTH);
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
		// add elements connected to this element
		addConnectedElements(element);
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
			if(paramDef != null) {
				addParameter(paramDef, parameter, propGroup);
			} else {
				addParameter(parameter, parameter, propGroup);
//				String tooltipText = "<html>Parameter ID: " + getLabel(parameter);
//				tooltipText += "<br>Value: " + value;
//				tooltipText += "<br><br><i>This is an unknown parameter, i.e. " +
//						"it has no definition in the parameter database.</i>";
//				JLabel label = new JLabel(getLabel(parameter) + ": ");
//				label.setToolTipText(tooltipText);
//				propGroup.add(label);
//				label = new JLabel(value);
//				label.setToolTipText(tooltipText);
//				propGroup.add(label);
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
					panel.add(createLabel(paramDef, propertyValue));
					panel.addElementLink(bus, AbstractNetworkElement.DISPLAY_DEFAULT).setToolTipText(
							ModelDBUtils.getParameterDescription(paramDef, propertyValue, true));
					parameterAdded(paramDef);
					return;
				}
			}
		}
		// hide empty values
		String displayValue = ModelDBUtils.getParameterDisplayValue(element, paramDef, IDisplayConstants.PARAMETER_DISPLAY_DEFAULT);
		if(isEditable() || element == null || displayValue != null && displayValue.length() > 0)
			super.addParameter(paramDef, propertyValue, panel);
	}
	
	protected void addFlags(AbstractNetworkElement childData) {
		if(isEditable())
			return; // do not show flags in editing mode
		Group flagGroup = new Group("Flags");
		flagGroup.setLayout(new MigLayout("", "[]20[]20[]"));
		for (NetworkFlag flag : childData.getFlags()) {
			JLabel label = new JLabel(flag.getLabel());
			Color foreground;
			if(flag.isFailure())
				foreground = Preferences.getFlagFailureColor();
			else if(flag.isWarning())
				foreground = Preferences.getFlagWarningColor();
			else
				foreground = Color.BLACK;
			label.setForeground(foreground);
			flagGroup.add(label);
			double percentage = flag.getPercentage();
			if(percentage > -1) {
				String value = null;
				if(flag.getValueParameter() != null) {
					if(flag.getValue() == null)
						value = ModelDBUtils.getParameterDisplayValue(childData, flag.getValueParameter(), 
								IDisplayConstants.PARAMETER_DISPLAY_DEFAULT);
					else
						value = ModelDBUtils.getParameterDisplayValue(flag.getValue().toString(), 
								childData.getParameterDefinition(flag.getValueParameter()), 
								IDisplayConstants.PARAMETER_DISPLAY_DEFAULT);
				} else if(flag.getValue() != null)
					value = flag.getValue().toString();
				if(value != null) {
					JLabel valueLabel = new JLabel(value + " ");
					valueLabel.setForeground(foreground);
					flagGroup.add(valueLabel);
				}
				ProgressBar progressBar = new ProgressBar((int) Math.floor(percentage), 
						flag.isFailure(), flag.isWarning());
				flagGroup.add(progressBar, "wrap");
			} else
				flagGroup.add(new JLabel("unknown (" + percentage + "%)"), "wrap");
		}
		if(flagGroup.getComponentCount() > 0)
			addElementGroup(flagGroup);
	}
	
	protected void addConnectedElements(AbstractNetworkElement element) {
		if(isEditable() || element instanceof Bus == false)
			return; // do not show connections in editing mode and for non-busses
		Bus bus = (Bus) element;
		Group connGroup = new Group("Connected Elements");
		for (AbstractNetworkElement e : element.getNetwork().getElements()) {
			if(e == element)// do not add itself
				continue;
			if(e instanceof Branch) {
				Branch branch = (Branch) e;
				if(branch.getFromBus() == bus || branch.getToBus() == bus)
					connGroup.addElementLink(e, AbstractNetworkElement.DISPLAY_DEFAULT);
			} else if(ElementGroupingUtils.getParentBus(e) == bus) {
				connGroup.addElementLink(e, AbstractNetworkElement.DISPLAY_DEFAULT);
			}
		}
		if(connGroup.getComponentCount() > 0)
			addElementGroup(connGroup);
	}
}