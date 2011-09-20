package net.ee.pfanalyzer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ToolTipManager;
import javax.swing.border.EmptyBorder;

import net.ee.pfanalyzer.io.MatpowerGUIServer;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.PowerFlowCase;
import net.ee.pfanalyzer.model.data.NetworkData;
import net.ee.pfanalyzer.preferences.IPreferenceConstants;
import net.ee.pfanalyzer.preferences.Preferences;
import net.ee.pfanalyzer.preferences.PreferencesInitializer;
import net.ee.pfanalyzer.ui.NetworkContainer;
import net.ee.pfanalyzer.ui.PowerFlowViewer;
import net.ee.pfanalyzer.ui.dialog.CaseCalculationDialog;
import net.ee.pfanalyzer.ui.dialog.CaseSelectionDialog;
import net.ee.pfanalyzer.ui.util.ClosableTabbedPane;
import net.ee.pfanalyzer.ui.util.IActionUpdater;
import net.ee.pfanalyzer.ui.util.TabListener;

import com.mathworks.jmi.Matlab;
import com.mathworks.jmi.MatlabException;


public class PowerFlowAnalyzer extends JFrame implements ActionListener, IActionUpdater, IPreferenceConstants {

	public final static int APPLICATION_ENVIRONMENT = 0;
	public final static int MATLAB_ENVIRONMENT = 1;
	
	private final static String ACTION_CASE_NEW = "action.case.new";
	private final static String ACTION_CASE_OPEN = "action.case.open";
//	private final static String ACTION_CASE_EDIT = "action.case.edit";
//	private final static String ACTION_CASE_REMOVE = "action.case.remove";
	private final static String ACTION_CASE_SAVE = "action.case.save";
//	private final static String ACTION_CASE_LAYOUT = "action.case.layout";
	
//	private final static String ACTION_DIAGRAM_ADD = "action.diagram.add";
//	private final static String ACTION_DIAGRAM_EDIT = "action.diagram.edit";
//	private final static String ACTION_DIAGRAM_REMOVE = "action.diagram.remove";

	private final static String ACTION_MAP_PROPERTIES = "action.map.properties";
//	private final static String ACTION_PANEL_PROPERTIES = "action.panel.properties";
	private final static String ACTION_MODEL_DB_PROPERTIES = "action.model.db.properties";
//	private final static String ACTION_APP_PROPERTIES = "action.app.properties";
	private final static String ACTION_CASE_CALCULATE = "action.case.calculate";
	
	private final static String ACTION_SELECT_PREVIOUS = "action.select.previous";
	private final static String ACTION_SELECT_NEXT = "action.select.next";
	
	private static PowerFlowAnalyzer INSTANCE;
	
	private MatpowerGUIServer server;
	
	private Map<String, Boolean> success = new HashMap<String, Boolean>();
	private ClosableTabbedPane casesParent;
	private CaseSelectionDialog caseDialog;
	String nextCase;
	private List<PowerFlowCase> cases = new ArrayList<PowerFlowCase>();
	private Map<String, JButton> toolbarButtons = new HashMap<String, JButton>();
	private String workingDirectory;

	private boolean largeIcons = Preferences.getBooleanProperty(PROPERTY_UI_LARGE_ICONS);
//	private boolean showSuccessMessage = Preferences.getBooleanProperty(PROPERTY_UI_SHOW_SUCCESS_MESSAGE);
	
	private final int environment;
	
	public static void main(String[] args) {
		PowerFlowAnalyzer app = new PowerFlowAnalyzer(APPLICATION_ENVIRONMENT);
		app.setWorkingDirectory(System.getProperty("user.home"));
	}
	
	public static PowerFlowAnalyzer getInstance() {
		return INSTANCE;
	}
	
