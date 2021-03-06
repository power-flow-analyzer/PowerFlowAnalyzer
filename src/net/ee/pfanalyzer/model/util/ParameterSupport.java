/*******************************************************************************
 * Copyright 2019 Markus Gronau
 * 
 * This file is part of PowerFlowAnalyzer.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.ee.pfanalyzer.model.util;

import java.util.List;

import net.ee.pfanalyzer.model.data.NetworkParameter;

public abstract class ParameterSupport {

	public abstract List<NetworkParameter> getParameterList();
	
	public NetworkParameter getOwnParameter(String id) {
		for (NetworkParameter p : getParameterList()) {
			if(p.getID() != null && p.getID().equals(id))
				return p;
		}
		return null;
	}
	
	public NetworkParameter getParameter(String id, boolean create) {
		NetworkParameter p = getOwnParameter(id);
		if(p != null)
			return p;
		if(create == false)
			return null;
//		p = getParameterValue(id);
//		if(p != null) {
			NetworkParameter reference = new NetworkParameter();
			reference.setID(id);
//			reference.setTextValue(new String(p.getTextValue()));
			getParameterList().add(reference);
			return reference;
//		}
//		return null;
	}
	
	public void removeOwnParameter(String id) {
		NetworkParameter parameter = getOwnParameter(id);
		if(parameter != null)
			getParameterList().remove(parameter);
	}
	
	public NetworkParameter getParameterValue(String id) {
		NetworkParameter parameter = getOwnParameter(id);
		if(parameter != null && parameter.getValue() != null)
			return parameter;
		return null;
	}
	
	public boolean hasParameterValue(String id) {
		NetworkParameter parameter = getParameterValue(id); 
		return parameter != null && getParameterValue(id).getValue() != null;
	}
	
	public int getIntParameter(String name, int defaultValue) {
		Integer value = getIntParameter(name);
		if(value == null)
			return defaultValue;
		return value;
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
	
	public boolean getBooleanParameter(String name, boolean defaultValue) {
		Boolean value = getBooleanParameter(name);
		if(value == null)
			return defaultValue;
		return defaultValue;
	}
	
	public Boolean getBooleanParameter(String name) {
		NetworkParameter p = getParameterValue(name);
		if(p != null && p.getValue() != null)
			return Boolean.valueOf(p.getValue());
		return null;
	}
	
	public double getDoubleParameter(String name, double defaultValue) {
		Double value = getDoubleParameter(name);
		if(value == null || value.isNaN())
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
	
	public String getTextParameter(String name, String defaultValue) {
		String text = getTextParameter(name);
		if(text != null)
			return text;
		else
			return defaultValue;
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
