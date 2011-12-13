package net.ee.pfanalyzer.ui.viewer;

import java.util.List;

import net.ee.pfanalyzer.PowerFlowAnalyzer;
import net.ee.pfanalyzer.model.data.DataViewerDialogData;
import net.ee.pfanalyzer.model.data.ModelData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ParameterSupport;

public class DataViewerDialog extends ParameterSupport {

	private DataViewerDialogData data;
	private ModelData dialogModel;
	
	public DataViewerDialog(String dialogID) {
		data = new DataViewerDialogData();
		data.setModelID(dialogID);
		dialogModel = PowerFlowAnalyzer.getConfiguration().getConfiguration(dialogID);
	}
	
	public DataViewerDialog(DataViewerDialogData data) {
		this.data = data;
		dialogModel = PowerFlowAnalyzer.getConfiguration().getConfiguration(data.getModelID());
	}
	
	public String getDialogID() {
		return data.getModelID();
	}
	
	public ModelData getDialogModel() {
		return dialogModel;
	}

	@Override
	public List<NetworkParameter> getParameterList() {
		return data.getParameter();
	}
	
	public DataViewerDialogData getDialogData() {
		return data;
	}
}
