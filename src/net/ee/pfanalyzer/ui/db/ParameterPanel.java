package net.ee.pfanalyzer.ui.db;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;

import net.ee.pfanalyzer.model.data.AbstractModelElementData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.data.NetworkParameterType;
import net.ee.pfanalyzer.model.data.NetworkParameterValueDisplay;
import net.ee.pfanalyzer.model.data.NetworkParameterValueOption;
import net.ee.pfanalyzer.model.data.NetworkParameterValueRestriction;
import net.ee.pfanalyzer.model.util.ModelDBUtils;

public class ParameterPanel extends Box {
	
	public final static String PROPERTY_NAME_RELOAD_ELEMENT = "reload-element";

	private DefaultMutableTreeNode node;
	private AbstractModelElementData master;
	private Map<String, NetworkParameter> properties = new HashMap<String, NetworkParameter>();
	private JLabel referenceLabel;
	
	public ParameterPanel(DefaultMutableTreeNode treeNode, AbstractModelElementData element, boolean devMode) {
		super(BoxLayout.Y_AXIS);
		node = treeNode;
		master = element;
		referenceLabel = new JLabel(ModelDBUtils.getParameterID(master));
		JPanel commonPanel = new JPanel(new GridLayout(0, 2));
		commonPanel.setBorder(new TitledBorder("Common Parameters"));
		commonPanel.add(new JLabel("Label: "));
		commonPanel.add(new ParameterLabelBox());
		commonPanel.add(new JLabel("Description: "));
		commonPanel.add(new ParameterDescriptionBox());
		commonPanel.add(new JLabel("Element ID: "));
		commonPanel.add(new ParameterIDBox());
		commonPanel.add(new JLabel("Reference: "));
		commonPanel.add(referenceLabel);
		add(commonPanel);
		addProperties(element, element, this);
	}
	
	private void addProperties(AbstractModelElementData element, AbstractModelElementData master, JComponent parent) {
		if(element.getParent() != null)
			addProperties(element.getParent(), master, parent);
		JPanel resizer = new JPanel(new GridLayout(0, 2));
		resizer.setBorder(new TitledBorder("Parameters of \"" + element.getLabel() + "\""));
		for (NetworkParameter property : element.getParameter()) {
			NetworkParameter propertyValue = ModelDBUtils.getParameterValue(master, property.getID());
			if(propertyValue != null)
				addProperty(property, propertyValue, resizer);
			else
				addProperty(property, property, resizer);
		}
		if(resizer.getComponentCount() > 0)
			parent.add(resizer);
	}
	
	private void addProperty(NetworkParameter propertyDefinition, NetworkParameter propertyValue, JComponent panel) {
		NetworkParameter oldProp = properties.get(propertyDefinition.getID());
		if(oldProp != null) {
			return;
		}
		if(NetworkParameterValueRestriction.LIST.equals(propertyDefinition.getRestriction()) == false) {
			if(NetworkParameterType.BOOLEAN.equals(propertyDefinition.getType())) {
				panel.add(new JLabel(propertyDefinition.getLabel() + ": "));
				panel.add(new ParameterCheckBox(propertyDefinition, propertyValue));
			} else if(NetworkParameterType.DOUBLE.equals(propertyDefinition.getType())) {
				ParameterDoubleField box = new ParameterDoubleField(propertyDefinition, propertyValue);
				panel.add(new JLabel(propertyDefinition.getLabel() + ": "));
				panel.add(box);
			} else if(NetworkParameterType.INTEGER.equals(propertyDefinition.getType())) {
				ParameterIntField box = new ParameterIntField(propertyDefinition, propertyValue);
				panel.add(new JLabel(propertyDefinition.getLabel() + ": "));
				panel.add(box);
			}
		} else if(NetworkParameterValueRestriction.LIST.equals(propertyDefinition.getRestriction())) {
			ParameterValueBox box = new ParameterValueBox(propertyDefinition, propertyValue);
			panel.add(new JLabel(propertyDefinition.getLabel() + ": "));
			panel.add(box);
		}
		properties.put(propertyDefinition.getID(), propertyDefinition);
	}
	
	private void refresh() {
		firePropertyChange(PROPERTY_NAME_RELOAD_ELEMENT, null, node);
	}
	
	private void refreshID() {
		referenceLabel.setText(ModelDBUtils.getParameterID(master));
	}
	
	class ParameterIDBox extends ParameterTextBox implements ActionListener, KeyListener {
		ParameterIDBox() {
			super("ID");
		}
		
		protected String getParameterValue() {
			if(master.getID() == null)
				return "";
			return master.getID().toString();
		}
		
		protected void setParameterValue(String text) {
			if(text == null || text.trim().length() == 0)
				master.setID(null);
			else
				master.setID(text);
		}
		
		@Override
		protected void updateView() {
			refreshID();
		}
	}
	
	class ParameterLabelBox extends ParameterTextBox implements ActionListener, KeyListener {
		ParameterLabelBox() {
			super("LABEL");
		}
		
