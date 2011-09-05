package net.ee.pfanalyzer.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.Branch;
import net.ee.pfanalyzer.model.Bus;
import net.ee.pfanalyzer.model.Generator;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.PowerFlowCase;
import net.ee.pfanalyzer.model.diagram.DiagramSheetProperties;
import net.ee.pfanalyzer.ui.db.ModelDBDialog;
import net.ee.pfanalyzer.ui.diagram.PowerFlowDiagram;
import net.ee.pfanalyzer.ui.model.ElementPanelController;
import net.ee.pfanalyzer.ui.table.DataTable;
import net.ee.pfanalyzer.ui.util.ClosableTabbedPane;
import net.ee.pfanalyzer.ui.util.IActionUpdater;
import net.ee.pfanalyzer.ui.util.INetworkDataViewer;
import net.ee.pfanalyzer.ui.util.TabListener;

public class PowerFlowViewer implements INetworkElementSelectionListener {

	private PowerFlowCase powerFlowCase;
	
	private NetworkViewer viewer;
	private JPanel contentPane = new JPanel(new BorderLayout());
	private ClosableTabbedPane dataTabs;
	private JSplitPane horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	private JSplitPane verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	private NetworkViewerController viewerController;
	private ElementPanelController panelController;
	private ClosableTabbedPane diagramTabs = new ClosableTabbedPane();
	private ModelDBDialog modelDBDialog;
	private Object selection;

	private List<DiagramSheet> diagrams = new ArrayList<DiagramSheet>();
	private List<IActionUpdater> actionUpdater = new ArrayList<IActionUpdater>();
	private List<INetworkDataViewer> viewers = new ArrayList<INetworkDataViewer>();
	
	public PowerFlowViewer(PowerFlowCase caze) {
		this.powerFlowCase = caze;
		viewer = new NetworkViewer(getNetwork());
		viewerController = new NetworkViewerController(viewer);
		viewer.setController(viewerController);
		panelController = new ElementPanelController(getNetwork());
		JPanel dataPanel = new JPanel(new BorderLayout());
		dataPanel.add(panelController, BorderLayout.CENTER);
		dataTabs = new ClosableTabbedPane();
		horizontalSplitPane.setLeftComponent(viewerController);
		horizontalSplitPane.setRightComponent(dataPanel);
		horizontalSplitPane.setContinuousLayout(true);
		horizontalSplitPane.setOneTouchExpandable(true);
		horizontalSplitPane.setDividerLocation(300);
		horizontalSplitPane.setDividerSize(10);
		verticalSplitPane.setTopComponent(horizontalSplitPane);
		verticalSplitPane.setBottomComponent(dataTabs.getComponent());
		verticalSplitPane.setContinuousLayout(true);
		verticalSplitPane.setOneTouchExpandable(true);
		verticalSplitPane.setDividerLocation(450);
		verticalSplitPane.setDividerSize(10);
		contentPane.add(verticalSplitPane, BorderLayout.CENTER);
		
//		dataPanel.add(diagramTabs.getComponent(), BorderLayout.CENTER);
		
		addTable("Bus Data", "bus");
		addTable("Branch Data", "branch");
		addTable("Generator Data", "generator");

		addDiagram("Voltage Magnitude", "bus", "VM");

		getNetwork().addNetworkChangeListener(viewer);
		getNetwork().addNetworkChangeListener(panelController);
		addNetworkElementSelectionListener(viewer);
		addNetworkElementSelectionListener(panelController);
		addNetworkElementSelectionListener(this);
		
		diagramTabs.setTabListener(new TabListener() {
			@Override
			public boolean tabClosing(int tabIndex) {
				int choice = JOptionPane.showConfirmDialog(PowerFlowViewer.this.getContentPane(), 
						"Do you want to close this diagram?", "Close diagram", JOptionPane.YES_NO_OPTION);
				return choice == JOptionPane.YES_OPTION;
			}
			@Override
			public void tabClosed(int tabIndex) {
				diagrams.remove(tabIndex);
				fireActionUpdate();
			}
		});
	}
	
//	public void setNetwork(Network network) {
//		this.network = network;
//		for (INetworkDataViewer viewer : viewers) {
//			viewer.setData(network);
//			viewer.refresh();
//		}
//	}
	
	private void addTable(String label, String elementID) {
		INetworkDataViewer table = new DataTable(elementID);
		table.setData(getNetwork());
		table.refresh();
		viewers.add(table);
		dataTabs.addTab(label, new JScrollPane(table.getComponent()));
		addNetworkElementSelectionListener(table);
		getNetwork().addNetworkChangeListener(table);
	}
	
