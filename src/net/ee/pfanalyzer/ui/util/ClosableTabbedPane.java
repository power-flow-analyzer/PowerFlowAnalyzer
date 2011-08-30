package net.ee.pfanalyzer.ui.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

public class ClosableTabbedPane {

	private final static int buttonSize = 16;
	private final static int crossMargin = 5;
	private final static Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
	private final static Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
	
	private TabListener tabListener;
	private JTabbedPane tabbedPane;
	
	public ClosableTabbedPane() {
		tabbedPane = new JTabbedPane();
	}
	
	public void addTab(String title, Component component) {
		getTabbedPane().addTab(title, component);
		getTabbedPane().setTabComponentAt(getTabbedPane().getTabCount() - 1, new TabRenderer());
	}
	
	private JTabbedPane getTabbedPane() {
		return tabbedPane;
	}
	
	public Component getComponent() {
		return getTabbedPane();
	}
	
	public boolean hasTabs() {
		return ! isEmpty();
	}
	
	public boolean isEmpty() {
		return getTabbedPane().getSelectedIndex() == -1;
	}
	
	public int getTabCount() {
		return getTabbedPane().getTabCount();
	}
	
	public void setTitleAt(int index, String title) {
		getTabbedPane().setTitleAt(index, title);
	}
	
	public int getSelectedIndex() {
		return getTabbedPane().getSelectedIndex();
	}
	
	public void setSelectedIndex(int index) {
		if(index < getTabCount())
			getTabbedPane().setSelectedIndex(index);
	}
	
	public void selectLastTab() {
		getTabbedPane().setSelectedIndex(getTabbedPane().getTabCount() - 1);
	}
	
	public TabListener getTabListener() {
		return tabListener;
	}

	public void setTabListener(TabListener tabListener) {
		this.tabListener = tabListener;
	}
	
	private void closeTab(int index) {
		if(getTabListener() != null) {
			if(getTabListener().tabClosing(index)) {
				getTabbedPane().remove(index);
				getTabListener().tabClosed(index);
			}
		} else
			getTabbedPane().remove(index);
	}

	class TabRenderer extends JPanel {
		TabRenderer() {
			super(new FlowLayout(FlowLayout.LEFT, 0, 0));
			setOpaque(false);
			JLabel label = new JLabel() {
				public String getText() {
					int index = getTabbedPane().indexOfTabComponent(TabRenderer.this);
					return index > -1 ? getTabbedPane().getTitleAt(index) : null;
				}
			};
			label.setBorder(new EmptyBorder(0, 0, 0, 5));
			add(label);
			CloseButton closeButton = new CloseButton();
			add(closeButton);
		}
		
		private void tabClosed() {
			int index = getTabbedPane().indexOfTabComponent(TabRenderer.this);
			if(index > -1)
				closeTab(index);
		}
		
		class CloseButton extends JButton implements MouseListener {
			boolean mouseOver = false;
			boolean mousePressed = false;
			CloseButton() {
				setBorderPainted(false);
				setContentAreaFilled(false);
				setPreferredSize(new Dimension(buttonSize, buttonSize));
				setFocusable(false);
				addMouseListener(this);
				setToolTipText("Close this tab");
			}
			
			protected void paintComponent(Graphics g) {
	            if(mouseOver || mousePressed) {
	            	if(mousePressed)
	            		g.setColor(Color.DARK_GRAY);
	            	else if(mouseOver)
	            		g.setColor(Color.LIGHT_GRAY);
		            g.fillRect(1, 1, getWidth(), getHeight());
	            } else
	            	super.paintComponent(g);
	            if(mousePressed)
	            	g.setColor(Color.WHITE);
	            else
	            	g.setColor(Color.BLACK);
	            g.drawLine(crossMargin, crossMargin, 
	            		getWidth() - crossMargin, getHeight() - crossMargin);
	            g.drawLine(getWidth() - crossMargin, crossMargin, 
	            		crossMargin, getHeight() - crossMargin);
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
			public void mouseClicked(MouseEvent e) {
				tabClosed();
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
	}
}
