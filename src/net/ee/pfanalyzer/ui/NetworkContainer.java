package net.ee.pfanalyzer.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.ee.pfanalyzer.PowerFlowAnalyzer;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.PowerFlowCase;
import net.ee.pfanalyzer.ui.db.ModelDBDialog;
import net.ee.pfanalyzer.ui.dialog.AddScenarioDialog;
import net.ee.pfanalyzer.ui.dialog.SetScenarioParametersDialog;
import net.ee.pfanalyzer.ui.util.ClosableTabbedPane;
import net.ee.pfanalyzer.ui.util.IActionUpdater;
import net.ee.pfanalyzer.ui.util.MouseOverButton;
import net.ee.pfanalyzer.ui.util.TabListener;

public class NetworkContainer extends JPanel implements IActionUpdater {

	private PowerFlowCase powerFlowCase;

	private NetworkTabbedPane networkTabs = new NetworkTabbedPane();
	private List<IActionUpdater> actionUpdater = new ArrayList<IActionUpdater>();
	
	private ModelDBDialog modelDBDialog;

	public NetworkContainer(PowerFlowCase caze) {
		super(new BorderLayout());
		this.powerFlowCase = caze;
		
		modelDBDialog = new ModelDBDialog((Frame) SwingUtilities.getWindowAncestor(this), 
				getPowerFlowCase().getModelDB().getData());
		
		for (int i = 0; i < caze.getNetworks().size(); i++) {
			Network scenario = caze.getNetworks().get(i);
			PowerFlowViewer viewer = new PowerFlowViewer(caze, scenario);
			networkTabs.addNetworkTab(getScenarioName(scenario), viewer, true);
			viewer.addActionUpdateListener(this);
			viewer.addNetworkElementSelectionListener(modelDBDialog);
		}
		networkTabs.setTabListener(new TabListener() {
			PowerFlowViewer lastViewer;
			@Override
			public boolean tabClosing(int tabIndex) {
				String name = getScenarioName(getCurrentViewer().getNetwork());
				int choice = JOptionPane.showConfirmDialog(NetworkContainer.this, 
						"Do you want to close the network \"" + name + "\"?", "Close network", JOptionPane.YES_NO_OPTION);
				if(choice == JOptionPane.YES_OPTION) {
					lastViewer = getCurrentViewer();
					return true;
				}
				return false;
			}
			@Override
			public void tabClosed(int tabIndex) {
				lastViewer.removeActionUpdateListener(NetworkContainer.this);
				getPowerFlowCase().removeNetwork(lastViewer.getNetwork());
				if(networkTabs.getSelectedIndex() == networkTabs.getTabCount() - 1)
					networkTabs.setSelectedIndex(0);
				fireActionUpdate();
			}
			@Override
			public void tabOpened(int tabIndex) {
				if(tabIndex == networkTabs.getTabCount() - 1)
					networkTabs.setSelectedIndex(tabIndex - 1);
				fireActionUpdate();
			}
		});
		add(networkTabs.getComponent(), BorderLayout.CENTER);
	}
	
	public void showModelDBDialog() {
		if(modelDBDialog.isVisible() == false) {
			modelDBDialog.showDialog(900, 500);
		} else {
			modelDBDialog.setVisible(true);
			modelDBDialog.toFront();
		}
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
	
	private String getScenarioName(Network scenario) {
		String name = scenario.getName();
		if(name.isEmpty()) {
			int i = 1;
			name = "Network ";
			while(containsNetworkName(name + i))
				i++;
			
			return name + i;
		}
		return name;
	}
	
	private boolean containsNetworkName(String name) {
		for (Network network : getPowerFlowCase().getNetworks()) {
			if(network.getName().equals(name))
				return true;
		}
		return false;
	}
	
	@Override
	public void updateActions() {
		// forward request to own listeners
		fireActionUpdate();
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
	
	public void dispose() {
		modelDBDialog.setVisible(false);
	}
	
	class NetworkTabbedPane extends ClosableTabbedPane {
		
		AddNetworkButton addScenarioButton = new AddNetworkButton();
		
		NetworkTabbedPane() {
			addTab("", new JPanel(), addScenarioButton);
		}
		
		private void addNetwork() {
			Network newNetwork = null;
			try {
				if(getCurrentViewer() != null) {
					AddScenarioDialog scenarioSelectionDialog = new AddScenarioDialog(
							(Frame) SwingUtilities.getWindowAncestor(NetworkContainer.this), 
							getCurrentViewer().getNetwork());
					scenarioSelectionDialog.showDialog(-1, -1);
					if(scenarioSelectionDialog.isCancelPressed())
						return;
					newNetwork = getPowerFlowCase().createNetworkCopy(getCurrentViewer().getNetwork());
					if(scenarioSelectionDialog.getSelectedScenarioType() == AddScenarioDialog.SCENARIO_TYPE_SET_PARAMETER) {
						SetScenarioParametersDialog setParameterDialog = new SetScenarioParametersDialog(
							(Frame) SwingUtilities.getWindowAncestor(NetworkContainer.this), newNetwork);
						setParameterDialog.showDialog(-1, -1);
						if(setParameterDialog.isCancelPressed())
							return;
					}
				} else {
					newNetwork = new Network();
				}
				getPowerFlowCase().addNetwork(newNetwork);
				newNetwork.setName(getScenarioName(newNetwork));
				PowerFlowViewer viewer = new PowerFlowViewer(getPowerFlowCase(), newNetwork);
				addNetworkTab(newNetwork.getName(), viewer, true);
				viewer.addActionUpdateListener(NetworkContainer.this);
				viewer.addNetworkElementSelectionListener(modelDBDialog);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public void addNetworkTab(String title, PowerFlowViewer c, boolean closable) {
			addTab(title, c, getTabCount() - 1, closable);
			setSelectedIndex(getTabCount() - 2);
		}
		
		class AddNetworkButton extends MouseOverButton {
			AddNetworkButton() {
				PowerFlowAnalyzer.decorateButton(this,
						"Add a new network", "add.png", "Add network", false);
				setText("Network");
				setBorder(null);
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				addNetwork();
			}
		}
	}
}
