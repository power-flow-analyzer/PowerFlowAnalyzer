package net.ee.pfanalyzer.ui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.PowerFlowCase;
import net.ee.pfanalyzer.model.data.DataViewerData;
import net.ee.pfanalyzer.model.data.DataViewerType;
import net.ee.pfanalyzer.model.diagram.DiagramSheetProperties;
import net.ee.pfanalyzer.ui.dataviewer.DataViewerContainer;
import net.ee.pfanalyzer.ui.dataviewer.DataViewerDialog;
import net.ee.pfanalyzer.ui.dataviewer.INetworkDataViewer;
import net.ee.pfanalyzer.ui.diagram.PowerFlowDiagram;
import net.ee.pfanalyzer.ui.model.ElementPanelController;
import net.ee.pfanalyzer.ui.table.DataTable;
import net.ee.pfanalyzer.ui.util.ClosableTabbedPane;
import net.ee.pfanalyzer.ui.util.IActionUpdater;
import net.ee.pfanalyzer.ui.util.SwingUtils;
import net.ee.pfanalyzer.ui.util.TabListener;

public class PowerFlowViewer extends JPanel implements INetworkElementSelectionListener {

	private PowerFlowCase powerFlowCase;
	private Network network;
	
	private NetworkViewer networkViewer;
	private NetworkElementSelectionManager selectionManager;
	private ClosableTabbedPane dataTabs;
	private JSplitPane horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	private JSplitPane verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	private NetworkViewerController viewerController;
	private ElementPanelController panelController;

	private List<IActionUpdater> actionUpdater = new ArrayList<IActionUpdater>();
	private List<INetworkDataViewer> viewers = new ArrayList<INetworkDataViewer>();
	private JLabel networkDescriptionLabel = new JLabel();
	
	public PowerFlowViewer(PowerFlowCase caze, Network network) {
		super(new BorderLayout());
		this.powerFlowCase = caze;
		this.network = network;
		selectionManager = new NetworkElementSelectionManager();
		networkViewer = new NetworkViewer(getNetwork());
		viewerController = new NetworkViewerController(networkViewer);
		networkViewer.setController(viewerController);
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
		add(networkDescriptionLabel, BorderLayout.NORTH);
		add(verticalSplitPane, BorderLayout.CENTER);
		
		networkDescriptionLabel.setText(network.getDescription());
		networkDescriptionLabel.setFont(networkDescriptionLabel.getFont().deriveFont(14f));
		
		// add data viewers
		for (DataViewerData viewerData : getPowerFlowCase().getDataViewerData()) {
			addDataViewer(viewerData);
		}

		getNetwork().addNetworkChangeListener(networkViewer);
		getNetwork().addNetworkChangeListener(panelController);
		addNetworkElementSelectionListener(networkViewer);
		addNetworkElementSelectionListener(panelController);
		addNetworkElementSelectionListener(this);
		
		dataTabs.setTabListener(new TabListener() {
			@Override
			public boolean tabClosing(int tabIndex) {
				int choice = JOptionPane.showConfirmDialog(PowerFlowViewer.this, 
						"Do you want to remove this viewer?", "Close viewer", JOptionPane.YES_NO_OPTION);
				return choice == JOptionPane.YES_OPTION;
			}
			@Override
			public void tabClosed(int tabIndex) {
				INetworkDataViewer viewer = viewers.get(tabIndex);
				viewers.remove(tabIndex);
				removeViewer(viewer);
				fireActionUpdate();
			}
			@Override
			public void tabOpened(int tabIndex) {
			}
		});
	}
	
	private void removeViewer(INetworkDataViewer viewer) {
		removeNetworkElementSelectionListener(viewer);
		getNetwork().removeNetworkChangeListener(viewer);
		getPowerFlowCase().getDataViewerData().remove(viewer.getViewerData());
	}
	
	public void dispose() {
		getNetwork().removeNetworkChangeListener(networkViewer);
		getNetwork().removeNetworkChangeListener(panelController);
		removeNetworkElementSelectionListener(networkViewer);
		removeNetworkElementSelectionListener(panelController);
		removeNetworkElementSelectionListener(this);
		for (INetworkDataViewer viewer : viewers) {
			removeViewer(viewer);
		}
	}
	
	public void addTable() {
		DataViewerData viewerData = new DataViewerData();
		viewerData.setType(DataViewerType.TABLE);
		DataViewerDialog dialog = new DataViewerDialog(SwingUtils.getTopLevelFrame(this), 
				"Create Table", viewerData);
		dialog.showDialog(-1, -1);
		if(dialog.isCancelPressed())
			return;
		addDataViewer(viewerData);
		dataTabs.selectLastTab();
		getPowerFlowCase().getDataViewerData().add(viewerData);
	}
	
