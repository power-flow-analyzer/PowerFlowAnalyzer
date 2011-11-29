package net.ee.pfanalyzer.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.ee.pfanalyzer.math.coordinate.ICoordinateConverter;
import net.ee.pfanalyzer.math.coordinate.Mercator;
import net.ee.pfanalyzer.model.DatabaseChangeEvent;
import net.ee.pfanalyzer.model.IDatabaseChangeListener;
import net.ee.pfanalyzer.model.IPowerFlowCaseListener;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.PowerFlowCase;
import net.ee.pfanalyzer.model.data.AbstractModelElementData;
import net.ee.pfanalyzer.model.data.ModelData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.ui.db.ModelDBDialog;
import net.ee.pfanalyzer.ui.util.ClosableTabbedPane;
import net.ee.pfanalyzer.ui.util.IActionUpdater;
import net.ee.pfanalyzer.ui.util.TabListener;
import net.ee.pfanalyzer.ui.viewer.network.Outline;

public class CaseViewer extends JPanel implements IActionUpdater, IDatabaseChangeListener, IPowerFlowCaseListener {

	private PowerFlowCase powerFlowCase;

	private NetworkTabbedPane networkTabs;
	private List<IActionUpdater> actionUpdater = new ArrayList<IActionUpdater>();
	
	private ModelDBDialog modelDBDialog;
	private NetworkOverviewPane overviewPane;
	
	private Map<String, Outline> outlinesMap = new HashMap<String, Outline>();
	private Collection<Outline> cachedOutlines;

	public CaseViewer(PowerFlowCase caze) {
		super(new BorderLayout());
		this.powerFlowCase = caze;
		
		overviewPane = new NetworkOverviewPane(this);
		networkTabs = new NetworkTabbedPane();
		modelDBDialog = new ModelDBDialog(SwingUtilities.getWindowAncestor(this), ModelDBDialog.SET_MODEL_MODE, 
				getPowerFlowCase().getModelDB(), false, "Set Model");
		
		networkTabs.setTabListener(new TabListener() {
			NetworkViewer lastViewer;
			@Override
			public boolean tabClosing(int tabIndex) {
				lastViewer = getViewer(tabIndex);
				return true;
			}
			@Override
			public void tabClosed(int tabIndex) {
				lastViewer.removeActionUpdateListener(CaseViewer.this);
				lastViewer.removeNetworkElementSelectionListener(modelDBDialog);
				lastViewer.dispose();
				lastViewer = null;
				fireActionUpdate();
			}
			@Override
			public void tabOpened(int tabIndex) {
				fireActionUpdate();
			}
		});
		add(networkTabs.getComponent(), BorderLayout.CENTER);
		overviewPane.networkSelectionChanged();
		getPowerFlowCase().getModelDB().addDatabaseChangeListener(this);
		getPowerFlowCase().addPowerFlowCaseListener(this);
		updateOutlines(null, false);
	}
	
