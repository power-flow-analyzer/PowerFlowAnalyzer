package net.ee.pfanalyzer.ui.viewer;

import java.util.ArrayList;
import java.util.List;

import net.ee.pfanalyzer.PowerFlowAnalyzer;
import net.ee.pfanalyzer.model.DatabaseChangeEvent;
import net.ee.pfanalyzer.model.IDatabaseChangeListener;
import net.ee.pfanalyzer.model.data.DataViewerData;
import net.ee.pfanalyzer.model.data.DataViewerDialogData;
import net.ee.pfanalyzer.model.data.ModelData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.model.util.ParameterSupport;

public class DataViewerConfiguration extends ParameterSupport {

	private DataViewerData data;
	private ModelData dataDefinition;
	private List<IDatabaseChangeListener> listeners = new ArrayList<IDatabaseChangeListener>();
	private List<DataViewerDialog> dialogs = new ArrayList<DataViewerDialog>();
	
	public DataViewerConfiguration(ModelData dataDefinition) {
		data = new DataViewerData();
		this.dataDefinition = dataDefinition;
		data.setModelID(ModelDBUtils.getFullElementID(dataDefinition));
	}
	
	public DataViewerConfiguration(DataViewerData data) {
		this.data = data;
		this.dataDefinition = PowerFlowAnalyzer.getConfiguration().getConfiguration(getModelID());
	}

	@Override
	public List<NetworkParameter> getParameterList() {
		return getData().getParameter();
	}

	@Override
	public NetworkParameter getParameterValue(String id) {
		NetworkParameter parameter = super.getParameterValue(id);
		if(parameter != null)
			return parameter;
		if(getDataDefinition() != null)
			return ModelDBUtils.getParameterValue(getDataDefinition(), id);
		return null;
	}

	public DataViewerData getData() {
		return data;
	}

	public ModelData getDataDefinition() {
		return dataDefinition;
	}
	
	public String getModelID() {
		return getData().getModelID();
	}
	
	private void readDialogs() {
		for (DataViewerDialogData dialog : getData().getDialog()) {
			dialogs.add(new DataViewerDialog(dialog));
		}
	}
	
	public DataViewerDialog getDialogData(String dialogID, boolean create) {
		if(dialogs == null)
			readDialogs();
		for (DataViewerDialog dialog : dialogs) {
			if(dialog.getDialogID().equals(dialogID))
				return dialog;
		}
		if(create) {
			DataViewerDialog dialog = new DataViewerDialog(dialogID);
			getData().getDialog().add(dialog.getDialogData());
			dialogs.add(dialog);
			return dialog;
		}
		return null;
	}
	
	public String getTitle() {
		return getTextParameter("TITLE", "");
	}
	
	public String getElementFilter() {
		return getTextParameter("ELEMENT_FILTER", "");
	}
	
	public void fireElementChanged(DatabaseChangeEvent event) {
		for (IDatabaseChangeListener listener : listeners) {
			listener.elementChanged(event);
		}
	}
	
	public void fireParameterChanged(DatabaseChangeEvent event) {
		for (IDatabaseChangeListener listener : listeners) {
			listener.parameterChanged(event);
		}
	}
	
	public void addDatabaseChangeListener(IDatabaseChangeListener listener) {
		listeners.add(listener);
	}
	
	public void removeDatabaseChangeListener(IDatabaseChangeListener listener) {
		listeners.remove(listener);
	}
}
