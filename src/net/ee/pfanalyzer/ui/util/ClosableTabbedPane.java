package net.ee.pfanalyzer.ui.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

public class ClosableTabbedPane {

	private final static int buttonSize = 16;
	private final static int crossMargin = 5;
//	private final static Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
//	private final static Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
	
	private TabListener tabListener;
	private JTabbedPane tabbedPane;
	
	public ClosableTabbedPane() {
		tabbedPane = new JTabbedPane() {
			public void setSelectedIndex(int index) {
				super.setSelectedIndex(index);
				if(getTabListener() != null)
					getTabListener().tabOpened(index);
			}
		};
	}
	
	public void addTab(String title, Component component) {
		addTab(title, component, true);
	}
	
	public void addTab(String title, Component component, boolean closable) {
		addTab(title, component, getTabbedPane().getTabCount(), closable);
	}
	
	public void addTab(String title, Component component, int index, boolean closable) {
		getTabbedPane().insertTab(title, null, component, null, index);
		getTabbedPane().setTabComponentAt(index, new TabRenderer(closable));
	}
	
	public void addTab(String title, Component component, Component tabRenderer) {
		getTabbedPane().addTab(title, component);
		getTabbedPane().setTabComponentAt(getTabbedPane().getTabCount() - 1, tabRenderer);
	}
	
	protected JTabbedPane getTabbedPane() {
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
		getTabbedPane().getTabComponentAt(index).doLayout();
	}
	
	public int getSelectedIndex() {
		return getTabbedPane().getSelectedIndex();
	}
	
	public Component getVisibleTabComponent() {
		if(getSelectedIndex() == -1)
			return null;
		return getTabbedPane().getSelectedComponent();
	}
	
	public Component getTabComponent(int index) {
		return getTabbedPane().getComponentAt(index);
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
	
	public void closeCurrentTab() {
		if(getSelectedIndex() > -1)
			closeTab(getSelectedIndex());
	}
	
	public void closeTab(int index) {
		if(getTabListener() != null) {
			if(getTabListener().tabClosing(index)) {
				getTabbedPane().remove(index);
				getTabListener().tabClosed(index);
			}
		} else
			getTabbedPane().remove(index);
	}

	class TabRenderer extends JPanel {
		TabRenderer(boolean closable) {
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
			if(closable) {
				CloseButton closeButton = new CloseButton();
				add(closeButton);
			}
		}
		
		private void tabClosed() {
			int index = getTabbedPane().indexOfTabComponent(TabRenderer.this);
			if(index > -1)
				closeTab(index);
		}
		
		class CloseButton extends MouseOverButton {
			CloseButton() {
				setPreferredSize(new Dimension(buttonSize, buttonSize));
				setToolTipText("Close this tab");
			}
			
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
	            g.drawLine(crossMargin, crossMargin, 
	            		getWidth() - crossMargin, getHeight() - crossMargin);
	            g.drawLine(getWidth() - crossMargin, crossMargin, 
	            		crossMargin, getHeight() - crossMargin);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				tabClosed();
			}
		}
	}
}
