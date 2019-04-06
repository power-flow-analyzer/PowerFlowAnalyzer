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
package net.ee.pfanalyzer.model;

import java.util.ArrayList;
import java.util.List;

public abstract class CombinedNetworkElement<TYPE extends AbstractNetworkElement> {

	private List<TYPE> networkElements = new ArrayList<TYPE>();
	private int index;
	private String typeLabel;
	private Boolean isActive = null;
	private boolean isUngrouped = false;
	protected Boolean hasFailures = null;
	protected Boolean hasWarnings = null;
	
	public abstract String getLabel();
	
	public boolean hasFailures() {
		if(hasFailures == null) {
			hasFailures = false;
			for (AbstractNetworkElement element : getNetworkElements()) {
				if(element.hasFailures()) {
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
			for (AbstractNetworkElement element : getNetworkElements()) {
				if(element.hasWarnings()) {
					hasWarnings = true;
					break;
				}
			}
		}
		return hasWarnings;
	}
	
	public void updateFlags() {
		hasWarnings = null;
		hasFailures = null;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	public void addNetworkElement(TYPE element) {
		networkElements.add(element);
	}
	
	public List<TYPE> getNetworkElements() {
		return networkElements;
	}
	
	public int getNetworkElementCount() {
		return networkElements.size();
	}
	
	public TYPE getNetworkElement(int index) {
		return networkElements.get(index);
	}
	
	public TYPE getFirstNetworkElement() {
		return getNetworkElement(0);
	}
	
	public boolean contains(AbstractNetworkElement data) {
		return networkElements.contains(data);
	}
	
	public double getSumOfParameters(String parameterID) {
		double result = 0;
		for (TYPE element : getNetworkElements()) {
			result += element.getDoubleParameter(parameterID, 0);
		}
		return result;
	}
	
	public String getDisplayName(int displayFlags) {
		String text = getLabel();
		if(getNetworkElementCount() > 1 && (displayFlags & AbstractNetworkElement.DISPLAY_ELEMENT_COUNT) != 0) {
			String entityLabel = getTypeLabel() != null ? getTypeLabel() : "elements";
			text += " (" + getNetworkElementCount() + " " + entityLabel + ")";
		}
		return text;
	}
	
	public boolean isActive() {
		if(isActive == null) {
			isActive = false;
			for (AbstractNetworkElement element : getNetworkElements()) {
				if(element.isActive()) {
					isActive = true;
					break;
				}
			}
		}
		return isActive;
	}
	
	public String getTypeLabel() {
		return typeLabel;
	}
	
	public void setTypeLabel(String typeLabel) {
		this.typeLabel = typeLabel;
	}

	public boolean isUngrouped() {
		return isUngrouped;
	}

	public void setUngrouped(boolean isUngrouped) {
		this.isUngrouped = isUngrouped;
	}
}