	public void addDiagram() {
		DataViewerData viewerData = new DataViewerData();
		viewerData.setType(DataViewerType.DIAGRAM);
		DataViewerDialog dialog = new DataViewerDialog(SwingUtils.getTopLevelFrame(this), 
				"Create Diagram", viewerData);
		dialog.showDialog(-1, -1);
		if(dialog.isCancelPressed())
			return;
		addDataViewer(viewerData);
		dataTabs.selectLastTab();
		getPowerFlowCase().getDataViewerData().add(viewerData);
	}
	
	private void addDataViewer(DataViewerData viewerData) {
		INetworkDataViewer viewer = null;
		if(DataViewerType.TABLE.equals(viewerData.getType()))
			viewer = new DataTable(viewerData);
		else if(DataViewerType.DIAGRAM.equals(viewerData.getType()))
			viewer = new PowerFlowDiagram(viewerData);
		if(viewer == null)
			return;
		viewer.setData(getNetwork());
		viewer.refresh();
		viewers.add(viewer);
		dataTabs.addTab(viewerData.getTitle(), new DataViewerContainer(viewer));
		addNetworkElementSelectionListener(viewer);
		getNetwork().addNetworkChangeListener(viewer);
	}
	
//	private void addDiagram(String label, String elementID, String parameterID) {
//		PowerFlowDiagram diagram = new PowerFlowDiagram(elementID, parameterID);
//		diagram.setTitle(label);
//		diagram.setData(getNetwork());
//		diagram.refresh();
//		viewers.add(diagram);
//		dataTabs.addTab(label, new JScrollPane(diagram.getComponent()));
//		getNetwork().addNetworkChangeListener(diagram);
//	}
	
//	public void addDiagramSheet(DiagramSheetProperties props) {
//		DiagramSheet sheet = new DiagramSheet(props);
//		diagrams.add(sheet);
//		diagramTabs.addTab(props.getTitle(), new JScrollPane(sheet));
//		diagramTabs.selectLastTab();
//	}
	
//	public void setCurrentDiagramSheet(DiagramSheetProperties props) {
//		if(getCurrentSheet() == null)
//			return;
//		getCurrentSheet().setProperties(props);
//		diagramTabs.setTitleAt(diagramTabs.getSelectedIndex(), props.getTitle());
//	}
//	
//	public DiagramSheetProperties getCurrentDiagramSheetProperties() {
//		if(getCurrentSheet() == null)
//			return null;
//		return getCurrentSheet().getProperties();
//	}
	
//	private DiagramSheet getCurrentSheet() {
//		if(diagramTabs.getSelectedIndex() == -1)
//			return null;
//		return diagrams.get(diagramTabs.getSelectedIndex());
//	}
//	
//	public boolean hasDiagramSheet() {
//		return diagramTabs.hasTabs();
//	}
	
	public PowerFlowCase getPowerFlowCase() {
		return powerFlowCase;
	}
	
	public Network getNetwork() {
		return network;
	}

	public NetworkViewerController getViewerController() {
		return viewerController;
	}

	public ElementPanelController getPanelController() {
		return panelController;
	}
	
	public void updateNetworkDescription() {
		networkDescriptionLabel.setText(network.getDescription());
	}
	
	public NetworkElementSelectionManager getSelectionManager() {
		return selectionManager;
	}

	@Override
	public void selectionChanged(Object data) {
		if(data == null)
			return;
		// show tab which contains the new selection
		if(data instanceof AbstractNetworkElement) {
			String modelID = ((AbstractNetworkElement) data).getModelID();
			if(modelID == null || modelID.isEmpty())
				return;
			for (int i = 0; i < getPowerFlowCase().getDataViewerData().size(); i++) {
				if(modelID.startsWith(getPowerFlowCase().getDataViewerData().get(i).getElementFilter())) {
					dataTabs.setSelectedIndex(i);
					break;
				}
			}
		}
	}
	
	public void addNetworkElementSelectionListener(INetworkElementSelectionListener listener) {
		getSelectionManager().addNetworkElementSelectionListener(listener);
	}
	
	public void removeNetworkElementSelectionListener(INetworkElementSelectionListener listener) {
		getSelectionManager().removeNetworkElementSelectionListener(listener);
	}

	public void addActionUpdateListener(IActionUpdater listener) {
		actionUpdater.add(listener);
		getSelectionManager().addActionUpdateListener(listener);
	}
	
	public void removeActionUpdateListener(IActionUpdater listener) {
		actionUpdater.remove(listener);
		getSelectionManager().removeActionUpdateListener(listener);
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
