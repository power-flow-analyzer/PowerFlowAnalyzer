package net.ee.pfanalyzer.ui.util;

public interface TabListener {

	boolean tabClosing(int tabIndex);
	
	void tabClosed(int tabIndex);
}
