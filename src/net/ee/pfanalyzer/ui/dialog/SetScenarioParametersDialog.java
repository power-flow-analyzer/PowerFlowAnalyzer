package net.ee.pfanalyzer.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JPanel;

import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.data.ModelClassData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.data.NetworkParameterPurposeRestriction;
import net.ee.pfanalyzer.ui.model.ModelElementPanel;
import net.ee.pfanalyzer.ui.parameter.ParameterMasterNetwork;
import net.ee.pfanalyzer.ui.util.Group;

public class SetScenarioParametersDialog extends BaseDialog {

	public SetScenarioParametersDialog(Frame frame, Network network) {
		super(frame, "Set scenario parameters", true);
		setText("Select the desired scenario parameters and press OK.");
		JPanel contentPane = new JPanel(new BorderLayout());
		
		ModelElementPanel parameterPanel = new ModelElementPanel(null);
		parameterPanel.setEditable(true);// default setting
		parameterPanel.setParameterMaster(new ParameterMasterNetwork(network, false));
		ModelClassData clazz = network.getGlobalParameterClass();
		Group paramPanel = new Group("Scenario Parameters");
		for (NetworkParameter parameter : clazz.getParameter()) {
			boolean isScenarioParam = NetworkParameterPurposeRestriction.SCENARIO.equals(parameter.getPurpose());
			if(isScenarioParam == false)
				continue;
			NetworkParameter propertyValue = network.getParameterValue(parameter.getID());
			parameterPanel.addParameter(parameter, propertyValue, paramPanel);
		}
		
		addOKButton();
		addCancelButton();
		
		contentPane.add(paramPanel, BorderLayout.CENTER);
		setCenterComponent(contentPane);
	}

}
