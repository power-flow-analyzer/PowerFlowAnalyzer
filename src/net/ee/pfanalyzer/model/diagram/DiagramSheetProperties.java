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
