package net.ee.pfanalyzer.ui.model;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Frame;

import javax.swing.JPanel;

import net.ee.pfanalyzer.model.Branch;
import net.ee.pfanalyzer.model.Bus;
import net.ee.pfanalyzer.model.CombinedBranch;
import net.ee.pfanalyzer.model.CombinedBus;
import net.ee.pfanalyzer.model.Generator;
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
	private final static String SINGLE_BUS_CARD = "single-bus";
	private final static String SINGLE_BRANCH_CARD = "single-branch";
	private final static String GENERATOR_CARD = "generator";
	
	private Network data;
	
	private ModelPropertiesDialog panelPropertiesDialog;

	private CardLayout cardLayout;
	private NetworkPanel networkPanel;
	private CombinedBusPanel cBusPanel;
	private CombinedBranchPanel cBranchPanel;
	private BusPanel busPanel;
	private BranchPanel branchPanel;
	private GeneratorPanel generatorPanel;
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
		busPanel = new BusPanel(this);
		add(busPanel, SINGLE_BUS_CARD);
		branchPanel = new BranchPanel(this);
		add(branchPanel, SINGLE_BRANCH_CARD);
		generatorPanel = new GeneratorPanel(this);
		add(generatorPanel, GENERATOR_CARD);
		
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
			cBusPanel.setCombinedBus((CombinedBus) selection);
			cardLayout.show(this, COMBINED_BUS_CARD);
		} else if(selection instanceof CombinedBranch) {
			cBranchPanel.setCombinedBranch((CombinedBranch) selection);
			cardLayout.show(this, COMBINED_BRANCH_CARD);
		} else if(selection instanceof Bus) {
			busPanel.setBus((Bus) selection);
			cardLayout.show(this, SINGLE_BUS_CARD);
		} else if(selection instanceof Branch) {
			branchPanel.setBranch((Branch) selection);
			cardLayout.show(this, SINGLE_BRANCH_CARD);
		} else if(selection instanceof Generator) {
			generatorPanel.setGenerator((Generator) selection);
			cardLayout.show(this, GENERATOR_CARD);
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
		selectionChanged(oldSelection);
	}
	
	public boolean getViewerProperty(String property, boolean defaultValue) {
		String pref = Preferences.getProperty(property, true);
		if(pref == null)
			return defaultValue;
		return Boolean.parseBoolean(pref);
	}
}
