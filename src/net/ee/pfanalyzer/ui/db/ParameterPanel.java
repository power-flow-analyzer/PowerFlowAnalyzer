package net.ee.pfanalyzer.ui.db;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;

import net.ee.pfanalyzer.model.DatabaseChangeEvent;
import net.ee.pfanalyzer.model.ModelDB;
import net.ee.pfanalyzer.model.data.AbstractModelElementData;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.ui.parameter.ParameterContainer;
import net.ee.pfanalyzer.ui.parameter.ParameterMasterModel;
import net.ee.pfanalyzer.ui.util.Group;

public class ParameterPanel extends ParameterContainer {
	
	public final static String PROPERTY_NAME_RELOAD_ELEMENT = "reload-element";

	private DefaultMutableTreeNode node;
	private AbstractModelElementData master;
	private ModelDB paramDB;
	private JLabel referenceLabel;
	
	public ParameterPanel(DefaultMutableTreeNode treeNode, AbstractModelElementData element, ModelDB paramDB) {
		super(new ParameterMasterModel(element, paramDB), false);
		node = treeNode;
		master = element;
		this.paramDB = paramDB;
		if(master.getParent() == null)
			setShowNetworkParameters(true);
		referenceLabel = new JLabel(ModelDBUtils.getFullElementID(master));
		Group commonPanel = addElementGroup("Common Parameters");
		commonPanel.addLabel(new JLabel("Label: "));
		commonPanel.addValue(new ParameterLabelBox());
		commonPanel.addLabel(new JLabel("Description: "));
		commonPanel.addValue(new JScrollPane(new ParameterDescriptionBox()));
		commonPanel.addLabel(new JLabel("Element ID: "));
		commonPanel.addValue(new ParameterIDBox());
		commonPanel.addLabel(new JLabel("Reference: "));
		commonPanel.addValue(referenceLabel);
		addParameters(element, element, getElementContainer());
	}
	
	private void refresh() {
		firePropertyChange(PROPERTY_NAME_RELOAD_ELEMENT, null, node);
	}
	
	private void refreshID() {
		referenceLabel.setText(ModelDBUtils.getFullElementID(master));
	}
	
	private void fireElementChanged() {
		if(paramDB != null)
			paramDB.fireElementChanged(new DatabaseChangeEvent(DatabaseChangeEvent.CHANGED, master));
	}
	
	class ParameterIDBox extends ParameterTextField implements ActionListener, KeyListener {
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
			fireElementChanged();
		}
	}
	
	class ParameterLabelBox extends ParameterTextField implements ActionListener, KeyListener {
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
			fireElementChanged();
		}
	}
	
	class ParameterDescriptionBox extends ParameterTextBox implements KeyListener {
		ParameterDescriptionBox() {
			super("DESCR");
		}
		
		protected String getParameterValue() {
			return master.getDescription();
		}
		
		protected void setParameterValue(String text) {
			master.setDescription(text);
		}
		
		@Override
		protected void updateView() {
			fireElementChanged();
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
	
	abstract class ParameterTextField extends JTextField implements ActionListener, KeyListener {
		public ParameterTextField(String parameterID) {
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
	
	abstract class ParameterTextBox extends JTextArea implements KeyListener {
		public ParameterTextBox(String parameterID) {
			super(3, 20);
			initField();
		}
		
		private void initField() {
			if(getParameterValue() != null)
				setText(getParameterValue());
			addKeyListener(this);
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
