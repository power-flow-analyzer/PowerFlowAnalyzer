package net.ee.pfanalyzer.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.ee.pfanalyzer.PowerFlowAnalyzer;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.PowerFlowCase;
import net.ee.pfanalyzer.model.data.ModelData;
import net.ee.pfanalyzer.ui.db.ModelDBDialog;
import net.ee.pfanalyzer.ui.dialog.ImportFromScriptDialog;
import net.ee.pfanalyzer.ui.dialog.ImportMatpowerDialog;
import net.ee.pfanalyzer.ui.util.AbstractTextEditor;
import net.ee.pfanalyzer.ui.util.ClosableTabbedPane;
import net.ee.pfanalyzer.ui.util.HyperLinkAction;
import net.ee.pfanalyzer.ui.util.IActionUpdater;
import net.ee.pfanalyzer.ui.util.TabListener;

public class NetworkContainer extends JPanel implements IActionUpdater {

	private PowerFlowCase powerFlowCase;

	private NetworkTabbedPane networkTabs;
	private List<IActionUpdater> actionUpdater = new ArrayList<IActionUpdater>();
	
	private ModelDBDialog modelDBDialog;
	private NetworkOverviewPane overviewPane;

	public NetworkContainer(PowerFlowCase caze) {
		super(new BorderLayout());
		this.powerFlowCase = caze;
		
		overviewPane = new NetworkOverviewPane();
		networkTabs = new NetworkTabbedPane();
		modelDBDialog = new ModelDBDialog((Frame) SwingUtilities.getWindowAncestor(this), 
				getPowerFlowCase().getModelDB().getData());
		
//		for (int i = 0; i < caze.getNetworks().size(); i++) {
//			Network scenario = caze.getNetworks().get(i);
//			addNetwork(scenario);
//		}
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
		overviewPane.updateScriptActions();
	}
	
	private int getNetworkTabIndex(Network network) {
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
		networkTabs.addNetworkTab(getScenarioName(network), viewer, true);
		viewer.addActionUpdateListener(this);
		viewer.addNetworkElementSelectionListener(modelDBDialog);
		overviewPane.refreshList();
	}
	
	public void showModelDBDialog() {
		if(modelDBDialog.isVisible() == false) {
			modelDBDialog.showDialog(900, 500);
		} else {
			modelDBDialog.setVisible(true);
			modelDBDialog.toFront();
		}
	}
	