	int getNetworkTabIndex(Network network) {
		for (int i = 0; i < networkTabs.getTabCount(); i++) {
			Component comp = networkTabs.getTabComponent(i);
			if(comp instanceof NetworkViewer) {
				Network n = ((NetworkViewer) comp).getNetwork();
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
		NetworkViewer viewer = new NetworkViewer(powerFlowCase, network);
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
	
	public NetworkViewer getCurrentViewer() {
		Component c = networkTabs.getVisibleTabComponent();
		if(c != null && c instanceof NetworkViewer)
			return (NetworkViewer) c;
		return null;
	}
	
	public NetworkViewer getViewer(Network network) {
		Component c = networkTabs.getVisibleTabComponent();
		if(c != null && c instanceof NetworkViewer) {
			NetworkViewer viewer = (NetworkViewer) c;
			if(viewer.getNetwork() == network  || viewer.getNetwork().getInternalID() == network.getInternalID())
				return viewer;
		}
		return null;
	}
	
	public int getViewerCount() {
		return networkTabs.getTabCount() - 1;
	}
	
	public NetworkViewer getViewer(int index) {
		Component c = networkTabs.getTabComponent(index);
		if(c != null && c instanceof NetworkViewer)
			return (NetworkViewer) c;
		return null;
	}
	
	public NetworkElementSelectionManager getSelectionManager() {
		NetworkViewer viewer = getCurrentViewer();
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
		updateTabTitles();
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
			if(comp instanceof NetworkViewer) {
				Network n = ((NetworkViewer) comp).getNetwork();
				String dirtySuffix = n.isDirty() ? " *" : "";
				networkTabs.setTitleAt(i, n.getDisplayName() + dirtySuffix);
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
		updateOutlinesFromDB(event);
	}
	
	@Override
	public void parameterChanged(DatabaseChangeEvent event) {
		updateScriptsFromDB(event);
		updateOutlinesFromDB(event);
	}
	
	private void updateScriptsFromDB(DatabaseChangeEvent event) {
		// update scripts from DB
		if(event.getElementData() != null && ModelDBUtils.isScriptClass(event.getElementData()))
			overviewPane.updateScriptActions();
	}
	
	private void updateOutlinesFromDB(DatabaseChangeEvent event) {
		// update outlines from DB
		if(event.getElementData() != null && ModelDBUtils.isOutlineClass(event.getElementData()))
			updateOutlines(event.getElementData(), event.getType() == DatabaseChangeEvent.REMOVED);
	}
	
	private void updateOutlines(AbstractModelElementData changedElement, boolean deleted) {
//		System.out.println("deleted: " + deleted);
//		System.out.println("changedElement: " + changedElement);
//		outlines.clear();
		String changedID = changedElement == null ? null : changedElement.getID();
//		System.out.println("changedID: " + changedID);
		if(deleted && changedID != null && getOutline(changedID) != null) {
			outlinesMap.remove(changedID);
//			System.out.println("remove outline " + changedID);
		}
		ICoordinateConverter converter = new Mercator();
		if(getPowerFlowCase().getModelDB().getOutlineClass() != null) {
			for (final ModelData outlineData : getPowerFlowCase().getModelDB().getOutlineClass().getModel()) {
				Outline oldOutline = getOutline(outlineData.getID());
				if(oldOutline != null && oldOutline.getOutlineID().equals(changedID) == false)
					continue;
	//			System.out.println("reload outline " + outlineData.getID());
				outlinesMap.remove(outlineData.getID());
				NetworkParameter fileParam = ModelDBUtils.getParameterValue(outlineData, "CSV_DATA_FILE");
				if(fileParam == null || fileParam.getValue() == null)
					continue;
				File csvDataFile = getPowerFlowCase().getAbsolutePath(fileParam.getValue());
				if(csvDataFile == null)
					continue;
				if(csvDataFile.exists() == false || csvDataFile.isDirectory())
					continue;
				Outline outline = new Outline(converter, outlineData, csvDataFile);
				outlinesMap.put(outlineData.getID(), outline);
			}
		}
		cachedOutlines = null;
		if(getCurrentViewer() != null)
			getCurrentViewer().repaint();
	}
	
	public Outline getOutline(String name) {
		return outlinesMap.get(name);
	}
	
	public Collection<Outline> getOutlines() {
		if(cachedOutlines == null)
			cachedOutlines = new ArrayList<Outline>(outlinesMap.values());
		return cachedOutlines;
	}

	@Override
	public void caseChanged(PowerFlowCase caze) {
		updateActions();
	}

	public void dispose() {
		modelDBDialog.setVisible(false);
		getPowerFlowCase().getModelDB().removeDatabaseChangeListener(this);
		getPowerFlowCase().removePowerFlowCaseListener(this);
		for (int i = 0; i < getViewerCount(); i++) {
			getViewer(i + 1).removeActionUpdateListener(CaseViewer.this);
			getViewer(i + 1).removeNetworkElementSelectionListener(modelDBDialog);
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
		
		public void addNetworkTab(String title, NetworkViewer c, boolean closable) {
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
