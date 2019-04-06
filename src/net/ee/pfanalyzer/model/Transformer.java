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

public class Transformer extends Branch implements IDerivedElement<Branch> {

	private Branch parentElement;
	
	public Transformer(Branch branch) {
		super(branch.getNetwork(), branch.getElementData(), branch.getIndex());
		parentElement = branch;
	}
	
	public String getDisplayName() {
		Bus fromBus = getFromBus();
		String fromBusVoltage = "??";
		if(fromBus != null)
			fromBusVoltage = fromBus.getBaseVoltage() + " kV";
		Bus toBus = getToBus();
		String toBusVoltage = "??";
		if(toBus != null)
			toBusVoltage = toBus.getBaseVoltage() + " kV";
		return "Transformer " + (getIndex() + 1) + " (" + fromBusVoltage + " - " + toBusVoltage + ")";
	}

	@Override
	public Branch getRealElement() {
		return parentElement;
	}
	
	@Override
	public Bus getFromBus() {
		return parentElement.getFromBus();
	}

	@Override
	public Bus getToBus() {
		return parentElement.getToBus();
	}
}
