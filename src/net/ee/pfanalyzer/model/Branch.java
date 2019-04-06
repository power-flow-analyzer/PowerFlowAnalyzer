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

import net.ee.pfanalyzer.model.data.AbstractNetworkElementData;
import net.ee.pfanalyzer.model.matpower.IBranchDataConstants;
import net.ee.pfanalyzer.ui.shape.DefaultBranchShape;


public class Branch extends AbstractNetworkElement implements IBranchDataConstants {
	
	private Bus fromBus, toBus;
	private boolean isInverted = false;
	
	public Branch(Network data, int index) {
		super(data, index);
	}
	
	public Branch(Network data, AbstractNetworkElementData elementData, int index) {
		super(data, elementData, index);
	}
	
	@Override
	public String getDefaultModelID() {
		return ModelDB.DEFAULT_BRANCH_MODEL;
	}
	
	@Override
	public String getDefaultShapeID() {
		return DefaultBranchShape.ID;
	}
	
	public int getInitialBranchStatus() {
		return getIntParameter(PROPERTY_INITIAL_BRANCH_STATUS, -1);
	}
	
	public boolean isActive() {
		return getInitialBranchStatus() > 0;
	}
	
	public Bus getFromBus() {
		return fromBus;
	}

	public void setFromBus(Bus fromBus) {
		this.fromBus = fromBus;
	}

	public Bus getToBus() {
		return toBus;
	}

	public void setToBus(Bus toBus) {
		this.toBus = toBus;
	}
	
	public int getFromBusNumber() {
		return getIntParameter(PROPERTY_FROM_BUS_NUMBER, -1);
	}
	
	void setFromBusNumber(int number) {
		setParameter(PROPERTY_FROM_BUS_NUMBER, number);
	}
	
	public int getFromBusVoltage() {
		return getFromBus().getBaseVoltage();
	}
	
	public int getToBusVoltage() {
		return getToBus().getBaseVoltage();
	}

	public int getToBusNumber() {
		return getIntParameter(PROPERTY_TO_BUS_NUMBER, -1);
	}
	
	void setToBusNumber(int number) {
		setParameter(PROPERTY_TO_BUS_NUMBER, number);
	}
	
	public boolean isInverted() {
		return isInverted;
	}

	public void setInverted(boolean isInverted) {
		this.isInverted = isInverted;
	}

	@Override
	public String getDisplayName(int displayFlags) {
		Bus fromBus = getFromBus();
		String fromBusName = "??";
		if(fromBus != null) {
			fromBusName = fromBus.getName();
			if(fromBusName == null)
				fromBusName = fromBus.getDisplayName(displayFlags);
		}
		Bus toBus = getToBus();
		String toBusName = "??";
		if(toBus != null) {
			toBusName = toBus.getName();
			if(toBusName == null)
				toBusName = toBus.getDisplayName(displayFlags);
		}
		String appendix = "";
		int fromBusVoltage = getFromBusVoltage();
		int toBusVoltage = getToBusVoltage();
		if(fromBusVoltage > 0 && toBusVoltage > 0 && (displayFlags & DISPLAY_ADDITIONAL_INFO) != 0) {
			if(fromBusVoltage == toBusVoltage)
				appendix = "(" + fromBusVoltage + " kV)";
			else {
				fromBusName += "(" + fromBusVoltage + " kV)";
				toBusName += "(" + toBusVoltage + " kV)";
			}
		}
		return "Branch " + (getIndex() + 1) + " (" + fromBusName + " - " + toBusName + ")" + appendix;
	}
}
