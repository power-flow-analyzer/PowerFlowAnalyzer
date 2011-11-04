package net.ee.pfanalyzer.ui.dialog;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import net.ee.pfanalyzer.ui.map.NetworkViewerController;

public class MapPropertiesDialog extends AbstractPropertyDialog {
	
	private NetworkViewerController controller;
	private JComboBox zoomBox;
	
	public MapPropertiesDialog(Frame frame, NetworkViewerController controller) {
		super(frame, "Map Properties", false);
		this.controller = controller;
		setText("The following options apply directly to the map.");
		
		Container contentPane = Box.createVerticalBox();
		JComponent optionsPane = new JPanel(new GridLayout(0, 1));
//		optionsPane.add(Box.createHorizontalGlue());
		optionsPane.setBorder(new TitledBorder("View"));
		contentPane.add(optionsPane);
		zoomBox = new JComboBox(new String[] { "Best Fit", "Custom", "Germany" });
		zoomBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selection = zoomBox.getSelectedIndex();
				applyProperty(NetworkViewerController.PROPERTY_ZOOM_CHOICE, selection);
			}
		});
		JPanel zoomBoxParent = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		zoomBoxParent.add(new JLabel("Zoom:"));
		zoomBoxParent.add(zoomBox);
		optionsPane.add(zoomBoxParent);
		addBooleanProperty(optionsPane, "Show Legend", 
				NetworkViewerController.PROPERTY_DRAW_LEGEND, true);
		addBooleanProperty(optionsPane, "Keep Aspect Ratio", 
				NetworkViewerController.PROPERTY_RESPECT_ASPECT_RATIO, true);

		optionsPane = new JPanel(new GridLayout(0, 1));
//		optionsPane.add(Box.createHorizontalGlue());
		optionsPane.setBorder(new TitledBorder("Draw"));
		contentPane.add(optionsPane);
		addBooleanProperty(optionsPane, "Draw Bus Nodes", 
				NetworkViewerController.PROPERTY_DRAW_BUSSES, true);
		addBooleanProperty(optionsPane, "Draw Branches", 
				NetworkViewerController.PROPERTY_DRAW_BRANCHES, true);
		addBooleanProperty(optionsPane, "Draw Power Flow Direction", 
				NetworkViewerController.PROPERTY_DRAW_POWER_DIRECTION, true);
		addBooleanProperty(optionsPane, "Draw Generators", 
				NetworkViewerController.PROPERTY_DRAW_GENERATORS, true);
		addBooleanProperty(optionsPane, "Draw Outline", 
				NetworkViewerController.PROPERTY_DRAW_OUTLINE, true);

		optionsPane = new JPanel(new GridLayout(0, 1));
//		optionsPane.add(Box.createHorizontalGlue());
		optionsPane.setBorder(new TitledBorder("Interaction"));
		contentPane.add(optionsPane);
		addBooleanProperty(optionsPane, "Allow zooming", 
				NetworkViewerController.PROPERTY_INTERACTION_ZOOM, true);
		addBooleanProperty(optionsPane, "Allow dragging", 
				NetworkViewerController.PROPERTY_INTERACTION_MOVE, true);
		addBooleanProperty(optionsPane, "Show Tooltips", 
				NetworkViewerController.PROPERTY_SHOW_TOOLTIPS, true);

		contentPane.add(Box.createVerticalGlue());
		contentPane.add(Box.createVerticalGlue());
		contentPane.add(Box.createVerticalGlue());
		contentPane.add(Box.createVerticalGlue());
		contentPane.add(Box.createVerticalGlue());
		addButton("Close", true, true);
		
		setCenterComponent(contentPane);
		showDialog(400, 500);
	}
	
	public void setZoomChoice(int zoomID) {
		zoomBox.setSelectedIndex(zoomID);
	}
	
	protected void applyProperty(String property, boolean value) {
//		controller.setViewerProperty(property, value);
	}
	
	private void applyProperty(String property, int value) {
//		controller.setViewerProperty(property, value);
	}
}
