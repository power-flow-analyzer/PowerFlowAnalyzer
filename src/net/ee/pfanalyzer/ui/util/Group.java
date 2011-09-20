package net.ee.pfanalyzer.ui.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.CombinedNetworkElement;
import net.ee.pfanalyzer.preferences.Preferences;

public class Group extends JPanel {
	
	Font groupFont = new Font(null, Font.BOLD, 12);
	TitledBorder border;
	
	public Group(String title) {
		super(new GridLayout(0, 2));
		border = new TitledBorder(title);
		border.setTitleColor(Color.BLACK);
		border.setTitleFont(groupFont);
		setBorder(border);
	}
	
	public void addElementLink(CombinedNetworkElement<?> element) {
		Color foreground;
		if(element.hasFailures())
			foreground = Preferences.getFlagFailureColor();
		else if(element.hasWarnings())
			foreground = Preferences.getFlagWarningColor();
		else
			foreground = Preferences.getHyperlinkForeground();
		add(new HyperLinkLabel(element.getLabel(), element, foreground));
	}
	
	public void addElementLink(AbstractNetworkElement childData, int displayFlags) {
		add(createElementLink(childData, displayFlags));
	}
	
	public HyperLinkLabel createElementLink(AbstractNetworkElement childData, int displayFlags) {
		Color foreground = Preferences.getHyperlinkForeground();
		if(childData.hasFailures())
			foreground = Preferences.getFlagFailureColor();
		else if(childData.hasWarnings())
			foreground = Preferences.getFlagWarningColor();
		else if(childData.isActive() == false)
			foreground = Preferences.getDisabledForeground();
		return new HyperLinkLabel(childData.getDisplayName(displayFlags), childData, foreground);
	}
	
	public void removeFlags() {
		removeAll();
		setLayout(new GridLayout(0, 2));
		revalidate();
	}
//	
//	protected void addElementLink(ChildData childData, Color foreground) {
//		add(new HyperLinkLabel(childData.getDisplayName(), childData, foreground));
//	}
}