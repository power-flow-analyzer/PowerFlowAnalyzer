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