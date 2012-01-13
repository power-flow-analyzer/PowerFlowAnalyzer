package net.ee.pfanalyzer.ui.util;

import java.awt.Color;

import javax.swing.JLabel;

import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.preferences.Preferences;

public class NetworkCellRenderer {

	public static JLabel setupRenderer(JLabel renderer, Network network) {
		String dirtySuffix = network.isDirty() ? " *" : "";
		String opGradeSuffix = network.wasCalculated() ? 
				" (" + (int) (network.getOperatingGrade()) + "%)" : "";
		renderer.setText(network.getDisplayName() + opGradeSuffix + dirtySuffix);
		if(network.hasFailures())
			renderer.setForeground(Preferences.getFlagFailureColor());
		else if(network.hasWarnings())
			renderer.setForeground(Preferences.getFlagWarningColor());
		else if(network.wasCalculated())
			renderer.setForeground(Preferences.getHyperlinkForeground());
		else
			renderer.setForeground(Color.BLACK);
		return renderer;
	}
}
