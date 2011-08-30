package net.ee.pfanalyzer.ui.dialog;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import layout.SpringUtilities;
import net.ee.pfanalyzer.PowerFlowAnalyzer;
import net.ee.pfanalyzer.preferences.IPreferenceConstants;
import net.ee.pfanalyzer.ui.util.FileSelectionPanel;

public class CaseSelectionDialog extends BaseDialog implements IPreferenceConstants {
	
	public final static int CASE_FILE_INPUT_SOURCE = 0;
	
	public final static int MATPOWER_CASE_INPUT_SOURCE = 1;
	
	public final static int MATLAB_SCRIPT_INPUT_SOURCE = 2;
	
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
	private int selectedInputSource;
	private String workingDirectory;
	private JLabel workingDirLabel;
	private JTabbedPane tabbedPanel = new JTabbedPane();
	private FileSelectionPanel caseFileInputPanel, matlabScriptInputPanel, matpowerCaseInputPanel;
//	private JComboBox powerFlowBox = new JComboBox(new String[] {"Standard", "Optimal", "Optimal with unit-decommitment heuristic"});
//	private int powerFlowAlgorithm;
	
	public CaseSelectionDialog(PowerFlowAnalyzer frame, String workingDir) {
		super(frame, "Open case");
		setText("Select a case file and press OK.");
		File workingDirFile = new File(workingDir);
		if(workingDirFile.isFile())
			workingDirFile = workingDirFile.getParentFile();
		workingDirectory = workingDirFile.getAbsolutePath();

//		String powerFlowAlgo = Preferences.getProperty(PROPERTY_POWER_FLOW_ALGORITHM, true);
//		if(powerFlowAlgo != null) {
//			powerFlowBox.setSelectedIndex(Integer.parseInt(powerFlowAlgo.trim()));
//		}
		
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
		tabbedPanel.addTab("Open Case File", resizer);
		
		if(frame.getEnvironment() == PowerFlowAnalyzer.MATLAB_ENVIRONMENT) {
			matpowerCaseInputPanel = new FileSelectionPanel(PROPERTY_MATPOWER_CASE_FILES, MATPOWER_CASE_FILTER, false);
			JComponent openMatlabDataFilePanel = new JPanel(new SpringLayout());
			openMatlabDataFilePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
			openMatlabDataFilePanel.add(new JLabel("Matlab Data File: "));
			openMatlabDataFilePanel.add(matpowerCaseInputPanel);
			SpringUtilities.makeCompactGrid(openMatlabDataFilePanel,
	                1, 2, //rows, cols
	                5, 5, //initialX, initialY
	                5, 5);//xPad, yPad
			resizer = new JPanel(new BorderLayout());
			resizer.add(openMatlabDataFilePanel, BorderLayout.NORTH);
			tabbedPanel.addTab("Import Matpower Case", resizer);

			matlabScriptInputPanel = new FileSelectionPanel(PROPERTY_MATLAB_SCRIPT_FILES, MATLAB_SCRIPT_FILTER, true);
			JComponent openMatpowerCasePanel = new JPanel(new SpringLayout());
			openMatpowerCasePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
			openMatpowerCasePanel.add(new JLabel("Matlab Script: "));
			openMatpowerCasePanel.add(matlabScriptInputPanel);
			openMatpowerCasePanel.add(new JLabel("Current folder: "));
			workingDirLabel = new JLabel(workingDirectory);
			openMatpowerCasePanel.add(workingDirLabel);
//			resizer = new JPanel(new BorderLayout());
//			openMatpowerCasePanel.add(new JLabel("Power Flow Algorithm: "));
//			openMatpowerCasePanel.add(powerFlowBox);
//			openMatpowerCasePanel.add(resizer);
			SpringUtilities.makeCompactGrid(openMatpowerCasePanel,
	                2, 2, //rows, cols
	                5, 5, //initialX, initialY
	                5, 5);//xPad, yPad
			resizer = new JPanel(new BorderLayout());
			resizer.add(openMatpowerCasePanel, BorderLayout.NORTH);
			tabbedPanel.addTab("Execute Matlab Script", resizer);
		}
		
		addOKButton();
		addCancelButton();
		
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(tabbedPanel, BorderLayout.CENTER);
		
		setCenterComponent(contentPane);
	}
	
	public void showDialog(int width, int height) {
		super.showDialog(width, height);
	}
	
	protected boolean checkInput() {
		if(tabbedPanel.getSelectedIndex() == CASE_FILE_INPUT_SOURCE) { // proprietary case file
			return caseFileInputPanel.checkFile();
		} else if(tabbedPanel.getSelectedIndex() == MATPOWER_CASE_INPUT_SOURCE) { // Matpower Case
			return matpowerCaseInputPanel.checkFile();
		} else if(tabbedPanel.getSelectedIndex() == MATLAB_SCRIPT_INPUT_SOURCE) { // Matlab script
			return matlabScriptInputPanel.checkFile();
		}
		return false;
	}
	
	protected void okPressed() {
		selectedInputSource = tabbedPanel.getSelectedIndex();
		if(selectedInputSource == CASE_FILE_INPUT_SOURCE) { // proprietary case file
			caseFileInputPanel.setNewLocation(null);
			selectedInputFile = caseFileInputPanel.getFilePath();
			caseFileInputPanel.saveEntriesToProperties();
		} else if(selectedInputSource == MATPOWER_CASE_INPUT_SOURCE) { // Matpower Case
			matpowerCaseInputPanel.setNewLocation(null);
			selectedInputFile = matpowerCaseInputPanel.getFilePath();
			matpowerCaseInputPanel.saveEntriesToProperties();
		} else if(selectedInputSource == MATLAB_SCRIPT_INPUT_SOURCE) { // Matlab script
			if(matlabScriptInputPanel.setNewLocation(null) == false)
				okPressed = false; // cancel case dialog afterwards
			selectedInputFile = matlabScriptInputPanel.getFilePath();
			matlabScriptInputPanel.saveEntriesToProperties();
//			powerFlowAlgorithm = powerFlowBox.getSelectedIndex();
//			Preferences.setProperty(PROPERTY_POWER_FLOW_ALGORITHM, powerFlowAlgorithm);
		}
	}
	
	public int getSelectedInputSource() {
		return selectedInputSource;
	}
	
	public String getSelectedInputFile() {
		return selectedInputFile;
	}
	
//	public int getSelectedPowerFlowAlgorithm() {
//		return powerFlowAlgorithm;
//	}
	
	public void setWorkingDirectory(final String workingDirectory) {
		workingDirLabel.setText(workingDirectory);				
	}
	
//	class FileSelectionPanel
}