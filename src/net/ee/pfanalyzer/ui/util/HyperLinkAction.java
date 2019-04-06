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
import java.awt.Cursor;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

public abstract class HyperLinkAction extends JLabel {
	
	private final static Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
	private final static Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
	
	private Color foreground;

	public HyperLinkAction(final String text) {
		this(text, Color.BLUE);
	}
	
	public HyperLinkAction(final String text, Color foreground) {
		super("<html>" + text);
		this.foreground = foreground;
		setForeground(foreground);
		MouseInputListener mouseListener = new MouseInputAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if(isEnabled()) {
					setText("<html><u>" + text);
					setCursor(HAND_CURSOR);
				}
			}
			@Override
			public void mouseExited(MouseEvent e) {
				setText("<html>" + text);
				setCursor(DEFAULT_CURSOR);
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				if(isEnabled()) {
					actionPerformed();
				}
			}
		};
		addMouseListener(mouseListener);
	}
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		setForeground(isEnabled() ? foreground : Color.GRAY);
	}

	protected abstract void actionPerformed();
}
