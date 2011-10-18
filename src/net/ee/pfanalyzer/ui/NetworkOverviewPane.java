package net.ee.pfanalyzer.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
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
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.ui.dialog.ImportFromScriptDialog;
import net.ee.pfanalyzer.ui.dialog.ImportMatpowerDialog;
import net.ee.pfanalyzer.ui.util.AbstractTextEditor;
import net.ee.pfanalyzer.ui.util.HyperLinkAction;

public class NetworkOverviewPane extends JPanel {
	
	private NetworkContainer parent;
//	private JList networkList;
	private JTree networkTree;
	private NetworkTreeModel treeModel;
//	private NetworkListModel listModel;
	private List<Network> selectedNetworks = new ArrayList<Network>();
	private NetworkName networkLabel = new NetworkName();
	private NetworkDescription networkDescription = new NetworkDescription();
	private HyperLinkAction createEmptyNetworkAction, importMatpowerNetworkAction, //importFromScriptAction,
			duplicateNetworkAction, deleteNetworkAction;
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
		
//		listModel = new NetworkListModel();
//		networkList = new JList(listModel);
//		networkList.addMouseListener(new MouseAdapter() {
//			public void mouseClicked(MouseEvent e) {
//				if(e.getClickCount() == 2)
//					parent.openNetwork(getPowerFlowCase().getNetworks().get(networkList.getSelectedIndex()));
//			}
//		});
//		networkList.setCellRenderer(new DefaultListCellRenderer() {
//			public Component getListCellRendererComponent(JList list, Object value, int index,
//			        boolean isSelected, boolean cellHasFocus) {
//					Network network = (Network) value;
//					String text = parent.getNetworkName(network);
//					return super.getListCellRendererComponent(list, text, 
//							index, isSelected, cellHasFocus);
//			    }
//		});
//		networkList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//			@Override
//			public void valueChanged(ListSelectionEvent e) {
//				networkSelectionChanged();
//			}
//		});
		
		createEmptyNetworkAction = new HyperLinkAction("Create empty network") {
			@Override
			protected void actionPerformed() {
				Network network = new Network();
				getPowerFlowCase().addNetwork(network);
				refreshList();
				selectNetwork(network);
//				networkList.setSelectedIndex(listModel.getSize() - 1);
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
						refreshList();
						selectNetwork(network);
//						networkList.setSelectedIndex(listModel.getSize() - 1);
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
						String name = parent.getNetworkName(network);
						int choice = JOptionPane.showConfirmDialog(NetworkOverviewPane.this, 
								"Do you want to delete the network \"" + name + "\"?", "Delete network", JOptionPane.YES_NO_OPTION);
						if(choice == JOptionPane.YES_OPTION) {
							getPowerFlowCase().removeNetwork(network);
							int tabIndex = parent.getNetworkTabIndex(network);
							if(tabIndex != -1)
								parent.closeTab(tabIndex);
							refreshList();
							networkTree.clearSelection();
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
//		networkActionPane.add(importFromScriptAction);
		networkActionPane.add(duplicateNetworkAction);
		networkActionPane.add(deleteNetworkAction);
		
		scriptActionPane = new JPanel(new GridLayout(0, 1));
		scriptActionPane.setBorder(new TitledBorder("Execute Script"));
		
		JPanel actionGroupPanel = new JPanel();
		actionGroupPanel.setBorder(new CompoundBorder(
				new EmptyBorder(5, 5, 5, 5),
				new TitledBorder("Actions")));
		actionGroupPanel.add(networkActionPane);
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
	
	private Network getSelectedNetwork() {
		if(selectedNetworks.size() == 1)
			return selectedNetworks.get(0);
		return null;
	}
	
	private Network getSelectedNetwork2() {
		Object selection = networkTree.getSelectionModel().getSelectionPath().getLastPathComponent();
		if(selection instanceof Network)
			return (Network) selection;
		return null;
	}
	
	private void selectNetwork(Network network) {
		networkTree.setSelectionPath(new TreePath(new Object[] { treeModel.getRoot(), network}));
	}
	
	void networkSelectionChanged() {
		selectedNetworks.clear();
		TreePath[] selection = networkTree.getSelectionPaths();
//		Object[] selection = networkList.getSelectedValues();
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
		updateScriptActions();
	}
	
	public void setWorkingDirectory(String workingDirectory) {
		if(importFromScriptDialg != null)
			importFromScriptDialg.setWorkingDirectory(workingDirectory);
	}
	
	void updateScriptActions() {
		scriptActionPane.removeAll();
		for (final ModelData script : getPowerFlowCase().getModelDB().getScriptClass().getModel()) {
			if(selectedNetworks.size() == 0 && ModelDBUtils.isNetworkCreatingScript(script) == false)
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
		Network network = getSelectedNetwork();
		if(network == null) {
			if(ModelDBUtils.isNetworkCreatingScript(script)) {
				network = new Network();
				getPowerFlowCase().addNetwork(network);
				refreshList();
				selectNetwork(network);
//				networkList.setSelectedIndex(listModel.getSize() - 1);
			} else
				return;
		} else if(network.isEmpty() == false && ModelDBUtils.isNetworkCreatingScript(script)) {
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
				refreshList();
				selectNetwork(network);
//				networkList.setSelectedIndex(listModel.getSize() - 1);
			} else if(action == JOptionPane.CANCEL_OPTION)
				return;
		}
		if(network == null)
			return;
		PowerFlowAnalyzer.getInstance().executeScript(network, script);
	}
	
//	class NetworkListModel extends AbstractListModel {
//		@Override
//		public Object getElementAt(int index) {
//			return getPowerFlowCase().getNetworks().get(index);
//		}
//		@Override
//		public int getSize() {
//			return getPowerFlowCase().getNetworks().size();
//		}
//		private void refreshList() {
//			fireContentsChanged(this, 0, getSize());
//		}
//	};
	
	void refreshList() {
//		listModel.refreshList();
		treeModel.refreshModel();
		networkTree.revalidate();
	}
	
	class NetworkTreeModel implements TreeModel {
		
		private Vector<TreeModelListener> treeModelListeners = new Vector<TreeModelListener>();

		NetworkTreeModel() {
			
		}
		
		@Override
		public Object getRoot() {
			return getPowerFlowCase().getNetworks();
		}
		
		@Override
		public Object getChild(Object parent, int index) {
			if(parent instanceof List)
				return getPowerFlowCase().getNetworks().get(index);
			return null;
		}

		@Override
		public int getChildCount(Object parent) {
			if(parent instanceof List)
				return getPowerFlowCase().getNetworks().size();
			return 0;
		}

		@Override
		public int getIndexOfChild(Object parent, Object child) {
			if(parent instanceof List)
				return getPowerFlowCase().getNetworks().indexOf(child);
			return -1;
		}

		@Override
		public boolean isLeaf(Object node) {
			if(node instanceof List)
				return false;
			return true;
		}

		@Override
		public void valueForPathChanged(TreePath path, Object newValue) {
			
		}
		
		public void refreshModel() {
			fireTreeStructureChanged();
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
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			if(value instanceof Network) {
				Network network = (Network) value;
				setText(parent.getNetworkName(network));
			} else
				setText("");
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