package net.ee.pfanalyzer.ui.viewer.element;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.CombinedBranch;
import net.ee.pfanalyzer.model.CombinedBus;
import net.ee.pfanalyzer.model.DatabaseChangeEvent;
import net.ee.pfanalyzer.model.ElementList;
import net.ee.pfanalyzer.model.IDatabaseChangeListener;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.NetworkChangeEvent;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.ui.viewer.DataViewerConfiguration;
import net.ee.pfanalyzer.ui.viewer.DataViewerContainer;
import net.ee.pfanalyzer.ui.viewer.INetworkDataViewer;

public class ElementViewer extends JPanel implements INetworkDataViewer, IDatabaseChangeListener {

	public final static String VIEWER_ID = "viewer.element.viewer";
	
	private final static String PROPERTY_VIEWER_AREA_NAME = "VIEWER_AREA_NAME";
	private final static String PROPERTY_VIEWER_AREA_PARAMETER = "VIEWER_AREA_PARAMETER";
	private final static String PROPERTY_GROUP_BUS_BY_AREA = "GROUP_BUS_BY_AREA";
	private final static String PROPERTY_GROUP_BUS_BY_LOCATION = "GROUP_BUS_BY_LOCATION";
	private final static String PROPERTY_GROUP_BRANCH_BY_AREA = "GROUP_BRANCH_BY_AREA";
	private final static String PROPERTY_GROUP_BRANCH_BY_LOCATION = "GROUP_BRANCH_BY_LOCATION";
	private final static String PROPERTY_GROUP_BRANCH_BY_VOLTAGE = "GROUP_BRANCH_BY_VOLTAGE";
	private final static String PROPERTY_GROUP_ELEMENTS_BY_AREA = "GROUP_ELEMENTS_BY_AREA";
	private final static String PROPERTY_GROUP_ELEMENTS_BY_LOCATION = "GROUP_ELEMENTS_BY_LOCATION";
	private final static String PROPERTY_SHOW_NETWORK_PARAMETERS = "SHOW_NETWORK_PARAMETERS";
	private final static String PROPERTY_SHOW_SUMS_OF_VALUES = "SHOW_SUMS_OF_VALUES";
	
	private final static String NETWORK_CARD = "network";
	private final static String COMBINED_BUS_CARD = "combined-bus";
	private final static String COMBINED_BRANCH_CARD = "combined-branch";
	private final static String ELEMENT_LIST_CARD = "element-list";
	private final static String ELEMENT_CARD = "element";

	private DataViewerConfiguration viewerConfiguration;
	private Network network;
	
	private CardLayout cardLayout;
	private NetworkPanel networkPanel;
	private CombinedBusPanel cBusPanel;
	private CombinedBranchPanel cBranchPanel;
	private ModelElementPanel elementPanel;
	private ElementListPanel listPanel;
	private AbstractButton editButton;
	private boolean isEditable = false;
	private Object oldSelection;
	
	String viewerAreaLabel, viewerAreaParameter;
	boolean groupBusByArea, groupBusByLocation, 
			groupBranchByArea, groupBranchByLocation, groupBranchByVoltage,
			groupElementByArea, groupElementByLocation,
			showNetworkParameters, showSumsOfElements;

	public ElementViewer(Network data, DataViewerConfiguration viewerConfiguration, Component parent) {
		network = data;
		this.viewerConfiguration = viewerConfiguration;
		
		setBackground(Color.WHITE);
		cardLayout = new CardLayout();
		setLayout(cardLayout);
		
		networkPanel = new NetworkPanel(this, data);
		add(networkPanel, NETWORK_CARD);
		cBusPanel = new CombinedBusPanel(this, data);
		add(cBusPanel, COMBINED_BUS_CARD);
		cBranchPanel = new CombinedBranchPanel(this, data);
		add(cBranchPanel, COMBINED_BRANCH_CARD);
		elementPanel = new ModelElementPanel(this);
		elementPanel.setShowResultsWhenEditing(false);
		add(elementPanel, ELEMENT_CARD);
		listPanel = new ElementListPanel(this, data);
		add(listPanel, ELEMENT_LIST_CARD);
		
		cardLayout.show(this, NETWORK_CARD);
		
		initializeSettings();
		getViewerConfiguration().addDatabaseChangeListener(this);
	}
	
