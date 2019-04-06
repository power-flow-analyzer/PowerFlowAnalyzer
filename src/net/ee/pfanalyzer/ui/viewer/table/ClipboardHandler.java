/*******************************************************************************
 * Copyright 2019 Markus Gronau
 * 
 * This file is part of PowerFlowAnalyzer.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.ee.pfanalyzer.ui.viewer.table;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import net.ee.pfanalyzer.PowerFlowAnalyzer;
import net.ee.pfanalyzer.model.ParameterException;

public class ClipboardHandler {
	
	private DataTable dataTable;

	public ClipboardHandler(DataTable table) {
		dataTable = table;
		// register up copy action
		dataTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK), "Copy");
		dataTable.getActionMap().put("Copy", new CopyAction());
		// register up paste action
		dataTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK), "Paste");
		dataTable.getActionMap().put("Paste", new PasteAction());
	}
	
	class CopyAction extends AbstractAction {
		public void actionPerformed(ActionEvent event) {
			try {
				int columnCount = dataTable.getSelectedColumnCount();
				int rowCount = dataTable.getSelectedRowCount();
				int[] selectedRows = dataTable.getSelectedRows();
				int[] selectedColumns = dataTable.getSelectedColumns();
				StringBuffer buffer = new StringBuffer();
				for (int row = 0; row < rowCount; row++) {
					for (int col = 0; col < columnCount; col++) {
						String value = dataTable.exportValue(selectedRows[row], selectedColumns[col]);
						buffer.append(value);
						if (col < columnCount - 1)
							buffer.append("\t");
					}
					buffer.append("\n");
				}
//				System.out.println("export=\""+buffer.toString() + "\"");
				StringSelection content = new StringSelection(buffer.toString());
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(content, content);
			} catch(Exception e) {
				Toolkit.getDefaultToolkit().beep();
				PowerFlowAnalyzer.getInstance().setError("Cannot copy contents from table: " + e);
				e.printStackTrace();
			}
		}
	}

	class PasteAction extends AbstractAction {
		public void actionPerformed(ActionEvent event) {
			int[] selectedRows = dataTable.getSelectedRows();
			int[] selectedColumns = dataTable.getSelectedColumns();
			int row = 0, column = 0;
			try {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				if(clipboard.getContents(null) == null)
					return;
				Object clipboardContent = clipboard.getContents(null).getTransferData(DataFlavor.stringFlavor);
				if( ! (clipboardContent instanceof String))
					return;
				String content = (String) clipboardContent;
//				System.out.println("import=\""+content + "\"");
				List<String> rows = splitRows(content);
				for (row = 0; row < rows.size(); row++) {
					String rowstring = rows.get(row);
					List<String> columns = splitColumns(rowstring);
					for (column = 0; column < columns.size(); column++) {
						String value = columns.get(column);
						int tagetCellRow;
						if(row < selectedRows.length)
							tagetCellRow = selectedRows[row];
						else
							tagetCellRow = selectedRows[selectedRows.length - 1] + row - selectedRows.length + 1;
						int tagetCellColumn;
						if(column < selectedColumns.length)
							tagetCellColumn = selectedColumns[column];
						else
							tagetCellColumn = selectedColumns[selectedColumns.length - 1] + column - selectedColumns.length + 1;
						if (tagetCellRow < dataTable.getRowCount() 
								&& tagetCellColumn < dataTable.getColumnCount())
							dataTable.importValue(value, tagetCellRow, tagetCellColumn);
					}
				}
			} catch (ParameterException e) {
				Toolkit.getDefaultToolkit().beep();
				String text = "Cannot import value \"" + e.getValue() + "\""
						+ "\nThe column " + e.getParameterID() + " only allows values of type " + e.getExpectedType()
						+ "\n"
						+ "\nSelection: row " + (row+1) + ", column " + (column+1)
						+ "\nTarget cell: row " + (e.getRowIndex()+1) + ", column " + (e.getColumnIndex()+1);
				PowerFlowAnalyzer.getInstance().setError(text);
			} catch (Exception e) {
				Toolkit.getDefaultToolkit().beep();
				PowerFlowAnalyzer.getInstance().setError("Cannot paste contents into table: " + e);
				e.printStackTrace();
			}
		}
	}


	private static List<String> splitRows(String s) {
		return splitString(s, '\n', false);
	}

	private static List<String> splitColumns(String s) {
		return splitString(s, '\t', true);
	}

	private static List<String> splitString(String s, char separator, boolean includeSeparatorAtEnd) {
		List<String> list = new ArrayList<String>();
		char[] text = s.toCharArray();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < text.length; i++) {
			if(text[i] == separator) {
				list.add(buffer.toString());
				buffer = new StringBuffer();
			} else
				buffer.append(text[i]);
		}
		if(buffer.length() > 0)
			list.add(buffer.toString());
		else if(includeSeparatorAtEnd && text.length > 0 && text[text.length - 1] == separator)
			list.add("");
		return list;
	}
}
