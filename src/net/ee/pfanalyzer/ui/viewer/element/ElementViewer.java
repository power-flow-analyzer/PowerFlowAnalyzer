package net.ee.pfanalyzer.ui.viewer.element;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JPanel;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.CombinedBranch;
import net.ee.pfanalyzer.model.CombinedBus;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.NetworkChangeEvent;
import net.ee.pfanalyzer.ui.viewer.DataViewerConfiguration;
import net.ee.pfanalyzer.ui.viewer.INetworkDataViewer;

public class ElementViewer extends JPanel implements INetworkDataViewer {

	public final static String VIEWER_ID = "viewer.element.viewer";
	
	private final static String NETWORK_CARD = "network";
	private final static String COMBINED_BUS_CARD = "combined-bus";
	private final static String COMBINED_BRANCH_CARD = "combined-branch";
	private final static String ELEMENT_CARD = "element";

	private DataViewerConfiguration viewerConfiguration;
	private Network network;
	
	private CardLayout cardLayout;
	private NetworkPanel networkPanel;
	private CombinedBusPanel cBusPanel;
	private CombinedBranchPanel cBranchPanel;
	private ModelElementPanel elementPanel;
	private Object oldSelection;

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
		
		cardLayout.show(this, NETWORK_CARD);
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
	public void refresh() {
		
	}
	
	public void reloadCard() {
		selectionChanged(oldSelection);
	}
	
	@Override
	public void selectionChanged(Object selection) {
		if(selection == null) {
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
			cBranchPanel.setCombinedBranch(cbranch, getNetwork().getCombinedBusses());
			setPreferredSize(cBranchPanel.getPreferredSize());
			cardLayout.show(this, COMBINED_BRANCH_CARD);
		} else if(selection instanceof AbstractNetworkElement) {
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
		
	}
}
