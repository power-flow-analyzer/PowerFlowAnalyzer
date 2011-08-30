package net.ee.pfanalyzer.io;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DataMap implements IConnectionConstants {

	private String connectionType;
	private Map<String, Object> dataMap = new HashMap<String, Object>();
	
	public DataMap(String connectionType) {
		this.connectionType = connectionType;
		if(connectionType.equals(NETWORK_DATA_CONNECTION)) {
			dataMap.put(IMPORT_TYPE_DATA_FIELD, new EmptyObject(DATA_TYPE_STRING, true));
			dataMap.put(NETWORK_DATA_FIELD, new EmptyObject(DATA_TYPE_STRING, true));
		} else if(connectionType.equals(POWER_FLOW_DATA_CONNECTION)) {
			// Matpower matrices
			dataMap.put(BUS_DATA_FIELD, new EmptyObject(DATA_TYPE_2DIM_DOUBLE_ARRAY, true));
			dataMap.put(BRANCH_DATA_FIELD, new EmptyObject(DATA_TYPE_2DIM_DOUBLE_ARRAY, true));
			dataMap.put(GENERATOR_DATA_FIELD, new EmptyObject(DATA_TYPE_2DIM_DOUBLE_ARRAY, true));
			// additional data
			dataMap.put(BUS_NAMES_DATA_FIELD, new EmptyObject(DATA_TYPE_STRING_ARRAY, false));
			dataMap.put(BUS_COORDINATES_DATA_FIELD, new EmptyObject(DATA_TYPE_2DIM_DOUBLE_ARRAY, false));
			// bus flags
			dataMap.put(FLAGS_DATA_FIELD_PREFIX + "-" + BUS_DATA_FIELD + "-" + FLAG_LABELS_DATA_FIELD, 
					new EmptyObject(DATA_TYPE_STRING_ARRAY, false));
			dataMap.put(FLAGS_DATA_FIELD_PREFIX + "-" + BUS_DATA_FIELD + "-" + FLAG_FAILURES_DATA_FIELD, 
					new EmptyObject(DATA_TYPE_2DIM_BOOLEAN_ARRAY, false));
			dataMap.put(FLAGS_DATA_FIELD_PREFIX + "-" + BUS_DATA_FIELD + "-" + FLAG_PERCENTAGES_DATA_FIELD, 
					new EmptyObject(DATA_TYPE_2DIM_DOUBLE_ARRAY, false));
			dataMap.put(FLAGS_DATA_FIELD_PREFIX + "-" + BUS_DATA_FIELD + "-" + FLAG_DATA_INDICES_DATA_FIELD, 
					new EmptyObject(DATA_TYPE_DOUBLE_ARRAY, false));
			// branch flags
			dataMap.put(FLAGS_DATA_FIELD_PREFIX + "-" + BRANCH_DATA_FIELD + "-" + FLAG_LABELS_DATA_FIELD, 
					new EmptyObject(DATA_TYPE_STRING_ARRAY, false));
			dataMap.put(FLAGS_DATA_FIELD_PREFIX + "-" + BRANCH_DATA_FIELD + "-" + FLAG_FAILURES_DATA_FIELD, 
					new EmptyObject(DATA_TYPE_2DIM_BOOLEAN_ARRAY, false));
			dataMap.put(FLAGS_DATA_FIELD_PREFIX + "-" + BRANCH_DATA_FIELD + "-" + FLAG_PERCENTAGES_DATA_FIELD, 
					new EmptyObject(DATA_TYPE_2DIM_DOUBLE_ARRAY, false));
			dataMap.put(FLAGS_DATA_FIELD_PREFIX + "-" + BRANCH_DATA_FIELD + "-" + FLAG_DATA_INDICES_DATA_FIELD, 
					new EmptyObject(DATA_TYPE_DOUBLE_ARRAY, false));
			// generator flags
			dataMap.put(FLAGS_DATA_FIELD_PREFIX + "-" + GENERATOR_DATA_FIELD + "-" + FLAG_LABELS_DATA_FIELD, 
					new EmptyObject(DATA_TYPE_STRING_ARRAY, false));
			dataMap.put(FLAGS_DATA_FIELD_PREFIX + "-" + GENERATOR_DATA_FIELD + "-" + FLAG_FAILURES_DATA_FIELD, 
					new EmptyObject(DATA_TYPE_2DIM_BOOLEAN_ARRAY, false));
			dataMap.put(FLAGS_DATA_FIELD_PREFIX + "-" + GENERATOR_DATA_FIELD + "-" + FLAG_PERCENTAGES_DATA_FIELD, 
					new EmptyObject(DATA_TYPE_2DIM_DOUBLE_ARRAY, false));
			dataMap.put(FLAGS_DATA_FIELD_PREFIX + "-" + GENERATOR_DATA_FIELD + "-" + FLAG_DATA_INDICES_DATA_FIELD, 
					new EmptyObject(DATA_TYPE_DOUBLE_ARRAY, false));
		} else if(connectionType.equals(SET_WORKING_DIR_CONNECTION)) {
			dataMap.put(WORKING_DIR_DATA_FIELD, new EmptyObject(DATA_TYPE_STRING, true));
//			dataMap.put(SIGNAL_TO_USER_DATA_FIELD, new EmptyObject(DATA_TYPE_BOOLEAN, true));
		}
	}

	public String getConnectionType() {
		return connectionType;
	}

	private Map<String, Object> getDataMap() {
		return dataMap;
	}
	
	public Object get(String dataField) {
		Object result = getDataMap().get(dataField);
		if(result instanceof EmptyObject)
			return null;
		return result;
	}
	
	public void put(String dataField, Object value) {
		if(value == null)
			throw new IllegalDataException(dataField, "data must not be null");
		Object old = getDataMap().get(dataField);
		if(old == null)
			throw new IllegalDataException(dataField, "unknown data field");
		if(old instanceof EmptyObject) { // field is empty
			EmptyObject eo = (EmptyObject) old;
			if(eo.isValidData(dataField, value) == false)
				throw new IllegalDataException(dataField, "wrong data type; " +
						"must be a " + getDataTypeName(eo.dataType) + 
						" but is " + value.getClass());
		}
		getDataMap().put(dataField, value);
	}
	
	public Set<String> keySet() {
		return getDataMap().keySet();
	}
	
	public void checkValues() {
		for (String dataField : getDataMap().keySet()) {
			Object value = getDataMap().get(dataField);
			if(value instanceof EmptyObject) {
				if(((EmptyObject) value).mustExist)
					throw new IllegalDataException(dataField, "data is missing");
			}
		}
	}
	
	public static String getDataTypeName(int dataType) {
		switch(dataType) {
		case DATA_TYPE_DOUBLE:
			return "number";
		case DATA_TYPE_DOUBLE_ARRAY:
			return "number vector (double[])";
		case DATA_TYPE_2DIM_DOUBLE_ARRAY:
			return "number matrix (double[][])";
		case DATA_TYPE_STRING_ARRAY:
			return "text vector (String[])";
		case DATA_TYPE_BOOLEAN:
			return "boolean";
		case DATA_TYPE_BOOLEAN_ARRAY:
			return "boolean vector (boolean[])";
		case DATA_TYPE_2DIM_BOOLEAN_ARRAY:
			return "boolean matrix (boolean[][])";
		}
		return "unknown";
	}
	
	// marker class
	class EmptyObject {
		
		int dataType;
		boolean mustExist;
		
		EmptyObject(int dataType, boolean mustExist) {
			this.dataType = dataType;
			this.mustExist = mustExist;
		}
		
		public boolean isValidData(String dataField, Object value) {
			switch(dataType) {
			case DATA_TYPE_DOUBLE:
				return value instanceof Double;
			case DATA_TYPE_DOUBLE_ARRAY:
				return value instanceof double[];
			case DATA_TYPE_2DIM_DOUBLE_ARRAY:
				return value instanceof double[][];
			case DATA_TYPE_STRING:
				return value instanceof String;
			case DATA_TYPE_STRING_ARRAY:
				return value instanceof String[];
			case DATA_TYPE_BOOLEAN:
				return value instanceof Boolean;
			case DATA_TYPE_BOOLEAN_ARRAY:
				return value instanceof boolean[];
			case DATA_TYPE_2DIM_BOOLEAN_ARRAY:
				return value instanceof boolean[][];
			}
			return false;
		}
	}
}
