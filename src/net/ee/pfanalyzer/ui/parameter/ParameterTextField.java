package net.ee.pfanalyzer.ui.parameter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;
import javax.swing.JTextField;

import net.ee.pfanalyzer.model.data.NetworkParameter;

public class ParameterTextField extends ParameterValuePanel implements ActionListener, KeyListener {
	
	private JTextField textfield;
	
	public ParameterTextField(IParameterMasterElement element, NetworkParameter property, NetworkParameter propertyValue) {
		super(element, property, propertyValue);
		textfield.addActionListener(this);
		textfield.addKeyListener(this);
	}
	
	protected void createValuePanel() {
		textfield = new JTextField();
	}
	
	protected JTextField getTextField() {
		return textfield;
	}
	
	protected JComponent getValuePanel() {
		return textfield;
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
