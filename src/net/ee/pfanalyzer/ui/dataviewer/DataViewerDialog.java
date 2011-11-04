package net.ee.pfanalyzer.ui.dataviewer;

import java.awt.BorderLayout;
import java.awt.Window;

import javax.swing.JPanel;

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

	public DataViewerDialog(Window frame, String title, DataViewerConfiguration viewer, PowerFlowCase caze) {
		super(frame, title, true);
		this.viewer = viewer;
		setText(DEFAULT_TEXT);
		

		ModelElementPanel parameterPanel = new ModelElementPanel(null);
//		parameterPanel.setShowNetworkParameters(true);
		parameterPanel.setEditable(true);// default setting
		parameterPanel.setParameterMaster(new ParameterMasterViewer(caze, viewer, true));
		Group paramPanel = new Group("Parameters for " + viewer.getDataDefinition().getLabel());
		addParameters(viewer.getDataDefinition(), parameterPanel, paramPanel);
		
		addButton("Close", true, true);
		
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(paramPanel, BorderLayout.CENTER);
		
		setCenterComponent(contentPane);
	}
	
	private void addParameters(AbstractModelElementData element, ModelElementPanel parameterPanel, Group paramPanel) {
		if(element.getParent() != null)
			addParameters(element.getParent(), parameterPanel, paramPanel);
		for (NetworkParameter paramDef : element.getParameter()) {
			NetworkParameter parameterValue = viewer.getParameter(paramDef.getID(), true);
			if(parameterValue.getValue() == null) {
				NetworkParameter modelValue = ModelDBUtils.getParameterValue(element, paramDef.getID());
				if(modelValue != null)
					parameterValue.setValue(modelValue.getValue());
			}
			parameterValue = viewer.getParameter(paramDef.getID(), true);
			parameterPanel.addParameter(paramDef, parameterValue, paramPanel);
		}
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
