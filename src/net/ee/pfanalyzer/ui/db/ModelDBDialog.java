package net.ee.pfanalyzer.ui.db;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.ee.pfanalyzer.PowerFlowAnalyzer;
import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.CaseSerializer;
import net.ee.pfanalyzer.model.DatabaseChangeEvent;
import net.ee.pfanalyzer.model.ModelDB;
import net.ee.pfanalyzer.model.NetworkChangeEvent;
import net.ee.pfanalyzer.model.data.AbstractModelElementData;
import net.ee.pfanalyzer.model.data.CaseData;
import net.ee.pfanalyzer.model.data.ModelClassData;
import net.ee.pfanalyzer.model.data.ModelDBData;
import net.ee.pfanalyzer.model.data.ModelData;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.ui.INetworkElementSelectionListener;
import net.ee.pfanalyzer.ui.dialog.BaseDialog;

public class ModelDBDialog extends BaseDialog implements PropertyChangeListener, INetworkElementSelectionListener {
	
	public final static int STANDALONE_MODE = 0;
	public final static int SET_MODEL_MODE = 1;
	public final static int GET_MODEL_MODE = 2;
	public final static int GET_MODEL_OR_CLASS_MODE = 3;
	
	private final int mode;
	private ModelDB db;
	private ModelDBData dbData;
	private JComponent propPanel;
	private JLabel elementTitleLabel;
	private JTree modelTree;
	private DefaultTreeModel treeModel;
	private DefaultMutableTreeNode currentNode;
	private AbstractNetworkElement selectedNetworkElement;
	private AbstractModelElementData selectedModelOrClass;
	private JButton addClassButton, addModelButton, removeButton, managePropsButton, performActionButton;
	
	public ModelDBDialog(Window frame, int mode, ModelDB modelDB, boolean modal, String actionText) {
		this(frame, mode, modelDB, modelDB.getData(), modal, actionText);
	}
	
	private ModelDBDialog(Window frame, int mode, ModelDB db, ModelDBData dbData, boolean modal, String actionText) {
		super(frame, "Parameter Database", modal);
		this.mode = mode;
		this.db = db;
		this.dbData = dbData;
		
		treeModel = new DefaultTreeModel(createTreeData());
		modelTree = new JTree(treeModel);
		modelTree.setRootVisible(false);
		modelTree.setExpandsSelectedPaths(true);
//		modelTree.setToggleClickCount(1);
		for (int i = treeModel.getChildCount(treeModel.getRoot()) - 1; i >= 0 ; i--) {
			modelTree.expandRow(i);
		}
		modelTree.setCellRenderer(new DefaultTreeCellRenderer() {
			public Component getTreeCellRendererComponent(JTree tree, Object value,
					  boolean sel,
					  boolean expanded,
					  boolean leaf, int row,
					  boolean hasFocus) {
				String label = "<empty>";
				if(value instanceof DefaultMutableTreeNode) {
					value = ((DefaultMutableTreeNode) value).getUserObject();
				}
				if(value instanceof ModelClassData) {
					label = ((ModelClassData) value).getLabel();
					leaf = false;
				} else if (value instanceof ModelData) {
					label = ((ModelData) value).getLabel();
					leaf = true;
				}
				if(label == null || label.isEmpty())
					label = "<no label>";
				return super.getTreeCellRendererComponent(tree, label, sel, expanded, leaf, row, hasFocus);
			}
		});
		modelTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = getSelectedNode();
				showElementProperties(node);
				updateButtons(node);
			}
		});
		modelTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		Container contentPane = Box.createVerticalBox();
		JComponent optionsPane = new JPanel(new GridLayout(0, 1));
