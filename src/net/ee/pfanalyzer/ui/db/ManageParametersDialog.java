package net.ee.pfanalyzer.ui.db;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import net.ee.pfanalyzer.PowerFlowAnalyzer;
import net.ee.pfanalyzer.model.data.AbstractModelElementData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.data.NetworkParameterValueDisplay;
import net.ee.pfanalyzer.model.data.NetworkParameterValueRestriction;
import net.ee.pfanalyzer.model.data.NetworkParameterType;
import net.ee.pfanalyzer.model.data.NetworkParameterValueOption;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.ui.dialog.BaseDialog;

public class ManageParametersDialog extends BaseDialog {
	
//	private ElementPanelController controller;
	
	private AbstractModelElementData element;
	private JComponent propPanel;
	private JList parameterList;
	private ParameterListModel listModel;
	private JButton addButton, removeButton;
	
	public ManageParametersDialog(Frame frame, AbstractModelElementData element) {
		super(frame, "Manage Parameters", true);
		this.element = element;
//		this.controller = controller;
//		setText("The following options apply directly to all views.");
		listModel = new ParameterListModel(element.getParameter());
		parameterList = new JList(listModel);
		parameterList.setCellRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(JList list, Object value, int index,
			        boolean isSelected, boolean cellHasFocus) {
				String text = ((NetworkParameter) value).getLabel();
				if(text == null || text.isEmpty())
					text = "<no label>";
				return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
			}
		});
		parameterList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				showElementProperties(listModel.getSelectedParameter());
				updateButtons(listModel.getSelectedParameter());
			}
		});
		parameterList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		Container contentPane = Box.createVerticalBox();
		JComponent optionsPane = new JPanel(new GridLayout(0, 1));
//		optionsPane.add(Box.createHorizontalGlue());
		optionsPane.setBorder(new TitledBorder("Show Bus Data"));
		contentPane.add(optionsPane);
		addButton("Close", true, true);
		
		propPanel = new JPanel(new BorderLayout());
		JLabel elementTitleLabel = new JLabel("Parameter Attributes");
		elementTitleLabel.setForeground(Color.WHITE);
		JPanel titlePanel = new JPanel();
		titlePanel.setBackground(Color.DARK_GRAY);
		titlePanel.add(elementTitleLabel);
		JPanel propPanelResizer = new JPanel(new BorderLayout());
		propPanelResizer.add(titlePanel, BorderLayout.NORTH);
		propPanelResizer.add(new JScrollPane(propPanel), BorderLayout.CENTER);
