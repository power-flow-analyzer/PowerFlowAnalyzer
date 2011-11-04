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
import javax.swing.SwingUtilities;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.PowerFlowCase;
import net.ee.pfanalyzer.model.data.DataViewerData;
import net.ee.pfanalyzer.model.diagram.DiagramSheetProperties;
import net.ee.pfanalyzer.ui.dataviewer.DataViewerConfiguration;
import net.ee.pfanalyzer.ui.dataviewer.DataViewerContainer;
import net.ee.pfanalyzer.ui.dataviewer.DataViewerDialog;
import net.ee.pfanalyzer.ui.dataviewer.INetworkDataViewer;
import net.ee.pfanalyzer.ui.dataviewer.SelectViewerDialog;
import net.ee.pfanalyzer.ui.diagram.PowerFlowDiagram;
import net.ee.pfanalyzer.ui.model.ElementPanelController;
import net.ee.pfanalyzer.ui.table.DataTable;
import net.ee.pfanalyzer.ui.util.ClosableTabbedPane;
import net.ee.pfanalyzer.ui.util.IActionUpdater;
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
		verticalSplitPane.setContinuousLayout(true);
		verticalSplitPane.setOneTouchExpandable(true);
		verticalSplitPane.setDividerSize(10);
		add(networkDescriptionLabel, BorderLayout.NORTH);
		add(verticalSplitPane, BorderLayout.CENTER);
		
		networkDescriptionLabel.setText(network.getDescription());
		networkDescriptionLabel.setFont(networkDescriptionLabel.getFont().deriveFont(14f));
		
		// add data viewers
		for (DataViewerData viewerData : getPowerFlowCase().getDataViewerData()) {
			addDataViewer(new DataViewerConfiguration(viewerData));
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
				removeViewer(viewer, true);
				fireActionUpdate();
			}
			@Override
			public void tabOpened(int tabIndex) {
			}
		});
	}
	
	private void removeViewer(INetworkDataViewer viewer, boolean removeFromCase) {
		removeNetworkElementSelectionListener(viewer);
		getNetwork().removeNetworkChangeListener(viewer);
		if(removeFromCase)
			getPowerFlowCase().getDataViewerData().remove(viewer.getViewerConfiguration().getData());
	}
	
	public void dispose() {
		getNetwork().removeNetworkChangeListener(networkViewer);
		getNetwork().removeNetworkChangeListener(panelController);
		removeNetworkElementSelectionListener(networkViewer);
		removeNetworkElementSelectionListener(panelController);
		removeNetworkElementSelectionListener(this);
		for (INetworkDataViewer viewer : viewers) {
			removeViewer(viewer, false);
		}
	}
	
	public void addViewer() {
		SelectViewerDialog dialog1 = new SelectViewerDialog(SwingUtilities.getWindowAncestor(this));
		dialog1.showDialog(-1, -1);
		if(dialog1.isCancelPressed())
			return;
		DataViewerConfiguration configuration = new DataViewerConfiguration(dialog1.getSelectedViewer());
		DataViewerDialog dialog2 = new DataViewerDialog(SwingUtilities.getWindowAncestor(this), 
				"Create Table", configuration, getPowerFlowCase());
		dialog2.showDialog(-1, -1);
		if(dialog2.isCancelPressed())
			return;
		addDataViewer(configuration);
		dataTabs.selectLastTab();
		getPowerFlowCase().getDataViewerData().add(configuration.getData());
	}
	
	private void addDataViewer(DataViewerConfiguration viewerData) {
		INetworkDataViewer viewer = null;
		if("viewer.table.type_filter".equals(viewerData.getModelID()))
			viewer = new DataTable(viewerData);
//		else if("viewer.diagram.bar".equals(viewerData.getModelID()))
//			viewer = new PowerFlowDiagram(viewerData);
		if(viewer == null)
			return;
		viewer.setData(getNetwork());
		viewer.refresh();
		viewers.add(viewer);
		dataTabs.addTab(viewerData.getTitle(), new DataViewerContainer(viewer, this));
		addNetworkElementSelectionListener(viewer);
		getNetwork().addNetworkChangeListener(viewer);
		if(verticalSplitPane.getBottomComponent() == null)
			verticalSplitPane.setBottomComponent(dataTabs.getComponent());
		if(dataTabs.getComponent().getHeight() == 0) {
			verticalSplitPane.setDividerLocation(400);
		}
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
			for (int i = 0; i < viewers.size(); i++) {
				if(modelID.startsWith(viewers.get(i).getViewerConfiguration().getElementFilter())) {
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

	
	public void updateTabTitles() {
		// update tab titles for viewers
		for (int i = 0; i < viewers.size(); i++) {
			dataTabs.setTitleAt(i, viewers.get(i).getViewerConfiguration().getTitle());
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
