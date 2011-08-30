package net.ee.pfanalyzer.model;

public class PowerFlowFlag {

	private boolean[] failures;
	private double[] percentages;
	private String label;
	private int dataIndex;
//	private double[] warningBounds = { Double.NaN, Double.NaN };
//	private double[] errorBounds = { Double.NaN, Double.NaN };
	
	public PowerFlowFlag(String label) {
		this(label, -1);
	}
	
	public PowerFlowFlag(String label, int dataIndex) {
		this.label = label;
		this.dataIndex = dataIndex;
	}
	
	public int getDataIndex() {
		return dataIndex;
	}

	public void setFailures(boolean[] failures) {
		this.failures = failures;
	}
	
	public boolean[] getFailures() {
		return failures;
	}
	
	public boolean isCorrect(int index) {
		if(failures != null)
			return ! failures[index];
		return true;
	}
	
	public void setPercentages(double[] values) {
		this.percentages = values;
	}
	
	public double[] getPercentages() {
		return percentages;
	}
	
	public double getPercentage(int index) {
		if(percentages != null)
			return percentages[index];
		return -1;
	}
	
	public String getLabel() {
		return label;
	}
	
//	public void setMinErrorBound(double value) {
//		errorBounds[0] = value;
//	}
//	
//	public void setMaxErrorBound(double value) {
//		errorBounds[1] = value;
//	}
//	
//	public double getMinErrorBound() {
//		return errorBounds[0];
//	}
//	
//	public double getMaxErrorBound() {
//		return errorBounds[1];
//	}
}