	public PowerFlowAnalyzer(int environment) {
		super();
		this.environment = environment;
		INSTANCE = this;
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		ToolTipManager.sharedInstance().setInitialDelay(100);// reduce delay in showing tooltips
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if(JOptionPane.showConfirmDialog(PowerFlowAnalyzer.this, "Exit Power Flow Analyzer?", 
						"Confirm Exit", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					closeViewer();
					stopServer();
					destroyViewer();
				}
			}
		});
		
		getContentPane().setLayout(new BorderLayout());
		
		casesParent = new ClosableTabbedPane();
		casesParent.setTabListener(new TabListener() {
			@Override
			public boolean tabClosing(int tabIndex) {
				int choice = JOptionPane.showConfirmDialog(PowerFlowAnalyzer.this, 
						"Do you want to close this case?", "Close case", JOptionPane.YES_NO_OPTION);
				return choice == JOptionPane.YES_OPTION;
			}
			@Override
			public void tabClosed(int tabIndex) {
				NetworkContainer container = cases.get(tabIndex).getViewer();
				container.getSelectionManager().removeActionUpdateListener(PowerFlowAnalyzer.this);
				container.dispose();
				cases.remove(tabIndex);
				updateToolbarButtons();
			}
			@Override
			public void tabOpened(int tabIndex) {
				updateToolbarButtons();
			}
		});
		getContentPane().add(casesParent.getComponent(), BorderLayout.CENTER);
		
		JToolBar toolbar = new JToolBar();
		getContentPane().add(toolbar, BorderLayout.NORTH);
		toolbar.add(createToolbarButton(ACTION_CASE_NEW, "Create a new case", "page_add.png", "Create case"));
		toolbar.add(createToolbarButton(ACTION_CASE_OPEN, "Open an existing case", "folder.png", "Open case"));
//		toolbar.add(createToolbarButton(ACTION_CASE_EDIT, "Edit this case", "report_edit.png", "Edit case"));
		toolbar.add(createToolbarButton(ACTION_CASE_SAVE, "Save this case", "save_as.png", "Save case"));
//		toolbar.add(createToolbarButton(ACTION_CASE_REMOVE, "Remove this case", "report_delete.png", "Remove Case"));
		toolbar.addSeparator();
		toolbar.add(createToolbarButton(ACTION_CASE_CALCULATE, "Calculate power flow", "calculator.png", "Calculate power flow"));
		toolbar.addSeparator();
//		toolbar.add(createToolbarButton(ACTION_DIAGRAM_ADD, "Create a new diagram sheet", "chart_bar_add.png", "New Diagram"));
//		toolbar.add(createToolbarButton(ACTION_DIAGRAM_EDIT, "Edit diagram sheet", "chart_bar_edit.png", "Edit Diagram"));
//		toolbar.add(createToolbarButton(ACTION_DIAGRAM_REMOVE, "Remove diagram", "chart_bar_delete.png", "Remove Diagram"));
		toolbar.add(createToolbarButton(ACTION_MAP_PROPERTIES, "Edit map settings", "map_edit.png", "Edit map"));
