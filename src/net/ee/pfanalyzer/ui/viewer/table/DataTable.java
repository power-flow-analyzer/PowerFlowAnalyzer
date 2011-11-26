package net.ee.pfanalyzer.ui.viewer.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.CombinedNetworkElement;
import net.ee.pfanalyzer.model.IDerivedElement;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.NetworkChangeEvent;
import net.ee.pfanalyzer.model.ParameterException;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.ui.NetworkElementSelectionManager;
import net.ee.pfanalyzer.ui.util.SwingUtils;
import net.ee.pfanalyzer.ui.viewer.DataViewerConfiguration;
import net.ee.pfanalyzer.ui.viewer.INetworkDataViewer;

public class DataTable extends JTable implements INetworkDataViewer {

	public final static String VIEWER_ID = "viewer.table.type_filter";
	
	private DataViewerConfiguration viewerConfiguration;
	private Network network;
	private DataTableModel model;
	private boolean selfSelection = false;
//	private Component parentContainer;
	
	public DataTable(DataViewerConfiguration viewerConfiguration, Component parent) {
		super();
		this.viewerConfiguration = viewerConfiguration;
//		this.parentContainer = parent;
		model = new DataTableModel();
		setModel(model);
        getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        getColumnModel().getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setColumnSelectionAllowed(true);
		setAutoResizeMode(AUTO_RESIZE_OFF);
		for (int i = 0; i < getColumnCount(); i++) {
			getColumnModel().getColumn(i).setMinWidth(50);
		}
		setFillsViewportHeight(true);
		setDefaultRenderer(Object.class, new CellRenderer());
		revalidate();
		
		getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				fireSelectionChanged();
			}
		});
