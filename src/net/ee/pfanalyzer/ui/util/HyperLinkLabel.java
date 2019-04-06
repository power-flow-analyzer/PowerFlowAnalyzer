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

import net.ee.pfanalyzer.ui.NetworkElementSelectionManager;

public class HyperLinkLabel extends HyperLinkAction {
	
	private Object networkElement;
	private IObjectAction action;
	
	public HyperLinkLabel(String text, Object element) {
		this(text, element, (IObjectAction) null);
	}
	
	public HyperLinkLabel(String text, Object element, IObjectAction action) {
		super(text);
		this.networkElement = element;
		setToolTipText("Click to open element");
	}
	
	public HyperLinkLabel(String text, Object element, Color foreground) {
		this(text, element, foreground, null);
	}
	
	public HyperLinkLabel(String text, Object element, Color foreground, IObjectAction action) {
		super(text, foreground);
		this.networkElement = element;
		this.action = action;
		setToolTipText("Click to open element");
	}

	@Override
	protected void actionPerformed() {
		if(action != null)
			action.actionPerformed(networkElement, null);
		else // backward compatible
			NetworkElementSelectionManager.selectionChanged(HyperLinkLabel.this, networkElement);
	}
}
