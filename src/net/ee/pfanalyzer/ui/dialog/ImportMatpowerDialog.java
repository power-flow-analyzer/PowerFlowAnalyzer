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

public class ImportMatpowerDialog extends BaseDialog implements IPreferenceConstants {
	
	public static FileFilter MATPOWER_CASE_FILTER = new FileFilter() {
		@Override
		public boolean accept(File f) {
			return f.isDirectory() || (f.getName().startsWith("case") && f.getName().endsWith(".m"));
		}

		@Override
		public String getDescription() {
			return "Matpower Case Files (case*.m)";
		}
	};
	
	private String selectedInputFile;
	private JLabel workingDirLabel;
	private FileSelectionPanel matpowerCaseInputPanel;
	
	public ImportMatpowerDialog(Frame frame) {
		super(frame, "Open Matpower case");
		setText("Select a Matpower case and press OK.");

		matpowerCaseInputPanel = new FileSelectionPanel(PROPERTY_MATPOWER_CASE_FILES, MATPOWER_CASE_FILTER, false);
		JComponent openMatlabDataFilePanel = new JPanel(new SpringLayout());
		openMatlabDataFilePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		openMatlabDataFilePanel.add(new JLabel("Matpower Case: "));
		openMatlabDataFilePanel.add(matpowerCaseInputPanel);
		SpringUtilities.makeCompactGrid(openMatlabDataFilePanel,
                1, 2, //rows, cols
                5, 5, //initialX, initialY
                5, 5);//xPad, yPad
		JPanel resizer = new JPanel(new BorderLayout());
		resizer.add(openMatlabDataFilePanel, BorderLayout.NORTH);

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
		return matpowerCaseInputPanel.checkFile();
	}
	
	protected void okPressed() {
		matpowerCaseInputPanel.setNewLocation(null);
		selectedInputFile = matpowerCaseInputPanel.getFilePath();
		matpowerCaseInputPanel.saveEntriesToProperties();
	}
	
	public String getSelectedInputFile() {
		return selectedInputFile;
	}
	
	public void setWorkingDirectory(final String workingDirectory) {
		workingDirLabel.setText(workingDirectory);				
	}
}