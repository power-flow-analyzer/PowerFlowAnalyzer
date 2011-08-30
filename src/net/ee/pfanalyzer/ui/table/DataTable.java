package net.ee.pfanalyzer.ui.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import net.ee.pfanalyzer.model.CombinedNetworkElement;
import net.ee.pfanalyzer.model.IDerivedElement;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.ui.NetworkElementSelectionManager;
import net.ee.pfanalyzer.ui.util.INetworkDataViewer;

public class DataTable extends JTable implements INetworkDataViewer {

	private Network network;
	private String elementID;
	private DataTableModel model;
	private boolean selfSelection = false;
	
	public DataTable(String elementID) {
		super();
		this.elementID = elementID;
		model = new DataTableModel();
		setModel(model);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
	}
	
	protected JTableHeader createDefaultTableHeader() {
		return new JTableHeader(getColumnModel()) {
			public String getToolTipText(MouseEvent e) {
				Point p = e.getPoint();
				int index = getColumnModel().getColumnIndexAtX(p.x);
				int realIndex = 
                    columnModel.getColumn(index).getModelIndex();
				return model.getColumnDescription(realIndex);
			}
		};
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
		model.setData(network.getElements(elementID));
	}

	@Override
	public void refresh() {
		model.fireTableStructureChanged();
		revalidate();
	}

	@Override
	public void selectionChanged(Object data) {
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
					getSelectionModel().setSelectionInterval(i, i);
					scrollRectToVisible(getCellRect(i, 1, true));
					break;
				}
			}
		}
		selfSelection = false;
	}

	private void fireSelectionChanged() {
		if(selfSelection)// selection may be done by method selectionChanged
			return;
		Object selection = (getSelectedRow() >= 0) ? model.getDataObject(getSelectedRow()) : null;
		NetworkElementSelectionManager.getInstance().selectionChanged(selection);
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
		    	String text = value.toString();
		    	if(value instanceof Double)
		    		text = format.format(((Double) value).doubleValue());
		    	// getValueAt(row, column)
		    	setText(text);
		    	setToolTipText(model.getColumnDescription(realColumn));
	    	} catch(Exception e) {
	    		System.err.println(e);
	    		setText("Error");
	    		setForeground(Color.RED);
	    	}
	    	return this;
	    }
	}
}
