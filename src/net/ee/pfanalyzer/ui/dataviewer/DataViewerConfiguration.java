package net.ee.pfanalyzer.ui.dataviewer;

import java.util.List;

import net.ee.pfanalyzer.PowerFlowAnalyzer;
import net.ee.pfanalyzer.model.data.DataViewerData;
import net.ee.pfanalyzer.model.data.ModelData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.model.util.ParameterSupport;

public class DataViewerConfiguration extends ParameterSupport {

	private DataViewerData data;
	private ModelData dataDefinition;
	
	public DataViewerConfiguration(ModelData dataDefinition) {
		data = new DataViewerData();
		this.dataDefinition = dataDefinition;
		data.setModelID(ModelDBUtils.getFullElementID(dataDefinition));
	}
	
	public DataViewerConfiguration(DataViewerData data) {
		this.data = data;
		this.dataDefinition = PowerFlowAnalyzer.getConfiguration().getModel(getModelID());
	}

	@Override
	public List<NetworkParameter> getParameterList() {
		return getData().getParameter();
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
	
	public String getTitle() {
		return getTextParameter("TITLE", "");
	}
	
	public String getElementFilter() {
		return getTextParameter("ELEMENT_FILTER", "");
	}
}
