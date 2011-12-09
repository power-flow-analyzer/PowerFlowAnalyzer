package net.ee.pfanalyzer.ui.viewer;

import java.awt.Window;

import net.ee.pfanalyzer.model.PowerFlowCase;
import net.ee.pfanalyzer.model.data.ModelClassData;
import net.ee.pfanalyzer.model.data.ModelData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.ui.dialog.ParameterChooserDialog;
import net.ee.pfanalyzer.ui.parameter.ParameterCheckBox;
import net.ee.pfanalyzer.ui.parameter.ParameterMasterViewer;
import net.ee.pfanalyzer.ui.viewer.element.ModelElementPanel;
import net.ee.pfanalyzer.ui.viewer.network.NetworkMapViewer;

public class DataViewerDialog extends ParameterChooserDialog {
	
	private DataViewerConfiguration viewer;

	public DataViewerDialog(Window frame, String title, DataViewerConfiguration viewer, PowerFlowCase caze, boolean canCancel) {
		super(frame, title, true);
		this.viewer = viewer;
		setText("<html><b>" + "Enter the properties for this viewer.");
		
		ModelElementPanel parameterPanel = new ModelElementPanel(null);
//		parameterPanel.setShowNetworkParameters(true);
		parameterPanel.setEditable(true);// default setting
		parameterPanel.setParameterMaster(new ParameterMasterViewer(caze, viewer, true));
		parameterPanel.setShowFullParameterInfo(false);
		addParameters(viewer.getDataDefinition(), parameterPanel);
		if(viewer.getModelID().startsWith(NetworkMapViewer.BASE_NETWORK_VIEWER_ID)
				&& caze.getModelDB().getOutlineClass() != null) {
			addOutlineParameters(caze.getModelDB().getOutlineClass(), parameterPanel);
		}
		
		if(canCancel) {
			addOKButton();
			addCancelButton();
		} else
			addButton("Close", true, true);
		
		finishLayout();
	}
	
	private void addOutlineParameters(ModelClassData outlines, ModelElementPanel parameterPanel) {
		for (ModelData outline : outlines.getModel()) {
			String viewerParamName = "OUTLINE." + outline.getID();
			NetworkParameter enablement = viewer.getParameter(viewerParamName, true);
			if(enablement.getValue() == null) {
				NetworkParameter modelValue = ModelDBUtils.getParameterValue(outline, "ENABLED");
				if(modelValue != null && modelValue.getValue() != null)
					enablement.setValue(modelValue.getValue());
				else
					enablement.setValue("false");
			}
			ParameterCheckBox box = new ParameterCheckBox(
					parameterPanel.getParameterMaster(), enablement, enablement);
			box.setShowFullParameterInfo(false);
			getGroup("Outlines", "Outlines").add(box);
			box.getCheckBox().setText(outline.getLabel());
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
