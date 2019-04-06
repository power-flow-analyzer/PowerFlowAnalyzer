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

import net.ee.pfanalyzer.model.data.AbstractModelElementData;
import net.ee.pfanalyzer.model.util.ModelDBUtils;

public class DatabaseChangeEvent {
	
	public final static int ADDED = 0;
	public final static int REMOVED = 1;
	public final static int CHANGED = 2;

	private int type;
	private String parameterID, oldValue, newValue;
	private AbstractModelElementData elementData;

	public DatabaseChangeEvent(int type, String parameterID) {
		this.parameterID = parameterID;
	}

	public DatabaseChangeEvent(int type, AbstractModelElementData elementData) {
		this.type = type;
		this.elementData = elementData;
		this.parameterID = ModelDBUtils.getFullElementID(elementData);
	}

	public DatabaseChangeEvent(int type, String parameterID, String oldValue, String newValue) {
		this.type = type;
		this.parameterID = parameterID;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public int getType() {
		return type;
	}

	public AbstractModelElementData getElementData() {
		return elementData;
	}

	public void setElementData(AbstractModelElementData elementData) {
		this.elementData = elementData;
	}

	public String getParameterID() {
		return parameterID;
	}

	public void setParameterID(String parameterID) {
		this.parameterID = parameterID;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
}
