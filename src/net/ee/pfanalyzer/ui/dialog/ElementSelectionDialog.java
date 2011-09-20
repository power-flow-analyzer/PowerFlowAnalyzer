package net.ee.pfanalyzer.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.ee.pfanalyzer.model.AbstractNetworkElement;

public class ElementSelectionDialog extends BaseDialog {

	private final static String ERROR_TEXT = "<html><font color=\"red\">At least one element must be selected.";
	
	private Vector<Vector<Object>> items = new Vector<Vector<Object>>();
	List<AbstractNetworkElement> selectedElements = new ArrayList<AbstractNetworkElement>();
	private JTable elementTable;
	private String text;
	
	public ElementSelectionDialog(Frame frame, Vector<AbstractNetworkElement> elements, String title, String text) {
		super(frame, title, true);
		this.text = text;
		setText(text);
		JPanel contentPane = new JPanel(new BorderLayout());
		for (AbstractNetworkElement element : elements) {
			Vector<Object> item = new Vector<Object>();
			item.add(new Boolean(true));
			item.add(element);
			items.add(item);
		}
		
		TableCellRenderer checkBoxRenderer = new TableCellRenderer() {
			JCheckBox renderer = new JCheckBox();
			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				Boolean selected = ((Boolean) value);
				renderer.setSelected(selected);
				return renderer;
			}
		};
		TableCellRenderer elementRenderer = new TableCellRenderer() {
			JLabel renderer = new JLabel();
			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				String label = ((AbstractNetworkElement) value).getDisplayName(
						AbstractNetworkElement.DISPLAY_DEFAULT);
				renderer.setText(label);
				return renderer;
			}
		};
		Vector<String> columnNames = new Vector<String>();
		columnNames.add("");
		columnNames.add("Element");
		Vector<Vector<Object>> v = new Vector<Vector<Object>>();
		v.add(new Vector<Object>());
		elementTable = new JTable();
		elementTable.setAutoCreateColumnsFromModel(false);
		TableColumn firstColumn = new TableColumn(0, 20, checkBoxRenderer, new DefaultCellEditor(new JCheckBox()));
		firstColumn.setHeaderValue("");
		TableColumn secondColumn = new TableColumn(1, 150, elementRenderer, null);
		secondColumn.setHeaderValue("Element");
		elementTable.addColumn(firstColumn);
		elementTable.addColumn(secondColumn);
		elementTable.setModel(new DefaultTableModel(items, columnNames));
		
		addOKButton();
		addCancelButton();
		
		contentPane.add(new JScrollPane(elementTable), BorderLayout.CENTER);
		setCenterComponent(contentPane);
		showDialog(500, 300);
	}
	
	public List<AbstractNetworkElement> getSelectedElements() {
		return selectedElements;
	}

	@Override
	protected boolean checkInput() {
		selectedElements.clear();
		for (Vector<Object> item : items) {
			if((Boolean) item.get(0))
				selectedElements.add((AbstractNetworkElement) item.get(1));
		}
		if(selectedElements.isEmpty()) {
			setText(ERROR_TEXT);
			return false;
		}
		setText(text);
		return true;
	}
}
