/*******************************************************************************
 * Copyright 2019 Markus Gronau
 * 
 * This file is part of PowerFlowAnalyzer.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
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
