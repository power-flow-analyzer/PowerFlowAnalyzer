package net.ee.pfanalyzer.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JPanel;

import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.data.ModelData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.ui.model.ModelElementPanel;
import net.ee.pfanalyzer.ui.parameter.ParameterMasterNetwork;
import net.ee.pfanalyzer.ui.util.Group;

public class ExecuteScriptDialog extends BaseDialog {

	private final static String DEFAULT_TEXT = "Select the desired script parameters and press OK.";
	private final static String ERROR_TEXT = "<html><font color=\"red\">All values must be filled in to proceed.";
	
	private Network network;
	private ModelData script;
	private boolean showDialog = true;
	
	public ExecuteScriptDialog(Frame frame, Network network, ModelData script) {
		super(frame, "Script parameters", true);
		this.network = network;
		this.script = script;
		setText(DEFAULT_TEXT);
		JPanel contentPane = new JPanel(new BorderLayout());
		
		ModelElementPanel parameterPanel = new ModelElementPanel(null);
//		parameterPanel.setShowNetworkParameters(true);
		parameterPanel.setEditable(true);// default setting
		parameterPanel.setParameterMaster(new ParameterMasterNetwork(network, true));//(new ParameterMasterModel(script));
		Group paramPanel = new Group("Script Parameters");
		for (NetworkParameter parameter : script.getParameter()) {
			if(ModelDBUtils.isInternalScriptParameter(parameter.getID())) {
				NetworkParameter propertyValue = ModelDBUtils.getParameterValue(script, parameter.getID());
				if(propertyValue != null && propertyValue.getValue() != null && propertyValue.getValue().trim().length() > 0) {
					network.setParameter(parameter.getID(), propertyValue.getValue());
					// only save the value in the network, do not show the property
					continue;
				}
			}
			NetworkParameter paramDef = ModelDBUtils.getParameterDefinition(script, parameter.getID());
			NetworkParameter propertyValue = network.getParameterValue(parameter.getID());
			if(propertyValue == null || propertyValue.getValue() == null) {
				propertyValue = ModelDBUtils.getParameterValue(script, parameter.getID());
				network.setParameter(parameter.getID(), propertyValue.getValue());
				propertyValue = network.getParameterValue(parameter.getID());
			}
			parameterPanel.addParameter(paramDef, propertyValue, paramPanel);
		}
		
		showDialog = parameterPanel.getParameterCount() > 0;
		addOKButton();
		addCancelButton();
		
		contentPane.add(paramPanel, BorderLayout.CENTER);
		setCenterComponent(contentPane);
	}
	
	public boolean shouldShowDialog() {
		return showDialog;
	}

	@Override
	protected boolean checkInput() {
		for (NetworkParameter parameter : script.getParameter()) {
			NetworkParameter value = network.getParameterValue(parameter.getID());
			if(value == null || value.getValue() == null) {
				setText(ERROR_TEXT);
				return false;
			}
		}
		setText(DEFAULT_TEXT);
		return true;
	}
}
