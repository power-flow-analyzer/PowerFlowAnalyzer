package net.ee.pfanalyzer.ui.dialog;

import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import net.ee.pfanalyzer.model.matpower.BusDescriptor;
import net.ee.pfanalyzer.model.matpower.IBusDataConstants;
import net.ee.pfanalyzer.preferences.IPreferenceConstants;
import net.ee.pfanalyzer.ui.model.ElementPanelController;

public class ModelPropertiesDialog extends AbstractPropertyDialog {
	
	private ElementPanelController controller;
	
	public ModelPropertiesDialog(Frame frame, ElementPanelController controller) {
		super(frame, "Map Properties", false);
		this.controller = controller;
		setText("The following options apply directly to all views.");
		
		Container contentPane = Box.createVerticalBox();
		JComponent optionsPane = new JPanel(new GridLayout(0, 1));
//		optionsPane.add(Box.createHorizontalGlue());
		optionsPane.setBorder(new TitledBorder("Show Bus Data"));
		contentPane.add(optionsPane);
		addBusOption(IBusDataConstants.BUS_NUMBER, IBusDataConstants.PROPERTY_BUS_NUMBER, optionsPane);
		addBusOption(IBusDataConstants.BUS_TYPE, IBusDataConstants.PROPERTY_BUS_TYPE, optionsPane);
		addBusOption(IBusDataConstants.REAL_POWER_DEMAND, IBusDataConstants.PROPERTY_REAL_POWER_DEMAND, optionsPane);
		addBusOption(IBusDataConstants.REACTIVE_POWER_DEMAND, IBusDataConstants.PROPERTY_REACTIVE_POWER_DEMAND, optionsPane);
		addBusOption(IBusDataConstants.SHUNT_CONDUCTANCE, IBusDataConstants.PROPERTY_SHUNT_CONDUCTANCE, optionsPane);
		addBusOption(IBusDataConstants.SHUNT_SUSCEPTANCE, IBusDataConstants.PROPERTY_SHUNT_SUSCEPTANCE, optionsPane);
		addBusOption(IBusDataConstants.AREA_NUMBER, IBusDataConstants.PROPERTY_AREA_NUMBER, optionsPane);
		addBusOption(IBusDataConstants.VOLTAGE_MAGNITUDE, IBusDataConstants.PROPERTY_VOLTAGE_MAGNITUDE, optionsPane);
		addBusOption(IBusDataConstants.VOLTAGE_ANGLE, IBusDataConstants.PROPERTY_VOLTAGE_ANGLE, optionsPane);
		addBusOption(IBusDataConstants.BASE_VOLTAGE, IBusDataConstants.PROPERTY_BASE_VOLTAGE, optionsPane);
		addBusOption(IBusDataConstants.LOSS_ZONE, IBusDataConstants.PROPERTY_LOSS_ZONE, optionsPane);
		addBusOption(IBusDataConstants.AREA_NUMBER, IBusDataConstants.PROPERTY_AREA_NUMBER, optionsPane);
		addBusOption(IBusDataConstants.AREA_NUMBER, IBusDataConstants.PROPERTY_AREA_NUMBER, optionsPane);
		addBusOption(IBusDataConstants.AREA_NUMBER, IBusDataConstants.PROPERTY_AREA_NUMBER, optionsPane);
		addBusOption(IBusDataConstants.AREA_NUMBER, IBusDataConstants.PROPERTY_AREA_NUMBER, optionsPane);
		addBusOption(IBusDataConstants.AREA_NUMBER, IBusDataConstants.PROPERTY_AREA_NUMBER, optionsPane);
//		addBooleanProperty(optionsPane, "Keep Aspect Ratio", 
//				NetworkViewerController.PROPERTY_RESPECT_ASPECT_RATIO, true);

//		optionsPane = new JPanel(new GridLayout(0, 1));
////		optionsPane.add(Box.createHorizontalGlue());
//		optionsPane.setBorder(new TitledBorder("Draw"));
//		contentPane.add(optionsPane);
//		addBooleanProperty(optionsPane, "Draw Bus Nodes", 
//				NetworkViewerController.PROPERTY_DRAW_BUSSES, true);
//		addBooleanProperty(optionsPane, "Draw Branches", 
//				NetworkViewerController.PROPERTY_DRAW_BRANCHES, true);
//		addBooleanProperty(optionsPane, "Draw Generators", 
//				NetworkViewerController.PROPERTY_DRAW_GENERATORS, true);
//		addBooleanProperty(optionsPane, "Draw Outline", 
//				NetworkViewerController.PROPERTY_DRAW_OUTLINE, true);
//
//		optionsPane = new JPanel(new GridLayout(0, 1));
////		optionsPane.add(Box.createHorizontalGlue());
//		optionsPane.setBorder(new TitledBorder("Interaction"));
//		contentPane.add(optionsPane);
//		addBooleanProperty(optionsPane, "Allow zooming", 
//				NetworkViewerController.PROPERTY_INTERACTION_ZOOM, true);
//		addBooleanProperty(optionsPane, "Allow dragging", 
//				NetworkViewerController.PROPERTY_INTERACTION_MOVE, true);
//		addBooleanProperty(optionsPane, "Show Tooltips", 
//				NetworkViewerController.PROPERTY_SHOW_TOOLTIPS, true);
//
//		contentPane.add(Box.createVerticalGlue());
//		contentPane.add(Box.createVerticalGlue());
//		contentPane.add(Box.createVerticalGlue());
//		contentPane.add(Box.createVerticalGlue());
//		contentPane.add(Box.createVerticalGlue());
		addButton("Close", true, true);
		
		setCenterComponent(contentPane);
		showDialog(400, 500);
	}
	
	private void addBusOption(int dataField, String propertySuffix, JComponent optionsPane) {
		addBooleanProperty(optionsPane, BusDescriptor.getDescription(dataField), 
				IPreferenceConstants.PROPERTY_UI_SHOW_MODEL_PREFIX + propertySuffix, 
				getProperty(propertySuffix, true));
	}
	
	protected void applyProperty(String property, boolean value) {
		controller.setViewerProperty(property, value);
	}
	
	protected boolean getProperty(String propertySuffix, boolean defaultValue) {
		return controller.getViewerProperty(IPreferenceConstants.PROPERTY_UI_SHOW_MODEL_PREFIX + propertySuffix, defaultValue);
	}
}
