package net.ee.pfanalyzer.ui.dataviewer;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.ee.pfanalyzer.model.data.DataViewerData;
import net.ee.pfanalyzer.ui.dialog.BaseDialog;

public class DataViewerDialog extends BaseDialog {
	
	private final static String DEFAULT_TEXT = "<html><b>Enter the properties for this viewer.<br>" +
			"The viewer will only contain those elements whose model ID matches the filter.";
	private final static String ERROR_TEXT = "<html><font color=\"red\">All values must be filled in to proceed.<br>" +
			"The viewer will only contain those elements whose model ID matches the filter.";
	
	private JTextField titleField, filterField;
	private DataViewerData viewerData;

	public DataViewerDialog(Frame frame, String title, DataViewerData viewerData) {
		super(frame, title, true);
		this.viewerData = viewerData;
		setText(DEFAULT_TEXT);
		
		titleField = new JTextField(viewerData.getTitle() == null ? "" : viewerData.getTitle());
		filterField = new JTextField(viewerData.getElementFilter() == null ? "" : viewerData.getElementFilter());
		JComponent openCaseFilePanel = new JPanel(new GridLayout(0, 2));
		openCaseFilePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		openCaseFilePanel.add(new JLabel("Viewer Title: "));
		openCaseFilePanel.add(titleField);
		openCaseFilePanel.add(new JLabel("Element Filter: "));
		openCaseFilePanel.add(filterField);
		
		JComponent resizer = new JPanel(new BorderLayout());
		resizer.add(openCaseFilePanel, BorderLayout.NORTH);
		
		addOKButton();
		addCancelButton();
		
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(resizer, BorderLayout.CENTER);
		
		setCenterComponent(contentPane);
	}
	
	@Override
	protected boolean checkInput() {
		if(titleField.getText().length() > 0 && filterField.getText().length() > 0) {
			setText(DEFAULT_TEXT);
			return true;
		} else {
			setText(ERROR_TEXT);
			return false;
		}
	}
	
	@Override
	protected void okPressed() {
		viewerData.setTitle(titleField.getText());
		viewerData.setElementFilter(filterField.getText());
	}
}
