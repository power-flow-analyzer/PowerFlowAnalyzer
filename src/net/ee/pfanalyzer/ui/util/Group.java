package net.ee.pfanalyzer.ui.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.CombinedNetworkElement;
import net.ee.pfanalyzer.preferences.Preferences;
import net.miginfocom.swing.MigLayout;

public class Group extends JPanel {
	
	Font groupFont = new Font(null, Font.BOLD, 12);
	TitledBorder border;
	
	public Group(String title) {
		super(new MigLayout("wrap 2", "[]20[grow]"));
		border = new TitledBorder(title);
		border.setTitleColor(Color.BLACK);
		border.setTitleFont(groupFont);
		setBorder(border);
	}
	
	public Component addLabel(Component c) {
		add(c);
		return c;
	}
	
	public Component addValue(Component c) {
		add(c, "width 300:300:, grow");
		return c;
	}
	
	public void addElementLink(CombinedNetworkElement<?> element, int displayFlags) {
		Color foreground;
		if(element.hasFailures())
			foreground = Preferences.getFlagFailureColor();
		else if(element.hasWarnings())
			foreground = Preferences.getFlagWarningColor();
		else if(element.isActive() == false)
			foreground = Preferences.getDisabledForeground();
		else
			foreground = Preferences.getHyperlinkForeground();
		String label = element.getDisplayName(displayFlags);
		add(new HyperLinkLabel(label, element, foreground));
	}
	
	public JComponent addElementLink(AbstractNetworkElement childData, int displayFlags) {
		return (JComponent) add(createElementLink(childData, displayFlags));
	}
	
	public static HyperLinkLabel createElementLink(AbstractNetworkElement childData, int displayFlags) {
		return createElementLink(childData, displayFlags, null);
	}
	
	public static HyperLinkLabel createElementLink(AbstractNetworkElement childData, int displayFlags, 
			IObjectAction action) {
		Color foreground = Preferences.getHyperlinkForeground();
		if(childData.hasFailures())
			foreground = Preferences.getFlagFailureColor();
		else if(childData.hasWarnings())
			foreground = Preferences.getFlagWarningColor();
		else if(childData.isActive() == false)
			foreground = Preferences.getDisabledForeground();
		return new HyperLinkLabel(childData.getDisplayName(displayFlags), childData, foreground, action);
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