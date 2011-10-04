package net.ee.pfanalyzer.ui.util;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

public abstract class AbstractTextEditor extends JPanel implements ActionListener, KeyListener {

//	private JTextComponent component;
	
	protected AbstractTextEditor() {
		super(new BorderLayout());
//		this.component = component;
		initField();
	}
	
	private void initField() {
		if(getTextValue() != null)
			setText(getTextValue());
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		setTextValue(getText());
		updateView();
	}
	
	public void updateView() {
		setText(getTextValue());
	}
	
	protected abstract String getText();
	
	protected abstract void setText(String text);
	
	protected abstract void setTextValue(String text);
	
	protected abstract String getTextValue();
	
	@Override
	public void keyReleased(KeyEvent e) {
		setTextValue(getText());
		updateView();
	}

	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyPressed(KeyEvent e) {}
	
//	public abstract class TextFieldEditor extends AbstractTextEditor {
//
//		public TextFieldEditor() {
//			super(new JTextField());
//		}
//		
//	}
}