//		if(model.getColumnCount() > 2)
//			model.moveColumns(getColumnModel());
		
		// add handler for copy and paste actions
		new ClipboardHandler(this);
	}
	
	public DataTableModel getTableModel() {
		return model;
	}
	
	protected JTableHeader createDefaultTableHeader() {
		return new JTableHeader(getColumnModel()) {
			public String getToolTipText(MouseEvent e) {
				Point p = e.getPoint();
				int index = getColumnModel().getColumnIndexAtX(p.x);
				if(index == -1)
					return null;
				int realIndex = 
                    columnModel.getColumn(index).getModelIndex();
				return model.getColumnDescription(realIndex);
			}
			public void updateUI() {
				setUI(new DataTableHeaderUI());
			}
		};
	}
	
	@Override
	public DataViewerConfiguration getViewerConfiguration() {
		return viewerConfiguration;
	}

	@Override
	public JComponent getComponent() {
		return this;
	}

	@Override
	public Network getNetwork() {
		return network;
	}

	@Override
	public void setData(Network network) {
		this.network = network;
		filterElements();
		refresh();
	}
	
	public String exportValue(int rowIndex, int columnIndex) {
		boolean roundedValues = getViewerConfiguration().getBooleanParameter(
				"EXPORT_ROUND_VALUES", false);
		boolean removePercentageSymbol = getViewerConfiguration().getBooleanParameter(
				"EXPORT_REMOVE_PERCENTAGE_SYMBOL", true);
		boolean convertBooleanValues = getViewerConfiguration().getBooleanParameter(
				"EXPORT_CONVERT_BOOLEAN_VALUES", true);
		String decimalSeparator = getViewerConfiguration().getTextParameter(
				"EXPORT_DECIMAL_SEPARATOR", SwingUtils.DEFAULT_DECIMAL_SEPARATOR);
		String exportListValues = getViewerConfiguration().getTextParameter(
				"EXPORT_LIST_VALUES", "value");
		AbstractNetworkElement element = getTableModel().getElement(rowIndex);
		String parameterID = getTableModel().getParameterID(columnIndex);
		String value = ModelDBUtils.getParameterValue(element, parameterID, roundedValues, 
				decimalSeparator, exportListValues, removePercentageSymbol, convertBooleanValues);
		if(value != null)
			return value;
		else
			return "";
	}
	
	public void importValue(String value, int rowIndex, int columnIndex) throws ParameterException {
		String decimalSeparator = getViewerConfiguration().getTextParameter(
				"IMPORT_DECIMAL_SEPARATOR", SwingUtils.DEFAULT_DECIMAL_SEPARATOR);
		boolean overwriteWithEmpty = getViewerConfiguration().getBooleanParameter(
				"IMPORT_OVERWRITE_WITH_EMPTY", true);
		AbstractNetworkElement element = getTableModel().getElement(rowIndex);
		String parameterID = getTableModel().getParameterID(columnIndex);
		try {
//			System.out.println("import value=" + value);
			NetworkParameter oldParameter = element.getParameterValue(parameterID);
			String oldValue = oldParameter != null ? oldParameter.getValue() : null;
//			System.out.println("    old Value="+oldValue);
			String newValue = ModelDBUtils.setParameterValue(element, parameterID, value, 
					decimalSeparator, overwriteWithEmpty);
//			System.out.println("    new value="+newValue); 
			NetworkChangeEvent event = new NetworkChangeEvent(element, parameterID, oldValue, newValue);
			element.getNetwork().fireNetworkElementChanged(event);
		} catch (ParameterException e) {
			e.setRowIndex(rowIndex);
			e.setColumnIndex(columnIndex);
			throw e;
		}
	}
	
	private void filterElements() {
//		model.setData(getNetwork().getElements(elementID));
	}

	@Override
	public void refresh() {
		selfSelection = true;
		model.setData(getNetwork().getElements(viewerConfiguration.getTextParameter("ELEMENT_FILTER", "")));
//		model.reloadTableData();
		resizeAndRepaint();
		selfSelection = false;
	}

	@Override
	public void selectionChanged(Object data) {
		if(selfSelection)// selection may be done by method fireSelectionChanged
			return;
		selfSelection = true;
		if(data == null) {
			getSelectionModel().clearSelection();
		} else {
			// check for combined elements -> tables don't show combined elements
			if(data instanceof CombinedNetworkElement<?>)
				data = ((CombinedNetworkElement<?>) data).getFirstNetworkElement();
			// check for derived elements -> transformers have parent elements
			if(data instanceof IDerivedElement<?>)
				data = ((IDerivedElement<?>) data).getRealElement();
			for (int i = 0; i < getRowCount(); i++) {
				if(model.getDataObject(i) == data) {
					if(getSelectedColumnCount() == 0 && getColumnCount() > 0)
						setColumnSelectionInterval(0, getColumnCount() - 1);
					getSelectionModel().setSelectionInterval(i, i);
					scrollRectToVisible(getCellRect(i, 1, true));
					break;
				}
			}
		}
		selfSelection = false;
	}

	@Override
	public void networkChanged(NetworkChangeEvent event) {
//		System.out.println("table: networkChanged");
//		filterElements();
		refresh();
	}

	@Override
	public void networkElementAdded(NetworkChangeEvent event) {
//		System.out.println("viewer: networkElementAdded");
//		filterElements();
		refresh();
	}

	@Override
	public void networkElementChanged(NetworkChangeEvent event) {
		selfSelection = true; // TODO
		if(event.getParameterID() != null && model.containsParameter(event.getParameterID()))
			model.updateTableData();
		else
			model.reloadTableData();
		resizeAndRepaint();
		selfSelection = false;
	}

	@Override
	public void networkElementRemoved(NetworkChangeEvent event) {
//		filterElements();
		refresh();
	}

	private void fireSelectionChanged() {
		if(selfSelection)// selection may be done by method selectionChanged
			return;
		selfSelection = true;
		Object selection = (getSelectedRow() >= 0) ? model.getDataObject(getSelectedRow()) : null;
		NetworkElementSelectionManager.selectionChanged(this, selection);
		selfSelection = false;
	}
	
	@Override
	public void dispose() {
		
	}

	class CellRenderer extends JLabel 
    implements TableCellRenderer {
		
		DecimalFormat format = new DecimalFormat("#.######");
		
		CellRenderer() {
	    	setOpaque(true);
		}
		
	    public Component getTableCellRendererComponent(
	                            JTable table, Object value,
	                            boolean isSelected, boolean hasFocus,
	                            int row, int column) {
	    	try {
				int realColumn = columnModel.getColumn(column).getModelIndex();
	    		boolean isCorrect = model.isValueCorrect(row, realColumn);
		    	if(isSelected) {
		    		if(isCorrect)
		    			setForeground(table.getSelectionForeground());
		    		else
		    			setForeground(Color.YELLOW);
		    		setBackground(table.getSelectionBackground());
		    	} else {
		    		if(isCorrect)
		    			setForeground(table.getForeground());
		    		else
		    			setForeground(Color.RED);
		    		setBackground(table.getBackground());
		    	}
		    	String text = value != null ? value.toString() : "";
		    	if(value instanceof Double)
		    		text = format.format(((Double) value).doubleValue()); // TODO
		    	// getValueAt(row, column)
		    	setText(text);
		    	setToolTipText(model.getColumnDescription(realColumn));
	    	} catch(Exception e) {
	    		e.printStackTrace();
	    		setText("Error");
	    		setForeground(Color.RED);
	    	}
	    	return this;
	    }
	}
	
	public void updateUI() {
		setUI(new DataTableUI());
	}
	
	class DataTableUI extends BasicTableUI {
		public Dimension getPreferredSize(JComponent c) {
			try {
				return super.getPreferredSize(c);
			} catch(Exception e) {
//				System.err.println("Exception caught 1");
				return new Dimension(0, 0);
			}
		}
	}
	
	class DataTableHeaderUI extends BasicTableHeaderUI {
		public Dimension getPreferredSize(JComponent c) {
			try {
				return super.getPreferredSize(c);
			} catch(Exception e) {
//				System.err.println("Exception caught 2");
				return new Dimension(0, 0);
			}
		}
	}
}
