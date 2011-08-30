package net.ee.pfanalyzer.ui.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.filechooser.FileFilter;

import net.ee.pfanalyzer.PowerFlowAnalyzer;
import net.ee.pfanalyzer.preferences.Preferences;

public class FileSelectionPanel extends JPanel {
	private JComboBox fileLocationBox;
	private FileFilter fileFilter;
	private Vector<String> files = new Vector<String>();
	private String propertyKey;
	private boolean setMatlabCurrentFolder;
	private boolean internalSet = false;
	private String workingDirectory;
	
	public FileSelectionPanel(String propertyKey, FileFilter fileFilter, boolean setMatlabCurrentFolder) {
		this(propertyKey, fileFilter, setMatlabCurrentFolder, null);
	}
	
	public FileSelectionPanel(String propertyKey, FileFilter fileFilter, boolean setMatlabCurrentFolder, String label) {
		super(new BorderLayout());
		this.propertyKey = propertyKey;
		this.fileFilter = fileFilter;
		this.setMatlabCurrentFolder = setMatlabCurrentFolder;
		workingDirectory = PowerFlowAnalyzer.getInstance().getWorkingDirectory();
		addEntriesFromProperties();
		fileLocationBox = new JComboBox(new DefaultComboBoxModel(files));
		fileLocationBox.setEditable( ! setMatlabCurrentFolder);
		fileLocationBox.setRenderer(new PathRenderer());
		fileLocationBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(internalSet == false)// prevent loop
					setNewLocation(null);
				fileLocationBox.setToolTipText((String) fileLocationBox.getSelectedItem());
			}
		});
		add(fileLocationBox, BorderLayout.CENTER);
		JButton browseButton = new JButton("Browse...");
		browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFile();
			}
		});
		add(browseButton, BorderLayout.EAST);
		
		if(label != null)
			add(new JLabel(label), BorderLayout.WEST);
	}
	
	private void openFile() {
		File selectedFile = new File(getFilePath());
		if(selectedFile.isAbsolute() && selectedFile.exists()) {
			if(selectedFile.isDirectory())
				workingDirectory = selectedFile.getAbsolutePath();
			else
				workingDirectory = selectedFile.getParentFile().getAbsolutePath();
		}
		if(workingDirectory == null)
			workingDirectory = "";
		JFileChooser fileChooser = new JFileChooser(workingDirectory);
		fileChooser.setAcceptAllFileFilterUsed(true);
		fileChooser.setFileFilter(fileFilter);
		customizeFileChooser(fileChooser);
		int action = fileChooser.showOpenDialog(this);
		if(action == JFileChooser.APPROVE_OPTION)
			setNewLocation(fileChooser.getSelectedFile().getAbsolutePath());
	}
	
	protected void customizeFileChooser(JFileChooser fileChooser) {
		// empty implementation
	}
	
	private boolean acceptNewWorkingDirectory(String directory) {
		if(directory.equals(workingDirectory)) {
			return true;
		} else {
			int action = JOptionPane.showConfirmDialog(this, "The selected file is not in the MATLAB current folder. " +
					"\nDo you want to change the MATLAB current folder to:" +
					"\n" + directory, "Change current folder?", JOptionPane.YES_NO_OPTION);
			if(action == JOptionPane.YES_OPTION) {
				PowerFlowAnalyzer.getInstance().setMatlabCurrentFolder(directory);
				return true;
			} else //if(action == JOptionPane.CANCEL_OPTION)
				return false;
		}
	}
	
	public boolean setNewLocation(String fullPath) {
		if(fullPath == null)
			fullPath = (String) fileLocationBox.getSelectedItem();
		File pathFile = new File(fullPath);
		String directory = fullPath;
		if(pathFile.isFile())
			directory = pathFile.getParentFile().getAbsolutePath();
		if(setMatlabCurrentFolder && acceptNewWorkingDirectory(directory) == false)
			return false;
		String entryPath = fullPath;
		int index = getIndexOfEntry(entryPath);
		if(index > -1) {// path was found
			files.remove(index);
		}
		files.add(0, entryPath);
		internalSet = true;
		fileLocationBox.setSelectedIndex(0);
		internalSet = false;
		workingDirectory = directory;
		return true;
	}
	
	public String getFilePath() {
		if(fileLocationBox.getSelectedItem() == null)
			return "";
		return fileLocationBox.getSelectedItem().toString();
	}
	
	public boolean isValid(String path) {
		return new File(path).exists();
	}
	
	public boolean checkFile() {
		if(isValid(getFilePath()) == false) {
			JOptionPane.showMessageDialog(this, "The file " + getFilePath() + " cannot be found.", 
					"File not found", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}
	
//	private boolean checkWrite() {
//		File f = new File(getFilePath());
//		if(f.exists() == false) {
//			try {
//				f.createNewFile();
//			} catch (IOException e) {
//				System.err.println(e);
//				JOptionPane.showMessageDialog(this, "Cannot create file " + getFilePath() + "", 
//						"Write Error", JOptionPane.ERROR_MESSAGE);
//				return false;
//			}
//		}
//		if(f.canWrite() == false) {
//			JOptionPane.showMessageDialog(this, "Cannot write to file " + getFilePath() + "", 
//					"Write Error", JOptionPane.ERROR_MESSAGE);
//			return false;
//		}
//		return true;
//	}
	
	private void addEntriesFromProperties() {
		String property = Preferences.getProperty(propertyKey, true);
		if(property == null)
			return;
		String[] paths = property.split(",");
		for (String path : paths) {
			files.add(path.trim());
		}
	}
	
	public void saveEntriesToProperties() {
		saveEntriesToProperties(false);
	}
	
	public void saveEntriesToProperties(boolean omitMissingFiles) {
		StringBuilder builder = new StringBuilder();
		boolean filesAdded = false;
		for (int i = 0; i < files.size(); i++) {
			if(omitMissingFiles && isValid(files.get(i)) == false)
				continue;
			if(filesAdded)
				builder.append(", ");
			builder.append(files.get(i));
			filesAdded = true;
		}
		Preferences.setProperty(propertyKey, builder.toString());
	}
	
	private int getIndexOfEntry(String path) {
		for (int i = 0; i < files.size(); i++) {
			if(files.get(i).equals(path)) {
				return i;
			}
		}
		return -1;
	}
	
	class PathRenderer extends DefaultListCellRenderer implements ListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList list,
				Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			String text = (String) value;
			setToolTipText(text);
			if(setMatlabCurrentFolder && text != null)
				text = new File(text).getName();
			return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
		}
	}
}
