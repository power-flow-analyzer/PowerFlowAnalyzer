package net.ee.pfanalyzer.ui.util;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Window;

import javax.swing.SwingUtilities;

public class SwingUtils {

	public static Frame getTopLevelFrame(Component c) {
		Window window = SwingUtilities.getWindowAncestor(c);
		if(window instanceof Frame)
			return (Frame) window;
		return null;
	}
}
