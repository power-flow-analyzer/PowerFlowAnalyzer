package net.ee.pfanalyzer.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.ee.pfanalyzer.PowerFlowAnalyzer;
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
import net.ee.pfanalyzer.ui.timer.DisplayTimer;
import net.ee.pfanalyzer.ui.util.ClosableTabbedPane;
import net.ee.pfanalyzer.ui.util.IActionUpdater;
import net.ee.pfanalyzer.ui.util.NetworkCellRenderer;
import net.ee.pfanalyzer.ui.util.TabListener;
import net.ee.pfanalyzer.ui.viewer.network.Outline;

public class CaseViewer extends JPanel implements IActionUpdater, IDatabaseChangeListener, IPowerFlowCaseListener {

	private PowerFlowCase powerFlowCase;

	private NetworkTabbedPane networkTabs;
	private List<IActionUpdater> actionUpdater = new ArrayList<IActionUpdater>();
	
	private ModelDBDialog modelDBDialog;
	private NetworkOverviewPane overviewPane;
	private NetworkSwitcher networkSwitcher;
	private DisplayTimerControls timerControls;
	
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
		networkSwitcher = new NetworkSwitcher();
		timerControls = new DisplayTimerControls();
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(networkSwitcher);
		topPanel.add(timerControls);
		add(topPanel, BorderLayout.NORTH);
		add(networkTabs.getComponent(), BorderLayout.CENTER);
		overviewPane.networkSelectionChanged();
		getPowerFlowCase().getModelDB().addDatabaseChangeListener(this);
		getPowerFlowCase().addPowerFlowCaseListener(this);
		addActionUpdateListener(networkSwitcher);
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
	
	private void setNetwork(Network network) {
		NetworkViewer viewer = getCurrentViewer();
		if(viewer == null) {
			openNetwork(network);
		} else {
			viewer.setNetwork(network);
			updateTabTitles();
		}
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
			modelDBDialog.showDialog(900, 600);
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
		removeActionUpdateListener(networkSwitcher);
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
	
	class NetworkSwitcher extends JPanel implements IActionUpdater {
		
		private JComboBox switcherBox;
		private boolean selfSelection = false;
		private JButton previousButton, nextButton;
		
		NetworkSwitcher() {
			super(new FlowLayout(FlowLayout.LEFT));
			switcherBox = new JComboBox();
			switcherBox.setRenderer(new NetworkListCellRenderer());
			switcherBox.addActionListener(new SwitcherListener());
			previousButton = PowerFlowAnalyzer.createButton("Show previous network in list", 
					"arrow_left.png", "Previous", false);
			previousButton.setMargin(new Insets(2, 2, 2, 2));
			previousButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					showPreviousNetwork();
				}
			});
			nextButton = PowerFlowAnalyzer.createButton("Show next network in list", 
					"arrow_right.png", "Next", false);
			nextButton.setMargin(new Insets(2, 2, 2, 2));
			nextButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					showNextNetwork();
				}
			});
			add(new JLabel("Network: "));
			add(switcherBox);
			add(previousButton);
			add(nextButton);
			updateSwitcherBox();
		}
		
		private void updateListItems() {
			List<Network> networks = overviewPane.getNetworkList();
			Vector<Network> items = new Vector<Network>(networks.size() + 1);
			items.add(null);
			for (int i = 0; i < networks.size(); i++) {
				items.add(networks.get(i));
			}
			switcherBox.setModel(new DefaultComboBoxModel(items));
		}
		
		public Network getSelectedNetwork() {
			return (Network) switcherBox.getSelectedItem();
		}
		
		class NetworkListCellRenderer extends DefaultListCellRenderer {
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected, boolean cellHasFocus) {
				Network network = (Network) value;
				String text = network != null ? network.getDisplayName() : "Select a network";
				super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
				if(network != null)
					return NetworkCellRenderer.setupRenderer(this, network);
				return this;
			}
		}
		
		private void showPreviousNetwork() {
			int index = switcherBox.getSelectedIndex();
			if(index > 1)
				switcherBox.setSelectedIndex(index - 1);
		}
		
		private void showNextNetwork() {
			int index = switcherBox.getSelectedIndex();
			if(index < switcherBox.getItemCount() - 1)
				switcherBox.setSelectedIndex(index + 1);
		}

		@Override
		public void updateActions() {
			updateSwitcherBox();
		}
		
		private void updateSwitcherBox() {
			selfSelection = true;
			updateListItems();
			NetworkViewer viewer = getCurrentViewer();
			if(viewer != null)
				switcherBox.setSelectedItem(viewer.getNetwork());
			else
				switcherBox.setSelectedIndex(0);
			previousButton.setEnabled(viewer != null 
					&& switcherBox.getSelectedIndex() > 1);
			nextButton.setEnabled(viewer != null 
					&& switcherBox.getSelectedIndex() < switcherBox.getItemCount() - 1);
			selfSelection = false;
		}
		
		class SwitcherListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(selfSelection)
					return;
				Network network = getSelectedNetwork();
				if(network == null)
					networkTabs.setSelectedIndex(0);
				else
					setNetwork(network);
				fireActionUpdate();
			}
		}
	}
	
	class DisplayTimerControls extends JPanel implements IActionUpdater, ActionListener, ChangeListener {
		
		Timer animation;
		boolean isRunning = false;
		JSlider timeSlider;
		DisplayTimer timer;
		boolean isTimerAction = false;
		JButton startStopButton;
		JLabel timeLabel;
		
		public DisplayTimerControls() {
			super(new FlowLayout(FlowLayout.LEFT));
			timer = powerFlowCase.getTimer();
			JButton nextButton = PowerFlowAnalyzer.createButton("Set display time to next time step", 
					"control_fastforward_blue.png", "Next", false);
			nextButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					timer.nextStep();
					updateViewer();
				}
			});
			JButton previousButton = PowerFlowAnalyzer.createButton("Set display time to previous time step", 
					"control_rewind_blue.png", "Previous", false);
			previousButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					timer.previousStep();
					updateViewer();
				}
			});
			startStopButton = PowerFlowAnalyzer.createButton("Start animation", 
					"control_play_blue.png", "Play", false);
