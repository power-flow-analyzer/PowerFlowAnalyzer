package net.ee.pfanalyzer.ui;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
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
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.ui.util.ClosableTabbedPane;
import net.ee.pfanalyzer.ui.util.IActionUpdater;
import net.ee.pfanalyzer.ui.util.TabListener;
import net.ee.pfanalyzer.ui.viewer.DataViewerConfiguration;
import net.ee.pfanalyzer.ui.viewer.DataViewerContainer;
import net.ee.pfanalyzer.ui.viewer.DataViewerParameterDialog;
import net.ee.pfanalyzer.ui.viewer.INetworkDataViewer;
import net.ee.pfanalyzer.ui.viewer.SelectViewerDialog;
import net.ee.pfanalyzer.ui.viewer.diagram.PowerFlowDiagram;
import net.ee.pfanalyzer.ui.viewer.element.ElementViewer;
import net.ee.pfanalyzer.ui.viewer.network.BusBarViewer;
import net.ee.pfanalyzer.ui.viewer.network.ContourDiagramViewer;
import net.ee.pfanalyzer.ui.viewer.network.NetworkMapViewer;
import net.ee.pfanalyzer.ui.viewer.table.DataTable;

public class NetworkViewer extends JPanel implements INetworkElementSelectionListener {

	private PowerFlowCase powerFlowCase;
	private Network network;
	
	private NetworkElementSelectionManager selectionManager;
	private ViewerTabbedPane bottomViewers, leftViewers, rightViewers;
	private List<ViewerFrame> viewerFrames = new ArrayList<ViewerFrame>();
	private JSplitPane horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	private JSplitPane verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

	private List<IActionUpdater> actionUpdater = new ArrayList<IActionUpdater>();
	private JLabel networkDescriptionLabel = new JLabel();
	
	public NetworkViewer(PowerFlowCase caze, Network network) {
		super(new BorderLayout());
		this.powerFlowCase = caze;
		this.network = network;
		selectionManager = new NetworkElementSelectionManager();
		bottomViewers = new ViewerTabbedPane();
		leftViewers = new ViewerTabbedPane();
		rightViewers = new ViewerTabbedPane();
		horizontalSplitPane.setLeftComponent(leftViewers.getComponent());
		horizontalSplitPane.setRightComponent(rightViewers.getComponent());
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
			addDataViewer(new DataViewerConfiguration(viewerData), false);
		}