//		toolbar.add(createToolbarButton(ACTION_PANEL_PROPERTIES, "Edit model view", "table_edit.png", "Edit model view"));
//		toolbar.add(createToolbarButton(ACTION_CASE_LAYOUT, "Change layout", "grid.png", "Change layout"));
//		toolbar.addSeparator();
//		toolbar.add(createToolbarButton(ACTION_APP_PROPERTIES, "Edit program settings", "widgets.png", "App settings"));
		toolbar.addSeparator();
		toolbar.add(createToolbarButton(ACTION_MODEL_DB_PROPERTIES, "Open Model Database", "database.png", "Model DB"));
		toolbar.addSeparator();
		toolbar.add(createToolbarButton(ACTION_SELECT_PREVIOUS, "Show previous selection", "resultset_previous.png", "Previous"));
		toolbar.add(createToolbarButton(ACTION_SELECT_NEXT, "Show next selection", "resultset_next.png", "Next"));

		updateToolbarButtons();
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(new JMenu("File"));
		setJMenuBar(menuBar);
		
		pack();
		setSize(1024, 700);
		setVisible(true);
		PreferencesInitializer.checkForEmptyPreferences();
		
		server = new MatpowerGUIServer(this);
	}
	
	public int getEnvironment() {
		return environment;
	}
	
	private String getWindowTitle() {
		String title = "Power Flow Analyzer " + Preferences.getDefaultProperty(PROPERTY_VERSION);
		if(getCurrentCase() != null) {
			if(getCurrentCase().getCaseFile() == null)
				title = "Untitled - " + title;
			else
				title = getCurrentCase().getCaseFile().getName() + " - " + title;
		}
		return title;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand() == null)
			return;
		try {
			if(e.getActionCommand().equals(ACTION_CASE_NEW)) {
				createNewCase();
			} else if(e.getActionCommand().equals(ACTION_CASE_OPEN)) {
				openCaseFile();
			} else if(e.getActionCommand().equals(ACTION_CASE_SAVE)) {
				saveCaseFile();
//			} else if(e.getActionCommand().equals(ACTION_DIAGRAM_ADD)) {
//				DiagramSheetPropertiesDialog dialog = new DiagramSheetPropertiesDialog(this);
//				if(dialog.isCancelPressed())
//					return;
//				getCurrentViewer().addDiagramSheet(dialog.getDiagramSheetProperties());
//			} else if(e.getActionCommand().equals(ACTION_DIAGRAM_EDIT)) {
//				DiagramSheetPropertiesDialog dialog = new DiagramSheetPropertiesDialog(this, 
//						getCurrentViewer().getCurrentDiagramSheetProperties());
//				if(dialog.isCancelPressed())
//					return;
//				getCurrentViewer().setCurrentDiagramSheet(dialog.getDiagramSheetProperties());
			} else if(e.getActionCommand().equals(ACTION_MAP_PROPERTIES)) {
				getCurrentViewer().getViewerController().showMapPropertiesDialog(this);
//			} else if(e.getActionCommand().equals(ACTION_PANEL_PROPERTIES)) {
//				getCurrentViewer().getPanelController().showPanelPropertiesDialog(this);
			} else if(e.getActionCommand().equals(ACTION_MODEL_DB_PROPERTIES)) {
				showModelDBDialog();
			} else if(e.getActionCommand().equals(ACTION_CASE_CALCULATE)) {
				calculatePowerFlow();
			} else if(e.getActionCommand().equals(ACTION_SELECT_PREVIOUS)) {
				if(getCurrentViewer() != null)
					getCurrentViewer().getSelectionManager().showPreviousElement();
			} else if(e.getActionCommand().equals(ACTION_SELECT_NEXT)) {
				if(getCurrentViewer() != null)
					getCurrentViewer().getSelectionManager().showNextElement();
			}
		} catch(Exception error) {
			error.printStackTrace();
		}
		updateToolbarButtons();
	}

	public static JButton createButton(String tooltipText, String iconName, String altText, boolean largeIcons) {
		JButton button = new JButton();
		decorateButton(button, tooltipText, iconName, altText, largeIcons);
		return button;
	}

	public static JToggleButton createToggleButton(String tooltipText, String iconName, String altText, boolean largeIcons) {
		JToggleButton button = new JToggleButton();
		decorateButton(button, tooltipText, iconName, altText, largeIcons);
		return button;
	}
	
	public static void decorateButton(AbstractButton button, String tooltipText, String iconName, String altText, 
			boolean largeIcons) {
			String iconSizePath = largeIcons ? "32x32" : "16x16";
			URL iconURL = PowerFlowAnalyzer.class.getResource("/" + iconSizePath + "/" + iconName);
			button.setFocusable(false);
			button.setToolTipText(tooltipText);
			if(iconURL != null) {
				button.setIcon(new ImageIcon(iconURL, altText));
			} else {
				button.setText(altText);
			}
	}

	private JButton createToolbarButton(String actionCommand, String tooltipText, String iconName, String altText) {
		JButton button = createButton(tooltipText, iconName, altText, largeIcons);
		button.setActionCommand(actionCommand);
		button.addActionListener(this);
		toolbarButtons.put(actionCommand, button);
		return button;
	}
	
	private void createNewCase() {
		PowerFlowCase caze = new PowerFlowCase();
		caze.addNetwork(new Network());
		openCase(caze);
	}
	
	private void openCaseFile() {
		caseDialog = new CaseSelectionDialog(this, getWorkingDirectory());
		caseDialog.showDialog(-1, -1);
		if(caseDialog.isCancelPressed())// cancel pressed
			return;
		String inputFile = caseDialog.getSelectedInputFile();
		String pfcase = findName(inputFile);
		nextCase = pfcase;
		File caseFile = new File(inputFile);
		int inputSource = caseDialog.getSelectedInputSource();
//		boolean showProgress = true;
		if(inputSource == CaseSelectionDialog.CASE_FILE_INPUT_SOURCE) {
			openCase(new PowerFlowCase(caseFile));
		} else if(inputSource == CaseSelectionDialog.MATPOWER_CASE_INPUT_SOURCE) {
			callMatlabCommand("import_matpower_case", new Object[] { caseFile.getAbsolutePath() }, 0, true);
		} else if(inputSource == CaseSelectionDialog.MATLAB_SCRIPT_INPUT_SOURCE) {
			String path = caseFile.getParentFile().getAbsolutePath();
			// change working directory in matlab if necessary
			if(path.equals(workingDirectory) == false) {
				callMatlabCommand("cd", new Object[] { path }, 0, true);
			}
			String mFile = caseFile.getName().substring(0, caseFile.getName().lastIndexOf(".m"));
			callMatlabCommand(mFile, new Object[0], 0, true);
		}
		caseDialog = null;
//		if(showProgress)
		openProgressDialog(pfcase);
	}
	
	private void saveCaseFile() {
		if(getCurrentCase().getCaseFile() == null) {
			JFileChooser fileChooser = new JFileChooser(getWorkingDirectory());
			fileChooser.setAcceptAllFileFilterUsed(true);
			fileChooser.setFileFilter(CaseSelectionDialog.CASE_FILE_FILTER);
			int action = fileChooser.showSaveDialog(this);
			if(action == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				String fileName = selectedFile.getName();
				// append file ending if necessary
				if( ! fileName.endsWith(".case")) {
					fileName += ".case";
					selectedFile = new File(selectedFile.getParentFile(), fileName);
				}
				// check if file exists
				if(selectedFile.exists()) {
					action = JOptionPane.showConfirmDialog(this, 
							"The file\n\t\t" + selectedFile.getAbsolutePath() + "\nalready exists. " +
									"Do you want to overwrite it?", "Overwrite?", JOptionPane.YES_NO_OPTION);
					if(action == JOptionPane.YES_OPTION) {
						// do nothing, catch YES directly
					} else {//if(action == JOptionPane.NO_OPTION) {
						saveCaseFile();
						return;
					}
				}
				getCurrentCase().setCaseFile(selectedFile);
			} else
				return;
		}
		getCurrentCase().save();
//		JFileChooser fileChooser = new JFileChooser(getWorkingDirectory());
//		fileChooser.setAcceptAllFileFilterUsed(true);
//		fileChooser.setFileFilter(CaseSelectionDialog.MATLAB_DATA_FILE_FILTER);
//		int action = fileChooser.showSaveDialog(this);
//		if(action == JFileChooser.APPROVE_OPTION) {
//			File selectedFile = fileChooser.getSelectedFile();
//			String fileName = selectedFile.getName();
//			// append file ending if necessary
//			if( ! fileName.endsWith(".mat")) {
//				fileName += ".mat";
//				selectedFile = new File(selectedFile.getParentFile(), fileName);
//			}
//			// check if file exists
//			if(selectedFile.exists()) {
//				action = JOptionPane.showConfirmDialog(this, 
//						"The file\n\t\t" + selectedFile.getAbsolutePath() + "\nalready exists. " +
//								"Do you want to overwrite it?", "Overwrite?", JOptionPane.YES_NO_OPTION);
//				if(action == JOptionPane.YES_OPTION) {
//					// do nothing, catch YES directly
//				} else {//if(action == JOptionPane.NO_OPTION) {
//					saveCaseFile();
//					return;
//				}
//			}
//			// call matlab script
//			callMatlabCommand("savepfdata", new Object[] {
//					selectedFile.getAbsolutePath(), getCurrentViewer().getPowerFlowData() },
//					0, true);
//		}
	}
	
	private void calculatePowerFlow() {
		CaseCalculationDialog dialog = new CaseCalculationDialog(this, getCurrentNetwork());
		dialog.showDialog(-1, -1);
		if(dialog.isCancelPressed())
			return;
		String pfcase = findName("Power Flow");
		nextCase = pfcase;
		// call matlab script
		callMatlabCommand("calc_power_flow", new Object[] {
				getCurrentNetwork() },
				0, true);
		openProgressDialog(pfcase);
	}
	
	public void showModelDBDialog() {
		if(getCurrentContainer() != null)
			getCurrentContainer().showModelDBDialog();
	}
	
	private void openProgressDialog(final String pfcase) {
		final ProgressDialog progressDialog = new ProgressDialog();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					progressDialog.setVisible(true);
					while(success.get(pfcase) == null) {
						Thread.sleep(500);
					}
				} catch(InterruptedException e) {
				} finally {
					progressDialog.setVisible(false);
					progressDialog.dispose();
				}
			}
		}).start();
	}
	
	private void stopServer() {
		if(server != null) {
			server.stopServer();
			server = null;
		}
	}
	
	private void destroyViewer() {
		if(environment == APPLICATION_ENVIRONMENT)
			System.exit(0);
		if(environment == MATLAB_ENVIRONMENT)
			callMatlabCommand("stoppfviewer", new Object[0], 0, false);
	}
	
	private void callMatlabCommand(final String command, final Object[] parameters, final int returnValueCount, final boolean printOutput) {
		try {
			Matlab.whenMatlabReady(new Runnable() {
				public void run() {
					try {
						if(printOutput)
							Matlab.mtFevalConsoleOutput(command, parameters, returnValueCount);
						else
							Matlab.mtFeval(command, parameters, returnValueCount);
					} catch(MatlabException e) {
						JOptionPane.showMessageDialog(PowerFlowAnalyzer.this, "Error: " + e.getMessage()
								+ "\n\nSee the Matlab console for more information.");
						success.put(nextCase, false);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(PowerFlowAnalyzer.this, "An error occurred while executing a matlab command: " + e
								+ "\n\nSee the Matlab console for more information.");
						success.put(nextCase, false);
					}
				}
			});
		} catch (Exception e) {
			JOptionPane.showMessageDialog(PowerFlowAnalyzer.this, "An error occurred while calling matlab: " + e
					+ "\n\nSee the Matlab console for more information.");
			e.printStackTrace();
		}
	}
	
	public void cancelPowerFlow() {
		PowerFlowAnalyzer.this.success.put(nextCase, false);
	}
	
