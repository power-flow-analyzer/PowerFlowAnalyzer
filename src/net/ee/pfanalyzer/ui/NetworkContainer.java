package net.ee.pfanalyzer.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.ee.pfanalyzer.model.DatabaseChangeEvent;
import net.ee.pfanalyzer.model.IDatabaseChangeListener;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.PowerFlowCase;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.ui.db.ModelDBDialog;
import net.ee.pfanalyzer.ui.util.ClosableTabbedPane;
import net.ee.pfanalyzer.ui.util.IActionUpdater;
import net.ee.pfanalyzer.ui.util.TabListener;

public class NetworkContainer extends JPanel implements IActionUpdater, IDatabaseChangeListener {

	private PowerFlowCase powerFlowCase;

	private NetworkTabbedPane networkTabs;
	private List<IActionUpdater> actionUpdater = new ArrayList<IActionUpdater>();
	
	private ModelDBDialog modelDBDialog;
	private NetworkOverviewPane overviewPane;

	public NetworkContainer(PowerFlowCase caze) {
		super(new BorderLayout());
		this.powerFlowCase = caze;
		
		overviewPane = new NetworkOverviewPane(this);
		networkTabs = new NetworkTabbedPane();
		modelDBDialog = new ModelDBDialog(SwingUtilities.getWindowAncestor(this), ModelDBDialog.SET_MODEL_MODE, 
				getPowerFlowCase().getModelDB(), false, "Set Model");
		
		networkTabs.setTabListener(new TabListener() {
			PowerFlowViewer lastViewer;
			@Override
			public boolean tabClosing(int tabIndex) {
//				String name = getScenarioName(getViewer(tabIndex).getNetwork());
//				int choice = JOptionPane.showConfirmDialog(NetworkContainer.this, 
//						"Do you want to close the network \"" + name + "\"?", "Close network", JOptionPane.YES_NO_OPTION);
//				if(choice == JOptionPane.YES_OPTION) {
					lastViewer = getViewer(tabIndex);
					return true;
//				}
//				return false;
			}
			@Override
			public void tabClosed(int tabIndex) {
				lastViewer.removeActionUpdateListener(NetworkContainer.this);
				lastViewer.removeNetworkElementSelectionListener(modelDBDialog);
				lastViewer.dispose();
//				getPowerFlowCase().removeNetwork(lastViewer.getNetwork());
//				if(networkTabs.getSelectedIndex() == networkTabs.getTabCount() - 1)
//					networkTabs.setSelectedIndex(0);
				fireActionUpdate();
			}
			@Override
			public void tabOpened(int tabIndex) {
//				if(tabIndex == networkTabs.getTabCount() - 1)
//					networkTabs.setSelectedIndex(tabIndex - 1);
				fireActionUpdate();
			}
		});
		add(networkTabs.getComponent(), BorderLayout.CENTER);
		overviewPane.networkSelectionChanged();
		getPowerFlowCase().getModelDB().addDatabaseChangeListener(this);
	}
	
	int getNetworkTabIndex(Network network) {
		for (int i = 0; i < networkTabs.getTabCount(); i++) {
			Component comp = networkTabs.getTabComponent(i);
			if(comp instanceof PowerFlowViewer) {
				Network n = ((PowerFlowViewer) comp).getNetwork();
				if(n.equals(network))
					return i;
			}
		}
		return -1;
	}
	
	void closeTab(int tabIndex) {
		networkTabs.closeTab(tabIndex);
	}
	
	public void openNetwork(Network network) {
		int tabIndex = getNetworkTabIndex(network);
		if(tabIndex != -1) {
			networkTabs.setSelectedIndex(tabIndex);
			return;
		}
//		for (int i = 0; i < networkTabs.getTabCount(); i++) {
//			Component comp = networkTabs.getTabComponent(i);
//			if(comp instanceof PowerFlowViewer) {
//				Network n = ((PowerFlowViewer) comp).getNetwork();
//				if(n.equals(network)) {
//					networkTabs.setSelectedIndex(i);
//					return;
//				}
//			}
//		}
		PowerFlowViewer viewer = new PowerFlowViewer(powerFlowCase, network);
		networkTabs.addNetworkTab(network.getDisplayName(), viewer, true);
		viewer.addActionUpdateListener(this);
		viewer.addNetworkElementSelectionListener(modelDBDialog);
		overviewPane.refreshTree();
	}
	
	public void showModelDBDialog() {
		if(modelDBDialog.isVisible() == false) {
			modelDBDialog.showDialog(900, 500);
		} else {
			modelDBDialog.setVisible(true);
			modelDBDialog.toFront();
		}
	}
	
	public void reloadModelDBDialog() {
		modelDBDialog.dispose();
		modelDBDialog = new ModelDBDialog(SwingUtilities.getWindowAncestor(this), ModelDBDialog.SET_MODEL_MODE, 
				getPowerFlowCase().getModelDB(), false, "Set Model");
	}
	
	public void setWorkingDirectory(String workingDirectory) {
		overviewPane.setWorkingDirectory(workingDirectory);
	}
	
	public PowerFlowCase getPowerFlowCase() {
		return powerFlowCase;
	}
	
	public PowerFlowViewer getCurrentViewer() {
		Component c = networkTabs.getVisibleTabComponent();
		if(c != null && c instanceof PowerFlowViewer)
			return (PowerFlowViewer) c;
		return null;
	}
	
	public PowerFlowViewer getViewer(Network network) {
		Component c = networkTabs.getVisibleTabComponent();
		if(c != null && c instanceof PowerFlowViewer) {
			PowerFlowViewer viewer = (PowerFlowViewer) c;
			if(viewer.getNetwork() == network  || viewer.getNetwork().getInternalID() == network.getInternalID())
				return viewer;
		}
		return null;
	}
	
