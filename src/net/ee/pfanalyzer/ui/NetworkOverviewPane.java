package net.ee.pfanalyzer.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.ee.pfanalyzer.PowerFlowAnalyzer;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.PowerFlowCase;
import net.ee.pfanalyzer.model.data.ModelData;
import net.ee.pfanalyzer.model.util.ListUtils;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.preferences.Preferences;
import net.ee.pfanalyzer.ui.dialog.ImportFromScriptDialog;
import net.ee.pfanalyzer.ui.dialog.ImportMatpowerDialog;
import net.ee.pfanalyzer.ui.util.AbstractTextEditor;
import net.ee.pfanalyzer.ui.util.HyperLinkAction;

public class NetworkOverviewPane extends JPanel {
	
	private NetworkContainer parent;
	private JTree networkTree;
	private NetworkTreeModel treeModel;
	private List<Network> selectedNetworks = new ArrayList<Network>();
	private NetworkName networkLabel = new NetworkName();
	private NetworkDescription networkDescription = new NetworkDescription();
	private HyperLinkAction createEmptyNetworkAction, importMatpowerNetworkAction, //importFromScriptAction,
			duplicateNetworkAction, deleteNetworkAction;
	private HyperLinkAction createScenarioAction;
	private JPanel scriptActionPane;
	private ImportFromScriptDialog importFromScriptDialg;
	
	NetworkOverviewPane(NetworkContainer parentContainer) {
		super(new BorderLayout());
		this.parent = parentContainer;
		
		treeModel = new NetworkTreeModel();
		networkTree = new JTree(treeModel);
		networkTree.setCellRenderer(new NetworkCellRenderer());
		networkTree.setExpandsSelectedPaths(true);
		networkTree.setRootVisible(false);
		networkTree.setShowsRootHandles(true);
		networkTree.setToggleClickCount(3);
		networkTree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					Network network = getSelectedNetwork2();
					if(network != null)
						parent.openNetwork(network);
				}
			}
		});
		networkTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				networkSelectionChanged();
			}
		});
		
		createEmptyNetworkAction = new HyperLinkAction("Create empty network") {
			@Override
			protected void actionPerformed() {
				Network network = new Network(getPowerFlowCase().getCaseID());
				getPowerFlowCase().addNetwork(network);
				refreshTree();
				selectNetwork(network);
			}
		};
		importMatpowerNetworkAction = new HyperLinkAction("Import Matpower case") {
			@Override
			protected void actionPerformed() {
				ImportMatpowerDialog dialog = new ImportMatpowerDialog(
						SwingUtilities.getWindowAncestor(NetworkOverviewPane.this));
				dialog.showDialog(-1, -1);
				if(dialog.isCancelPressed())// cancel pressed
					return;
				PowerFlowAnalyzer.getInstance().importMatpowerCase(dialog.getSelectedInputFile());
			}
		};
//		importFromScriptAction = new HyperLinkAction("Import from script") {
//			@Override
//			protected void actionPerformed() {
//				PowerFlowAnalyzer frame = PowerFlowAnalyzer.getInstance();
//				importFromScriptDialg = new ImportFromScriptDialog(frame, frame.getWorkingDirectory());
//				importFromScriptDialg.showDialog(-1, -1);
//				if(importFromScriptDialg.isCancelPressed()) { // cancel pressed
//					importFromScriptDialg = null;
//					return;
//				}
//				PowerFlowAnalyzer.getInstance().importFromScript(importFromScriptDialg.getSelectedInputFile());
//				importFromScriptDialg = null;
//			}
//		};
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
						refreshTree();
						selectNetwork(network);
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
				if(selection.length > 1) {
					int choice = JOptionPane.showConfirmDialog(NetworkOverviewPane.this, 
							"Do you want to delete the selected " + selection.length + " networks?", "Delete networks", JOptionPane.YES_NO_OPTION);
					if(choice == JOptionPane.YES_OPTION) {
						for (Network network : selection) {
							deleteNetwork(network);
						}
					}
				} else {
					for (Network network : selection) {
						if(network != null) {
							String name = network.getDisplayName();
							int choice = JOptionPane.showConfirmDialog(NetworkOverviewPane.this, 
									"Do you want to delete the network \"" + name + "\"?", "Delete network", JOptionPane.YES_NO_OPTION);
							if(choice == JOptionPane.YES_OPTION) {
								deleteNetwork(network);
							} else
								return;
						}
					}
				}
			}
		};
		JPanel networkActionPane = new JPanel(new GridLayout(0, 1));
		networkActionPane.setBorder(new TitledBorder("Add / Remove Network"));
		networkActionPane.add(createEmptyNetworkAction);
		networkActionPane.add(importMatpowerNetworkAction);
