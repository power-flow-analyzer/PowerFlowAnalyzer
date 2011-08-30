package net.ee.pfanalyzer.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.io.File;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import net.ee.pfanalyzer.preferences.IPreferenceConstants;
import net.ee.pfanalyzer.preferences.PreferencesInitializer;
import net.ee.pfanalyzer.ui.util.FileSelectionPanel;

public class SetupDialog extends BaseDialog implements IPreferenceConstants{

	private static FileFilter DIRECTORY_FILTER = new FileFilter() {
		@Override
		public boolean accept(File f) {
			return f.isDirectory();
		}

		@Override
		public String getDescription() {
			return "Directories";
		}
	};
	private FileSelectionPanel matpowerFolder;
	
	public SetupDialog(Frame frame) {
		super(frame, "MatpowerGUI Setup");
		
		matpowerFolder = new FileSelectionPanel(PROPERTY_MATPOWER_DIRECTORY, DIRECTORY_FILTER, false, 
				"Location: ") {
			public boolean isValid(String path) {
				return PreferencesInitializer.isMatpowerDir(path);
			}
			public boolean setNewLocation(String fullPath) {
				boolean correct = super.setNewLocation(fullPath);
				checkInput();
				return correct;
			}
			protected void customizeFileChooser(JFileChooser fileChooser) {
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			}
		};
		
		Box matpowerDirPanel = Box.createVerticalBox();
		matpowerDirPanel.setBorder(new TitledBorder("Matpower Directory"));
		matpowerDirPanel.add(matpowerFolder);
		matpowerDirPanel.add(createFullWidthLabel("<html>This information is required for calculating power flows " +
				"with Matpower directly from within this viewer."));
		JPanel resizer = new JPanel(new BorderLayout());
		resizer.add(matpowerDirPanel, BorderLayout.NORTH);

		addOKButton();
		addCancelButton();
		
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(resizer, BorderLayout.CENTER);
		
		setCenterComponent(contentPane);
		checkInput();

		showDialog(-1, -1);
	}
	
	private JComponent createFullWidthLabel(String text) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel(text), BorderLayout.CENTER);
		return panel;
	}
	
	protected boolean checkInput() {
		if(matpowerFolder.getFilePath().isEmpty()) {
			setText("<html><h2>MatpowerGUI Setup</h2>" +
			"<b><font color=\"red\">Enter a value for Matpower directory");
			return false;
		}
		boolean correct = PreferencesInitializer.isMatpowerDir(matpowerFolder.getFilePath());
		if(correct)
			setText("<html><h2>MatpowerGUI Setup</h2>" +
					"<b>Click OK to proceed");
		else
			setText("<html><h2>MatpowerGUI Setup</h2>" +
					"<b><font color=\"red\">Wrong Matpower directory");
		return correct;
	}

	protected void okPressed() {
		if(PreferencesInitializer.isMatpowerDir(matpowerFolder.getFilePath()))
			matpowerFolder.saveEntriesToProperties(true);
	}
	
	public String getMatpowerDirectory() {
		return matpowerFolder.getFilePath();
	}
}
