package net.ee.pfanalyzer.ui.util;

import java.text.DecimalFormatSymbols;

import javax.swing.JComponent;
import javax.swing.JScrollPane;


public class SwingUtils {

	public final static String DEFAULT_DECIMAL_SEPARATOR = new DecimalFormatSymbols().getDecimalSeparator()+ "";
	public final static char DEFAULT_DECIMAL_SEPARATOR_CHAR = DEFAULT_DECIMAL_SEPARATOR.charAt(0);
	
//	public static Window getTopLevelFrame(Component c) {
//		return SwingUtilities.getWindowAncestor(c);
//	}
	
	public static JScrollPane createScrollpane(JComponent comp) {
		JScrollPane scroller = new JScrollPane(comp);
		scroller.getHorizontalScrollBar().setUnitIncrement(40);
		scroller.getVerticalScrollBar().setUnitIncrement(40);
		return scroller;
	}
}