//		networkActionPane.add(importFromScriptAction);
		networkActionPane.add(duplicateNetworkAction);
		networkActionPane.add(deleteNetworkAction);
		
		createScenarioAction = new HyperLinkAction("Add scenario") {
			@Override
			protected void actionPerformed() {
				Network network = getSelectedNetwork();
				if(network != null) {
					try {
						String newName = "Scenario of " + network.getName();
						Network scenario = getPowerFlowCase().createNetworkCopy(network);
						scenario.setName(newName);
						getPowerFlowCase().addScenario(network, scenario);
						refreshTree();
						selectNetwork(scenario);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		JPanel scenarioActionPane = new JPanel(new GridLayout(0, 1));
		scenarioActionPane.setBorder(new TitledBorder("Manage Scenarios"));
		scenarioActionPane.add(createScenarioAction);
		
		scriptActionPane = new JPanel(new GridLayout(0, 1));
		scriptActionPane.setBorder(new TitledBorder("Execute Script"));
		
		JPanel actionGroupPanel = new JPanel();
		actionGroupPanel.setBorder(new CompoundBorder(
				new EmptyBorder(5, 5, 5, 5),
				new TitledBorder("Actions")));
		actionGroupPanel.add(networkActionPane);
//		actionGroupPanel.add(scenarioActionPane);
		actionGroupPanel.add(scriptActionPane);
		
		JPanel actionContainer = new JPanel(new BorderLayout());
		JPanel networkDataPane = new JPanel(new GridLayout(0, 2));
		networkDataPane.setBorder(new CompoundBorder(
				new EmptyBorder(5, 5, 5, 5),
				new TitledBorder("Network Information")));
		networkDataPane.add(new JLabel("Name: "));
		networkDataPane.add(networkLabel);
		networkDataPane.add(new JLabel("Description: "));
		networkDataPane.add(networkDescription);
		
		actionContainer.add(networkDataPane, BorderLayout.NORTH);
		actionContainer.add(actionGroupPanel, BorderLayout.CENTER);
		JSplitPane centerSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
				new JScrollPane(networkTree), actionContainer);
		centerSplitter.setDividerLocation(300);
		
		add(centerSplitter, BorderLayout.CENTER);
	}
	
	public PowerFlowCase getPowerFlowCase() {
		return parent.getPowerFlowCase();
	}
	
	private void deleteNetwork(Network network) {
		getPowerFlowCase().removeNetwork(network);
		int tabIndex = parent.getNetworkTabIndex(network);
		if(tabIndex != -1)
			parent.closeTab(tabIndex);
		refreshTree();
		networkTree.clearSelection();
	}
	
	private Network getSelectedNetwork() {
		if(selectedNetworks.size() == 1)
			return selectedNetworks.get(0);
		return null;
	}
	
	private Network getSelectedNetwork2() {
		if(networkTree.getSelectionModel() == null || networkTree.getSelectionModel().getSelectionPath() == null)
			return null;
		Object selection = networkTree.getSelectionModel().getSelectionPath().getLastPathComponent();
		if(selection instanceof Network)
			return (Network) selection;
		return null;
	}
	
	private void selectNetwork(Network network) {
		networkTree.setSelectionPath(new TreePath(getNetworkPath(network)));
	}
	
	private Object[] getNetworkPath(Network network) {
		List<Object> list = new ArrayList<Object>();
		list.add(treeModel.getRoot());
		getNetworkPath(list, network);
		return list.toArray(new Object[list.size()]);
	}
	
	private void getNetworkPath(List<Object> list, Network network) {
		if(network.getParentNetwork() != null)
			getNetworkPath(list, network.getParentNetwork());
		list.add(network);
	}
	
	void networkSelectionChanged() {
		selectedNetworks.clear();
		TreePath[] selection = networkTree.getSelectionPaths();
		if(selection != null) {
			for (int i = 0; i < selection.length; i++) {
				Object node = selection[i].getLastPathComponent();
				if(node instanceof Network)
					selectedNetworks.add((Network) node);
			}
		}
		boolean singleSelection = selectedNetworks.size() == 1;
		boolean emptySelection = selectedNetworks.size() == 0;
		boolean isMatlabEnv = PowerFlowAnalyzer.getInstance().getEnvironment() == PowerFlowAnalyzer.MATLAB_ENVIRONMENT;
		networkLabel.setEnabled(singleSelection);
		networkLabel.updateView();
		networkDescription.setEnabled(singleSelection);
		networkDescription.updateView();
		importMatpowerNetworkAction.setEnabled(isMatlabEnv);
//		importFromScriptAction.setEnabled(isMatlabEnv);
		duplicateNetworkAction.setEnabled(singleSelection);
		deleteNetworkAction.setEnabled( ! emptySelection);
		createScenarioAction.setEnabled(singleSelection);
		updateScriptActions();
	}
	
	public void setWorkingDirectory(String workingDirectory) {
		if(importFromScriptDialg != null)
			importFromScriptDialg.setWorkingDirectory(workingDirectory);
	}
	
	void updateScriptActions() {
		scriptActionPane.removeAll();
		for (final ModelData script : getPowerFlowCase().getModelDB().getScriptClass().getModel()) {
			if((selectedNetworks.size() == 0 && ModelDBUtils.isNetworkCreatingScript(script) == false)
					|| (selectedNetworks.size() > 1 && ModelDBUtils.isNetworkCreatingScript(script)))
				continue;
			String label = script.getLabel();
			if(label == null || label.isEmpty())
				label = "Untitled Script";
			HyperLinkAction action = new HyperLinkAction(label) {
				@Override
				protected void actionPerformed() {
					executeScript(script);
				}
			};
			scriptActionPane.add(action);
		}
		if(scriptActionPane.getComponentCount() == 0)
			scriptActionPane.add(new JLabel("<No network selected>"));
		scriptActionPane.revalidate();
		scriptActionPane.repaint();
	}
	
	private void executeScript(ModelData script) {
		if(ModelDBUtils.isNetworkCreatingScript(script)) {
			Network network = null;
			if(selectedNetworks.isEmpty()) {
				network = new Network(getPowerFlowCase().getCaseID());
				getPowerFlowCase().addNetwork(network);
				refreshTree();
				selectNetwork(network);
			} else if(selectedNetworks.size() == 1) {
				network = selectedNetworks.get(0);
				if(network.isEmpty() == false) {
					int action = JOptionPane.showOptionDialog(this, 
							"<html>This script will create a new network but the selected " +
							"network is not empty.<br>Do you want to overwrite " +
							"the selected network or create a new network instead?", "Question", 
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, 
							null, new String[] {
									"Overwrite network", "Create new network", "Cancel"
							}, null);
					if(action == JOptionPane.YES_OPTION) {
						network.removeAllElements();
						network.getParameterList().clear();
						network.fireNetworkChanged();
					} else if(action == JOptionPane.NO_OPTION) {
						network = new Network();
						getPowerFlowCase().addNetwork(network);
						refreshTree();
						selectNetwork(network);
					} else if(action == JOptionPane.CANCEL_OPTION)
						return;
				}
			} else
				return;
			if(network != null)
				PowerFlowAnalyzer.getInstance().executeScript(network, script);
		} else
			PowerFlowAnalyzer.getInstance().executeScript(
					selectedNetworks.toArray(new Network[selectedNetworks.size()]), script);

//		Network network = getSelectedNetwork();
//		selectedNetworks
//		if(network == null) {
//			if(ModelDBUtils.isNetworkCreatingScript(script)) {
//				network = new Network(getPowerFlowCase().getCaseID());
//				getPowerFlowCase().addNetwork(network);
//				refreshTree();
//				selectNetwork(network);
//			} else
//				return;
//		} else if(network.isEmpty() == false && ModelDBUtils.isNetworkCreatingScript(script)) {
//			int action = JOptionPane.showOptionDialog(this, 
//					"<html>This script will create a new network but the selected " +
//					"network is not empty.<br>Do you want to overwrite " +
//					"the selected network or create a new network instead?", "Question", 
//					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, 
//					null, new String[] {
//							"Overwrite network", "Create new network", "Cancel"
//					}, null);
//			if(action == JOptionPane.YES_OPTION) {
//				network.removeAllElements();
//				network.getParameterList().clear();
//				network.fireNetworkChanged();
//			} else if(action == JOptionPane.NO_OPTION) {
//				network = new Network();
//				getPowerFlowCase().addNetwork(network);
//				refreshTree();
//				selectNetwork(network);
//			} else if(action == JOptionPane.CANCEL_OPTION)
//				return;
//		}
//		if(network == null)
//			return;
//		PowerFlowAnalyzer.getInstance().executeScript(network, script);
	}
	
	void refreshTree() {
		treeModel.refreshModel();
		networkTree.revalidate();
	}
	
	void repaintTree() {
		networkTree.repaint();
	}
	
	class NetworkTreeModel implements TreeModel {
		
		private List<Network> sortedNetworks = new ArrayList<Network>();
		private Vector<TreeModelListener> treeModelListeners = new Vector<TreeModelListener>();

		NetworkTreeModel() {
			sortNetworks();
		}
		
		private void sortNetworks() {
			sortedNetworks.clear();
			for(Network network: getPowerFlowCase().getNetworks(false))
				sortedNetworks.add(network);
			Collections.sort(sortedNetworks, new Comparator<Network>() {
				@Override
				public int compare(Network n1, Network n2) {
					if(n1.getName() == null)
						return 0;
					return n1.getName().compareTo(n2.getName());
				}
			});
		}
		
		@Override
		public Object getRoot() {
			return sortedNetworks;
		}
		
		@Override
		public Object getChild(Object parent, int index) {
			if(parent instanceof List)
				return sortedNetworks.get(index);
			if(parent instanceof Network)
				return ((Network) parent).getScenarios().get(index);
			throw new IllegalArgumentException("Parent must be a list or a network but is " + parent);
		}

		@Override
		public int getChildCount(Object parent) {
			if(parent instanceof List)
				return sortedNetworks.size();
			if(parent instanceof Network)
				return ((Network) parent).getScenarios().size();
			return 0;
		}

		@Override
		public int getIndexOfChild(Object parent, Object child) {
			if(parent instanceof List)
				return ListUtils.getIndexOf(sortedNetworks, (Network) child);
			if(parent instanceof Network)
				return ListUtils.getIndexOf(((Network) parent).getScenarios(), (Network) child);
			return -1;
		}

		@Override
		public boolean isLeaf(Object node) {
			if(node instanceof List)
				return false;
			if(node instanceof Network)
				return ((Network) node).getScenarios().isEmpty();
			return true;
		}

		@Override
		public void valueForPathChanged(TreePath path, Object newValue) {
			
		}
		
		public void refreshModel() {
			sortNetworks();
			fireTreeStructureChanged();
		}
		
		protected void fireTreeNodeChanged(Network network) {
	        TreeModelEvent e = new TreeModelEvent(this, getNetworkPath(network));
	        for (TreeModelListener listener : treeModelListeners)
	        	listener.treeNodesChanged(e);
		}
		
		protected void fireTreeStructureChanged() {
	        TreeModelEvent e = new TreeModelEvent(this, new Object[] { getRoot() });
	        for (TreeModelListener listener : treeModelListeners)
	        	listener.treeStructureChanged(e);
	    }
		
		public void addTreeModelListener(TreeModelListener l) {
	        treeModelListeners.addElement(l);
	    }
		
		public void removeTreeModelListener(TreeModelListener l) {
	        treeModelListeners.removeElement(l);
	    }
	}
	
	class NetworkCellRenderer extends DefaultTreeCellRenderer {
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			try {
				super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
				if(value instanceof Network) {
					Network network = (Network) value;
					String dirtySuffix = network.isDirty() ? " *" : "";
					setText(network.getDisplayName() + dirtySuffix);
					if(network.hasFailures())
						setForeground(Preferences.getFlagFailureColor());
					else 
						if(network.wasCalculated())
						setForeground(Preferences.getHyperlinkForeground());
					else
						setForeground(Color.BLACK);
				} else
					setText("");
			} catch(Exception e) {
				System.err.println(e);
			}
			return this;
		}
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
			treeModel.fireTreeNodeChanged(selectedNetworks.get(0));
			networkTree.repaint();
			parent.updateTabTitles();
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
//			textarea.addActionListener(this);
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
				int tabIndex = parent.getNetworkTabIndex(selectedNetworks.get(0));
				if(tabIndex != -1)
					parent.getViewer(tabIndex).updateNetworkDescription();
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