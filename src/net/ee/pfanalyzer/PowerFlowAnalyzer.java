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
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Box;
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
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import net.ee.pfanalyzer.io.MatpowerGUIServer;
import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.CaseSerializer;
import net.ee.pfanalyzer.model.CombinedNetworkElement;
import net.ee.pfanalyzer.model.ModelDB;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.PowerFlowCase;
import net.ee.pfanalyzer.model.data.AbstractModelElementData;
import net.ee.pfanalyzer.model.data.CaseData;
import net.ee.pfanalyzer.model.data.ModelData;
import net.ee.pfanalyzer.model.data.NetworkData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.preferences.IPreferenceConstants;
import net.ee.pfanalyzer.preferences.Preferences;
import net.ee.pfanalyzer.preferences.PreferencesInitializer;
import net.ee.pfanalyzer.ui.CaseViewer;
import net.ee.pfanalyzer.ui.NetworkElementSelectionManager;
import net.ee.pfanalyzer.ui.NetworkViewer;
import net.ee.pfanalyzer.ui.db.ModelDBDialog;
import net.ee.pfanalyzer.ui.dialog.ElementSelectionDialog;
import net.ee.pfanalyzer.ui.dialog.ExecuteScriptDialog;
import net.ee.pfanalyzer.ui.dialog.NewCaseDialog;
import net.ee.pfanalyzer.ui.dialog.OpenCaseDialog;
import net.ee.pfanalyzer.ui.dialog.SelectScriptDialog;
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
	private final static String ACTION_CASE_SAVE_AS = "action.case.saveas";
//	private final static String ACTION_CASE_LAYOUT = "action.case.layout";
	 
//	private final static String ACTION_DIAGRAM_ADD = "action.diagram.add";
	private final static String ACTION_TABLE_ADD = "action.table.add";
//	private final static String ACTION_DIAGRAM_EDIT = "action.diagram.edit";
//	private final static String ACTION_DIAGRAM_REMOVE = "action.diagram.remove";

	private final static String ACTION_NETWORK_ADD_ELEMENT = "action.network.add.element";
	private final static String ACTION_NETWORK_REMOVE_ELEMENT = "action.network.remove.element";
	
//	private final static String ACTION_MAP_PROPERTIES = "action.map.properties";
//	private final static String ACTION_PANEL_PROPERTIES = "action.panel.properties";
	private final static String ACTION_MODEL_DB_PROPERTIES = "action.model.db.properties";
//	private final static String ACTION_APP_PROPERTIES = "action.app.properties";
	private final static String ACTION_EXECUTE_SCRIPT = "action.case.calculate";
	
	private final static String ACTION_SELECT_PREVIOUS = "action.select.previous";
	private final static String ACTION_SELECT_NEXT = "action.select.next";
	
	private final static String ACTION_CLOSE_PROGRAM = "action.program.close";
	private final static String ACTION_DUMP_MEMORY_INFO = "action.info.memory";
	
	private final static String ACTION_IMPORT_DB = "action.model.db.import";
	private final static String ACTION_EXPORT_DB = "action.model.db.export";
	
	private static PowerFlowAnalyzer INSTANCE;
	
	private MatpowerGUIServer server;
	
	private Map<Long, List<Long>> success = new HashMap<Long, List<Long>>();
	private boolean cancelScriptExecution = false;
	private ClosableTabbedPane casesParent;
	private OpenCaseDialog caseDialog;
//	String nextCase;
	private long maxCaseID = 0;
	private List<PowerFlowCase> cases = new ArrayList<PowerFlowCase>();
	private Map<String, JButton> toolbarButtons = new HashMap<String, JButton>();
	private Map<String, Action> actions = new HashMap<String, Action>();
	private String workingDirectory;

	private boolean largeIcons = Preferences.getBooleanProperty(PROPERTY_UI_LARGE_ICONS);