		protected String getParameterValue() {
			return master.getLabel();
		}
		
		protected void setParameterValue(String text) {
			master.setLabel(text);
		}
		
		@Override
		protected void updateView() {
			refresh();
		}
	}
	
	class ParameterDescriptionBox extends ParameterTextBox implements ActionListener, KeyListener {
		ParameterDescriptionBox() {
			super("DESCR");
		}
		
		protected String getParameterValue() {
			return master.getDescription();
		}
		
		protected void setParameterValue(String text) {
			master.setDescription(text);
		}
	}
	
	abstract class ParameterTextBox extends JTextField implements ActionListener, KeyListener {
		
		private String parameterID;
		
		ParameterTextBox(String parameterID) {
			super();
			this.parameterID = parameterID;
			if(getParameterValue() != null)
				setText(getParameterValue());
			addActionListener(this);
			addKeyListener(this);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			setParameterValue(getText());
			updateView();
		}
		
		protected void updateView() {
			// empty implementation
		}
		
		protected abstract void setParameterValue(String text);
		
		protected abstract String getParameterValue();
		
		public String getParameterID() {
			return parameterID;
		}

		@Override
		public void keyReleased(KeyEvent e) {
			setParameterValue(getText());
			updateView();
		}

		@Override
		public void keyTyped(KeyEvent e) {}
		@Override
		public void keyPressed(KeyEvent e) {}
	}
	
//	private ModelProperty copyProperty(ModelProperty p) {
//		try {
//			return (ModelProperty) CaseSerializer.copy(p);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	abstract class ParameterValuePanel extends JPanel {
		
		private NetworkParameter propertyDefinition, propertyValue;
		private JCheckBox inheritBox, emptyBox;
		private boolean userAction = true;

		ParameterValuePanel(NetworkParameter property, NetworkParameter propertyValue) {
			super(new BorderLayout());
			this.propertyDefinition = property;
			this.propertyValue = propertyValue;
			
			createValuePanel();
			boolean isDefinition = ModelDBUtils.hasParameterDefinition(master, property.getID());
			if(isDefinition) {
				emptyBox = new JCheckBox("empty");
				emptyBox.setToolTipText("Leave this value empty.");
				emptyBox.setSelected(property.isEmpty());
				getValuePanel().setEnabled( ! property.isEmpty());
				add(emptyBox, BorderLayout.EAST);
				
				emptyBox.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						ParameterValuePanel.this.propertyValue.setEmpty(emptyBox.isSelected());
						if(emptyBox.isSelected()) {
							ParameterValuePanel.this.propertyValue.setValue(null);
							getValuePanel().setEnabled(false);
						} else {
							getValuePanel().setEnabled(true);
						}
					}
				});
			} else {
				boolean inherited = ModelDBUtils.getOwnParameter(master, property.getID()) == null;
				inheritBox = new JCheckBox("inherit");
				inheritBox.setToolTipText("Inherit this value from the super type,\ni.e. do not define a particular value for this type.");
				inheritBox.setSelected(inherited);
				getValuePanel().setEnabled( ! inherited);
				add(inheritBox, BorderLayout.EAST);
				
				inheritBox.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if(inheritBox.isSelected()) {
							inheritValue();
						} else {
							getValuePanel().setEnabled(true);
						}
					}
				});
			}
			setValue(propertyValue);
			add(getValuePanel(), BorderLayout.CENTER);
		}
		
		protected String[] getOptionLabels() {
			String[] optionLabels = new String[getOptions().size()];
			for (int i = 0; i < optionLabels.length; i++) {
				optionLabels[i] = getOptions().get(i).getLabel();
			}
			return optionLabels;
		}
		
		protected abstract void setValue(String value);
		
		protected abstract JComponent getValuePanel();
		
		protected abstract void createValuePanel();
		
		private void setValue(NetworkParameter p) {
			if(p.getValue() != null) {
				setValue(p.getValue());
			} else if(p.getDefaultValue() != null)
				setValue(p.getDefaultValue());
		}
		
		private void inheritValue() {
			userAction = false;
			ModelDBUtils.removeOwnProperty(master, getPropertyID());
			propertyValue = ModelDBUtils.getParameterValue(master, getPropertyID());
			setValue(propertyValue);
			getValuePanel().setEnabled(false);
			userAction = true;
		}
		
		protected boolean ignoreAction() {
			return ! userAction;
		}
		
		public String getPropertyID() {
			return propertyDefinition.getID();
		}
		
		protected List<NetworkParameterValueOption> getOptions() {
			return propertyDefinition.getOption();
		}
		
		protected NetworkParameterValueDisplay getDisplayOptions() {
			return propertyDefinition.getDisplay();
		}
		
		protected double getMin() {
			NetworkParameterValueDisplay options = getDisplayOptions();
			if(options != null)
				return options.getMin();
			return 0.0;
		}
		
		protected double getMax() {
			NetworkParameterValueDisplay options = getDisplayOptions();
			if(options != null)
				return options.getMax();
			return 100;
		}
		
		protected double getIncrement() {
			NetworkParameterValueDisplay options = getDisplayOptions();
			if(options != null)
				return options.getIncrement();
			return 1;
		}
		
