package net.ee.pfanalyzer.ui.dataviewer;

import java.awt.Window;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;

import net.ee.pfanalyzer.model.PowerFlowCase;
import net.ee.pfanalyzer.model.data.AbstractModelElementData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.ui.dialog.BaseDialog;
import net.ee.pfanalyzer.ui.model.ModelElementPanel;
import net.ee.pfanalyzer.ui.parameter.ParameterMasterViewer;
import net.ee.pfanalyzer.ui.util.Group;

public class DataViewerDialog extends BaseDialog {
	
	private final static String DEFAULT_TEXT = "<html><b>Enter the properties for this viewer.<br>" +
			"The viewer will only contain those elements whose model ID matches the filter.";
//	private final static String ERROR_TEXT = "<html><font color=\"red\">All values must be filled in to proceed.<br>" +
//			"The viewer will only contain those elements whose model ID matches the filter.";
	
	private DataViewerConfiguration viewer;
	private Map<String, Group> groups = new HashMap<String, Group>();
	private Box contentPane;

	public DataViewerDialog(Window frame, String title, DataViewerConfiguration viewer, PowerFlowCase caze, boolean canCancel) {
		super(frame, title, true);
		this.viewer = viewer;
		setText(DEFAULT_TEXT);
		
		contentPane = Box.createVerticalBox();

		ModelElementPanel parameterPanel = new ModelElementPanel(null);
//		parameterPanel.setShowNetworkParameters(true);
		parameterPanel.setEditable(true);// default setting
		parameterPanel.setParameterMaster(new ParameterMasterViewer(caze, viewer, true));
		addParameters(viewer.getDataDefinition(), parameterPanel);
		
		if(canCancel) {
			addOKButton();
			addCancelButton();
		} else
			addButton("Close", true, true);
		
		setCenterComponent(contentPane);
	}
	
	private void addParameters(AbstractModelElementData element, ModelElementPanel parameterPanel) {
		if(element.getParent() != null)
			addParameters(element.getParent(), parameterPanel);
		for (NetworkParameter paramDef : element.getParameter()) {
			if(ModelDBUtils.isInternalViewerParameter(paramDef.getID()))
				continue;
			NetworkParameter parameterValue = viewer.getParameter(paramDef.getID(), true);
			if(parameterValue.getValue() == null) {
				NetworkParameter modelValue = ModelDBUtils.getParameterValue(element, paramDef.getID());
				if(modelValue != null)
					parameterValue.setValue(modelValue.getValue());
			}
			parameterValue = viewer.getParameter(paramDef.getID(), true);
			String groupTitle = paramDef.getDisplay() == null ? null : paramDef.getDisplay().getGroup();
			parameterPanel.addParameter(paramDef, parameterValue, getGroup(groupTitle));
		}
	}
	
	private Group getGroup(String name) {
		Group group = groups.get(name);
		if(group == null) {
			group = new Group(name);
			groups.put(name, group);
			contentPane.add(group);
		}
		return group;
	}
	
	@Override
	protected boolean checkInput() {
//		if(titleField.getText().length() > 0 && filterField.getText().length() > 0) {
//			setText(DEFAULT_TEXT);
//			return true;
//		} else {
//			setText(ERROR_TEXT);
//			return false;
//		}
		return true;
	}
}
