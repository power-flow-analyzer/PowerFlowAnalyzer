package net.ee.pfanalyzer.ui.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.NetworkFlag;
import net.ee.pfanalyzer.model.data.NetworkParameter;

public class DataTableModel extends AbstractTableModel {

	private List<AbstractNetworkElement> data;
	private List<String> parameters = new ArrayList<String>();
	private List<String> columnNames = new ArrayList<String>();
	private List<String> columnDescriptions = new ArrayList<String>();
	private boolean emptyParameters = false;
	
	public DataTableModel() {
		data = new ArrayList<AbstractNetworkElement>();
	}
	
	void setData(List<AbstractNetworkElement> data) {
		this.data = data;
		reloadTableData();
	}
	
	void updateTableData() {
		fireTableDataChanged();
	}
	
	void reloadTableData() {
		parameters.clear();
		columnNames.clear();
		columnDescriptions.clear();
		emptyParameters = false;
		for (AbstractNetworkElement element : data) {
			for (NetworkParameter parameter : element.getParameterList()) {
				if(parameters.contains(parameter.getID()) == false) {
					parameters.add(parameter.getID());
					columnNames.add(parameter.getID());
					NetworkParameter param = element.getParameterDefinition(parameter.getID());
					String description = "<html>";
					if(param != null) {
						if(param.getLabel() != null)
							description += param.getLabel() + "<br>";
						else
							description += "&lt;no label defined&gt;" + "<br>";
						if(param.getDescription() != null)
							description += param.getDescription();
						else
							description += "&lt;no description defined&gt;";
					} else {
						description += "Undefined Parameter: " + parameter.getID();
					}
					columnDescriptions.add(description);
				}
			}
		}
		if(parameters.isEmpty()) {
			emptyParameters = true;
			parameters.add("EMPTY");
			columnNames.add(" ");
			columnDescriptions.add("This network element has no parameters.");
		}
		fireTableStructureChanged();
	}
	
	protected AbstractNetworkElement getDataObject(int row) {
		return data.get(row);
	}
	
	protected void moveColumns(TableColumnModel columnModel) {
		
	}
	
	@Override
	public int getColumnCount() {
		return parameters.size();
	}

	@Override
	public int getRowCount() {
		return data.size();
	}
	
	public String getColumnDescription(int column) {
		return columnDescriptions.get(column);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		AbstractNetworkElement element = data.get(rowIndex);
		if(emptyParameters) {
			return element.getDisplayName(AbstractNetworkElement.DISPLAY_NAME);
		} else {
			String parameterID = parameters.get(columnIndex);
			return element.getParameterDisplayValue(parameterID);
		}
	}
	
	AbstractNetworkElement getElement(int rowIndex) {
		return data.get(rowIndex);
	}
	
	String getParameterID(int columnIndex) {
		return parameters.get(columnIndex);
	}
	
//	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
//		if(emptyParameters)
//			return;
//		String value = (String) aValue;
//		AbstractNetworkElement element = data.get(rowIndex);
//		String parameterID = parameters.get(columnIndex);
//		element.setParameter(parameterID, value);
//		NetworkChangeEvent event = new NetworkChangeEvent(element, parameterID, null, value);
//		element.getNetwork().fireNetworkElementChanged(event);
//    }
	
	protected boolean isValueCorrect(int row, int column) {
		AbstractNetworkElement element = data.get(row);
		List<NetworkFlag> flags = element.getFlags();
		boolean correct = true;
		for (NetworkFlag flag : flags) {
			if(flag.isFailure()) {
				String parameterID = parameters.get(column);
				if(flag.containsParameter(parameterID)) {
					correct = false;
					break;
				}
			}
		}
		return correct;
	}
	
	public boolean containsParameter(String id) {
		return parameters.contains(id);
	}
	
	@Override
	public String getColumnName(int column) {
		return columnNames.get(column);
	}
}