		addNetworkElementSelectionListener(this);
	}
	
	public void dispose() {
		removeNetworkElementSelectionListener(this);
		leftViewers.dispose();
		bottomViewers.dispose();
		rightViewers.dispose();
		for (ViewerFrame frame : viewerFrames) {
			frame.closeFrame(false);
		}
		viewerFrames.clear();
	}
	
	public void addViewer() {
		SelectViewerDialog dialog1 = new SelectViewerDialog(SwingUtilities.getWindowAncestor(this));
		dialog1.showDialog(-1, -1);
		if(dialog1.isCancelPressed())
			return;
		DataViewerConfiguration configuration = new DataViewerConfiguration(dialog1.getSelectedViewer());
		DataViewerParameterDialog dialog2 = new DataViewerParameterDialog(SwingUtilities.getWindowAncestor(this), 
				"Add viewer", configuration, getPowerFlowCase(), true);
		dialog2.showDialog(-1, -1);
		if(dialog2.isCancelPressed())
			return;
		addDataViewer(configuration, true);
		getPowerFlowCase().getDataViewerData().add(configuration.getData());
	}
	
	private void addDataViewer(DataViewerConfiguration viewerData, boolean selectTab) {
		INetworkDataViewer viewer = null;
		if(DataTable.VIEWER_ID.equals(viewerData.getModelID()))
			viewer = new DataTable(viewerData, this);
		else if(ElementViewer.VIEWER_ID.equals(viewerData.getModelID()))
			viewer = new ElementViewer(getNetwork(), viewerData, this);
//		else if("viewer.diagram.bar".equals(viewerData.getModelID()))
//			viewer = new PowerFlowDiagram(viewerData);
		else if(NetworkMapViewer.VIEWER_ID.equals(viewerData.getModelID()))
			viewer = new NetworkMapViewer(getNetwork(), viewerData, this);
		else if(BusBarViewer.VIEWER_ID.equals(viewerData.getModelID()))
			viewer = new BusBarViewer(getNetwork(), viewerData, this);
		else if(ContourDiagramViewer.VIEWER_ID.equals(viewerData.getModelID()))
			viewer = new ContourDiagramViewer(getNetwork(), viewerData, this);
		if(viewer == null)
			return;
		viewer.setData(getNetwork());
		viewer.refresh();
		ViewerTabbedPane tabPane = getViewerTab(viewer);
		if(tabPane == null) {
			ViewerFrame frame = new ViewerFrame(viewer);
			viewerFrames.add(frame);
//			frame.toFront();
		} else {
			tabPane.addViewer(viewer);
			if(selectTab)
				tabPane.selectLastTab();
			if(verticalSplitPane.getBottomComponent() == null)
				verticalSplitPane.setBottomComponent(bottomViewers.getComponent());
			if(bottomViewers.getComponent().getHeight() == 0) {
				verticalSplitPane.setDividerLocation(400);
			}
		}
		addNetworkElementSelectionListener(viewer);
		getNetwork().addNetworkChangeListener(viewer);
	}
	
	private ViewerTabbedPane getViewerTab(INetworkDataViewer viewer) {
		String position = viewer.getViewerConfiguration().getTextParameter("POSITION", "bottom");
		ViewerTabbedPane tabPane;
		if(position.equals("bottom"))
			tabPane = bottomViewers;
		else if(position.equals("left"))
			tabPane = leftViewers;
		else if(position.equals("right"))
			tabPane = rightViewers;
		else if(position.equals("free"))
			tabPane = null;
		else
			tabPane = bottomViewers;
		return tabPane;
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
	
	public void setNetwork(Network newNetwork) {
		Network oldNetwork = getNetwork();
		this.network = newNetwork;
		leftViewers.changeNetwork(oldNetwork, newNetwork);
		rightViewers.changeNetwork(oldNetwork, newNetwork);
		bottomViewers.changeNetwork(oldNetwork, newNetwork);
		for (ViewerFrame frame : viewerFrames) {
			frame.changeNetwork(oldNetwork, newNetwork);
		}
		updateNetworkDescription();
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
			leftViewers.selectViewer((AbstractNetworkElement) data);
			bottomViewers.selectViewer((AbstractNetworkElement) data);
			rightViewers.selectViewer((AbstractNetworkElement) data);
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
		leftViewers.updateTabs("left");
		bottomViewers.updateTabs("bottom");
		rightViewers.updateTabs("right");
		updateFrames();
		fireActionUpdate();
	}
	
	private void updateFrames() {
		boolean updateAgain = false;
		for (ViewerFrame frame : viewerFrames) {
			if(frame.updateFrame() == false) {
				updateAgain = true;
				break;
			}
		}
		if(updateAgain)
			updateFrames();
	}
	
	private void removeViewer(INetworkDataViewer viewer, boolean removeFromCase) {
		removeNetworkElementSelectionListener(viewer);
		getNetwork().removeNetworkChangeListener(viewer);
		if(removeFromCase)
			getPowerFlowCase().getDataViewerData().remove(viewer.getViewerConfiguration().getData());
	}
	
	class ViewerTabbedPane extends ClosableTabbedPane {
		private List<INetworkDataViewer> viewers = new ArrayList<INetworkDataViewer>();
		ViewerTabbedPane() {
			setTabListener(new TabListener() {
				@Override
				public boolean tabClosing(int tabIndex) {
					int choice = JOptionPane.showConfirmDialog(NetworkViewer.this, 
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
		
		private boolean selectViewer(AbstractNetworkElement element) {
			String modelID = element.getModelID();
			if(modelID == null || modelID.isEmpty())
				return false;
			for (int i = 0; i < viewers.size(); i++) {
				String elementFilter = viewers.get(i).getViewerConfiguration().getElementFilter();
				if(elementFilter.length() > 0 && modelID.startsWith(elementFilter)) {
					setSelectedIndex(i);
					return true;
				}
			}
			return false;
		}
		
		private void addViewer(INetworkDataViewer viewer) {
			viewers.add(viewer);
			addTab(viewer.getViewerConfiguration().getTitle(), 
					new DataViewerContainer(viewer, NetworkViewer.this));
			
		}
		
		public void dispose() {
			for (INetworkDataViewer viewer : viewers) {
				viewer.dispose();
				removeViewer(viewer, false);
			}
		}
		
		public void updateTabs(String tabPosition) {
			boolean updateAgain = false;
			// update tab titles for viewers
			for (int i = 0; i < viewers.size(); i++) {
				DataViewerConfiguration conf = viewers.get(i).getViewerConfiguration();
				String position = conf.getTextParameter("POSITION", "bottom");
				// check if position is still correct
				if(position.equals(tabPosition)) {
					setTitleAt(i, conf.getTitle());
				} else {
					removeViewer(viewers.get(i), false);
					viewers.remove(i);
					getTabbedPane().remove(i);
					addDataViewer(conf, true);
					updateAgain = true;
					break;
				}
			}
			if(updateAgain)
				updateTabs(tabPosition);
		}
		
		private void changeNetwork(Network oldNetwork, Network newNetwork) {
			for (INetworkDataViewer viewer : viewers) {
				oldNetwork.removeNetworkChangeListener(viewer);
				viewer.setData(newNetwork);
				newNetwork.addNetworkChangeListener(viewer);
			}
		}
	}
	
	public static String getFrameTitle(DataViewerConfiguration viewerConfiguration, Network network) {
		return viewerConfiguration.getTitle() + " - " + network.getDisplayName();
	}
	
	class ViewerFrame extends JFrame {
		
		private INetworkDataViewer viewer;
		
		ViewerFrame(INetworkDataViewer viewer) {
			super(getFrameTitle(viewer.getViewerConfiguration(), getNetwork()));
			this.viewer = viewer;
			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					int choice = JOptionPane.showConfirmDialog(ViewerFrame.this, 
							"Do you want to remove this viewer?", "Close viewer", JOptionPane.YES_NO_OPTION);
					if(choice == JOptionPane.YES_OPTION) {
						closeFrame(true);
					}
				}
				@Override
				public void windowClosed(WindowEvent e) {
				}
			});
			addComponentListener(new ComponentAdapter() {
				public void componentResized(ComponentEvent e) {
					ViewerFrame.this.viewer.getViewerConfiguration().setParameter(
							ModelDBUtils.WIDTH_PARAMETER, getWidth());
					ViewerFrame.this.viewer.getViewerConfiguration().setParameter(
							ModelDBUtils.HEIGHT_PARAMETER, getHeight());
				}
			});
			
			getContentPane().add(new DataViewerContainer(viewer, NetworkViewer.this));
			pack();
			int width = viewer.getViewerConfiguration().getIntParameter(ModelDBUtils.WIDTH_PARAMETER, 300);
			int height = viewer.getViewerConfiguration().getIntParameter(ModelDBUtils.HEIGHT_PARAMETER, 300);
			setSize(width, height);
			setVisible(true);
		}
		
		private void changeNetwork(Network oldNetwork, Network newNetwork) {
			oldNetwork.removeNetworkChangeListener(viewer);
			viewer.setData(newNetwork);
			newNetwork.addNetworkChangeListener(viewer);
			updateFrame();
		}
		
		private void closeFrame(boolean removeFromCase) {
			viewer.dispose();
			removeViewer(viewer, removeFromCase);
			if(removeFromCase)
				viewerFrames.remove(ViewerFrame.this);
			setVisible(false);
			dispose();
			fireActionUpdate();
		}
		
		public boolean updateFrame() {
			DataViewerConfiguration conf = viewer.getViewerConfiguration();
			String position = conf.getTextParameter("POSITION", "bottom");
			// check if position is "free"
			if(position.equals("free")) {
				setTitle(getFrameTitle(conf, getNetwork()));
				return true;
			} else {
				closeFrame(false);
				viewerFrames.remove(this);
				addDataViewer(conf, true);
				return false;
			}
		}
		
		NetworkViewer getPowerFlowViewer() {
			return NetworkViewer.this;
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
