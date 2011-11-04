package net.ee.pfanalyzer.ui.parameter;

import net.ee.pfanalyzer.model.DatabaseChangeEvent;
import net.ee.pfanalyzer.model.ModelDB;
import net.ee.pfanalyzer.model.data.AbstractModelElementData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.model.util.ParameterSupport;

public class ParameterMasterModel implements IParameterMasterElement {

	private AbstractModelElementData master;
	private ModelDB paramDB;
	
	public ParameterMasterModel(AbstractModelElementData element, ModelDB paramDB) {
		this.master = element;
		this.paramDB = paramDB;
	}
	
	protected AbstractModelElementData getMasterElement() {
		return master;
	}
	
	@Override
	public boolean isRequired(String parameterID) {
		return false;
	}
	
	@Override
	public boolean showCheckBoxes(String parameterID) {
		return true;
	}

	@Override
	public NetworkParameter getParameter(String parameterID, boolean create) {
		return ModelDBUtils.getParameter(getMasterElement(), parameterID, create);
	}
	
	@Override
	public boolean hasParameterDefinition(String parameterID) {
		return ModelDBUtils.hasParameterDefinition(getMasterElement(), parameterID);
	}

	@Override
	public NetworkParameter getParameterValue(String parameterID) {
		return ModelDBUtils.getParameterValue(getMasterElement(), parameterID);
	}
	
	@Override
	public NetworkParameter getOwnParameter(String parameterID) {
		return ModelDBUtils.getOwnParameter(getMasterElement(), parameterID);
	}
	
	@Override
	public void removeOwnParameter(String parameterID) {
		ModelDBUtils.removeOwnParameter(getMasterElement(), parameterID);
	}
	
	@Override
	public void fireValueChanged(String parameterID, String oldValue, String newValue) {
		if(paramDB != null) {
			DatabaseChangeEvent event = new DatabaseChangeEvent(DatabaseChangeEvent.CHANGED, parameterID, oldValue, newValue);
			event.setElementData(getMasterElement());
			paramDB.fireParameterChanged(event);
		}
	}

	@Override
	public ParameterSupport getParameterSupport() {
		return null;
	}
}
