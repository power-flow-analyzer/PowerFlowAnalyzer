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

public class ImportFromScriptDialog extends BaseDialog implements IPreferenceConstants {
	
	private static FileFilter MATLAB_SCRIPT_FILTER = new FileFilter() {
		@Override
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith(".m");
		}

		@Override
		public String getDescription() {
			return "Matlab Scripts (*.m)";
		}
	};
	
	private String selectedInputFile;
	private String workingDirectory;
	private JLabel workingDirLabel;
	private FileSelectionPanel matlabScriptInputPanel;
	
	public ImportFromScriptDialog(Frame frame, String workingDir) {
		super(frame, "Execute Matlab script");
		setText("Select a Matlab script and press OK.");
		File workingDirFile = new File(workingDir);
		if(workingDirFile.isFile())
			workingDirFile = workingDirFile.getParentFile();
		workingDirectory = workingDirFile.getAbsolutePath();
		matlabScriptInputPanel = new FileSelectionPanel(PROPERTY_MATLAB_SCRIPT_FILES, MATLAB_SCRIPT_FILTER, true);
		JComponent openMatpowerCasePanel = new JPanel(new SpringLayout());
		openMatpowerCasePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		openMatpowerCasePanel.add(new JLabel("Matlab Script: "));
		openMatpowerCasePanel.add(matlabScriptInputPanel);
		openMatpowerCasePanel.add(new JLabel("Current folder: "));
		workingDirLabel = new JLabel(workingDirectory);
		openMatpowerCasePanel.add(workingDirLabel);
		SpringUtilities.makeCompactGrid(openMatpowerCasePanel,
                2, 2, //rows, cols
                5, 5, //initialX, initialY
                5, 5);//xPad, yPad
		JPanel resizer = new JPanel(new BorderLayout());
		resizer.add(openMatpowerCasePanel, BorderLayout.NORTH);
		
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
		return matlabScriptInputPanel.checkFile();
	}
	
	protected void okPressed() {
		if(matlabScriptInputPanel.setNewLocation(null) == false)
			okPressed = false; // cancel case dialog afterwards
		selectedInputFile = matlabScriptInputPanel.getFilePath();
		matlabScriptInputPanel.saveEntriesToProperties();
	}
	
	public String getSelectedInputFile() {
		return selectedInputFile;
	}
	
	public void setWorkingDirectory(final String workingDirectory) {
		workingDirLabel.setText(workingDirectory);				
	}
}