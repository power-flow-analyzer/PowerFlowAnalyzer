package net.ee.pfanalyzer.model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import net.ee.pfanalyzer.model.data.AbstractNetworkElementData;
import net.ee.pfanalyzer.model.data.ModelData;
import net.ee.pfanalyzer.model.data.NetworkFlagData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.data.NetworkParameterType;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.model.util.ParameterSupport;

public abstract class AbstractNetworkElement extends ParameterSupport {

	private Network network;
	private int indexInParent;
//	private double[] data;
	private Boolean isCorrect = null;
	private AbstractNetworkElementData elementData;
	private ModelData model;
	private List<NetworkFlag> flags = new ArrayList<NetworkFlag>();
	
	public abstract String getDisplayName();
	
	AbstractNetworkElement(Network data, int index) {
		this(data, new AbstractNetworkElementData(), index);
	}
	
	AbstractNetworkElement(Network data, AbstractNetworkElementData elementData, int index) {
		this.network = data;
//		this.data = matpowerData;
		this.indexInParent = index;
		this.elementData = elementData;
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
			NetworkParameterType type = param.getType();
//			NetworkParameterValueRestriction restriction = param.getRestriction();
//			param.getOption().
			if(type != null) {
				if(type.equals(NetworkParameterType.INTEGER)) {
					Integer result = getIntParameter(parameterID);
					if(result != null)
						return result.toString();
					return "";
				} else if(type.equals(NetworkParameterType.DOUBLE)) {
					Double value = getDoubleParameter(parameterID);
					if(value != null && param.getDisplay() != null) {
						String pattern = param.getDisplay().getDecimalFormatPattern();
						DecimalFormat format = new DecimalFormat(pattern);
						return format.format(value);
					}
					if(value != null)
						return value.toString();
					return "";
				}
			}
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
	
//	public double[] getData() {
//		return data;
//	}
	
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
	
	public void addFlag(NetworkFlag flag) {
		flags.add(flag);
		elementData.getFlag().add(flag.getData());
	}
	
	public boolean isCorrect() {
		if(isCorrect == null) {
			isCorrect = true;
			for (NetworkFlag flag : getFlags()) {
				if(flag.isFailure()) {
					isCorrect = false;
					break;
				}
			}
		}
		return isCorrect;
	}
	
	public boolean isActive() {
		return true;
	}
}
