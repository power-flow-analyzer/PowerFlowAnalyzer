package net.ee.pfanalyzer.ui.viewer;

import java.awt.Window;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JTabbedPane;

import net.ee.pfanalyzer.model.PowerFlowCase;
import net.ee.pfanalyzer.model.data.AbstractModelElementData;
import net.ee.pfanalyzer.model.data.ModelClassData;
import net.ee.pfanalyzer.model.data.ModelData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.ui.dialog.BaseDialog;
import net.ee.pfanalyzer.ui.model.ModelElementPanel;
import net.ee.pfanalyzer.ui.parameter.ParameterCheckBox;
import net.ee.pfanalyzer.ui.parameter.ParameterMasterViewer;
import net.ee.pfanalyzer.ui.util.Group;
import net.ee.pfanalyzer.ui.viewer.network.NetworkMapViewer;

public class DataViewerDialog extends BaseDialog {
	
	private DataViewerConfiguration viewer;
	private Map<String, Group> groups = new HashMap<String, Group>();
	private Map<String, Box> tabs = new HashMap<String, Box>();
	private JTabbedPane tabbedPane;

	public DataViewerDialog(Window frame, String title, DataViewerConfiguration viewer, PowerFlowCase caze, boolean canCancel) {
		super(frame, title, true);
		this.viewer = viewer;
		setText("<html><b>" + "Enter the properties for this viewer.");
		
		tabbedPane = new JTabbedPane();

		ModelElementPanel parameterPanel = new ModelElementPanel(null);
//		parameterPanel.setShowNetworkParameters(true);
		parameterPanel.setEditable(true);// default setting
		parameterPanel.setParameterMaster(new ParameterMasterViewer(caze, viewer, true));
		parameterPanel.setShowFullParameterInfo(false);
		addParameters(viewer.getDataDefinition(), parameterPanel);
		if(viewer.getModelID().startsWith(NetworkMapViewer.BASE_NETWORK_VIEWER_ID)
				&& caze.getModelDB().getOutlineClass() != null) {
			addOutlineParameters(caze.getModelDB().getOutlineClass(), parameterPanel);
		}
		
		if(canCancel) {
			addOKButton();
			addCancelButton();
		} else
			addButton("Close", true, true);
		
		finishLayout();
		setCenterComponent(tabbedPane);
	}
	
	private void addParameters(AbstractModelElementData element, ModelElementPanel parameterPanel) {
		if(element.getParent() != null)
			addParameters(element.getParent(), parameterPanel);
		for (NetworkParameter paramDef : element.getParameter()) {
			if(ModelDBUtils.isInternalViewerParameter(paramDef.getID()))
				continue;
			NetworkParameter parameterValue = viewer.getParameter(paramDef.getID(), true);
			if(parameterValue.getValue() == null) {
				NetworkParameter modelValue = ModelDBUtils.getParameterValue(element, paramDef.getID());
				if(modelValue != null)
					parameterValue.setValue(modelValue.getValue());
			}
			parameterValue = viewer.getParameter(paramDef.getID(), true);
			String groupTitle = paramDef.getDisplay() == null ? null : paramDef.getDisplay().getGroup();
			String sectionTitle = paramDef.getDisplay() == null ? null : paramDef.getDisplay().getSection();
			parameterPanel.addParameter(paramDef, parameterValue, getGroup(groupTitle, sectionTitle));
		}
	}
	
	private void addOutlineParameters(ModelClassData outlines, ModelElementPanel parameterPanel) {
		for (ModelData outline : outlines.getModel()) {
			String viewerParamName = "OUTLINE." + outline.getID();
			NetworkParameter enablement = viewer.getParameter(viewerParamName, true);
			if(enablement.getValue() == null) {
				NetworkParameter modelValue = ModelDBUtils.getParameterValue(outline, "ENABLED");
				if(modelValue != null && modelValue.getValue() != null)
					enablement.setValue(modelValue.getValue());
				else
					enablement.setValue("false");
			}
			ParameterCheckBox box = new ParameterCheckBox(
					parameterPanel.getParameterMaster(), enablement, enablement);
			box.setShowFullParameterInfo(false);
			getGroup("Outlines", "Outlines").add(box);
			box.getCheckBox().setText(outline.getLabel());
		}
	}
	
	private Group getGroup(String name, String section) {
		Group group = groups.get(name);
		if(group == null) {
			group = new Group(name);
			groups.put(name, group);
			getSection(section).add(group);
		}
		return group;
	}
	
	private Box getSection(String name) {
		Box box = tabs.get(name);
		if(box == null) {
			box = Box.createVerticalBox();
			box.setOpaque(true);
			tabs.put(name, box);
			String title = name == null || name.isEmpty() ? "Common" : name;
			tabbedPane.addTab(title, box);
		}
		return box;
	}
	
	private void finishLayout() {
		for (Box box : tabs.values()) {
			box.add(Box.createVerticalGlue());
			box.add(Box.createVerticalGlue());
			box.add(Box.createVerticalGlue());
		}
	}
	
	@Override
	protected boolean checkInput() {
//		if(titleField.getText().length() > 0 && filterField.getText().length() > 0) {
//			setText(DEFAULT_TEXT);
//			return true;
//		} else {
//			setText(ERROR_TEXT);
//			return false;
//		}
		return true;
	}
}
