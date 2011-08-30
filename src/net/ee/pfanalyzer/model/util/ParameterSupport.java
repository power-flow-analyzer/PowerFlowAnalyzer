package net.ee.pfanalyzer.model.util;

import java.util.List;

import net.ee.pfanalyzer.model.data.NetworkParameter;

public abstract class ParameterSupport {

	public abstract List<NetworkParameter> getParameterList();
	
	protected NetworkParameter getOwnParameter(String id) {
		for (NetworkParameter p : getParameterList()) {
			if(p.getID() != null && p.getID().equals(id))
				return p;
		}
		return null;
	}
	
	protected NetworkParameter getParameterValue(String id) {
		NetworkParameter parameter = getOwnParameter(id);
		if(parameter != null && parameter.getValue() != null)
			return parameter;
		return null;
	}
	
	public Integer getIntParameter(String name) {
		NetworkParameter p = getParameterValue(name);
		if(p != null && p.getValue() != null) {
			// prevents e.g. "1.0" from throwing a parser error
			return Double.valueOf(p.getValue()).intValue();
//			return Integer.valueOf(p.getValue());
		}
		return null;
	}
	
	public double getDoubleParameter(String name, double defaultValue) {
		Double value = getDoubleParameter(name);
		if(value == null)
			return defaultValue;
		return value;
	}
	
	public Double getDoubleParameter(String name) {
		NetworkParameter p = getParameterValue(name);
		if(p != null && p.getValue() != null)
			return Double.valueOf(p.getValue());
		return null;
	}
	
	public String getTextParameter(String name) {
		NetworkParameter p = getParameterValue(name);
		if(p != null && p.getValue() != null)
			return p.getValue();
		return null;
	}
	
	public void setParameter(String name, String value) {
		NetworkParameter p = getOwnParameter(name);
		if(p == null) {
			p = new NetworkParameter();
			p.setID(name);
			getParameterList().add(p);
		}
		p.setValue(value);
	}
	
	public void setParameter(String name, double value) {
//		if(value == (int) value)
//			System.out.println("is integer: " + value);
		setParameter(name, Double.toString(value));
	}
	
	public void setParameter(String name, int value) {
		setParameter(name, Integer.toString(value));
	}
}
