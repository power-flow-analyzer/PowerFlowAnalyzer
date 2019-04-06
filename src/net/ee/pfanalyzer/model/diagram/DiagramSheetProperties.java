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
package net.ee.pfanalyzer.model.diagram;

import net.ee.pfanalyzer.model.matpower.BranchDescriptor;
import net.ee.pfanalyzer.model.matpower.BusDescriptor;
import net.ee.pfanalyzer.model.matpower.GeneratorDescriptor;

public class DiagramSheetProperties {

	private boolean[] selectedBusDataDiagrams = new boolean[BusDescriptor.DATA_FIELD_COUNT];
	private boolean[] selectedBranchDataDiagrams = new boolean[BranchDescriptor.DATA_FIELD_COUNT];
	private boolean[] selectedGeneratorDataDiagrams = new boolean[GeneratorDescriptor.DATA_FIELD_COUNT];
	private String title;
	
	public int getBusDataFieldsCount() {
		return BusDescriptor.DATA_FIELD_COUNT;
	}
	
	public int getBranchDataFieldsCount() {
		return BranchDescriptor.DATA_FIELD_COUNT;
	}
	
	public int getGeneratorDataFieldsCount() {
		return GeneratorDescriptor.DATA_FIELD_COUNT;
	}
	
	public boolean hasBusDataDiagram(int index) {
		return selectedBusDataDiagrams[index];
	}
	
	public void setBusDataDiagram(int index, boolean select) {
		selectedBusDataDiagrams[index] = select;
	}
	
	public boolean hasBranchDataDiagram(int index) {
		return selectedBranchDataDiagrams[index];
	}
	
	public void setBranchDataDiagram(int index, boolean select) {
		selectedBranchDataDiagrams[index] = select;
	}
	
	public boolean hasGeneratorDataDiagram(int index) {
		return selectedGeneratorDataDiagrams[index];
	}
	
	public void setGeneratorDataDiagram(int index, boolean select) {
		selectedGeneratorDataDiagrams[index] = select;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
}
