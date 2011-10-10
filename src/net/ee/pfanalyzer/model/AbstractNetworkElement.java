package net.ee.pfanalyzer.model;

import java.util.ArrayList;
import java.util.List;

import net.ee.pfanalyzer.model.data.AbstractNetworkElementData;
import net.ee.pfanalyzer.model.data.ModelData;
import net.ee.pfanalyzer.model.data.NetworkFlagData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.model.util.ParameterSupport;

public abstract class AbstractNetworkElement extends ParameterSupport {
	
	public final static int DISPLAY_NAME = 2;
	public final static int DISPLAY_ADDITIONAL_INFO = 4;

	public final static int DISPLAY_DEFAULT = DISPLAY_NAME | DISPLAY_ADDITIONAL_INFO;
	
	private Network network;
	private int indexInParent;
	private Boolean hasFailures = null;
	private Boolean hasWarnings = null;
	private AbstractNetworkElementData elementData;
	private ModelData model;
	private List<NetworkFlag> flags = new ArrayList<NetworkFlag>();
	
	public abstract String getDisplayName(int displayFlags);
	
	AbstractNetworkElement(Network data, int index) {
		this(data, new AbstractNetworkElementData(), index);
	}
	
	AbstractNetworkElement(Network data, AbstractNetworkElementData elementData, int index) {
		this.network = data;
		this.indexInParent = index;
		this.elementData = elementData;
		
		if(elementData.getModelID() == null)
			setModelID(getDefaultModelID());
		
		updateElementData();
	}

	private void updateElementData() {
		// update flags
		getFlags().clear();
		for (NetworkFlagData flagData : getElementData().getFlag()) {
			NetworkFlag flag = new NetworkFlag(flagData);
			getFlags().add(flag);
		}
	}
	
	public ModelData getModel() {
		return model;
	}
	
	public void setModel(ModelData model) {
		this.model = model;
	}
	
	@Override
	public NetworkParameter getParameterValue(String id) {
		NetworkParameter parameter = super.getParameterValue(id);
		if(parameter != null)
			return parameter;
		if(getModel() != null)
			return ModelDBUtils.getParameterValue(getModel(), id);
		return null;
	}
	
	public NetworkParameter getParameterDefinition(String id) {
		if(getModel() != null)
			return ModelDBUtils.getParameterDefinition(getModel(), id);
		return null;
	}
	
//	public NetworkParameterType getParameterType(String id) {
//		NetworkParameter parameter = super.getOwnParameter(id);
//		if(parameter != null && parameter.getType() != null)
//			return parameter.getType();
//		if(getModel() != null)
//			return ModelDBUtils.getParameterType(getModel(), id);
//		return null;
//	}
	
	public String getParameterDisplayValue(String parameterID) {
		NetworkParameter param = getParameterDefinition(parameterID);
		if(param != null) {
			return ModelDBUtils.getParameterDisplayValue(this, param);
		}
		return getTextParameter(parameterID);
	}
	
	public String getModelID() {
		if(getElementData().getModelID() == null)
			return getDefaultModelID();
		return getElementData().getModelID();
	}
	
	public void setModelID(String id) {
		if(id == null || id.isEmpty())
			getElementData().setModelID(null);
		else
			getElementData().setModelID(id);
	}
	
	public boolean isModel(String modelIDPrefix) {
		return getModelID() != null && getModelID().startsWith(modelIDPrefix);
	}
	
	public String getDefaultModelID() {
		return "";
	}
	
	public Network getNetwork() {
		return network;
	}
	
	public AbstractNetworkElementData getElementData() {
		return elementData;
	}
	
	@Override
	public List<NetworkParameter> getParameterList() {
		return elementData.getParameter();
	}
	
	public int getIndex() {
		return indexInParent;
	}
	
	void setIndex(int newIndex) {
		indexInParent = newIndex;
	}
	
	public boolean equals(Object o) {
		if(o != null && o instanceof AbstractNetworkElement && getClass().equals(o.getClass()))
			return getIndex() == ((AbstractNetworkElement) o).getIndex();
		else
			return false;
	}
	
	public List<NetworkFlag> getFlags() {
		return flags;
	}
	
	public void clearFlags() {
		flags.clear();
		elementData.getFlag().clear();
	}
	
	public void addFlag(NetworkFlag flag) {
		flags.add(flag);
		elementData.getFlag().add(flag.getData());
	}
	
	public boolean isCorrect() {
		return hasFailures() == false;
	}
	
	public boolean hasFailures() {
		if(hasFailures == null) {
			hasFailures = false;
			for (NetworkFlag flag : getFlags()) {
				if(flag.isFailure()) {
					hasFailures = true;
					break;
				}
			}
		}
		return hasFailures;
	}
	
	public boolean hasWarnings() {
		if(hasWarnings == null) {
			hasWarnings = false;
			for (NetworkFlag flag : getFlags()) {
				if(flag.isWarning()) {
					hasWarnings = true;
					break;
				}
			}
		}
		return hasWarnings;
	}
	
	public boolean isActive() {
		return true;
	}
}
