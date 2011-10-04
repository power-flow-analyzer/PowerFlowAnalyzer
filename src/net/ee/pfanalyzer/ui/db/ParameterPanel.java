package net.ee.pfanalyzer.ui.db;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;

import net.ee.pfanalyzer.model.data.AbstractModelElementData;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.ui.parameter.ParameterContainer;
import net.ee.pfanalyzer.ui.parameter.ParameterMasterModel;

public class ParameterPanel extends ParameterContainer {
	
	public final static String PROPERTY_NAME_RELOAD_ELEMENT = "reload-element";

	private DefaultMutableTreeNode node;
	private AbstractModelElementData master;
	private JLabel referenceLabel;
	
	public ParameterPanel(DefaultMutableTreeNode treeNode, AbstractModelElementData element, boolean devMode) {
		super(new ParameterMasterModel(element), false);
		node = treeNode;
		master = element;
		if(master.getParent() == null)
			setShowNetworkParameters(true);
		referenceLabel = new JLabel(ModelDBUtils.getParameterID(master));
		JPanel commonPanel = addElementGroup("Common Parameters");
		commonPanel.add(new JLabel("Label: "));
		commonPanel.add(new ParameterLabelBox());
		commonPanel.add(new JLabel("Description: "));
		commonPanel.add(new ParameterDescriptionBox());
		commonPanel.add(new JLabel("Element ID: "));
		commonPanel.add(new ParameterIDBox());
		commonPanel.add(new JLabel("Reference: "));
		commonPanel.add(referenceLabel);
		addParameters(element, element, getElementContainer());
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
			return master.getID();
		}
		
		protected void setParameterValue(String text) {
			if(text == null || text.trim().isEmpty())
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
		
		@Override
		protected String getParameterValue() {
			return master.getLabel();
		}
		
		@Override
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
	
	
//	private ModelProperty copyProperty(ModelProperty p) {
//		try {
//			return (ModelProperty) CaseSerializer.copy(p);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	abstract class ParameterTextBox extends JTextField implements ActionListener, KeyListener {
		
		
		public ParameterTextBox(String parameterID) {
			super();
			initField();
		}
		
		private void initField() {
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

}
