package net.ee.pfanalyzer.ui.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileFilter;

import layout.SpringUtilities;
import net.ee.pfanalyzer.PowerFlowAnalyzer;
import net.ee.pfanalyzer.preferences.IPreferenceConstants;
import net.ee.pfanalyzer.preferences.Preferences;
import net.ee.pfanalyzer.ui.util.FileSelectionPanel;

public class NewCaseDialog extends BaseDialog implements IPreferenceConstants {
	
	public final static int CASE_FILE_INPUT_SOURCE = 0;
	
	public final static int MATPOWER_CASE_INPUT_SOURCE = 1;
	
	public final static int MATLAB_SCRIPT_INPUT_SOURCE = 2;
	
	public static FileFilter PARAMETER_FILE_FILTER = new FileFilter() {
		@Override
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith(".xml");
		}

		@Override
		public String getDescription() {
			return "Parameter Files (*.xml)";
		}
	};
	
	private String parameterFile;
	private int selectedParameterSource;
	private FileSelectionPanel parameterFileInputPanel;
	private JRadioButton matpowerParametersButton, customParametersButton;
	
	public NewCaseDialog(PowerFlowAnalyzer frame) {
		super(frame, "Create new case");
		setText("Select a parameter database and press OK.");
		
		parameterFileInputPanel = new FileSelectionPanel(PROPERTY_PARAMETERS_FILES, PARAMETER_FILE_FILTER, false);
		matpowerParametersButton = new JRadioButton("Matpower Compatible");
		customParametersButton = new JRadioButton("Custom Parameters");
		JPanel resizer = new JPanel(new FlowLayout(FlowLayout.LEFT));
		resizer.add(matpowerParametersButton);
		resizer.add(customParametersButton);
		JComponent openMatpowerCasePanel = new JPanel(new SpringLayout());
		openMatpowerCasePanel.add(new JLabel("Parameters: "));
		openMatpowerCasePanel.add(resizer);
		final JLabel parameterFileLabel = new JLabel("Parameter File: ");
		openMatpowerCasePanel.add(parameterFileLabel);
		openMatpowerCasePanel.add(parameterFileInputPanel);
		ButtonGroup bg = new ButtonGroup();
		bg.add(matpowerParametersButton);
		bg.add(customParametersButton);
		ActionListener radioListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parameterFileInputPanel.setEnabled(customParametersButton.isSelected());
				parameterFileLabel.setEnabled(customParametersButton.isSelected());
			}
		};
		matpowerParametersButton.addActionListener(radioListener);
		customParametersButton.addActionListener(radioListener);
		String parameterSelection = Preferences.getProperty(PROPERTY_PARAMETER_MODE_SELECTION, true);
		if(parameterSelection == null || "matpower".equals(parameterSelection))
			matpowerParametersButton.doClick();
		else if("custom".equals(parameterSelection))
			customParametersButton.doClick();
		
		SpringUtilities.makeCompactGrid(openMatpowerCasePanel,
                2, 2, //rows, cols
                5, 5, //initialX, initialY
                5, 5);//xPad, yPad
		
		addOKButton();
		addCancelButton();
		
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(openMatpowerCasePanel, BorderLayout.CENTER);
		
		setCenterComponent(contentPane);
	}
	
	public void showDialog(int width, int height) {
		super.showDialog(width, height);
	}
	
	protected boolean checkInput() {
		if(customParametersButton.isSelected())
			return parameterFileInputPanel.checkFile();
		return true;
	}
	
	protected void okPressed() {
		if(matpowerParametersButton.isSelected()) {
			selectedParameterSource = 0;
		} else if(customParametersButton.isSelected()) {
			selectedParameterSource = 1;
			parameterFileInputPanel.setNewLocation(null);
			parameterFile = parameterFileInputPanel.getFilePath();
			parameterFileInputPanel.saveEntriesToProperties();
		}
		Preferences.setProperty(PROPERTY_PARAMETER_MODE_SELECTION, customParametersButton.isSelected() ? "custom" : "matpower");
	}
	
	public int getSelectedParameterSource() {
		return selectedParameterSource;
	}
	
	public String getSelectedParameterFile() {
		return parameterFile;
	}
}