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