//			JButton playButton = PowerFlowAnalyzer.createButton("Start animation", 
//					"control_play_blue.png", "Play", false);
			startStopButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(animation.isRunning()) {
						animation.stop();
						PowerFlowAnalyzer.decorateButton(startStopButton, "Start animation", 
								"control_play_blue.png", "Play", false);
					} else {
						animation.start();
						PowerFlowAnalyzer.decorateButton(startStopButton, "Stop animation", 
								"control_stop_blue.png", "Stop", false);
					}
				}
			});
//			JButton pauseButton = PowerFlowAnalyzer.createButton("Stop animation", 
//					"control_pause_blue.png", "Pause", false);
//			pauseButton.addActionListener(new ActionListener() {
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					animation.stop();
//				}
//			});
			add(previousButton);
			add(startStopButton);
//			add(pauseButton);
			add(nextButton);
			timeSlider = new JSlider(JSlider.HORIZONTAL, 0, 8736, timer.getTimeStep());
			timeSlider.addChangeListener(this);
			timeSlider.setMajorTickSpacing(24 * 30);
//			timeSlider.setSnapToTicks(true);
			timeSlider.setPaintTicks(true);
			add(timeSlider);
			
			timeLabel = new JLabel();
			add(timeLabel);
			
			animation = new Timer(300, this);
			animation.setInitialDelay(500);
		}

		@Override
		public void updateActions() {
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			if(isTimerAction == false) {
				timer.setTimeStep(timeSlider.getValue());
				updateViewer();
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			timer.nextStep();
			isTimerAction = true;
			timeSlider.setValue(timer.getTimeStep());
			isTimerAction = false;
			updateViewer();
		}
		
		private void updateViewer() {
			String text = SimpleDateFormat.getDateTimeInstance().format(new Date(timer.getTime()));
			timeLabel.setText(text + " (time step " + timer.getTimeStep() + ")");
			if(getCurrentViewer() != null)
				getCurrentViewer().fireDisplayTimeChanged(timer);
		}
	}
}
