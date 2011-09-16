package net.ee.pfanalyzer.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.ee.pfanalyzer.PowerFlowAnalyzer;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.PowerFlowCase;
import net.ee.pfanalyzer.ui.dialog.AddScenarioDialog;
import net.ee.pfanalyzer.ui.dialog.SetScenarioParametersDialog;
import net.ee.pfanalyzer.ui.util.ClosableTabbedPane;
import net.ee.pfanalyzer.ui.util.IActionUpdater;
import net.ee.pfanalyzer.ui.util.MouseOverButton;
import net.ee.pfanalyzer.ui.util.TabListener;

public class PowerFlowContainer extends JPanel {

	private PowerFlowCase powerFlowCase;

	private ScenarioTabbedPane scenarioTabs = new ScenarioTabbedPane();
	
	private PowerFlowViewer referenceViewer;

	public PowerFlowContainer(PowerFlowCase caze) {
		this(caze, caze.getNetwork());
	}

	public PowerFlowContainer(PowerFlowCase caze, Network network) {
		super(new BorderLayout());
		this.powerFlowCase = caze;
		
		referenceViewer = new PowerFlowViewer(caze);
		
		scenarioTabs.addScenarioTab("Base Network", referenceViewer, false);
		for (int i = 0; i < network.getScenarios().size(); i++) {
			Network scenario = network.getScenarios().get(i);
			scenarioTabs.addScenarioTab(getScenarioName(scenario), new PowerFlowViewer(caze, scenario), true);
		}
		scenarioTabs.setTabListener(new TabListener() {
			Network lastNetwork;
			@Override
			public boolean tabClosing(int tabIndex) {
				String name = getScenarioName(getCurrentViewer().getNetwork());
				int choice = JOptionPane.showConfirmDialog(PowerFlowContainer.this, 
						"Do you want to close the scenario \"" + name + "\"?", "Close scenario", JOptionPane.YES_NO_OPTION);
				if(choice == JOptionPane.YES_OPTION) {
					lastNetwork = getCurrentViewer().getNetwork();
					return true;
				}
				return false;
			}
			@Override
			public void tabClosed(int tabIndex) {
				referenceViewer.getNetwork().removeScenario(lastNetwork);
				if(scenarioTabs.getSelectedIndex() == scenarioTabs.getTabCount() - 1)
					scenarioTabs.setSelectedIndex(0);
			}
			@Override
			public void tabOpened(int tabIndex) {
				if(tabIndex == scenarioTabs.getTabCount() - 1)
					scenarioTabs.setSelectedIndex(tabIndex - 1);
			}
		});
		add(scenarioTabs.getComponent(), BorderLayout.CENTER);
	}
	
	public PowerFlowCase getPowerFlowCase() {
		return powerFlowCase;
	}
	
	public PowerFlowViewer getCurrentViewer() {
		Component c = scenarioTabs.getVisibleTabComponent();
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
	
	public void addActionUpdateListener(IActionUpdater listener) {
		getCurrentViewer().addActionUpdateListener(listener);
	}
	
	private String getScenarioName(Network scenario) {
		String name = scenario.getName();
		if(name.isEmpty()) {
			int i = 1;
			name = "Scenario ";
			while(containsNetworkName(name + i))
				i++;
			
			return name + i;
		}
		return name;
	}
	
	private boolean containsNetworkName(String name) {
		for (Network network : referenceViewer.getNetwork().getScenarios()) {
			if(network.getName().equals(name))
				return true;
		}
		return false;
	}
	
	class ScenarioTabbedPane extends ClosableTabbedPane {
		
		AddScenarioButton addScenarioButton = new AddScenarioButton();
		
		ScenarioTabbedPane() {
			addTab("", new JPanel(), addScenarioButton);
		}
		
		private void addScenario() {
			try {
				AddScenarioDialog scenarioSelectionDialog = new AddScenarioDialog(
						(Frame) SwingUtilities.getWindowAncestor(PowerFlowContainer.this), getCurrentViewer().getNetwork());
				scenarioSelectionDialog.showDialog(-1, -1);
				if(scenarioSelectionDialog.isCancelPressed())
					return;
				Network scenario = null;
				if(scenarioSelectionDialog.getSelectedScenarioType() == AddScenarioDialog.SCENARIO_TYPE_CLONE_NETWORK) {
					scenario = getPowerFlowCase().createScenario(referenceViewer.getNetwork());
				} else if(scenarioSelectionDialog.getSelectedScenarioType() == AddScenarioDialog.SCENARIO_TYPE_SET_PARAMETER) {
					scenario = getPowerFlowCase().createScenario(getCurrentViewer().getNetwork());
					SetScenarioParametersDialog setParameterDialog = new SetScenarioParametersDialog(
						(Frame) SwingUtilities.getWindowAncestor(PowerFlowContainer.this), scenario);
					setParameterDialog.showDialog(-1, -1);
					if(setParameterDialog.isCancelPressed())
						return;
				}
				scenario.setName(getScenarioName(scenario));
				addScenarioTab(scenario.getName(), 
						new PowerFlowViewer(getPowerFlowCase(), scenario), true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public void addScenarioTab(String title, Component c, boolean closable) {
			addTab(title, c, getTabCount() - 1, closable);
			setSelectedIndex(getTabCount() - 2);
		}
		
		class AddScenarioButton extends MouseOverButton {
			AddScenarioButton() {
				PowerFlowAnalyzer.decorateButton(this,
						"Create a new scenario, derived from this network", "add.png", "Create scenario", false);
				setText("Scenario");
				setBorder(null);
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				addScenario();
			}
		}
	}
}
