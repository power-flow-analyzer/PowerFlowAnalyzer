package net.ee.pfanalyzer.ui.db;

import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

import net.ee.pfanalyzer.model.data.AbstractModelElementData;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.ui.parameter.ParameterContainer;
import net.ee.pfanalyzer.ui.parameter.ParameterMasterModel;
import net.ee.pfanalyzer.ui.parameter.ParameterTextField;

public class ParameterPanel extends ParameterContainer {
	
	public final static String PROPERTY_NAME_RELOAD_ELEMENT = "reload-element";

	private DefaultMutableTreeNode node;
	private AbstractModelElementData master;
	private JLabel referenceLabel;
	
	public ParameterPanel(DefaultMutableTreeNode treeNode, AbstractModelElementData element, boolean devMode) {
		super(new ParameterMasterModel(element), false);
		node = treeNode;
		master = element;
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
	
	class ParameterIDBox extends ParameterTextField implements ActionListener, KeyListener {
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
		}
	}
	
	class ParameterDescriptionBox extends ParameterTextField implements ActionListener, KeyListener {
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
}
