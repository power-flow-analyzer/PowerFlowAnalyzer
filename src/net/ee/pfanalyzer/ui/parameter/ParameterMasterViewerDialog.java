package net.ee.pfanalyzer.ui.parameter;

import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ParameterSupport;
import net.ee.pfanalyzer.ui.viewer.DataViewerDialog;

public class ParameterMasterViewerDialog implements IParameterMasterElement {

	private DataViewerDialog dialogData;
	
	public ParameterMasterViewerDialog(DataViewerDialog dialogData) {
		this.dialogData = dialogData;
	}
	
	public String getDialogID() {
		return getDialogData().getDialogID();
	}
	
	public DataViewerDialog getDialogData() {
		return dialogData;
	}
	
	@Override
	public boolean isRequired(String parameterID) {
		return true;
	}
	
	@Override
	public boolean showCheckBoxes(String parameterID) {
		return false;
	}

	@Override
	public NetworkParameter getParameter(String parameterID, boolean create) {
		return getDialogData().getParameter(parameterID, create);
	}
	
	@Override
	public boolean hasParameterDefinition(String parameterID) {
		return false;// viewers never contain parameter definitions
	}

	@Override
	public NetworkParameter getParameterValue(String parameterID) {
		return getDialogData().getParameterValue(parameterID);
	}
	
	@Override
	public NetworkParameter getOwnParameter(String parameterID) {
		return getDialogData().getOwnParameter(parameterID);
	}
	
	@Override
	public void removeOwnParameter(String parameterID) {
//		network.removeOwnParameter(parameterID);
	}
	
	@Override
	public void fireValueChanged(String parameterID, String oldValue, String newValue) {
//		DatabaseChangeEvent event = new DatabaseChangeEvent(DatabaseChangeEvent.CHANGED, parameterID, oldValue, newValue);
////		event.setElementData(getMasterElement());
//		getDialogData().fireParameterChanged(event);
	}

	@Override
	public ParameterSupport getParameterSupport() {
		return getDialogData();
	}
}
