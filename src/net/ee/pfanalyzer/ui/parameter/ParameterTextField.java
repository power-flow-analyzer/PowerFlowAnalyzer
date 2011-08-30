package net.ee.pfanalyzer.ui.parameter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

import net.ee.pfanalyzer.model.data.NetworkParameter;

public class ParameterTextField extends JTextField implements ActionListener, KeyListener {
	
	private String parameterID;
	private NetworkParameter parameter;
	
	public ParameterTextField(String parameterID) {
		super();
		this.parameterID = parameterID;
	}
	
	public ParameterTextField(NetworkParameter parameter) {
		super();
		this.parameter = parameter;
		this.parameterID = parameter.getID();
		if(getParameterValue() != null)
			setText(getParameterValue());
		addActionListener(this);
		addKeyListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		setParameterValue(getText());
		updateView();
	}
	
	protected void updateView() {
		// empty implementation
	}
	
	protected void setParameterValue(String text) {
		parameter.setValue(text);
	}
	
	protected String getParameterValue() {
		return parameter.getValue();
	}
	
	public String getParameterID() {
		return parameterID;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		setParameterValue(getText());
		updateView();
	}

	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyPressed(KeyEvent e) {}
}
