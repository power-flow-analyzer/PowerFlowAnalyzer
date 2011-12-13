package net.ee.pfanalyzer.model;

import java.util.List;

import net.ee.pfanalyzer.model.data.ModelData;
import net.ee.pfanalyzer.model.data.NetworkFlagData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ModelDBUtils;

public class NetworkFlag {

	private NetworkFlagData data;
	private ModelData model;
	
	public NetworkFlag(NetworkFlagData data, ModelData flagModel) {
		this.data = data;
		this.model = flagModel;
	}
	
	public NetworkFlag(String id) {
		data = new NetworkFlagData();
		getData().setID(id);
	}
	
	public NetworkFlagData getData() {
		return data;
	}
	
	public String getID() {
		return getData().getID();
	}
	
	public ModelData getModel() {
		return model;
	}
	
	public String getLabel() {
		if(getModel() != null)
			return getModel().getLabel();
		else
			return getID();
	}
	
	public void setValue(double value) {
		getData().setValue(value);
	}
	
	public void setValue(double value, String valueParameterID) {// TODO remove
		getData().setValue(value);
	}
	
	public Double getValue() {
		return getData().getValue();
	}
	
	public boolean isVisible() {
		Boolean flag = getBooleanParameter("VISIBLE");
		if(flag != null)
			return flag;
		else
			return true;
	}
	
	public String getPattern() {
		if(getModel() != null) {
			NetworkParameter flagParam = ModelDBUtils.getParameterValue(getModel(), "PATTERN");
			if(flagParam != null && flagParam.getValue() != null && flagParam.getValue().length() > 0)
				return flagParam.getValue();
		}
		return "#.##";
	}
	
	public String getUnit() {
		if(getModel() != null) {
			NetworkParameter flagParam = ModelDBUtils.getParameterValue(getModel(), "UNIT");
			if(flagParam != null && flagParam.getValue() != null && flagParam.getValue().length() > 0)
				return flagParam.getValue();
		}
		return "";
	}
	
	public double getWarningLevel() {
		Double level = getDoubleParameter("WARNING_LEVEL");
		if(level != null)
			return level;
		else
			return 0.95;
	}
	
	public double getErrorLevel() {
		Double level = getDoubleParameter("ERROR_LEVEL");
		if(level != null)
			return level;
		else
			return 1.01;
	}
	
	private boolean onlyUseMaximumValues() {
		Boolean flag = getBooleanParameter("ONLY_USE_MAXIMUM_VALUES");
		if(flag != null)
			return flag;
		else
			return false;
	}
	
	private Boolean getBooleanParameter(String parameterID) {
		if(getModel() != null) {
			NetworkParameter flagParam = ModelDBUtils.getParameterValue(getModel(), parameterID);
			if(flagParam != null && flagParam.getValue() != null && flagParam.getValue().length() > 0)
				return Boolean.parseBoolean(flagParam.getValue());
		}
		return null;
	}
	
	private Double getDoubleParameter(String parameterID) {
		if(getModel() != null) {
			NetworkParameter flagParam = ModelDBUtils.getParameterValue(getModel(), parameterID);
			if(flagParam != null && flagParam.getValue() != null && flagParam.getValue().length() > 0) {
				try {
					return Double.parseDouble(flagParam.getValue());
				} catch(NumberFormatException ex) {
					// do nothing
				}
			}
		}
		return null;
	}
	
	public void addParameter(String id) {
		getData().getParameter().add(id);
	}
	
	public List<String> getParameters() {
		return getData().getParameter();
	}
	
	public boolean containsParameter(String parameterID) {
		return getData().getParameter().contains(parameterID);
	}

	public boolean isCorrect() {
		return ! getData().isFailure();
	}

	public boolean isWarning() {
		if(getData().isWarning() && onlyUseMaximumValues() == false)
			return true;
		if(getValue() == null)
			return false;
		if(getPercentage() >= getWarningLevel())
			return true;
		return false;
	}

	public void setWarning(boolean warning) {
		getData().setWarning(warning);
	}

	public boolean isFailure() {
		if(getData().isFailure() && onlyUseMaximumValues() == false)
			return true;
		if(getValue() == null)
			return false;
		if(getPercentage() >= getErrorLevel())
			return true;
		return false;
	}

	public void setFailure(boolean failure) {
		getData().setFailure(failure);
	}

	public double getPercentage() {
		return getData().getPercentage();
	}

	public void setPercentage(double percentage) {
		getData().setPercentage(percentage);
	}
}