	public int getViewerCount() {
		return networkTabs.getTabCount() - 1;
	}
	
	public PowerFlowViewer getViewer(int index) {
		Component c = networkTabs.getTabComponent(index);
		if(c != null && c instanceof PowerFlowViewer)
			return (PowerFlowViewer) c;
		return null;
	}
	
	public NetworkElementSelectionManager getSelectionManager() {
		PowerFlowViewer viewer = getCurrentViewer();
		if(viewer == null)
			return null;
		return viewer.getSelectionManager();
	}
	
//	private String getScenarioName(Network scenario) {
//		String name = scenario.getName();
//		if(name.isEmpty()) {
//			int i = 1;
//			name = "Untitled ";
//			while(containsNetworkName(name + i))
//				i++;
//			
//			return name + i;
//		}
//		return name;
//	}
	
//	String getNetworkName(Network network) {
//		String name = network.getName();
//		if(name.isEmpty())
//			return "Untitled " + network.getInternalID();
//		return name;
//	}
	
//	private boolean containsNetworkName(String name) {
//		for (Network network : getPowerFlowCase().getNetworks()) {
//			if(network.getName().equals(name))
//				return true;
//		}
//		return false;
//	}
	
	@Override
	public void updateActions() {
		// forward request to own listeners
		fireActionUpdate();
		overviewPane.refreshTree();
		overviewPane.updateScriptActions();
	}
	
	public void repaintNetworkTree() {
		overviewPane.repaintTree();
	}
	
	void updateTabTitles() {
//		for (int i = 0; i < getViewerCount(); i++) {
//			Network n = getViewer(i).getNetwork();
//			networkTabs.setTitleAt(i, getScenarioName(n));
//		}
		for (int i = 0; i < networkTabs.getTabCount(); i++) {
			Component comp = networkTabs.getTabComponent(i);
			if(comp instanceof PowerFlowViewer) {
				Network n = ((PowerFlowViewer) comp).getNetwork();
				networkTabs.setTitleAt(i, n.getDisplayName());
			}
		}
	}

	public void addActionUpdateListener(IActionUpdater listener) {
		actionUpdater.add(listener);
	}
	
	public void removeActionUpdateListener(IActionUpdater listener) {
		actionUpdater.remove(listener);
	}
	
	private void fireActionUpdate() {
		for (IActionUpdater listener : actionUpdater) {
			listener.updateActions();
		}
	}
	
	@Override
	public void elementChanged(DatabaseChangeEvent event) {
		updateScriptsFromDB(event);
	}
	
	@Override
	public void parameterChanged(DatabaseChangeEvent event) {
		updateScriptsFromDB(event);
	}
	
	private void updateScriptsFromDB(DatabaseChangeEvent event) {
		// update scripts from DB
		if(event.getElementData() != null && ModelDBUtils.isScriptClass(event.getElementData()))
			overviewPane.updateScriptActions();
	}

	public void dispose() {
		modelDBDialog.setVisible(false);
		getPowerFlowCase().getModelDB().removeDatabaseChangeListener(this);
		for (int i = 0; i < getViewerCount(); i++) {
			getViewer(i + 1).dispose();
		}
	}
	
	class NetworkTabbedPane extends ClosableTabbedPane {
		
//		AddNetworkButton addScenarioButton = new AddNetworkButton();
		
		NetworkTabbedPane() {
//			addTab("", new JPanel(), addScenarioButton);
			addTab("Overview", overviewPane, false);
		}
		
//		private void addNetwork() {
//			Network newNetwork = null;
//			try {
//				if(getCurrentViewer() != null) {
//					AddScenarioDialog scenarioSelectionDialog = new AddScenarioDialog(
//							(Frame) SwingUtilities.getWindowAncestor(NetworkContainer.this), 
//							getCurrentViewer().getNetwork());
//					scenarioSelectionDialog.showDialog(-1, -1);
//					if(scenarioSelectionDialog.isCancelPressed())
//						return;
//					newNetwork = getPowerFlowCase().createNetworkCopy(getCurrentViewer().getNetwork());
//					if(scenarioSelectionDialog.getSelectedScenarioType() == AddScenarioDialog.SCENARIO_TYPE_SET_PARAMETER) {
//						SetScenarioParametersDialog setParameterDialog = new SetScenarioParametersDialog(
//							(Frame) SwingUtilities.getWindowAncestor(NetworkContainer.this), newNetwork);
//						setParameterDialog.showDialog(-1, -1);
//						if(setParameterDialog.isCancelPressed())
//							return;
//					}
//				} else {
//					newNetwork = new Network();
//				}
//				getPowerFlowCase().addNetwork(newNetwork);
//				newNetwork.setName(getScenarioName(newNetwork));
//				PowerFlowViewer viewer = new PowerFlowViewer(getPowerFlowCase(), newNetwork);
//				addNetworkTab(newNetwork.getName(), viewer, true);
//				viewer.addActionUpdateListener(NetworkContainer.this);
//				viewer.addNetworkElementSelectionListener(modelDBDialog);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
		
		public void addNetworkTab(String title, PowerFlowViewer c, boolean closable) {
			addTab(title, c, closable);
			setSelectedIndex(getTabCount() - 1);
		}
		
//		class AddNetworkButton extends MouseOverButton {
//			AddNetworkButton() {
//				PowerFlowAnalyzer.decorateButton(this,
//						"Add a new network", "add.png", "Add network", false);
//				setText("Network");
//				setBorder(null);
//			}
//			
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				addNetwork();
//			}
//		}
	}
}