	protected void initializeSettings() {
		setSetting(PROPERTY_VIEWER_AREA_NAME);
		setSetting(PROPERTY_VIEWER_AREA_PARAMETER);
		setSetting(PROPERTY_GROUP_BUS_BY_AREA);
		setSetting(PROPERTY_GROUP_BUS_BY_LOCATION);
		setSetting(PROPERTY_GROUP_BRANCH_BY_AREA);
		setSetting(PROPERTY_GROUP_BRANCH_BY_LOCATION);
		setSetting(PROPERTY_GROUP_BRANCH_BY_VOLTAGE);
		setSetting(PROPERTY_GROUP_ELEMENTS_BY_AREA);
		setSetting(PROPERTY_GROUP_ELEMENTS_BY_LOCATION);
		setSetting(PROPERTY_SHOW_NETWORK_PARAMETERS);
		setSetting(PROPERTY_SHOW_SUMS_OF_VALUES);
		reloadCard();
	}
	
	protected void setSetting(String parameterID) {
		NetworkParameter value = getViewerConfiguration().getParameterValue(parameterID);
		if(value != null && value.getValue() != null)
			setViewerProperty(parameterID, value.getValue());
	}
	
	@Override
	public void addViewerActions(DataViewerContainer container) {
		editButton = container.addAction("Toggle editing mode", 
				"pencil.png", "Toggle editing mode", true, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isEditable = editButton.isSelected();
				reloadCard();
			}
		});
	}
	
	@Override
	public DataViewerConfiguration getViewerConfiguration() {
		return viewerConfiguration;
	}

	@Override
	public JComponent getComponent() {
		return this;
	}

	@Override
	public Network getNetwork() {
		return network;
	}

	@Override
	public void setData(Network network) {
		this.network = network;
//		filterElements();
		refresh();
	}

	@Override
	public void paintViewer(Graphics g) {
		paintComponent(g);
	}

	@Override
	public void refresh() {
		
	}
	
	public void reloadCard() {
		selectionChanged(oldSelection);
	}
	
	@Override
	public void selectionChanged(Object selection) {
		if(selection == null) {
			networkPanel.setShowNetworkParameters(showNetworkParameters);
			networkPanel.setShowSumsOfValues(showSumsOfElements);
			networkPanel.setEditable(isEditable);
			networkPanel.updateNetwork();
			setPreferredSize(networkPanel.getPreferredSize());
			cardLayout.show(this, NETWORK_CARD);
		} else if(selection instanceof CombinedBus) {
			CombinedBus cbus = (CombinedBus) selection;
			// show single child bus directly
			if(cbus.getBusNodes().size() == 1 && cbus.getGenerators().size() == 0) {
				selectionChanged(cbus.getFirstBus());
				return;
			}
			cBusPanel.setShowSumsOfValues(showSumsOfElements);
			cBusPanel.setEditable(isEditable);
			cBusPanel.setCombinedBus(cbus);
			setPreferredSize(cBusPanel.getPreferredSize());
			cardLayout.show(this, COMBINED_BUS_CARD);
		} else if(selection instanceof CombinedBranch) {
			CombinedBranch cbranch = (CombinedBranch) selection;
			// show single child branch directly
			if(cbranch.getBranchCount() == 1) {
				selectionChanged(cbranch.getBranch(0));
				return;
			}
			cBranchPanel.setShowSumsOfValues(showSumsOfElements);
			cBranchPanel.setEditable(isEditable);
			cBranchPanel.setCombinedBranch(cbranch, getNetwork().getCombinedBusses());
			setPreferredSize(cBranchPanel.getPreferredSize());
			cardLayout.show(this, COMBINED_BRANCH_CARD);
		} else if(selection instanceof ElementList) {
			ElementList list = (ElementList) selection;
			if(list.getNetworkElementCount() == 1) {
				selectionChanged(list.getFirstNetworkElement());
				return;
			}
			listPanel.setShowSumsOfValues(showSumsOfElements);
			listPanel.setEditable(isEditable);
			listPanel.setElementList((ElementList) selection);
			setPreferredSize(listPanel.getPreferredSize());
			cardLayout.show(this, ELEMENT_LIST_CARD);
		} else if(selection instanceof AbstractNetworkElement) {
			elementPanel.setEditable(isEditable);
			elementPanel.setNetworkElement((AbstractNetworkElement) selection);
			setPreferredSize(elementPanel.getPreferredSize());
			cardLayout.show(this, ELEMENT_CARD);
		}
		doLayout();
		revalidate();
		repaint();
		oldSelection = selection;
	}
	
	@Override
	public void networkChanged(NetworkChangeEvent event) {
		selectionChanged(null);
	}

	@Override
	public void networkElementAdded(NetworkChangeEvent event) {
		// do nothing
	}

	@Override
	public void networkElementChanged(NetworkChangeEvent event) {
		// the following lets the current selection jump between components
//		if(event.getNetworkElement() != null && event.getNetworkElement() == oldSelection)
//			selectionChanged(event.getNetworkElement());
	}

	@Override
	public void networkElementRemoved(NetworkChangeEvent event) {
		if(event.getNetworkElement() != null && event.getNetworkElement() == oldSelection)
			selectionChanged(null);
	}
	
	@Override
	public void dispose() {
		getViewerConfiguration().removeDatabaseChangeListener(this);
	}

	@Override
	public void elementChanged(DatabaseChangeEvent event) {
		
	}

	@Override
	public void parameterChanged(DatabaseChangeEvent event) {
		String property = event.getParameterID();
		setViewerProperty(property, event.getNewValue());
		reloadCard();
	}
	
	protected void setViewerProperty(String property, String value) {
		if(property.equals(PROPERTY_VIEWER_AREA_NAME)) {
			viewerAreaLabel = value;
		} else if(property.equals(PROPERTY_VIEWER_AREA_PARAMETER)) {
			viewerAreaParameter = value;
		} else if(property.equals(PROPERTY_GROUP_BUS_BY_AREA)) {
			groupBusByArea = Boolean.valueOf(value);
		} else if(property.equals(PROPERTY_GROUP_BUS_BY_LOCATION)) {
			groupBusByLocation = Boolean.valueOf(value);
		} else if(property.equals(PROPERTY_GROUP_BRANCH_BY_AREA)) {
			groupBranchByArea = Boolean.valueOf(value);
		} else if(property.equals(PROPERTY_GROUP_BRANCH_BY_LOCATION)) {
			groupBranchByLocation = Boolean.valueOf(value);
		} else if(property.equals(PROPERTY_GROUP_BRANCH_BY_VOLTAGE)) {
			groupBranchByVoltage = Boolean.valueOf(value);
		} else if(property.equals(PROPERTY_GROUP_ELEMENTS_BY_AREA)) {
			groupElementByArea = Boolean.valueOf(value);
		} else if(property.equals(PROPERTY_GROUP_ELEMENTS_BY_LOCATION)) {
			groupElementByLocation = Boolean.valueOf(value);
		} else if(property.equals(PROPERTY_SHOW_NETWORK_PARAMETERS)) {
			showNetworkParameters = Boolean.valueOf(value);
		} else if(property.equals(PROPERTY_SHOW_SUMS_OF_VALUES)) {
			showSumsOfElements = Boolean.valueOf(value);
		}
	}
}