//	public void setPowerFlowData(final Network data) {
//		try {
//			PowerFlowAnalyzer.this.success.put(nextCase, true);
//			PowerFlowAnalyzer.this.data.put(nextCase, data);
//			PowerFlowViewer viewer = new PowerFlowViewer(data);
//			cases.add(viewer);
//			casesParent.addTab(nextCase, viewer.getContentPane());
//			casesParent.selectLastTab();
//			viewer.addActionUpdateListener(this);
////			if(powerFlowCase != null)
////				powerFlowCase.setNetworkData(data.getData());
//			if(showSuccessMessage) {
//				if(data.isSuccessful())
//					JOptionPane.showMessageDialog(PowerFlowAnalyzer.this, "Calculation was successful!\n\nTime: " + data.getTime() + " seconds");
//				else
//					JOptionPane.showMessageDialog(PowerFlowAnalyzer.this, "Calculation was NOT successful!", "Error", JOptionPane.ERROR_MESSAGE);
//			}
//		} catch(Throwable t) {
//			JOptionPane.showMessageDialog(PowerFlowAnalyzer.this, "Calculation caused an error: " + t, "Error", JOptionPane.ERROR_MESSAGE);
//			t.printStackTrace();
//		}
//		updateToolbarButtons();
//	}
	
	public void createNewCase(NetworkData networkData) {
		PowerFlowCase caze = new PowerFlowCase();
		caze.addNetwork(networkData);
		openCase(caze);
	}
	
	public void updateNetwork(NetworkData networkData) {
		PowerFlowCase caze = getCurrentCase();
		if(caze == null) {
			int action = JOptionPane.showConfirmDialog(this, 
					"Do you want to import the network data as a new case?", "Create new case?", 
					JOptionPane.YES_NO_OPTION);
			if(action == JOptionPane.YES_OPTION) {
				caze = new PowerFlowCase();
				caze.addNetwork(networkData);
				openCase(caze);
			}
			return;
		}
		Network network = getCurrentNetwork();
		if(network == null)
			return;
		caze.setNetworkData(network, networkData);
		network.fireNetworkChanged();
		caze.getViewer().getSelectionManager().clearHistory();
		success.put(nextCase, true);
		updateToolbarButtons();
	}
	
	private void openCase(PowerFlowCase caze) {
		try {
			for (Network network : caze.getNetworks()) {
				if(network.getBusses().size() > 0 && network.getCombinedBusCount() == 0) {
					int action = JOptionPane.showConfirmDialog(this, 
							"This network does not contain any location data. " +
							"Do you want to create default coordinates for all bus nodes?", 
							"Create default coordinates?", 
							JOptionPane.YES_NO_OPTION);
					if(action == JOptionPane.YES_OPTION)
						network.setDefaultCoordinates();
				}
			}
			success.put(nextCase, true);
			NetworkContainer viewer = new NetworkContainer(caze);
			viewer.addActionUpdateListener(this);
			caze.setViewer(viewer);
			cases.add(caze);
			casesParent.addTab(nextCase, viewer);
			casesParent.selectLastTab();
		} catch(Throwable t) {
			JOptionPane.showMessageDialog(PowerFlowAnalyzer.this, "Calculation caused an error: " + t, "Error", JOptionPane.ERROR_MESSAGE);
			t.printStackTrace();
		}
		updateToolbarButtons();
	}
	
	public String getWorkingDirectory() {
		if(workingDirectory == null)
			return System.getProperty("user.home");
		return workingDirectory;
	}

	public void setWorkingDirectory(String workingDirectory) {
		setWorkingDirectory(workingDirectory, false);
	}

	public void setWorkingDirectory(String workingDirectory, boolean signalToUser) {
		this.workingDirectory = workingDirectory;
		if(caseDialog != null)
			caseDialog.setWorkingDirectory(workingDirectory);
//		else if(signalToUser)
//			JOptionPane.showMessageDialog(this, "Matlab current folder changed to:\n" + workingDirectory);
	}
	
	public void setMatlabCurrentFolder(String workingDirectory) {
		File dir = new File(workingDirectory);
		if(dir.exists())
			callMatlabCommand("setworkingdirectory", new Object[] { workingDirectory }, 0, true);
		else
			JOptionPane.showMessageDialog(PowerFlowAnalyzer.this, "The directory does not exist:\n" + workingDirectory, 
					"Error", JOptionPane.ERROR_MESSAGE);
	}
	
	private NetworkContainer getCurrentContainer() {
		if(casesParent.isEmpty())
			return null;
		return cases.get(casesParent.getSelectedIndex()).getViewer();
	}
	
	private PowerFlowViewer getCurrentViewer() {
		if(casesParent.isEmpty())
			return null;
		return cases.get(casesParent.getSelectedIndex()).getViewer().getCurrentViewer();
	}
	
	private Network getCurrentNetwork() {
		if(getCurrentViewer() != null)
			return getCurrentViewer().getNetwork();
		return null;
	}
	
	private PowerFlowCase getCurrentCase() {
		if(casesParent.isEmpty())
			return null;
		return cases.get(casesParent.getSelectedIndex());
	}
	
	@Override
	public void updateActions() {
		updateToolbarButtons();
	}

	private void updateToolbarButtons() {
		boolean hasViewer = getCurrentViewer() != null;
//		boolean hasDiagrams = hasViewer && getCurrentViewer().hasDiagramSheet();
		boolean isMatlabEnv = environment == MATLAB_ENVIRONMENT;
//		toolbarButtons.get(ACTION_DIAGRAM_ADD).setEnabled(hasViewer);
		toolbarButtons.get(ACTION_CASE_SAVE).setEnabled(getCurrentCase() != null);
//		toolbarButtons.get(ACTION_DIAGRAM_EDIT).setEnabled(hasDiagrams);
		toolbarButtons.get(ACTION_MAP_PROPERTIES).setEnabled(hasViewer);
		toolbarButtons.get(ACTION_CASE_CALCULATE).setEnabled(isMatlabEnv && hasViewer);
//		toolbarButtons.get(ACTION_CASE_LAYOUT).setEnabled(hasViewer);
		toolbarButtons.get(ACTION_MODEL_DB_PROPERTIES).setEnabled(getCurrentCase() != null);
//		toolbarButtons.get(ACTION_PANEL_PROPERTIES).setEnabled(hasViewer);
		toolbarButtons.get(ACTION_SELECT_PREVIOUS).setEnabled(hasViewer
				&& getCurrentViewer().getSelectionManager().hasPreviousElement());
		toolbarButtons.get(ACTION_SELECT_NEXT).setEnabled(hasViewer
				&& getCurrentViewer().getSelectionManager().hasNextElement());
		setTitle(getWindowTitle());
	}
	
	public void setError(String errorMessage) {
		JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	public void showViewer() {
		// check if window is minimized to tray
		if((getExtendedState() & ICONIFIED) != 0)
			setExtendedState(getExtendedState() & ~ICONIFIED);
		toFront();
	}
	
	public void closeViewer() {
		stopServer();
		dispose();
		Preferences.saveProperties();
		for (PowerFlowCase caze : cases) {
			caze.getViewer().dispose();
		}
	}
	
	public void ping() {
		JOptionPane.showMessageDialog(this, "Ping!");
	}
	
	private String findName(String text) {
		String result = text;
		int count = 2;
		while(success.get(result) != null) {
			result = text + "[" + count + "]";
			count++;
		}
		return result;
	}
	
	class ProgressDialog extends JDialog {
		
		private JProgressBar progressBar;
		
		ProgressDialog() {
			super(PowerFlowAnalyzer.this, "Calculating", false);
			progressBar = new JProgressBar();
			progressBar.setIndeterminate(true);
			JPanel contentPane = new JPanel(new BorderLayout());
			contentPane.setBorder(new EmptyBorder(10, 50, 20, 50));
			JLabel l = new JLabel("<html><b>Please wait while calculating power flow...");
			l.setBorder(new EmptyBorder(20, 10, 30, 10));
			contentPane.add(l, BorderLayout.CENTER);
			contentPane.add(progressBar, BorderLayout.SOUTH);
			getContentPane().setLayout(new BorderLayout());
			getContentPane().add(contentPane, BorderLayout.CENTER);
			pack();
			int centerX = PowerFlowAnalyzer.this.getX() + PowerFlowAnalyzer.this.getWidth() / 2;
			int centerY = PowerFlowAnalyzer.this.getY() + PowerFlowAnalyzer.this.getHeight() / 2;
			setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		}
	}
}
