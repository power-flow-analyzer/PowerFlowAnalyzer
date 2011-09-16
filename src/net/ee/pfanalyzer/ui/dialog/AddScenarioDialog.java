package net.ee.pfanalyzer.ui.dialog;

import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import net.ee.pfanalyzer.model.Network;

public class AddScenarioDialog extends BaseDialog {

	public final static int SCENARIO_TYPE_CLONE_NETWORK = 0;
	public final static int SCENARIO_TYPE_SET_PARAMETER = 1;
	
	private int selected_scenario_type = SCENARIO_TYPE_CLONE_NETWORK;
	
	public AddScenarioDialog(Frame frame, Network network) {
		super(frame, "Create new scenario", true);
		setText("Select which type of scenario you want to create and press OK.");
		JPanel contentPane = new JPanel();
		
		Box radioPanel = Box.createVerticalBox();
		JRadioButton cloneNetworkOption = new JRadioButton(
				"<html><b>Clone the current network</b><br>" +
				"Choose this option if you want to make structural changes to the network,<br>" +
				"i.e. adding/removing network elements or changing their parameters/models.", true);
		cloneNetworkOption.setFont(cloneNetworkOption.getFont().deriveFont(Font.PLAIN));
		radioPanel.add(cloneNetworkOption);
		JRadioButton setScenarioParameterOption = new JRadioButton(
				"<html><b>Select a scenario using parameters</b><br>" +
				"Choose this option if you want to define a scenario by setting a scenario parameter.");
		setScenarioParameterOption.setFont(setScenarioParameterOption.getFont().deriveFont(Font.PLAIN));
		if(network.getScenarioParameters().isEmpty())
			setScenarioParameterOption.setEnabled(false);
		radioPanel.add(setScenarioParameterOption);
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(cloneNetworkOption);
		buttonGroup.add(setScenarioParameterOption);
		
		cloneNetworkOption.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selected_scenario_type = SCENARIO_TYPE_CLONE_NETWORK;
			}
		});
		setScenarioParameterOption.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selected_scenario_type = SCENARIO_TYPE_SET_PARAMETER;
			}
		});
		
		addOKButton();
		addCancelButton();
		
		contentPane.add(radioPanel);
		setCenterComponent(contentPane);
	}

	public int getSelectedScenarioType() {
		return selected_scenario_type;
	}
}
