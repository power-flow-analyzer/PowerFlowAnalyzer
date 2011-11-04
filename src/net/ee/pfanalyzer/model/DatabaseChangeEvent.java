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