//	private boolean showSuccessMessage = Preferences.getBooleanProperty(PROPERTY_UI_SHOW_SUCCESS_MESSAGE);
	
	private ModelDB configuration;
	private final int environment;
	
	public static void main(String[] args) {
		// setting look and feel of current platform
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		PowerFlowAnalyzer app = new PowerFlowAnalyzer(APPLICATION_ENVIRONMENT);
		app.setWorkingDirectory(System.getProperty("user.home"));
	}
	
	public static PowerFlowAnalyzer getInstance() {
		return INSTANCE;
	}
	
	public static ModelDB getConfiguration() {
		return getInstance().configuration;
	}
	
	public PowerFlowAnalyzer(int environment) {
		super();
		this.environment = environment;
		INSTANCE = this;
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		ToolTipManager.sharedInstance().setInitialDelay(100);// reduce delay in showing tooltips
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				closeProgram();
			}
		});
		// load default configuration
		configuration = new ModelDB(CaseSerializer.readInternalConfigurationDB());
		
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
				CaseViewer container = cases.get(tabIndex).getViewer();
				if(container.getSelectionManager() != null)
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
		toolbar.add(createToolbarButton(ACTION_CASE_NEW, "Create a new power flow case", "page_add.png", "Create case"));
		toolbar.add(createToolbarButton(ACTION_CASE_OPEN, "Open an existing power flow case", "folder.png", "Open case"));
//		toolbar.add(createToolbarButton(ACTION_CASE_EDIT, "Edit this case", "report_edit.png", "Edit case"));
		toolbar.add(createToolbarButton(ACTION_CASE_SAVE, "Save this power flow case", "save_as.png", "Save case"));
//		toolbar.add(createToolbarButton(ACTION_CASE_REMOVE, "Remove this case", "report_delete.png", "Remove Case"));
		toolbar.addSeparator();
		toolbar.add(createToolbarButton(ACTION_EXECUTE_SCRIPT, "Execute a script on this network", "script_gear.png", "Execute script"));
		toolbar.addSeparator();
		toolbar.add(createToolbarButton(ACTION_MODEL_DB_PROPERTIES, "Open parameter database defining parameters for networks, elements and scripts", "database.png", "Parameter DB"));
		toolbar.add(createToolbarButton(ACTION_NETWORK_ADD_ELEMENT, "Add a new network element", "plugin_add.png", "Add element"));
		toolbar.add(createToolbarButton(ACTION_NETWORK_REMOVE_ELEMENT, "Remove the selected network element(s)", "plugin_delete.png", "Remove element"));
		toolbar.addSeparator();
//		toolbar.add(createToolbarButton(ACTION_DIAGRAM_ADD, "Create a new diagram sheet", "chart_bar_add.png", "New Diagram"));
//		toolbar.add(createToolbarButton(ACTION_DIAGRAM_EDIT, "Edit diagram sheet", "chart_bar_edit.png", "Edit Diagram"));
//		toolbar.add(createToolbarButton(ACTION_DIAGRAM_REMOVE, "Remove diagram", "chart_bar_delete.png", "Remove Diagram"));
//		toolbar.add(createToolbarButton(ACTION_PANEL_PROPERTIES, "Edit model view", "table_edit.png", "Edit model view"));
//		toolbar.add(createToolbarButton(ACTION_CASE_LAYOUT, "Change layout", "grid.png", "Change layout"));
//		toolbar.addSeparator();
//		toolbar.add(createToolbarButton(ACTION_APP_PROPERTIES, "Edit program settings", "widgets.png", "App settings"));
		toolbar.add(createToolbarButton(ACTION_TABLE_ADD, "Add a new data viewer to this layout", "layout_add.png", "New Table"));
//		toolbar.add(createToolbarButton(ACTION_MAP_PROPERTIES, "Edit map settings", "map_edit.png", "Edit map"));
		toolbar.addSeparator();
		toolbar.add(createToolbarButton(ACTION_SELECT_PREVIOUS, "Show previous selection", "resultset_previous.png", "Previous"));
		toolbar.add(createToolbarButton(ACTION_SELECT_NEXT, "Show next selection", "resultset_next.png", "Next"));
//		toolbar.addSeparator();
		toolbar.add(Box.createHorizontalGlue());
