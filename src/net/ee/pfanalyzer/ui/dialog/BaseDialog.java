package net.ee.pfanalyzer.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

public class BaseDialog extends JDialog {

	private Frame parentFrame;
	private JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
	private JPanel titlePane = new JPanel(new FlowLayout(FlowLayout.LEFT));
	protected boolean okPressed = false;
	
	protected BaseDialog(Frame frame, String title) {
		this(frame, title, true);
	}
	
	protected BaseDialog(Frame frame, String title, boolean modal) {
		super(frame, title, modal);
		parentFrame = frame;
		
		buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		titlePane.setBackground(Color.WHITE);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(titlePane, BorderLayout.NORTH);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
	}
	
	public void showDialog(int width, int height) {
		pack();
		if(width > -1 && height > -1)
			setSize(width, height);
		centerOnParent();
		setVisible(true);
	}
	
	protected void setCenterComponent(Component c) {
		setCenterComponent(c, true);
	}
	
	protected void setCenterComponent(Component c, boolean scrollable) {
		if(scrollable)
			getContentPane().add(new JScrollPane(c), BorderLayout.CENTER);
		else
			getContentPane().add(c, BorderLayout.CENTER);
	}
	
	protected void setText(String text) {
		JLabel title = new JLabel(text);
		title.setFont(title.getFont().deriveFont(Font.BOLD));
		title.setBorder(new EmptyBorder(10, 10, 10, 10));
		titlePane.removeAll();
		titlePane.add(title);
		titlePane.revalidate();
		titlePane.repaint();
	}
	
	protected void addOKButton() {
		addButton("OK", true, true);
	}
	
	protected void addCancelButton() {
		addButton("Cancel", false, false);
	}
	
	protected void addButton(String label, final boolean isOKButton, boolean isDefaultButton) {
		JButton button = new JButton(label);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				okPressed = isOKButton;
				boolean success = true;
				if(isOKButton) {
					success = checkInput();
					if(success)
						okPressed();
				}
				if(success)
					setVisible(false);
			}
		});
		if(isDefaultButton) {
			button.setDefaultCapable(true);
			getRootPane().setDefaultButton(button);
		}
		addBottomComponent(button);
	}
	
	protected void addBottomComponent(Component c) {
		buttonPanel.add(c);
	}
	
	protected void okPressed() {
		// empty implementation
	}
	
	protected boolean checkInput() {
		return true;
	}
	
	public boolean isOkPressed() {
		return okPressed;
	}
	
	public boolean isCancelPressed() {
		return ! isOkPressed();
	}
	
	protected void centerOnParent() {
		if(parentFrame == null || parentFrame.isShowing() == false)
			return;
		Point parentLocation = parentFrame.getLocationOnScreen();
		int x = (parentFrame.getWidth() - getWidth()) / 2 + parentLocation.x;
		int y = (parentFrame.getHeight() - getHeight()) / 2 + parentLocation.y;
		setLocation(x, y);
	}
}
