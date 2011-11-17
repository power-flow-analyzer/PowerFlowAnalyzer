package net.ee.pfanalyzer.model;

public class ParameterException extends Exception {

	private int rowIndex, columnIndex;
	private String parameterID, value, expectedType;
	
	public ParameterException() {
		
	}
	
	public ParameterException(String parameterID, String value, String expectedType) {
		this.parameterID = parameterID;
		this.value = value;
		this.expectedType = expectedType;
	}
	
	public void setParameterID(String parameterID) {
		this.parameterID = parameterID;
	}
	
	public String getParameterID() {
		return parameterID;
	}
	
	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}
	
	public int getRowIndex() {
		return rowIndex;
	}
	
	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}
	
	public int getColumnIndex() {
		return columnIndex;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getExpectedType() {
		return expectedType;
	}

	public void setExpectedType(String expectedType) {
		this.expectedType = expectedType;
	}
}