	private void addDiagram(String label, String elementID, String parameterID) {
		PowerFlowDiagram diagram = new PowerFlowDiagram(elementID, parameterID);
		diagram.setTitle(label);
		diagram.setData(getNetwork());
		diagram.refresh();
		viewers.add(diagram);
		dataTabs.addTab(label, new JScrollPane(diagram.getComponent()));
		getNetwork().addNetworkChangeListener(diagram);
	}
	
	public void addDiagramSheet(DiagramSheetProperties props) {
		DiagramSheet sheet = new DiagramSheet(props);
		diagrams.add(sheet);
		diagramTabs.addTab(props.getTitle(), new JScrollPane(sheet));
		diagramTabs.selectLastTab();
	}
	
	public void setCurrentDiagramSheet(DiagramSheetProperties props) {
		if(getCurrentSheet() == null)
			return;
		getCurrentSheet().setProperties(props);
		diagramTabs.setTitleAt(diagramTabs.getSelectedIndex(), props.getTitle());
	}
	
	public DiagramSheetProperties getCurrentDiagramSheetProperties() {
		if(getCurrentSheet() == null)
			return null;
		return getCurrentSheet().getProperties();
	}
	
	private DiagramSheet getCurrentSheet() {
		if(diagramTabs.getSelectedIndex() == -1)
			return null;
		return diagrams.get(diagramTabs.getSelectedIndex());
	}
	
	public boolean hasDiagramSheet() {
		return diagramTabs.hasTabs();
	}
	
	public PowerFlowCase getPowerFlowCase() {
		return powerFlowCase;
	}
	
	public Network getNetwork() {
		return powerFlowCase.getNetwork();
	}

	public NetworkViewerController getViewerController() {
		return viewerController;
	}

	public ElementPanelController getPanelController() {
		return panelController;
	}
	
	public JComponent getContentPane() {
		return contentPane;
	}
	
	public void showModelDBDialog() {
		if(modelDBDialog != null) {
			modelDBDialog.setVisible(true);
			modelDBDialog.toFront();
		} else {
			modelDBDialog = new ModelDBDialog((Frame) SwingUtilities.getWindowAncestor(getContentPane()), 
					getPowerFlowCase().getModelDB().getData());
			AbstractNetworkElement selectedElement = (AbstractNetworkElement) 
					(selection  instanceof AbstractNetworkElement ? selection : null);
			modelDBDialog.setSelectedElement(selectedElement);
			addNetworkElementSelectionListener(modelDBDialog);
		}
	}

	@Override
	public void selectionChanged(Object data) {
		selection = data;
		if(data == null)
			return;
		if(data instanceof Bus)
			dataTabs.setSelectedIndex(0);
		else if(data instanceof Branch)
			dataTabs.setSelectedIndex(1);
		else if(data instanceof Generator)
			dataTabs.setSelectedIndex(2);
	}

	public void addNetworkElementSelectionListener(INetworkElementSelectionListener listener) {
		NetworkElementSelectionManager.getInstance().addNetworkElementSelectionListener(listener);
	}
	
	public void removeNetworkElementSelectionListener(INetworkElementSelectionListener listener) {
		NetworkElementSelectionManager.getInstance().removeNetworkElementSelectionListener(listener);
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
	
	class DiagramSheet extends Box {

		private DiagramSheetProperties sheetProps;
		private List<PowerFlowDiagram> diagrams = new ArrayList<PowerFlowDiagram>();
		
		public DiagramSheet(DiagramSheetProperties props) {
			super(BoxLayout.Y_AXIS);
			setProperties(props);
		}
		
		public void setProperties(DiagramSheetProperties props) {
//			for (PowerFlowDiagram diagram : diagrams)
//				diagram.setNetworkElementSelectionListener(null);
			diagrams.clear();
			removeAll();
			sheetProps = props;
//			for (int i = 0; i < props.getBusDataFieldsCount(); i++) {
//				if(props.hasBusDataDiagram(i))
//					addDiagram(new BusDataDiagram(network, i));
//			}
//			for (int i = 0; i < props.getBranchDataFieldsCount(); i++) {
//				if(props.hasBranchDataDiagram(i))
//					addDiagram(new BranchDataDiagram(network, i));
//			}
//			for (int i = 0; i < props.getGeneratorDataFieldsCount(); i++) {
//				if(props.hasGeneratorDataDiagram(i))
//					addDiagram(new GeneratorDataDiagram(network, i));
//			}
			revalidate();
		}
		
		public void addDiagram(PowerFlowDiagram diagram) {
			diagrams.add(diagram);
			add(diagram);
//			diagram.setNetworkElementSelectionListener(PowerFlowViewer.this);
		}
		
		public DiagramSheetProperties getProperties() {
			return sheetProps;
		}
	}
}
