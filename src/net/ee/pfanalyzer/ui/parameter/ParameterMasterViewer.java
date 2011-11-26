package net.ee.pfanalyzer.ui.parameter;

import net.ee.pfanalyzer.model.DatabaseChangeEvent;
import net.ee.pfanalyzer.model.PowerFlowCase;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ParameterSupport;
import net.ee.pfanalyzer.ui.viewer.DataViewerConfiguration;

public class ParameterMasterViewer implements IParameterMasterElement {

	private PowerFlowCase caze;
	private DataViewerConfiguration viewer;
	private boolean parametersAreRequired;
	
	public ParameterMasterViewer(PowerFlowCase caze, DataViewerConfiguration viewer, boolean parametersAreRequired) {
		this.caze = caze;
		this.viewer = viewer;
		this.parametersAreRequired = parametersAreRequired;
	}
	
	public DataViewerConfiguration getViewerConfiguration() {
		return viewer;
	}
	
	public PowerFlowCase getPowerFlowCase() {
		return caze;
	}
	
	@Override
	public boolean isRequired(String parameterID) {
		return parametersAreRequired;
	}
	
	@Override
	public boolean showCheckBoxes(String parameterID) {
		return false;
	}

	@Override
	public NetworkParameter getParameter(String parameterID, boolean create) {
		return getViewerConfiguration().getParameter(parameterID, create);
	}
	
	@Override
	public boolean hasParameterDefinition(String parameterID) {
		return false;// viewers never contain parameter definitions
	}

	@Override
	public NetworkParameter getParameterValue(String parameterID) {
		return getViewerConfiguration().getParameterValue(parameterID);
	}
	
	@Override
	public NetworkParameter getOwnParameter(String parameterID) {
		return getViewerConfiguration().getOwnParameter(parameterID);
	}
	
	@Override
	public void removeOwnParameter(String parameterID) {
//		network.removeOwnParameter(parameterID);
	}
	
	@Override
	public void fireValueChanged(String parameterID, String oldValue, String newValue) {
		DatabaseChangeEvent event = new DatabaseChangeEvent(DatabaseChangeEvent.CHANGED, parameterID, oldValue, newValue);
//		event.setElementData(getMasterElement());
		getViewerConfiguration().fireParameterChanged(event);
	}

	@Override
	public ParameterSupport getParameterSupport() {
		return getViewerConfiguration();
	}
}
