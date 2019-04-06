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
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

public abstract class MouseOverButton extends JButton implements MouseListener {
	
	private final static Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
	private final static Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
	
	boolean mouseOver = false;
	boolean mousePressed = false;
	public MouseOverButton() {
		setBorderPainted(false);
		setContentAreaFilled(false);
		setFocusable(false);
		addMouseListener(this);
	}
	
	protected void paintComponent(Graphics g) {
        if(mouseOver || mousePressed) {
        	if(mousePressed)
        		g.setColor(Color.DARK_GRAY);
        	else if(mouseOver)
        		g.setColor(Color.LIGHT_GRAY);
            g.fillRect(1, 1, getWidth(), getHeight());
        }
//        else
//        	super.paintComponent(g);
        if(mousePressed) {
        	g.setColor(Color.WHITE);
        	setForeground(Color.WHITE);
        } else {
        	g.setColor(Color.BLACK);
        	setForeground(Color.BLACK);
        }
        super.paintComponent(g);
        if(mouseOver)
        	g.draw3DRect(1, 1, getWidth()-2, getHeight()-2, true);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		mouseOver = true;
		setCursor(HAND_CURSOR);
		repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		mouseOver = false;
		setCursor(DEFAULT_CURSOR);
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mousePressed = true;
		repaint();
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		mousePressed = false;
		repaint();
	}
}
