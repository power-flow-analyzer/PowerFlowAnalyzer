package net.ee.pfanalyzer.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import layout.SpringUtilities;
import net.ee.pfanalyzer.preferences.IPreferenceConstants;
import net.ee.pfanalyzer.ui.util.FileSelectionPanel;

public class OpenCaseDialog extends BaseDialog implements IPreferenceConstants {
	
	public static FileFilter CASE_FILE_FILTER = new FileFilter() {
		@Override
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith(".case");
		}

		@Override
		public String getDescription() {
			return "Case Files (*.case)";
		}
	};
	
	private String selectedInputFile;
	private FileSelectionPanel caseFileInputPanel;
	
	public OpenCaseDialog(Frame frame) {
		super(frame, "Open case");
		setText("Select a case file and press OK.");

		caseFileInputPanel = new FileSelectionPanel(PROPERTY_CASE_FILES, CASE_FILE_FILTER, false);
		JComponent openCaseFilePanel = new JPanel(new SpringLayout());
		openCaseFilePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		openCaseFilePanel.add(new JLabel("Case File: "));
		openCaseFilePanel.add(caseFileInputPanel);
		SpringUtilities.makeCompactGrid(openCaseFilePanel,
                1, 2, //rows, cols
                5, 5, //initialX, initialY
                5, 5);//xPad, yPad
		JComponent resizer = new JPanel(new BorderLayout());
		resizer.add(openCaseFilePanel, BorderLayout.NORTH);
		
		addOKButton();
		addCancelButton();
		
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(resizer, BorderLayout.CENTER);
		
		setCenterComponent(contentPane);
	}
	
	public void showDialog(int width, int height) {
		super.showDialog(width, height);
	}
	
	protected boolean checkInput() {
		return caseFileInputPanel.checkFile();
	}
	
	protected void okPressed() {
		caseFileInputPanel.setNewLocation(null);
		selectedInputFile = caseFileInputPanel.getFilePath();
		caseFileInputPanel.saveEntriesToProperties();
	}
	
	public String getSelectedInputFile() {
		return selectedInputFile;
	}
}