//		protected double getPrecision() {
//			ModelPropertyDisplay options = getDisplayOptions();
//			if(options != null && options.getPrecision() != null)
//				return options.getPrecision().doubleValue();
//			return 1;
//		}
		
		protected String getDecimalFormatPattern() {
			NetworkParameterValueDisplay options = getDisplayOptions();
			if(options != null && options.getDecimalFormatPattern() != null)
				return options.getDecimalFormatPattern();
			return "#.########";
		}
	}
	
	class ParameterValueBox extends ParameterValuePanel implements ActionListener {
		
		private JComboBox box;
		
		ParameterValueBox(NetworkParameter property, NetworkParameter propertyValue) {
			super(property, propertyValue);
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
					break;
				}
			}
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(ignoreAction())
				return;
			NetworkParameter property = ModelDBUtils.getParameter(master, getPropertyID(), true);
			String value = getOptions().get(box.getSelectedIndex()).getValue();
			property.setValue(value);
			refresh();
		}
	}
	
	abstract class ParameterNumberSpinnerField extends ParameterValuePanel implements ChangeListener {

		private JSpinner spinner;
		private boolean isInteger;
		
		ParameterNumberSpinnerField(NetworkParameter property,	NetworkParameter propertyValue, boolean isInteger) {
			super(property, propertyValue);
			this.isInteger = isInteger;
			spinner.addChangeListener(this);
		}
		
		protected void createValuePanel() {
			spinner = new JSpinner();
			if(isInteger) {
				int min = (int) getMin();
				int max = (int) getMax();
				int inc = (int) getIncrement();
				spinner.setModel(new SpinnerNumberModel(0, min, max, inc));
			} else {
				spinner.setModel(new SpinnerNumberModel(0, getMin(), getMax(), getIncrement()));
				spinner.setEditor(new JSpinner.NumberEditor(spinner, getDecimalFormatPattern()));
			}
		}
		
		protected JComponent getValuePanel() {
			return spinner;
		}
		
		protected void setSpinnerValue(Object value) {
			spinner.setValue(value);
		}
		
		protected abstract String getTextValue(Object value);

		@Override
		public void stateChanged(ChangeEvent e) {
			if(ignoreAction())
				return;
			NetworkParameter property = ModelDBUtils.getParameter(master, getPropertyID(), true);
			property.setValue(getTextValue(spinner.getValue()));
			refresh();
		}
	}
	
	class ParameterDoubleField extends ParameterNumberSpinnerField {
		
		ParameterDoubleField(NetworkParameter property, NetworkParameter propertyValue) {
			super(property, propertyValue, false);
		}
		
		protected void setValue(String value) {
			setSpinnerValue(new Double(value));
		}

		@Override
		protected String getTextValue(Object value) {
			return new DecimalFormat(getDecimalFormatPattern(), 
					new DecimalFormatSymbols(Locale.ENGLISH)).format(((Double) value).doubleValue());
		}
	}
	
	class ParameterIntField extends ParameterNumberSpinnerField {
		
		ParameterIntField(NetworkParameter property, NetworkParameter propertyValue) {
			super(property, propertyValue, true);
		}
		
		protected void setValue(String value) {
			setSpinnerValue(new Double(value));
		}

		@Override
		protected String getTextValue(Object value) {
			return ((Double) value).toString();
		}
	}
	
	class ParameterCheckBox extends ParameterValuePanel implements ActionListener {
		
		private JCheckBox checkBox;
		
		ParameterCheckBox(NetworkParameter property, NetworkParameter propertyValue) {
			super(property, propertyValue);
			checkBox.addActionListener(this);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(ignoreAction())
				return;
			NetworkParameter property = ModelDBUtils.getParameter(master, getPropertyID(), true);
			property.setValue(Boolean.toString(checkBox.isSelected()));
			refresh();
//			int option = -1;
//			for (int i = 0; i < labels.length; i++) {
//				if(isSelected()) {
//					if(isSelectedText(labels[i])) {
//						option = i;
//						break;
//					}
//				} else {
//					if(isDeselectedText(labels[i])) {
//						option = i;
//						break;
//					}
//				}
//			}
//			if(option > -1)
//				getScenario().getParameter(parameterID).setSelectedOption(option);
		}
//		
//		private boolean isSelectedText(String text) {
//			return text.equalsIgnoreCase("yes") 
//					|| text.equalsIgnoreCase("true")
//						|| text.equalsIgnoreCase("1");
//		}
//		
//		private boolean isDeselectedText(String text) {
//			return text.equalsIgnoreCase("no") 
//					|| text.equalsIgnoreCase("false")
//						|| text.equalsIgnoreCase("0");
//		}
		
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
}