//		optionsPane.add(Box.createHorizontalGlue());
		optionsPane.setBorder(new TitledBorder("Show Bus Data"));
		contentPane.add(optionsPane);
		
		propPanel = new JPanel(new BorderLayout());
		elementTitleLabel = new JLabel();
		elementTitleLabel.setForeground(Color.WHITE);
		JPanel titlePanel = new JPanel();
		titlePanel.setBackground(Color.DARK_GRAY);
		titlePanel.add(elementTitleLabel);
		JPanel propPanelResizer = new JPanel(new BorderLayout());
		propPanelResizer.add(titlePanel, BorderLayout.NORTH);
		propPanelResizer.add(new JScrollPane(propPanel), BorderLayout.CENTER);
		
		if(actionText != null) {
			performActionButton = new JButton(actionText);
			performActionButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					performAction();
				}
			});
			addBottomComponent(performActionButton);
		}
		addButton("Close", true, true);

		managePropsButton = new JButton("Manage Parameters...");
		managePropsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AbstractModelElementData element = getSelectedTreeElement();
				if(element != null)
					manageParameters(element);
			}
		});
		JPanel propButtonPanel = new JPanel();
		propButtonPanel.add(managePropsButton);
		propPanelResizer.add(propButtonPanel, BorderLayout.SOUTH);
		
		JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		JPanel treeParent = new JPanel(new BorderLayout());
		JLabel treeTitleLabel = new JLabel("Database Browser");
		treeTitleLabel.setForeground(Color.WHITE);
		JPanel treeTitlePanel = new JPanel();
		treeTitlePanel.setBackground(Color.DARK_GRAY);
		treeTitlePanel.add(treeTitleLabel);
		treeParent.add(treeTitlePanel, BorderLayout.NORTH);
		treeParent.add(new JScrollPane(modelTree), BorderLayout.CENTER);
		addClassButton = PowerFlowAnalyzer.createButton("Create a new model class", "folder_add.png", "Add Class", false);
		addClassButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addElement(new ModelClassData());
			}
		});
		addModelButton = PowerFlowAnalyzer.createButton("Create a new model deriving from the selected class", "add.png", "Add Model", false);
		addModelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addElement(new ModelData());
			}
		});
		removeButton = PowerFlowAnalyzer.createButton("Remove the selected element", "delete.png", "Remove...", false);
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeElement();
			}
		});
		JPanel treeButtonPane = new JPanel();
		treeButtonPane.add(addModelButton);
		treeButtonPane.add(addClassButton);
		treeButtonPane.add(removeButton);
		treeParent.add(treeButtonPane, BorderLayout.SOUTH);
		
		splitter.setLeftComponent(treeParent);
		splitter.setRightComponent(propPanelResizer);
		splitter.setDividerLocation(250);
		setCenterComponent(splitter, false);
		showElementProperties(null);
		updateButtons(null);
	}
	
	private void performAction() {
		if(mode == SET_MODEL_MODE)
			setModel();
		else if(mode == GET_MODEL_MODE)
			getModel();
		else if(mode == GET_MODEL_OR_CLASS_MODE)
			getModelOrClass();
	}
	
	private void setModel() {
		AbstractModelElementData selected = getSelectedTreeElement();
		if(selected instanceof ModelData && selectedNetworkElement != null) {
			String oldModelID = selectedNetworkElement.getModelID();
			ModelData model = (ModelData) selected;
			selectedNetworkElement.setModel(model);
			String newModelID = ModelDBUtils.getFullElementID(model);
			selectedNetworkElement.setModelID(newModelID);
			selectedNetworkElement.getNetwork().fireNetworkElementChanged(new NetworkChangeEvent(
					selectedNetworkElement, oldModelID, newModelID));
		}
	}
	
	private void getModel() {
		AbstractModelElementData selected = getSelectedTreeElement();
		if(selected instanceof ModelData)
			selectedModelOrClass = getSelectedTreeElement();
		setVisible(false);
	}
	
	private void getModelOrClass() {
		selectedModelOrClass = getSelectedTreeElement();
		setVisible(false);
	}
	
	private DefaultMutableTreeNode getSelectedNode() {
		if(modelTree.getSelectionPath() == null)
			return null;
		Object source = modelTree.getSelectionPath().getLastPathComponent();
		if(source != null && source instanceof DefaultMutableTreeNode)
			return (DefaultMutableTreeNode) source;
		return null;
	}
	
	private AbstractModelElementData getSelectedTreeElement() {
		DefaultMutableTreeNode node = getSelectedNode();
		if(node != null && node.getUserObject() instanceof AbstractModelElementData)
			return (AbstractModelElementData) node.getUserObject();
		return null;
	}
	
	public AbstractModelElementData getSelectedElement() {
		return selectedModelOrClass;
	}
	
	private void updateButtons(DefaultMutableTreeNode treeNode) {
		boolean isNetworkClass = getSelectedTreeElement() != null 
				&& ModelDBUtils.isNetworkClass(getSelectedTreeElement());
		if(treeNode == null) {
			addClassButton.setEnabled(true);
			addModelButton.setEnabled(false);
			removeButton.setEnabled(false);
			managePropsButton.setEnabled(false);
			if(performActionButton != null)
				performActionButton.setEnabled(false);
		} else {
			if(treeNode.getUserObject() instanceof ModelClassData) {
				addClassButton.setEnabled(true);
				addModelButton.setEnabled(true);
				removeButton.setEnabled(true);
				managePropsButton.setEnabled(true);
				if(performActionButton != null)
					performActionButton.setEnabled(mode == GET_MODEL_OR_CLASS_MODE && isNetworkClass);
			} else if(treeNode.getUserObject() instanceof ModelData) {
				addClassButton.setEnabled(false);
				addModelButton.setEnabled(false);
				removeButton.setEnabled(true);
				managePropsButton.setEnabled(true);
				if(performActionButton != null) {
					if(mode == SET_MODEL_MODE)
						performActionButton.setEnabled(selectedNetworkElement != null && isNetworkClass);
					else if(mode == GET_MODEL_MODE || mode == GET_MODEL_OR_CLASS_MODE)
						performActionButton.setEnabled(isNetworkClass);
				}
			} else {
				addClassButton.setEnabled(false);
				addModelButton.setEnabled(false);
				removeButton.setEnabled(false);
				managePropsButton.setEnabled(false);
				if(performActionButton != null)
					performActionButton.setEnabled(false);
			}
		}
	}
	
	private void showElementProperties(DefaultMutableTreeNode treeNode) {
		currentNode = treeNode;
		if(propPanel.getComponentCount() > 0)
			propPanel.getComponent(0).removePropertyChangeListener(this);
		propPanel.removeAll();
		if(treeNode != null) {
			AbstractModelElementData element = (AbstractModelElementData) treeNode.getUserObject();
			ParameterPanel paramPanel = new ParameterPanel(treeNode, element, db);
			paramPanel.addPropertyChangeListener(this);
			String typeText = (element instanceof ModelClassData ? "Class" : "Model") + " Parameters";
			elementTitleLabel.setText(typeText);
			propPanel.add(paramPanel, BorderLayout.NORTH);
		} else {
			elementTitleLabel.setText("Nothing selected");
			setText("<html><p>Select an element from the tree on the left side.</p>" +
					"<p>You can add new models/model classes, edit and remove existing elements.</p>");
		}
		propPanel.revalidate();
		propPanel.repaint();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(ParameterPanel.PROPERTY_NAME_RELOAD_ELEMENT.equals(evt.getPropertyName())) {
			treeModel.reload((TreeNode) evt.getNewValue());
		}
	}

	@Override
	public void selectionChanged(Object data) {
		if(data instanceof AbstractNetworkElement)
			setSelectedElement((AbstractNetworkElement) data);
		else
			setSelectedElement((AbstractNetworkElement) null);
	}
	
	public void setSelectedElement(AbstractNetworkElement element) {
		selectedNetworkElement = element;
		if(selectedNetworkElement != null) {
			ModelData model = selectedNetworkElement.getModel();
			if(model != null) {
				setSelectedElement(model);
			} else
				updateButtons(null);
		} else
			updateButtons(null);
	}
	
	public void setSelectedElement(AbstractModelElementData model) {
		DefaultMutableTreeNode node = findNode(model);
		if(node != null)
			modelTree.setSelectionPath(new TreePath(node.getPath()));
		updateButtons(node);
		modelTree.repaint();
	}
	
	private DefaultMutableTreeNode findNode(AbstractModelElementData data) {
		List<AbstractModelElementData> list = new ArrayList<AbstractModelElementData>();
		addElementsRecursive(data, list);
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) modelTree.getModel().getRoot();
		for (int i = 0; i < list.size(); i++) {
			boolean found = false;
			for (int j = 0; j < node.getChildCount(); j++) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(j);
				if(list.get(i) == child.getUserObject()) {
					node = child;
					found = true;
					break;
				}
			}
			if(found == false)
				return null;
		}
		return node;
	}
	
	private void addElementsRecursive(AbstractModelElementData data, List<AbstractModelElementData> list) {
		if(data.getParent() != null)
			addElementsRecursive(data.getParent(), list);
		list.add(data);
	}
	
	private void manageParameters(AbstractModelElementData element) {
//		if(manageParametersDialog == null) {
//			manageParametersDialog = 
				new ManageParametersDialog((Frame) getOwner(), element);
			showElementProperties(currentNode);
//		} else {
//			manageParametersDialog.setVisible(true);
//			manageParametersDialog.toFront();
//		}
	}
	
	private void addElement(AbstractModelElementData element) {
		DefaultMutableTreeNode parentNode = null;
		TreePath parentPath = modelTree.getSelectionPath();
		if(parentPath == null)
			parentNode = (DefaultMutableTreeNode) modelTree.getModel().getRoot();
		else
			parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
		
		ModelClassData parent = null;
		if(parentNode.getUserObject() != null) {
			if(parentNode.getUserObject() instanceof ModelClassData) {
				parent = (ModelClassData) parentNode.getUserObject();
			} else {
				parent = (ModelClassData) ((AbstractModelElementData) parentNode.getUserObject()).getParent();
			}
			element.setParent(parent);
		}
		if(parent != null) {
			if(element instanceof ModelClassData)
				parent.getModelClass().add((ModelClassData) element);
			else if(element instanceof ModelData)
				parent.getModel().add((ModelData) element);
		} else if(element instanceof ModelClassData)
			dbData.getModelClass().add((ModelClassData) element);
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(element);
		treeModel.insertNodeInto(newNode, parentNode, parentNode.getChildCount());
		modelTree.scrollPathToVisible(new TreePath(newNode.getPath()));
		modelTree.setSelectionPath(new TreePath(newNode.getPath()));
		if(db != null)
			db.fireElementChanged(new DatabaseChangeEvent(DatabaseChangeEvent.ADDED, element));
	}
	
	private void removeElement() {
		AbstractModelElementData element = getSelectedTreeElement();
		if(element != null) {
			int action = JOptionPane.showConfirmDialog(ModelDBDialog.this, 
					"Do you want to delete this element?", "Confirm", JOptionPane.YES_NO_OPTION);
			if(action != JOptionPane.YES_OPTION)
				return;
			ModelClassData parent = (ModelClassData) element.getParent();
			if(parent != null) {
				if(element instanceof ModelClassData)
					parent.getModelClass().remove(element);
				else if(element instanceof ModelData)
					parent.getModel().remove(element);
			} else if(element instanceof ModelClassData)
				dbData.getModelClass().remove(element);
			treeModel.removeNodeFromParent(getSelectedNode());
			modelTree.clearSelection();
			showElementProperties(null);
			if(db != null)
				db.fireElementChanged(new DatabaseChangeEvent(DatabaseChangeEvent.REMOVED, element));
		}
	}
	
	private DefaultMutableTreeNode createTreeData() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		for (ModelClassData clazz : dbData.getModelClass()) {
			addTreeData(root, clazz);
		}
		return root;
	}
	
	private void addTreeData(DefaultMutableTreeNode parent, ModelClassData clazz) {
		DefaultMutableTreeNode classNode = new DefaultMutableTreeNode(clazz);
		parent.add(classNode);
		Collections.sort(clazz.getModelClass(), new ElementSorter());
		for (ModelClassData subClass : clazz.getModelClass()) {
			addTreeData(classNode, subClass);
		}
		Collections.sort(clazz.getModel(), new ElementSorter());
		for (ModelData model : clazz.getModel()) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(model);
			classNode.add(node);
		}
	}
	
	class ElementSorter implements Comparator<AbstractModelElementData> {
		@Override
		public int compare(AbstractModelElementData o1, AbstractModelElementData o2) {
			if(o1.getLabel() == null)
				return 1;
			if(o2.getLabel() == null)
				return -1;
			return o1.getLabel().toLowerCase().compareTo(o2.getLabel().toLowerCase());
		}
	}
	
	private static File INPUT_FILE;
	
	public static void main(String[] args) {
		startAsApplication(args, true);
	}
	
	public static void startAsApplication(String[] args, final boolean exitJava) {
		if(args == null || args.length == 0 || args[0] == null || args[0].trim().isEmpty()) {
			JFileChooser chooser = new JFileChooser();
			int action = chooser.showOpenDialog(null);
			if(action == JFileChooser.APPROVE_OPTION)
				INPUT_FILE = chooser.getSelectedFile();
			else
				return;
		} else if(args.length > 1 && args[1] != null && args[1].trim().isEmpty() == false){
			INPUT_FILE = new File(args[1], args[0]);
		} else
			INPUT_FILE = new File(args[0]);
		if(INPUT_FILE.exists() == false) {
			System.err.println("File cannot be found: " + INPUT_FILE);
			return;
		}
		try {
			final CaseSerializer serializer = new CaseSerializer();
			final CaseData pfCase = serializer.readCase(INPUT_FILE);
			ModelDBData db = pfCase.getModelDb();
			ModelDBDialog dialog = new ModelDBDialog(null, STANDALONE_MODE, null, db, false, null) {
				protected void okPressed() {
					if(exitJava)
						System.exit(0);
					else
						dispose();
				}
			};
			JButton saveButton = new JButton("Save DB");
			saveButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						serializer.writeCase(pfCase, INPUT_FILE);
					} catch (Exception e2) {
						System.err.println("Cannot write file: " + INPUT_FILE);
						e2.printStackTrace();
					}
				}
			});
			dialog.addBottomComponent(saveButton);
			dialog.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					if(exitJava)
						System.exit(0);
//					else
//						dispose();
				}
			});
			dialog.showDialog(1000, 600);
		} catch (Exception e) {
			System.err.println("Cannot read file: " + INPUT_FILE);
			e.printStackTrace();
		}
	}
}