	public void setWorkingDirectory(String workingDirectory) {
		if(overviewPane.importFromScriptDialg != null)
			overviewPane.importFromScriptDialg.setWorkingDirectory(workingDirectory);
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
	
	private String getScenarioName(Network scenario) {
		String name = scenario.getName();
		if(name.isEmpty()) {
			int i = 1;
			name = "Untitled ";
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
		overviewPane.refreshList();
		overviewPane.updateScriptActions();
	}
	
	private void updateTabTitles() {
//		for (int i = 0; i < getViewerCount(); i++) {
//			Network n = getViewer(i).getNetwork();
//			networkTabs.setTitleAt(i, getScenarioName(n));
//		}
		for (int i = 0; i < networkTabs.getTabCount(); i++) {
			Component comp = networkTabs.getTabComponent(i);
			if(comp instanceof PowerFlowViewer) {
				Network n = ((PowerFlowViewer) comp).getNetwork();
				networkTabs.setTitleAt(i, getScenarioName(n));
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
	
	public void dispose() {
		modelDBDialog.setVisible(false);
	}
	
	class NetworkOverviewPane extends JPanel {
		
		private JList networkList;
		private NetworkListModel listModel;
		private List<Network> selectedNetworks = new ArrayList<Network>();
		private NetworkName networkLabel = new NetworkName();
		private NetworkDescription networkDescription = new NetworkDescription();
		private HyperLinkAction createEmptyNetworkAction, importMatpowerNetworkAction, importFromScriptAction,
				duplicateNetworkAction, deleteNetworkAction;
		private JPanel scriptActionPane;
		private ImportFromScriptDialog importFromScriptDialg;
		
		NetworkOverviewPane() {
			super(new BorderLayout());
			
			listModel = new NetworkListModel();
			networkList = new JList(listModel);
			networkList.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if(e.getClickCount() == 2)
						openNetwork(powerFlowCase.getNetworks().get(networkList.getSelectedIndex()));
				}
			});
			networkList.setCellRenderer(new DefaultListCellRenderer() {
				public Component getListCellRendererComponent(JList list, Object value, int index,
				        boolean isSelected, boolean cellHasFocus) {
						Network network = (Network) value;
						String text = getScenarioName(network);
						return super.getListCellRendererComponent(list, text, 
								index, isSelected, cellHasFocus);
				    }
			});
			networkList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					networkSelectionChanged();
				}
			});
			
			createEmptyNetworkAction = new HyperLinkAction("Create empty network") {
				@Override
				protected void actionPerformed() {
					getPowerFlowCase().addNetwork(new Network());
					refreshList();
					networkList.setSelectedIndex(listModel.getSize() - 1);
				}
			};
			importMatpowerNetworkAction = new HyperLinkAction("Import Matpower case") {
				@Override
				protected void actionPerformed() {
					ImportMatpowerDialog dialog = new ImportMatpowerDialog(
							(Frame) SwingUtilities.getWindowAncestor(NetworkContainer.this));
					dialog.showDialog(-1, -1);
					if(dialog.isCancelPressed())// cancel pressed
						return;
					PowerFlowAnalyzer.getInstance().importMatpowerCase(dialog.getSelectedInputFile());
				}
			};
			importFromScriptAction = new HyperLinkAction("Import from script") {
				@Override
				protected void actionPerformed() {
					PowerFlowAnalyzer frame = PowerFlowAnalyzer.getInstance();
					importFromScriptDialg = new ImportFromScriptDialog(frame, frame.getWorkingDirectory());
					importFromScriptDialg.showDialog(-1, -1);
					if(importFromScriptDialg.isCancelPressed()) { // cancel pressed
						importFromScriptDialg = null;
						return;
					}
					PowerFlowAnalyzer.getInstance().importFromScript(importFromScriptDialg.getSelectedInputFile());
					importFromScriptDialg = null;
				}
			};
			duplicateNetworkAction = new HyperLinkAction("Duplicate") {
				@Override
				protected void actionPerformed() {
					Network network = getSelectedNetwork();
					if(network != null) {
						try {
							String newName = "Copy of " + network.getName();
							network = getPowerFlowCase().createNetworkCopy(network);
							network.setName(newName);
							getPowerFlowCase().addNetwork(network);
							refreshList();
							networkList.setSelectedIndex(listModel.getSize() - 1);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			};
			deleteNetworkAction = new HyperLinkAction("Delete") {
				@Override
				protected void actionPerformed() {
					Network[] selection = selectedNetworks.toArray(new Network[selectedNetworks.size()]);
					for (Network network : selection) {
						if(network != null) {
							String name = getScenarioName(network);
							int choice = JOptionPane.showConfirmDialog(NetworkContainer.this, 
									"Do you want to delete the network \"" + name + "\"?", "Delete network", JOptionPane.YES_NO_OPTION);
							if(choice == JOptionPane.YES_OPTION) {
								getPowerFlowCase().removeNetwork(network);
								int tabIndex = getNetworkTabIndex(network);
								if(tabIndex != -1)
									networkTabs.closeTab(tabIndex);
								refreshList();
								networkList.clearSelection();
							} else
								return;
						}
					}
				}
			};
			JPanel networkActionPane = new JPanel(new GridLayout(0, 1));
			networkActionPane.setBorder(new TitledBorder("Add / Remove Network"));
			networkActionPane.add(createEmptyNetworkAction);
			networkActionPane.add(importMatpowerNetworkAction);
			networkActionPane.add(importFromScriptAction);
			networkActionPane.add(duplicateNetworkAction);
			networkActionPane.add(deleteNetworkAction);
			
			scriptActionPane = new JPanel(new GridLayout(0, 1));
			scriptActionPane.setBorder(new TitledBorder("Execute Script"));
			
			JPanel actionGroupPanel = new JPanel();
			actionGroupPanel.add(networkActionPane);
			actionGroupPanel.add(scriptActionPane);
			
			JPanel actionContainer = new JPanel(new BorderLayout());
			JPanel networkDataPane = new JPanel(new GridLayout(0, 2));
			networkDataPane.add(new JLabel("Name: "));
			networkDataPane.add(networkLabel);
			networkDataPane.add(new JLabel("Description: "));
			networkDataPane.add(networkDescription);
			
			actionContainer.add(networkDataPane, BorderLayout.NORTH);
			actionContainer.add(actionGroupPanel, BorderLayout.CENTER);
			JSplitPane centerSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
					new JScrollPane(networkList), actionContainer);
			centerSplitter.setDividerLocation(300);
			
			add(centerSplitter, BorderLayout.CENTER);
		}
		
		private Network getSelectedNetwork() {
			if(selectedNetworks.size() == 1)
				return selectedNetworks.get(0);
			return null;
		}
		
		private void networkSelectionChanged() {
			selectedNetworks.clear();
			Object[] selection = networkList.getSelectedValues();
			for (int i = 0; i < selection.length; i++) {
				selectedNetworks.add((Network) selection[i]);
			}
			boolean singleSelection = selectedNetworks.size() == 1;
			boolean emptySelection = selectedNetworks.size() == 0;
			boolean isMatlabEnv = PowerFlowAnalyzer.getInstance().getEnvironment() == PowerFlowAnalyzer.MATLAB_ENVIRONMENT;
			networkLabel.setEnabled(singleSelection);
			networkLabel.updateView();
			networkDescription.setEnabled(singleSelection);
			networkDescription.updateView();
			importMatpowerNetworkAction.setEnabled(isMatlabEnv);
			importFromScriptAction.setEnabled(isMatlabEnv);
			duplicateNetworkAction.setEnabled(singleSelection);
			deleteNetworkAction.setEnabled( ! emptySelection);
		}
		
		private void updateScriptActions() {
			scriptActionPane.removeAll();
			for (final ModelData script : getPowerFlowCase().getModelDB().getScriptClass().getModel()) {
				HyperLinkAction action = new HyperLinkAction(script.getLabel()) {
					@Override
					protected void actionPerformed() {
						Network network = getSelectedNetwork();
						if(network == null)
							return;
						PowerFlowAnalyzer.getInstance().executeScript(network, script);
					}
				};
				scriptActionPane.add(action);
			}
		}
		
		class NetworkListModel extends AbstractListModel {
			@Override
			public Object getElementAt(int index) {
				return powerFlowCase.getNetworks().get(index);
			}
			@Override
			public int getSize() {
				return powerFlowCase.getNetworks().size();
			}
			private void refreshList() {
				fireContentsChanged(this, 0, getSize());
			}
		};
		
		private void refreshList() {
			listModel.refreshList();
			networkList.revalidate();
		}
		
		class NetworkName extends AbstractTextEditor {

			JTextField textfield = new JTextField();
			
			protected NetworkName() {
				super();
				add(textfield, BorderLayout.NORTH);
				textfield.addActionListener(this);
				textfield.addKeyListener(this);
			}

			@Override
			protected String getTextValue() {
				if(selectedNetworks.size() == 1)
					return selectedNetworks.get(0).getName();
				return null;
			}

			@Override
			protected void setTextValue(String text) {
				if(selectedNetworks.size() == 1)
					selectedNetworks.get(0).setName(text);
				networkList.repaint();
				updateTabTitles();
			}

			@Override
			protected String getText() {
				return textfield.getText();
			}

			@Override
			protected void setText(String text) {
				textfield.setText(text);
			}
		}
		
		class NetworkDescription extends AbstractTextEditor {

			JTextArea textarea = new JTextArea(3, 30);
			
			protected NetworkDescription() {
				super();
				add(new JScrollPane(textarea), BorderLayout.NORTH);
//				textarea.addActionListener(this);
				textarea.addKeyListener(this);
			}

			@Override
			protected String getTextValue() {
				if(selectedNetworks.size() == 1)
					return selectedNetworks.get(0).getDescription();
				return null;
			}

			@Override
			protected void setTextValue(String text) {
				if(selectedNetworks.size() == 1) {
					selectedNetworks.get(0).setDescription(text);
					int tabIndex = getNetworkTabIndex(selectedNetworks.get(0));
					if(tabIndex != -1)
						getViewer(tabIndex).updateNetworkDescription();
				}
			}

			@Override
			protected String getText() {
				return textarea.getText();
			}

			@Override
			protected void setText(String text) {
				textarea.setText(text);
			}
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
