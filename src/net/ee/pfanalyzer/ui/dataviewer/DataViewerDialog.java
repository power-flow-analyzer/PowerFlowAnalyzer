package net.ee.pfanalyzer.ui.dataviewer;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.ee.pfanalyzer.PowerFlowAnalyzer;
import net.ee.pfanalyzer.model.PowerFlowCase;
import net.ee.pfanalyzer.model.data.AbstractModelElementData;
import net.ee.pfanalyzer.model.data.DataViewerData;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.ui.db.ModelDBDialog;
import net.ee.pfanalyzer.ui.dialog.BaseDialog;

public class DataViewerDialog extends BaseDialog {
	
	private final static String DEFAULT_TEXT = "<html><b>Enter the properties for this viewer.<br>" +
			"The viewer will only contain those elements whose model ID matches the filter.";
	private final static String ERROR_TEXT = "<html><font color=\"red\">All values must be filled in to proceed.<br>" +
			"The viewer will only contain those elements whose model ID matches the filter.";
	
	private JTextField titleField, filterField;
	private DataViewerData viewerData;

	public DataViewerDialog(Window frame, String title, DataViewerData viewerData, final PowerFlowCase caze) {
		super(frame, title, true);
		this.viewerData = viewerData;
		setText(DEFAULT_TEXT);
		
		titleField = new JTextField(viewerData.getTitle() == null ? "" : viewerData.getTitle());
		filterField = new JTextField(viewerData.getElementFilter() == null ? "" : viewerData.getElementFilter());
		JButton changeModelButton = PowerFlowAnalyzer.createButton("Select an element from the database", "database_go.png", "Select", false);
		changeModelButton.setMargin(new Insets(2, 2, 1, 1));
		changeModelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ModelDBDialog dialog = new ModelDBDialog(DataViewerDialog.this, 
						ModelDBDialog.GET_MODEL_OR_CLASS_MODE, caze.getModelDB(), true, "Show elements of type");
				dialog.showDialog(900, 500);
				AbstractModelElementData selected = dialog.getSelectedElement();
				if(selected != null) {
					filterField.setText(ModelDBUtils.getParameterID(selected));
					if(titleField.getText().isEmpty() && selected.getLabel() != null)
						titleField.setText(selected.getLabel());
				}
			}
		});
		JPanel filterResizer = new JPanel(new BorderLayout());
		filterResizer.add(filterField, BorderLayout.CENTER);
		filterResizer.add(changeModelButton, BorderLayout.EAST);
		JComponent openCaseFilePanel = new JPanel(new GridLayout(0, 2));
		openCaseFilePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		openCaseFilePanel.add(new JLabel("Viewer Title: "));
		openCaseFilePanel.add(titleField);
		openCaseFilePanel.add(new JLabel("Element Filter: "));
		openCaseFilePanel.add(filterResizer);
		
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
