package net.ee.pfanalyzer.ui.model;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Frame;

import javax.swing.JPanel;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.CombinedBranch;
import net.ee.pfanalyzer.model.CombinedBus;
import net.ee.pfanalyzer.model.INetworkChangeListener;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.NetworkChangeEvent;
import net.ee.pfanalyzer.preferences.Preferences;
import net.ee.pfanalyzer.ui.INetworkElementSelectionListener;
import net.ee.pfanalyzer.ui.dialog.ModelPropertiesDialog;

public class ElementPanelController extends JPanel implements INetworkElementSelectionListener, INetworkChangeListener {

	private final static String NETWORK_CARD = "network";
	private final static String COMBINED_BUS_CARD = "combined-bus";
	private final static String COMBINED_BRANCH_CARD = "combined-branch";
	private final static String ELEMENT_CARD = "element";
	
	private Network data;
	
	private ModelPropertiesDialog panelPropertiesDialog;

	private CardLayout cardLayout;
	private NetworkPanel networkPanel;
	private CombinedBusPanel cBusPanel;
	private CombinedBranchPanel cBranchPanel;
	private ModelElementPanel elementPanel;
	private Object oldSelection;
	
	public ElementPanelController(Network data) {
		this.data = data;
		
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
		
		cardLayout.show(this, NETWORK_CARD);
	}
	
	public void showNetworkElement(Object o) {
	}
	
	public Network getData() {
		return data;
	}

	@Override
	public void selectionChanged(Object selection) {
		if(selection == null) {
			networkPanel.updateNetwork();
			cardLayout.show(this, NETWORK_CARD);
		} else if(selection instanceof CombinedBus) {
			CombinedBus cbus = (CombinedBus) selection;
			// show single child bus directly
			if(cbus.getBusNodes().size() == 1 && cbus.getGenerators().size() == 0) {
				selectionChanged(cbus.getFirstBus());
				return;
			}
			cBusPanel.setCombinedBus(cbus);
			cardLayout.show(this, COMBINED_BUS_CARD);
		} else if(selection instanceof CombinedBranch) {
			CombinedBranch cbranch = (CombinedBranch) selection;
			// show single child branch directly
			if(cbranch.getBranchCount() == 1) {
				selectionChanged(cbranch.getBranch(0));
				return;
			}
			cBranchPanel.setCombinedBranch(cbranch);
			cardLayout.show(this, COMBINED_BRANCH_CARD);
		} else if(selection instanceof AbstractNetworkElement) {
			elementPanel.setNetworkElement((AbstractNetworkElement) selection);
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

	public void showPanelPropertiesDialog(Frame frame) {
		if(panelPropertiesDialog != null) {
			panelPropertiesDialog.setVisible(true);
			panelPropertiesDialog.toFront();
		} else
			panelPropertiesDialog = new ModelPropertiesDialog(frame, this);
	}
	
	public void setViewerProperty(String property, boolean value) {
		Preferences.setProperty(property, value);
		reloadCard();
	}
	
	public void reloadCard() {
		selectionChanged(oldSelection);
	}
	
	public boolean getViewerProperty(String property, boolean defaultValue) {
		String pref = Preferences.getProperty(property, true);
		if(pref == null)
			return defaultValue;
		return Boolean.parseBoolean(pref);
	}
}