//		JPanel propButtonPanel = new JPanel();
//		propPanelResizer.add(propButtonPanel, BorderLayout.SOUTH);
		
		JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		JPanel treeParent = new JPanel(new BorderLayout());
		JLabel treeTitleLabel = new JLabel("Parameter List");
		treeTitleLabel.setForeground(Color.WHITE);
		JPanel treeTitlePanel = new JPanel();
		treeTitlePanel.setBackground(Color.DARK_GRAY);
		treeTitlePanel.add(treeTitleLabel);
		treeParent.add(treeTitlePanel, BorderLayout.NORTH);
		treeParent.add(new JScrollPane(parameterList), BorderLayout.CENTER);
		addButton = PowerFlowAnalyzer.createButton("Add a new parameter", "add.png", "Add", false);
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addParameter();
			}
		});
		removeButton = PowerFlowAnalyzer.createButton("Remove the selected parameter", "delete.png", "Remove...", false);
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeParameter();
			}
		});
		JPanel treeButtonPane = new JPanel();
		treeButtonPane.add(addButton);
		treeButtonPane.add(removeButton);
		treeParent.add(treeButtonPane, BorderLayout.SOUTH);
		
		splitter.setLeftComponent(treeParent);
		splitter.setRightComponent(propPanelResizer);
		splitter.setDividerLocation(150);
		setCenterComponent(splitter, false);
		showElementProperties(null);
		updateButtons(null);
		showDialog(500, 400);
	}
	
	private void updateButtons(NetworkParameter property) {
		removeButton.setEnabled(property != null);
	}
	
	private void showElementProperties(NetworkParameter property) {
		propPanel.removeAll();
		if(property != null) {
			ParameterDefinitionPanel panel = new ParameterDefinitionPanel(property);
			propPanel.add(panel, BorderLayout.NORTH);
		} else if(listModel.getSize() == 0){
			propPanel.add(new JLabel("<html><p>The parameter list is empty.</p>" +
					"<br><p>You can add new parameters with the plus button.</p>"));
		} else {
			propPanel.add(new JLabel("<html><p>Select a parameter from the list on the left side.</p>" +
					"<br><p>You can add new parameters with the plus button.</p>" +
					"<br><p>You can remove existing parameters with the minus button.</p>"));
		}
		propPanel.validate();
		propPanel.repaint();
	}
	
	private void refreshList() {
		parameterList.revalidate();
		parameterList.repaint();
	}
	
	private void addParameter() {
		listModel.addParameter();
		parameterList.setSelectedIndex(listModel.getSize() - 1);
	}
	
	private void removeParameter() {
		NetworkParameter p = listModel.getSelectedParameter();
		if(p != null) {
			int action = JOptionPane.showConfirmDialog(ManageParametersDialog.this, 
					"Do you want to delete this parameter?", "Confirm", JOptionPane.YES_NO_OPTION);
			if(action != JOptionPane.YES_OPTION)
				return;
			listModel.removeParameter(p);
			parameterList.clearSelection();
			refreshList();
			showElementProperties(null);
		}
	}
	
	
	class ParameterDefinitionPanel extends Box {
		
		private NetworkParameter property;
		private NetworkParameterValueDisplay displayOptions;
		private JPanel commonPanel, valuePanel;
		private IntegerRestrictionField restriction;
		
		ParameterDefinitionPanel(NetworkParameter property) {
			super(BoxLayout.Y_AXIS);
			this.property = property;
			
			JPanel typePanel = new JPanel(new GridLayout(0, 2));
			typePanel.setBorder(new TitledBorder(""));
			typePanel.add(new JLabel("Type: "));
			typePanel.add(new TypeField());
			add(typePanel);
			
			commonPanel = new JPanel(new GridLayout(0, 2));
			commonPanel.setBorder(new TitledBorder("General Attributes"));
			commonPanel.add(new JLabel("ID: "));
			commonPanel.add(new IDField());
			commonPanel.add(new JLabel("Label: "));
			commonPanel.add(new LabelField());
			commonPanel.add(new JLabel("Description: "));
			commonPanel.add(new DescriptionField());
			commonPanel.add(new JLabel("Restriction: "));
			restriction = new IntegerRestrictionField();
			commonPanel.add(restriction);
			add(commonPanel);
			
			valuePanel = new JPanel(new GridLayout(0, 2));
			valuePanel.setBorder(new TitledBorder("Value"));
			add(valuePanel);
			
			displayOptions = property.getDisplay();
			if(displayOptions == null) {
				displayOptions = new NetworkParameterValueDisplay();
				property.setDisplay(displayOptions);
			}
			
			typeChanged();
		}
		
		private void typeChanged() {
			commonPanel.remove(restriction);
			restriction = new IntegerRestrictionField();
			commonPanel.add(restriction);
			valuePanel.removeAll();
			if(NetworkParameterValueRestriction.NONE.equals(property.getRestriction())) {
				if(NetworkParameterType.INTEGER.equals(property.getType())) {
					valuePanel.setLayout(new GridLayout(0, 2));
					valuePanel.add(new JLabel("Minimum value: "));
					valuePanel.add(new IntegerMinField());
					valuePanel.add(new JLabel("Maximum value: "));
					valuePanel.add(new IntegerMaxField());
					valuePanel.add(new JLabel("Value increment: "));
					valuePanel.add(new IntegerIncrementField());
				} else if(NetworkParameterType.DOUBLE.equals(property.getType())) {
					valuePanel.setLayout(new GridLayout(0, 2));
					valuePanel.add(new JLabel("Minimum value: "));
					valuePanel.add(new DoubleMinField());
					valuePanel.add(new JLabel("Maximum value: "));
					valuePanel.add(new DoubleMaxField());
					valuePanel.add(new JLabel("Value increment: "));
					valuePanel.add(new DoubleIncrementField());
					valuePanel.add(new JLabel("Decimal format pattern: "));
					valuePanel.add(new DecimalFormatPatternField());
				} else if(NetworkParameterType.BOOLEAN.equals(property.getType())) {
				}
			} else if(NetworkParameterValueRestriction.LIST.equals(property.getRestriction())) {
				valuePanel.setLayout(new BorderLayout());
				valuePanel.add(new OptionTableField(), BorderLayout.NORTH);
			}
			valuePanel.setVisible(valuePanel.getComponentCount() > 0);
			valuePanel.revalidate();
			valuePanel.repaint();
		}
		

//		abstract class NumberField extends JSpinner implements ChangeListener {
//			
////			protected abstract String getValue();
////			
////			protected abstract void setValue(String value);
//			
//			NumberField(boolean isInteger) {
////				setText(getValue());
//				addChangeListener(this);
////				if(isInteger) {
////					int min = (int) getMin();
////					int max = (int) getMax();
////					int inc = (int) getIncrement();
////					setModel(new SpinnerNumberModel(0, min, max, inc));
////				} else {
////					setModel(new SpinnerNumberModel(0, getMin(), getMax(), getIncrement()));
////					setEditor(new JSpinner.NumberEditor(this, getDecimalFormatPattern()));
////				}
//			}
//			
//			@Override
//			public void stateChanged(ChangeEvent e) {
//				
//			}
//		}
		
		abstract class StringField extends JTextField implements KeyListener {
			
			protected abstract String getValue();
			
			protected abstract void setValue(String value);
			
			StringField() {
				setText(getValue());
				setColumns(10);
				addKeyListener(this);
			}
			
			public void writeData() {
				setValue(getText());
			}

			@Override
			public void keyReleased(KeyEvent e) {
				writeData();
			}
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyPressed(KeyEvent e) {}
		}
		
		abstract class ComboBoxField extends JComboBox implements ActionListener {
			
			protected abstract String[] getLabels();
			protected abstract String getValue();
			protected abstract String getDefaultValue();
			
			protected abstract void setValue(String value);
			
			private boolean hasDefaultValue = true;
			
			ComboBoxField() {
				String[] labels = getLabels();
				String value = getValue();
				if(value == null)
					value = getDefaultValue();
				if(value == null) {
					hasDefaultValue = false;
					String[] newLabels = new String[labels.length + 1];
					newLabels[0] = "";
					System.arraycopy(labels, 0, newLabels, 1, labels.length);
					labels = newLabels;
				}
				setModel(new DefaultComboBoxModel(labels));
				if(value != null)
					setSelectedItem(value);
				addActionListener(this);
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(hasDefaultValue == false && getSelectedIndex() == 0)
					setValue(null);
				else
					setValue((String) getSelectedItem());
			}
		}
		
		class OptionListField extends JPanel {
			
			private JList optionList = new JList();
			
			OptionListField() {
				super(new BorderLayout());
				AbstractListModel model = new AbstractListModel() {
					@Override
					public Object getElementAt(int index) {
						return property.getOption().get(index);
					}
					@Override
					public int getSize() {
						return property.getOption().size();
					}
				};
				optionList.setModel(model);
				optionList.setCellRenderer(new DefaultListCellRenderer() {
					public Component getListCellRendererComponent(
							JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
						value = ((NetworkParameterValueOption) value).getLabel();
						return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
					}
				});
				add(new JScrollPane(optionList), BorderLayout.CENTER);
			}
		}
		
		class OptionTableField extends JPanel {
			
			private JTable optionTable = new JTable();
			
			OptionTableField() {
				super(new BorderLayout());
				final AbstractTableModel model = new AbstractTableModel() {
					@Override
					public int getColumnCount() {
						return 3;
					}
					@Override
					public int getRowCount() {
						return property.getOption().size();
					}
					@Override
					public Object getValueAt(int rowIndex, int columnIndex) {
						NetworkParameterValueOption option = property.getOption().get(rowIndex);
						switch (columnIndex) {
						case 0:
							return option.getLabel();
						case 1:
							return option.getID();
						case 2:
							return option.getValue();
						}
						return null;
					}
					public String getColumnName(int column) {
						switch (column) {
						case 0:
							return "Label";
						case 1:
							return "ID";
						case 2:
							return "Value";
						}
						return null;
					}
				};
				
				optionTable.setModel(model);
				add(optionTable.getTableHeader(), BorderLayout.NORTH);
				add(optionTable, BorderLayout.CENTER);
				JComponent buttonPane = new JPanel();
				JButton addButton = PowerFlowAnalyzer.createButton("Add a new option", "add.png", "Add", false);
				addButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						NetworkParameterValueOption option = new NetworkParameterValueOption();
						OptionDialog dialog = new OptionDialog(option, true);
						dialog.showDialog(300, 150);
					}
				});
				JButton editButton = PowerFlowAnalyzer.createButton("Edit the selected option", "pencil.png", "Add", false);
				editButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if(optionTable.getSelectedRow() == -1)
							return;
						NetworkParameterValueOption option = property.getOption().get(optionTable.getSelectedRow());
						OptionDialog dialog = new OptionDialog(option, false);
						dialog.showDialog(300, 150);
					}
				});
				JButton removeButton = PowerFlowAnalyzer.createButton("Remove the selected option", "delete.png", "Remove...", false);
				removeButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if(optionTable.getSelectedRow() == -1)
							return;
						int action = JOptionPane.showConfirmDialog(ManageParametersDialog.this, 
								"Do you want to delete this option?", "Confirm", JOptionPane.YES_NO_OPTION);
						if(action != JOptionPane.YES_OPTION)
							return;
						property.getOption().remove(optionTable.getSelectedRow());
						model.fireTableDataChanged();
					}
				});
				buttonPane.add(addButton);
				buttonPane.add(editButton);
				buttonPane.add(removeButton);
				add(buttonPane, BorderLayout.SOUTH);
			}
		}
		
		class OptionDialog extends BaseDialog {

			private NetworkParameterValueOption option;
			private JTextField id, label, value;
			private boolean addOption;
			
			protected OptionDialog(NetworkParameterValueOption option, boolean addOption) {
				super((Frame) ManageParametersDialog.this.getOwner(), "Define Option");
				this.option = option;
				this.addOption = addOption;
				
				id = new JTextField(option.getID());
				label = new JTextField(option.getLabel());
				value = new JTextField(option.getValue());
				
				JPanel centerPane = new JPanel(new BorderLayout());
				JPanel contentPane = new JPanel(new GridLayout(0, 2));
				contentPane.add(new JLabel("ID: "));
				contentPane.add(id);
				contentPane.add(new JLabel("Label: "));
				contentPane.add(label);
				contentPane.add(new JLabel("Value: "));
				contentPane.add(value);
				
				centerPane.add(contentPane, BorderLayout.NORTH);
				setCenterComponent(centerPane);
				addOKButton();
				addCancelButton();
			}
			
			protected void okPressed() {
				option.setID(id.getText());
				option.setLabel(label.getText());
				String textValue = value.getText();
				if(NetworkParameterType.INTEGER.equals(property.getType()))
					textValue = Integer.toString(parseInt(value.getText()));
				else if(NetworkParameterType.DOUBLE.equals(property.getType()))
					textValue = Double.toString(parseDouble(value.getText()));
				option.setValue(textValue);
				if(addOption)
					property.getOption().add(option);
				typeChanged();
			}
		}
		
		class TypeField extends ComboBoxField {
			@Override
			protected String[] getLabels() {
				NetworkParameterType[] types = NetworkParameterType.values();
				String[] labels = new String[types.length];
				for (int i = 0; i < labels.length; i++) {
					labels[i] = types[i].value();
				}
				return labels;
			}

			@Override
			protected String getValue() {
				NetworkParameterType type = property.getType();
				if(type == null)
					return null;//ModelPropertyType.TEXT.value();
				return type.value();
			}

			@Override
			protected String getDefaultValue() {
				return null;
			}

			@Override
			protected void setValue(String value) {
				if(value != null)
					property.setType(NetworkParameterType.fromValue(value));
				else
					property.setType(null);
				if((NetworkParameterType.INTEGER.equals(property.getType()) == false
						&& NetworkParameterValueRestriction.BUS_NUMBER.equals(property.getRestriction()))
						|| NetworkParameterType.BOOLEAN.equals(property.getType())) {
					property.setRestriction(null);
				}
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				super.actionPerformed(e);
				typeChanged();
			}
		}
		
		class RestrictionField extends ComboBoxField {
			
			protected NetworkParameterValueRestriction[] getRestrictions() {
				return NetworkParameterValueRestriction.values();
			}
			@Override
			protected String[] getLabels() {
				NetworkParameterValueRestriction[] types = getRestrictions();
				String[] labels = new String[types.length];
				for (int i = 0; i < labels.length; i++) {
					labels[i] = types[i].value();
				}
				return labels;
			}

			@Override
			protected String getValue() {
				NetworkParameterValueRestriction type = property.getRestriction();
				if(type == null)
					return null;//ModelPropertyRestriction.NONE.value();
				return type.value();
			}

			@Override
			protected String getDefaultValue() {
				return NetworkParameterValueRestriction.NONE.value();
			}

			@Override
			protected void setValue(String value) {
				property.setRestriction(NetworkParameterValueRestriction.fromValue(value));
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				super.actionPerformed(e);
				typeChanged();
			}
		}
		
		class IntegerRestrictionField extends RestrictionField {
			@Override
			protected NetworkParameterValueRestriction[] getRestrictions() {
				if(NetworkParameterType.INTEGER.equals(property.getType()))
					return new NetworkParameterValueRestriction[] { 
							NetworkParameterValueRestriction.NONE, 
							NetworkParameterValueRestriction.BUS_NUMBER, 
							NetworkParameterValueRestriction.LIST };
				else if(NetworkParameterType.BOOLEAN.equals(property.getType()))
					return new NetworkParameterValueRestriction[] { 
							NetworkParameterValueRestriction.NONE};
				else
					return new NetworkParameterValueRestriction[] { 
						NetworkParameterValueRestriction.NONE, 
						NetworkParameterValueRestriction.LIST };
			}
		}
		
		class IDField extends StringField {
			@Override
			protected String getValue() {
				return property.getID();
			}

			@Override
			protected void setValue(String value) {
				property.setID(value);
			}
		}
		
		class LabelField extends StringField {
			@Override
			protected String getValue() {
				return property.getLabel();
			}

			@Override
			protected void setValue(String value) {
				property.setLabel(value);
			}
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				refreshList();
			}
		}
		
		class DescriptionField extends StringField {
			@Override
			protected String getValue() {
				return property.getDescription();
			}

			@Override
			protected void setValue(String value) {
				property.setDescription(value);
			}
		}
		
		private Integer parseInt(String text, boolean nullOnError) {
			try {
				return Integer.parseInt(text.replace(',', '.'));
			} catch(NumberFormatException e) {
				if(nullOnError)
					return null;
				return 0;
			}
		}
		
		private int parseInt(String text) {
			return parseInt(text, false);
		}
		
		private Double parseDouble(String text, boolean nullOnError) {
			try {
				return Double.parseDouble(text.replace(',', '.'));
			} catch(NumberFormatException e) {
				if(nullOnError)
					return null;
				return 0.0;
			}
		}
		
		private double parseDouble(String text) {
			return parseDouble(text, false);
		}
		
		class IntegerMinField extends StringField {
			@Override
			protected String getValue() {
				if(displayOptions.getMin() == null)
					return "";
				return Integer.toString((int) Math.round(displayOptions.getMin()));
			}

			@Override
			protected void setValue(String value) {
				displayOptions.setMin((double) parseInt(value, true));
			}
		}
		
		class IntegerMaxField extends StringField {
			@Override
			protected String getValue() {
				if(displayOptions.getMax() == null)
					return "";
				return Integer.toString((int) Math.round(displayOptions.getMax()));
			}

			@Override
			protected void setValue(String value) {
				displayOptions.setMax((double) parseInt(value, true));
			}
		}
		
		class IntegerIncrementField extends StringField {
			@Override
			protected String getValue() {
				return Integer.toString((int) Math.round(displayOptions.getIncrement()));
			}

			@Override
			protected void setValue(String value) {
				displayOptions.setIncrement((double) parseInt(value));
			}
		}
		
		class DoubleMinField extends StringField {
			@Override
			protected String getValue() {
				if(displayOptions.getMin() == null)
					return "";
				return Double.toString(displayOptions.getMin());
			}

			@Override
			protected void setValue(String value) {
				displayOptions.setMin(parseDouble(value, true));
			}
		}
		
		class DoubleMaxField extends StringField {
			@Override
			protected String getValue() {
				if(displayOptions.getMax() == null)
					return "";
				return Double.toString(displayOptions.getMax());
			}

			@Override
			protected void setValue(String value) {
				displayOptions.setMax(parseDouble(value, true));
			}
		}
		
		class DoubleIncrementField extends StringField {
			@Override
			protected String getValue() {
				return Double.toString(displayOptions.getIncrement());
			}

			@Override
			protected void setValue(String value) {
				displayOptions.setIncrement(parseDouble(value));
			}
		}
		
		class DecimalFormatPatternField extends StringField {
			@Override
			protected String getValue() {
				return displayOptions.getDecimalFormatPattern();
			}

			@Override
			protected void setValue(String value) {
				displayOptions.setDecimalFormatPattern(value);
			}
		}
	}
	
	class ParameterListModel extends AbstractListModel {

		private List<NetworkParameter> parameters;
		
		ParameterListModel(List<NetworkParameter> parameters) {
			this.parameters = parameters;
		}
		
		public void addParameter() {
			parameters.add(new NetworkParameter());
			int index = getSize() - 1;
			fireIntervalAdded(parameterList, index, index);
		}
		
		public void removeParameter(NetworkParameter p) {
			parameters.remove(p);
			int index = getSize()+1;
			fireIntervalAdded(parameterList, index, index);
		}
		
		public NetworkParameter getSelectedParameter() {
			if(parameterList.getSelectedIndex() > -1)
				return getParameter(parameterList.getSelectedIndex());
			return null;
		}
		
		@Override
		public Object getElementAt(int index) {
			return getParameter(index);
		}
			
		public NetworkParameter getParameter(int index) {
			int definitionsCount = -1;
			for (NetworkParameter p : parameters) {
				if(p.getID() == null || ModelDBUtils.hasParameterDefinition(element, p.getID()))
					definitionsCount++;
				if(definitionsCount == index)
					return p;
			}
			throw new IndexOutOfBoundsException();
		}

		@Override
		public int getSize() {
			int definitionsCount = 0;
			for (NetworkParameter p : parameters) {
				if(p.getID() == null || ModelDBUtils.hasParameterDefinition(element, p.getID()))
					definitionsCount++;
			}
			return definitionsCount;
		}
	}
}
