package net.ee.pfanalyzer.ui.util;

import java.awt.Color;

import net.ee.pfanalyzer.ui.NetworkElementSelectionManager;

public class HyperLinkLabel extends HyperLinkAction {
	
	private Object networkElement;
	
	public HyperLinkLabel(String text, Object element) {
		super(text);
		this.networkElement = element;
	}
	
	public HyperLinkLabel(String text, Object element, Color foreground) {
		super(text, foreground);
		this.networkElement = element;
	}

	@Override
	protected void actionPerformed() {
		NetworkElementSelectionManager.selectionChanged(HyperLinkLabel.this, networkElement);
	}
}