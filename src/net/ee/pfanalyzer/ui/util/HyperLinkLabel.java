package net.ee.pfanalyzer.ui.util;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

import net.ee.pfanalyzer.ui.NetworkElementSelectionManager;

public class HyperLinkLabel extends JLabel {
	
	private final static Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
	private final static Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);

	private Object networkElement;
	
	public HyperLinkLabel(final String text, Object element) {
		this(text, element, Color.BLUE);
	}
	
	public HyperLinkLabel(final String text, Object element, Color foreground) {
		super("<html>" + text);
		this.networkElement = element;
		setForeground(foreground);
		MouseInputListener mouseListener = new MouseInputAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				setText("<html><u>" + text);
				setCursor(HAND_CURSOR);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				setText("<html>" + text);
				setCursor(DEFAULT_CURSOR);
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				NetworkElementSelectionManager.getInstance().selectionChanged(networkElement);
			}
		};
		addMouseListener(mouseListener);
	}
}