//		toolbar.add(createToolbarButton(ACTION_DUMP_MEMORY_INFO, "Dump memory information", "system_monitor.png", "Memory"));
		
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(createAction(ACTION_CASE_NEW, "New...", ""));
		fileMenu.add(createAction(ACTION_CASE_OPEN, "Open...", ""));
		fileMenu.addSeparator();
		fileMenu.add(createAction(ACTION_CASE_SAVE, "Save", ""));
		fileMenu.add(createAction(ACTION_CASE_SAVE_AS, "Save as...", ""));
		fileMenu.addSeparator();
		fileMenu.add(createAction(ACTION_IMPORT_DB, "Import parameter database...", ""));
		fileMenu.add(createAction(ACTION_EXPORT_DB, "Export parameter database...", ""));
		fileMenu.addSeparator();
		fileMenu.add(createAction(ACTION_CLOSE_PROGRAM, "Exit program...", ""));
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);

		updateToolbarButtons();
		
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
		if(hasUnsavedCases())
			title += " *";
		return title;
	}
	
	private boolean hasUnsavedCases() {
		for (int i = 0; i < cases.size(); i++) {
			if(cases.get(i).isDirty())
				return true;
		}
		return false;
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
				saveCaseFile(false);
			} else if(e.getActionCommand().equals(ACTION_CASE_SAVE_AS)) {
				saveCaseFile(true);
//			} else if(e.getActionCommand().equals(ACTION_DIAGRAM_ADD)) {
//				if(getCurrentViewer() == null)
//					return;
//				getCurrentViewer().addDiagram();
			} else if(e.getActionCommand().equals(ACTION_TABLE_ADD)) {
				if(getCurrentViewer() == null)
					return;
				getCurrentViewer().addViewer();
//			} else if(e.getActionCommand().equals(ACTION_DIAGRAM_EDIT)) {
//				DiagramSheetPropertiesDialog dialog = new DiagramSheetPropertiesDialog(this, 
//						getCurrentViewer().getCurrentDiagramSheetProperties());
//				if(dialog.isCancelPressed())
//					return;
//				getCurrentViewer().setCurrentDiagramSheet(dialog.getDiagramSheetProperties());
//			} else if(e.getActionCommand().equals(ACTION_MAP_PROPERTIES)) {
//				getCurrentViewer().getViewerController().showMapPropertiesDialog(this);
//			} else if(e.getActionCommand().equals(ACTION_PANEL_PROPERTIES)) {
//				getCurrentViewer().getPanelController().showPanelPropertiesDialog(this);
			} else if(e.getActionCommand().equals(ACTION_MODEL_DB_PROPERTIES)) {
				showModelDBDialog();
			} else if(e.getActionCommand().equals(ACTION_EXECUTE_SCRIPT)) {
				executeScript();
			} else if(e.getActionCommand().equals(ACTION_NETWORK_ADD_ELEMENT)) {
				createNewElement();
			} else if(e.getActionCommand().equals(ACTION_NETWORK_REMOVE_ELEMENT)) {
				removeElement();
			} else if(e.getActionCommand().equals(ACTION_SELECT_PREVIOUS)) {
				if(getCurrentViewer() != null)
					getCurrentViewer().getSelectionManager().showPreviousElement();
			} else if(e.getActionCommand().equals(ACTION_SELECT_NEXT)) {
				if(getCurrentViewer() != null)
					getCurrentViewer().getSelectionManager().showNextElement();
			} else if(e.getActionCommand().equals(ACTION_IMPORT_DB)) {
				if(getCurrentCase() != null)
					importParameterDB();
			} else if(e.getActionCommand().equals(ACTION_EXPORT_DB)) {
				if(getCurrentCase() != null)
					exportParameterDB();
			} else if(e.getActionCommand().equals(ACTION_CLOSE_PROGRAM)) {
				closeProgram();
			} else if(e.getActionCommand().equals(ACTION_DUMP_MEMORY_INFO)) {
				dumpMemoryUsage();
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
		URL iconURL = getIconURL(iconName, largeIcons);
		button.setFocusable(false);
		button.setToolTipText(tooltipText);
		if(iconURL != null) {
			button.setIcon(new ImageIcon(iconURL, altText));
		} else {
			button.setText(altText);
		}
	}
	
	public static URL getIconURL(String iconName, boolean largeIcons) {
		String iconSizePath = largeIcons ? "32x32" : "16x16";
		return PowerFlowAnalyzer.class.getResource("/" + iconSizePath + "/" + iconName);
	}

	private JButton createToolbarButton(String actionCommand, String tooltipText, String iconName, String altText) {
		JButton button = createButton(tooltipText, iconName, altText, largeIcons);
		button.setActionCommand(actionCommand);
		button.addActionListener(this);
		toolbarButtons.put(actionCommand, button);
		return button;
	}
	
	private Action createAction(final String actionCommand, String text, String tooltipText) {
		AbstractAction action = new AbstractAction(text) {
			@Override
			public void actionPerformed(ActionEvent e) {
				// delegate to action listener
				PowerFlowAnalyzer.this.actionPerformed(new ActionEvent(e.getSource(), e.getID(), actionCommand));
			}
		};
		actions.put(actionCommand, action);
		return action;
	}
	
	private void dumpMemoryUsage() {
//		for (int i = 0; i < 10; i++) {
//			System.gc();
//		}
		long free = Runtime.getRuntime().freeMemory() / (1024 * 1024);
		long total = Runtime.getRuntime().totalMemory() / (1024 * 1024);
		long used = total - free;
//		System.out.println("Free: " + free + "MB of " + total + "MB");
		System.out.println("Used: " + used + "MB, free: " + free + "MB");
	}
	
	private void importParameterDB() {
		NewCaseDialog dialog = new NewCaseDialog(this, "Import parameter database");
		dialog.showDialog(-1, -1);
		if(dialog.isCancelPressed())// cancel pressed
			return;
		int inputSource = dialog.getSelectedParameterSource();
		ModelDB database = null;
		if(inputSource == 0)
			database = new ModelDB();
		else if(inputSource == 1)
			database = new ModelDB(CaseSerializer.readModelDB(new File(dialog.getSelectedParameterFile())));
		if(database == null)
			return;
		database.setDirty(true);
		getCurrentCase().changeModelDB(database);
		for (Network net : getCurrentCase().getNetworks(true))
			net.fireNetworkChanged();
		getCurrentContainer().reloadModelDBDialog();
	}
	
	private void exportParameterDB() {
		JFileChooser fileChooser = new JFileChooser(getWorkingDirectory());
		fileChooser.setAcceptAllFileFilterUsed(true);
		fileChooser.setFileFilter(NewCaseDialog.PARAMETER_FILE_FILTER);
		int action = fileChooser.showSaveDialog(this);
		if(action == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			String fileName = selectedFile.getName();
			// append file ending if necessary
			if( ! fileName.endsWith(".xml")) {
				fileName += ".xml";
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
					exportParameterDB();
					return;
				}
			}
			CaseSerializer serializer = new CaseSerializer();
			CaseData pfCase = new CaseData();
			pfCase.setModelDb(getCurrentCase().getModelDB().getData());
			try {
				serializer.writeCase(pfCase, selectedFile);
			} catch (Exception e) {
				System.err.println("Cannot write file: " + selectedFile);
				e.printStackTrace();
			}
		}
	}
	
	private boolean createNewCase() {
		NewCaseDialog dialog = new NewCaseDialog(this, "Create new case");
		dialog.showDialog(-1, -1);
		if(dialog.isCancelPressed())// cancel pressed
			return false;
		int inputSource = dialog.getSelectedParameterSource();
		ModelDB database = null;
		if(inputSource == 0)
			database = new ModelDB();
		else if(inputSource == 1)
			database = new ModelDB(CaseSerializer.readModelDB(new File(dialog.getSelectedParameterFile())));
		if(database == null)
			return false;
		PowerFlowCase caze = new PowerFlowCase(database);
		openCase(caze);
		return true;
	}
	
	public void importMatpowerCase(String inputFile) {
		File caseFile = new File(inputFile);
		long caseID = getCurrentCase() == null ? -1 : getCurrentCase().getCaseID();
		callMatlabCommand("import_matpower_case", new Object[] { caseFile.getAbsolutePath() }, caseID, 0, true);
		openProgressDialog(caseID);
	}
		
	private void executeScript() {
		SelectScriptDialog dialog = new SelectScriptDialog(this, getCurrentCase());
		dialog.showDialog(-1, -1);
		if(dialog.isCancelPressed())
			return;
		Network network = getCurrentNetwork();
		ModelData script = dialog.getSelectedScript();
		if(ModelDBUtils.isNetworkCreatingScript(script) && network.isEmpty() == false) {
			int action = JOptionPane.showOptionDialog(this, 
					"<html>This script will create a new network but the selected " +
					"network is not empty.<br>Do you want to overwrite " +
					"the selected network?", "Question", 
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, 
					null, new String[] {
							"Overwrite network", "Cancel"
					}, null);
			if(action == JOptionPane.YES_OPTION) {
				network.removeAllElements();
				network.getParameterList().clear();
				network.fireNetworkChanged();
			} else
				return;
		}
		executeScript(network, script);
	}
	
	public void executeScript(Network network, ModelData script) {
		executeScript(new Network[] { network }, script);
	}
	
	public void executeScript(Network[] networks, ModelData script) {
		if(networks.length == 0)
			return;
		Network network = networks[0];
		ExecuteScriptDialog dialog = new ExecuteScriptDialog(this, network, script, networks.length > 1);
		// only show this dialog if it is necessary
		if(dialog.shouldShowDialog()) {
			dialog.showDialog(-1, -1);
			if(dialog.isCancelPressed())// cancel pressed
				return;
		}
		// call matlab script
		String scriptFile = network.getTextParameter("SCRIPT");
		if(scriptFile == null || scriptFile.isEmpty()) {
			JOptionPane.showMessageDialog(this, "The SCRIPT parameter must be set for this script in the parameter database!", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// remove file ending ".m" if necessary
		if(scriptFile.endsWith(".m"))
			scriptFile = scriptFile.substring(0, scriptFile.lastIndexOf(".m"));
//		executeScriptInternal(network, scriptFile);
		final boolean changeMatlabPath = network.getBooleanParameter(ModelDBUtils.CHANGE_PATH_PARAMETER, true);
		File caseFile = getCurrentCase().getCaseFile();
		if(changeMatlabPath && caseFile != null)
			setMatlabCurrentFolder(caseFile.getParent());
		// call script
		addProgress(network.getCaseID(), network.getInternalID());
		for (int i = 1; i < networks.length; i++) {
			for (NetworkParameter parameter : script.getParameter()) {
				String value = network.getTextParameter(parameter.getID());
				networks[i].setParameter(parameter.getID(), value);
			}
			addProgress(network.getCaseID(), networks[i].getInternalID());
		}
		openProgressDialog(network.getCaseID());
		for (int i = 0; i < networks.length; i++) {
//			System.out.println("executing script for network " + networks[i].getInternalID());
//			addProgress(network.getCaseID(), networks[i].getInternalID());
			callMatlabCommand(scriptFile, new Object[] { networks[i] }, network.getCaseID(), 0, true);
		}
//		addProgress(network.getCaseID(), network.getInternalID());
	}
	
//	private void executeScriptInternal(final Network network, final String scriptFile) {
//		final boolean changeMatlabPath = true;
//		workingDirectoryChanged = false;
//		File caseFile = getCurrentCase().getCaseFile();
//		if(changeMatlabPath && caseFile != null)
//			updateWorkingDirectory();
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				int count = 0;
//				try {
//					File caseFile = getCurrentCase().getCaseFile();
//					if(changeMatlabPath && caseFile != null) {
//						while(count <= 1000 && workingDirectoryChanged == false) {
//							Thread.sleep(200);
//							count += 200;
//						}
//						if(workingDirectoryChanged == false)
//							JOptionPane.showMessageDialog(PowerFlowAnalyzer.this, 
//									"Could not change Matlab's current path.");
//						else if(caseFile.getParent().equals(getWorkingDirectory()));
//					}
//				} catch (InterruptedException e) {
//					// do nothing
//				}
//				// call script
//				String pfcase = findName("Power Flow");
//				nextCase = pfcase;
//				callMatlabCommand(scriptFile, new Object[] { network }, 0, true);
//				openProgressDialog(pfcase);
//			}
//		}).start();
//	}
	
	private void openCaseFile() {
		caseDialog = new OpenCaseDialog(this);
		caseDialog.showDialog(-1, -1);
		if(caseDialog.isCancelPressed()) { // cancel pressed
			caseDialog = null;
			return;
		}
		String inputFile = caseDialog.getSelectedInputFile();
		File caseFile = new File(inputFile);
		openCase(new PowerFlowCase(caseFile));
		caseDialog = null;
	}
	
	private void saveCaseFile(boolean saveAs) {
		if(saveAs || getCurrentCase().getCaseFile() == null) {
			JFileChooser fileChooser = new JFileChooser(getWorkingDirectory());
			fileChooser.setAcceptAllFileFilterUsed(true);
			fileChooser.setFileFilter(OpenCaseDialog.CASE_FILE_FILTER);
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
						saveCaseFile(saveAs);
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
	
	private void createNewElement() {
		if(getCurrentCase() == null || getCurrentNetwork() == null)
			return;
		ModelDBDialog dialog = new ModelDBDialog(this, ModelDBDialog.GET_MODEL_MODE, 
				getCurrentCase().getModelDB(), true, "Create new element");
		dialog.showDialog(900, 500);
		AbstractModelElementData selected = dialog.getSelectedElement();
		if(selected instanceof ModelData) {
			String modelID = ModelDBUtils.getFullElementID(selected);
			AbstractNetworkElement element = getCurrentNetwork().createElement(modelID);
			getCurrentNetwork().addElement(element);
			getCurrentNetwork().fireNetworkElementAdded(element);
			NetworkElementSelectionManager.selectionChanged(getCurrentViewer(), element);
		}
	}
	
	private void removeElement() {
		if(getCurrentViewer() == null || getCurrentViewer().getSelectionManager().getSelection() == null)
			return;
		Object selection = getCurrentViewer().getSelectionManager().getSelection();
		Vector<AbstractNetworkElement> elements2remove = new Vector<AbstractNetworkElement>();
		if(selection instanceof AbstractNetworkElement) {
			elements2remove.add((AbstractNetworkElement) selection);
		} else if(selection instanceof CombinedNetworkElement<?>) {
			for (AbstractNetworkElement element : ((CombinedNetworkElement<?>) selection).getNetworkElements()) {
				elements2remove.add(element);
			}
		}
		ElementSelectionDialog dialog = new ElementSelectionDialog(this, elements2remove, 
				"Delete Network Elements", "Select the network elements you want to remove from the current network");
		if(dialog.isCancelPressed())
			return;
		if(dialog.getSelectedElements().size() > 0) {
			getCurrentViewer().getSelectionManager().removeFromHistory(selection);
			for (AbstractNetworkElement element : dialog.getSelectedElements()) {
				getCurrentNetwork().removeElement(element);
				getCurrentNetwork().fireNetworkElementRemoved(element);
				getCurrentViewer().getSelectionManager().removeFromHistory(element);
			}
			getCurrentNetwork().fireNetworkChanged();
			getCurrentViewer().getSelectionManager().selectionChanged(null);
		}
	}
	
	public void showModelDBDialog() {
		if(getCurrentContainer() != null)
			getCurrentContainer().showModelDBDialog();
	}
	
	private void openProgressDialog(final long caseID) {
		final ProgressDialog progressDialog = new ProgressDialog();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					progressDialog.setVisible(true);
					Thread.sleep(500);
					while(success.get(caseID) != null) {
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
	
	private void closeProgram() {
		if(JOptionPane.showConfirmDialog(this, "Exit Power Flow Analyzer?", 
				"Confirm Exit", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			closeViewer();
			stopServer();
			destroyViewer();
		}
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
			callMatlabCommand("stoppfviewer", new Object[0], -1, 0, false);
	}
	
	private void callMatlabCommand(final String command, final Object[] parameters, final long caseID, final int returnValueCount, final boolean printOutput) {
		try {
			Matlab.whenMatlabIdle(new Runnable() {
				public void run() {
					try {
						if(cancelScriptExecution)
							return;
						if(printOutput)
							Matlab.mtFevalConsoleOutput(command, parameters, returnValueCount);
						else
							Matlab.mtFeval(command, parameters, returnValueCount);
					} catch(MatlabException e) {
						JOptionPane.showMessageDialog(PowerFlowAnalyzer.this, "Error: " + e.getMessage()
								+ "\n\nSee the Matlab console for more information.");
						cancelPowerFlow();
					} catch (Exception e) {
						JOptionPane.showMessageDialog(PowerFlowAnalyzer.this, "An error occurred while executing a matlab command: " + e
								+ "\n\nSee the Matlab console for more information.");
						cancelPowerFlow();
					}
				}
			});
		} catch (Exception e) {
			JOptionPane.showMessageDialog(PowerFlowAnalyzer.this, "An error occurred while calling matlab: " + e
					+ "\n\nSee the Matlab console for more information.");
			e.printStackTrace();
		}
	}
	
	private void addProgress(long caseID, long networkID) {
		List<Long> progress = success.get(caseID);
		if(progress == null) {
			progress = new ArrayList<Long>();
			success.put(caseID, progress);
		}
		if(progress.contains(networkID))
			return;
		progress.add(networkID);
		cancelScriptExecution = false;
	}
	
	private void removeProgress(long caseID, long networkID) {
		List<Long> progress = success.get(caseID);
		if(progress != null) {
			progress.remove(networkID);
			if(progress.isEmpty())
				success.remove(caseID);
		}
	}
	
	public void cancelPowerFlow() {
		success.clear();
		cancelScriptExecution = true;
	}
	
	public void updateNetwork(NetworkData networkData, boolean createNewNetwork) {
		try {
			PowerFlowCase caze = getCase(networkData.getCaseID());
			if(caze == null) {
				caze = getCurrentCase();
				System.err.println("Warning: network does not define a power flow case. Taking current case instead.");
			}
			if(caze == null) {
				int action = JOptionPane.showConfirmDialog(this, 
						"Do you want to import the network data as a new case?", "Create new case?", 
						JOptionPane.YES_NO_OPTION);
				if(action == JOptionPane.YES_OPTION) {
					createNewCase();
					caze = getCurrentCase();
				}
				if(caze == null) {
					cancelPowerFlow();
					return;
				}
			}
			if(createNewNetwork) {
				Network network = caze.addNetwork(networkData);
				checkForMissingCoordinates(network);
				caze.getViewer().updateActions();
				removeProgress(caze.getCaseID(), network.getInternalID());
			} else {// update network
				Network network = caze.getNetwork(networkData.getInternalID());
				if(network == null) {
					int action = JOptionPane.showConfirmDialog(this, 
							"Do you want to import the network data as a new network?", "Create new network?", 
							JOptionPane.YES_NO_OPTION);
					if(action == JOptionPane.NO_OPTION) {
						removeProgress(caze.getCaseID(), -1);
						return;
					} else {
						network = caze.addNetwork(networkData);
						checkForMissingCoordinates(network);
						caze.getViewer().updateActions();
					}
				} else {
					caze.setNetworkData(network, networkData);
					checkForMissingCoordinates(network);
					network.fireNetworkChanged();
					if(caze.getViewer().getViewer(network) != null)
						caze.getViewer().getViewer(network).getSelectionManager().clearHistory();
					caze.getViewer().repaintNetworkTree();
				}
				removeProgress(caze.getCaseID(), network.getInternalID());
			}
		} catch(Throwable t) {
			removeProgress(networkData.getCaseID(), networkData.getInternalID());
			JOptionPane.showMessageDialog(PowerFlowAnalyzer.this, "Calculation caused an error: " + t, "Error", JOptionPane.ERROR_MESSAGE);
			t.printStackTrace();
		}
		updateToolbarButtons();
		updateTabTitles();
	}
	
	private void openCase(PowerFlowCase caze) {
		try {
			for (Network network : caze.getNetworks(true)) {
				checkForMissingCoordinates(network);
			}
			CaseViewer viewer = new CaseViewer(caze);
			viewer.setWorkingDirectory(getWorkingDirectory());
			viewer.addActionUpdateListener(this);
			caze.setViewer(viewer);
			addCaseInternal(caze);
			String tabTitle = findName(caze);
			casesParent.addTab(tabTitle, viewer);
			casesParent.selectLastTab();
		} catch(Throwable t) {
			JOptionPane.showMessageDialog(PowerFlowAnalyzer.this, "Calculation caused an error: " + t, "Error", JOptionPane.ERROR_MESSAGE);
			t.printStackTrace();
		}
		updateToolbarButtons();
	}
	
	private PowerFlowCase getCase(long cazeID) {
		for (PowerFlowCase caze : cases) {
			if(caze.getCaseID() == cazeID)
				return caze;
		}
		return null;
	}
	
	private void addCaseInternal(PowerFlowCase caze) {
		while(getCase(maxCaseID) != null) {
			maxCaseID++;
		}
		caze.setCaseID(maxCaseID);
		cases.add(caze);
	}
	
	private void updateTabTitles() {
//		for (int i = 0; i < getViewerCount(); i++) {
//			Network n = getViewer(i).getNetwork();
//			networkTabs.setTitleAt(i, getScenarioName(n));
//		}
		for (int i = 0; i < cases.size(); i++) {
			PowerFlowCase caze = cases.get(i);
			String dirtySuffix = caze.isDirty() ? " *" : "";
			casesParent.setTitleAt(i, findName(caze) + dirtySuffix);
		}
	}
	
	private void checkForMissingCoordinates(Network network) {
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
	
	public String getWorkingDirectory() {
		if(workingDirectory == null)
			return System.getProperty("user.home");
		return workingDirectory;
	}
	
	public void updateWorkingDirectory() {
		callMatlabCommand("update_working_directory", new Object[] { }, -1, 0, true);
	}

	public void setWorkingDirectory(String workingDirectory) {
		setWorkingDirectory(workingDirectory, false);
	}

	public void setWorkingDirectory(String workingDirectory, boolean signalToUser) {
		this.workingDirectory = workingDirectory;
		if(getCurrentContainer() != null)
			getCurrentContainer().setWorkingDirectory(workingDirectory);
//		else if(signalToUser)
//			JOptionPane.showMessageDialog(this, "Matlab current folder changed to:\n" + workingDirectory);
//		workingDirectoryChanged = true;
	}
	
	public void setMatlabCurrentFolder(String workingDirectory) {
		File dir = new File(workingDirectory);
		if(dir.exists())
			callMatlabCommand("setworkingdirectory", new Object[] { workingDirectory }, -1, 0, true);
		else
			JOptionPane.showMessageDialog(PowerFlowAnalyzer.this, "The directory does not exist:\n" + workingDirectory, 
					"Error", JOptionPane.ERROR_MESSAGE);
	}
	
	private CaseViewer getCurrentContainer() {
		if(casesParent.isEmpty())
			return null;
		return cases.get(casesParent.getSelectedIndex()).getViewer();
	}
	
	public NetworkViewer getCurrentViewer() {
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
		updateTabTitles();
	}

	private void updateToolbarButtons() {
		boolean hasViewer = getCurrentViewer() != null;
		boolean hasCase = getCurrentCase() != null;
		boolean isMatlabEnv = environment == MATLAB_ENVIRONMENT;
//		toolbarButtons.get(ACTION_DIAGRAM_ADD).setEnabled(hasViewer);
		toolbarButtons.get(ACTION_TABLE_ADD).setEnabled(hasViewer);
		toolbarButtons.get(ACTION_CASE_SAVE).setEnabled(hasCase);
//		toolbarButtons.get(ACTION_DIAGRAM_EDIT).setEnabled(hasDiagrams);
//		toolbarButtons.get(ACTION_MAP_PROPERTIES).setEnabled(hasViewer);
		toolbarButtons.get(ACTION_EXECUTE_SCRIPT).setEnabled(isMatlabEnv && hasViewer);
		toolbarButtons.get(ACTION_NETWORK_ADD_ELEMENT).setEnabled(hasViewer);
		toolbarButtons.get(ACTION_NETWORK_REMOVE_ELEMENT).setEnabled(hasViewer
				&& getCurrentViewer().getSelectionManager().getSelection() != null);
//		toolbarButtons.get(ACTION_CASE_LAYOUT).setEnabled(hasViewer);
		toolbarButtons.get(ACTION_MODEL_DB_PROPERTIES).setEnabled(hasCase);
//		toolbarButtons.get(ACTION_PANEL_PROPERTIES).setEnabled(hasViewer);
		toolbarButtons.get(ACTION_SELECT_PREVIOUS).setEnabled(hasViewer
				&& getCurrentViewer().getSelectionManager().hasPreviousElement());
		toolbarButtons.get(ACTION_SELECT_NEXT).setEnabled(hasViewer
				&& getCurrentViewer().getSelectionManager().hasNextElement());
		actions.get(ACTION_CASE_SAVE).setEnabled(hasCase);
		actions.get(ACTION_CASE_SAVE_AS).setEnabled(hasCase);
		actions.get(ACTION_IMPORT_DB).setEnabled(hasCase);
		actions.get(ACTION_EXPORT_DB).setEnabled(hasCase);
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
	
	private String findName(PowerFlowCase caze) {
		String text = caze.getCaseFile() == null ? "Unsaved Case " : caze.getCaseFile().getName();
		String result = text;
		int count = 2;
//		for (int i = 0; i < casesParent.getTabCount(); i++) {
//			if()
//		}
//		for (PowerFlowCase c : cases) {
//			if()
//		}
		while(success.get(result) != null) {
			result = text + "[" + count + "]";
			count++;
		}
		return result;
	}
	
	class ProgressDialog extends JDialog {
		
		private JProgressBar progressBar;
		
		ProgressDialog() {
			super(PowerFlowAnalyzer.this, "Please wait...", false);
			progressBar = new JProgressBar();
			progressBar.setIndeterminate(true);
			JPanel contentPane = new JPanel(new BorderLayout());
			contentPane.setBorder(new EmptyBorder(10, 50, 20, 50));
			JLabel l = new JLabel("<html><b>Please wait while the requested operations are executed...");
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
