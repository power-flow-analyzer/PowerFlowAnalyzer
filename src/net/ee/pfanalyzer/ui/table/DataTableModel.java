package net.ee.pfanalyzer.ui.table;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.NetworkFlag;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.data.NetworkParameterType;

public class DataTableModel extends AbstractTableModel {

	private List<AbstractNetworkElement> data;
	private List<String> parameters = new ArrayList<String>();
	private List<String> columnNames = new ArrayList<String>();
	private List<String> columnDescriptions = new ArrayList<String>();
	
	public DataTableModel() {
		data = new ArrayList<AbstractNetworkElement>();
	}
	
	void setData(List<AbstractNetworkElement> data) {
		this.data = data;
		parameters.clear();
		columnNames.clear();
		columnDescriptions.clear();
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
		String parameterID = parameters.get(columnIndex);
		AbstractNetworkElement element = data.get(rowIndex);
		NetworkParameter param = element.getParameterDefinition(parameterID);
		if(param != null) {
			NetworkParameterType type = param.getType();
			if(type != null) {
				if(type.equals(NetworkParameterType.INTEGER))
					return element.getIntParameter(parameterID);
				if(type.equals(NetworkParameterType.DOUBLE)) {
					Double value = element.getDoubleParameter(parameterID);
					if(value != null && param.getDisplay() != null) {
						String pattern = param.getDisplay().getDecimalFormatPattern();
						DecimalFormat format = new DecimalFormat(pattern);
						return format.format(value);
					}
					return value;
				}
			}
		}
		return element.getTextParameter(parameterID);
	}
	
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
	
	@Override
	public String getColumnName(int column) {
		return columnNames.get(column);
	}
}
