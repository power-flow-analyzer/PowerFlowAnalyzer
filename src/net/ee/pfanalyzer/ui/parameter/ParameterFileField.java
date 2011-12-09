package net.ee.pfanalyzer.ui.parameter;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.ee.pfanalyzer.PowerFlowAnalyzer;
import net.ee.pfanalyzer.model.data.NetworkParameter;

public class ParameterFileField extends ParameterValuePanel implements ActionListener, KeyListener {
	
	private JTextField textfield;
	private JButton browseButton;
	private JPanel valuePanel;
	private ParameterFileFilter fileFilter;
	private String workingDirectory;
	
	public ParameterFileField(IParameterMasterElement element, NetworkParameter property, 
			NetworkParameter propertyValue, ParameterFileFilter fileFilter) {
		super(element, property, propertyValue);
		this.fileFilter = fileFilter;
		textfield.addActionListener(this);
		textfield.addKeyListener(this);
	}
	
	protected void createValuePanel() {
		textfield = new JTextField(25);
		browseButton = new JButton("Browse...");
		browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFile();
			}
		});
		valuePanel = new JPanel(new BorderLayout()) {
			public void setEnabled(boolean enabled) {
				super.setEnabled(enabled);
				textfield.setEnabled(enabled);
				browseButton.setEnabled(enabled);
			}
			
			public void setToolTipText(String text) {
				super.setToolTipText(text);
				textfield.setToolTipText(text);
			}
		};
		valuePanel.add(textfield, BorderLayout.CENTER);
		valuePanel.add(browseButton, BorderLayout.EAST);
	}
	
	protected JTextField getTextField() {
		return textfield;
	}
	
	protected JComponent getValuePanel() {
		return valuePanel;
	}
	
	protected void setValue(String value) {
		textfield.setText(value);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		setNewValue();
	}
	
	protected void setNewValue() {
		if(ignoreAction())
			return;
		NetworkParameter property = getMasterElement().getParameter(getPropertyID(), true);
		String value = textfield.getText();
		String oldValue = property.getValue();
		property.setValue(value);
		fireValueChanged(oldValue, value);
		refresh();
	}
	
	private void openFile() {
		File selectedFile = new File(getTextField().getText());
		if(selectedFile.isAbsolute() && selectedFile.exists()) {
			if(selectedFile.isDirectory())
				workingDirectory = selectedFile.getAbsolutePath();
			else
				workingDirectory = selectedFile.getParentFile().getAbsolutePath();
		}
		if(workingDirectory == null)
			workingDirectory = PowerFlowAnalyzer.getInstance().getWorkingDirectory();
		JFileChooser fileChooser = new JFileChooser(workingDirectory);
		fileChooser.setAcceptAllFileFilterUsed(true);
		fileChooser.setSelectedFile(selectedFile);
		fileChooser.setFileFilter(fileFilter);
//		customizeFileChooser(fileChooser);
		int action = fileChooser.showOpenDialog(this);
		if(action == JFileChooser.APPROVE_OPTION) {
			String newPath = fileChooser.getSelectedFile().getAbsolutePath();
			// try to set relative path
			if(newPath.startsWith(workingDirectory) && newPath.length() > workingDirectory.length() + 2)
				newPath = newPath.substring(workingDirectory.length() + 1);
			if(fileFilter != null)
				newPath = fileFilter.getNormalizedFileName(newPath);
			getTextField().setText(newPath);
			setNewValue();
		}
	}
	
	protected void updateView() {
		// empty implementation
	}

	@Override
	public void keyReleased(KeyEvent e) {
		setNewValue();
	}

	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyPressed(KeyEvent e) {}
}
