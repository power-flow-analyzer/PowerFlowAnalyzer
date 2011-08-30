package net.ee.pfanalyzer.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import net.ee.pfanalyzer.model.scenario.Scenario;
import net.ee.pfanalyzer.model.scenario.ScenarioParameter;

public class ScenarioSelectionDialog extends BaseDialog {

	private Scenario scenario;
	
	public ScenarioSelectionDialog(Scenario scenario) {
		this(null, scenario);
	}
	
	public ScenarioSelectionDialog(Frame frame, Scenario scenario) {
		super(frame, "Select a scenario");
		setText("<html><center><b>Select the parameters below and press OK.");
		this.scenario = scenario;
		
		Box contentPane = Box.createVerticalBox();
		for (int i = 0; i < scenario.getSubScenarioCount(); i++) {
			JPanel subScenarioPane = new JPanel(new GridLayout(0, 2));
			subScenarioPane.setBorder(new TitledBorder(scenario.getSubScenarioLabel(i)));
			for (ScenarioParameter parameter : scenario.getSubScenarioParameters(i)) {
				if(parameter.isHidden())
					continue;
				String[] labels = new String[parameter.getOptionCount()];
				for (int o = 0; o < parameter.getOptionCount(); o++) {
					parameter.setSelectedOption(0);// default value
					labels[o] = parameter.getOptionLabel(o);
				}
				if(parameter.isListType()) {
					subScenarioPane.add(new JLabel(parameter.getLabel()));
					ParameterValueBox box = new ParameterValueBox(parameter.getID(), labels);
					subScenarioPane.add(box);
				} else if(parameter.isCheckBoxType()) {
					ParameterCheckBox box = new ParameterCheckBox(parameter.getID(), parameter.getLabel(), labels);
					subScenarioPane.add(box);
					subScenarioPane.add(new JPanel());
				}
			}
			contentPane.add(subScenarioPane);
		}
		addOKButton();
		addCancelButton();
		
		JPanel contentPaneResizer = new JPanel();
		contentPaneResizer.add(contentPane);
		
		getContentPane().add(contentPaneResizer, BorderLayout.CENTER);
		showDialog(-1, -1);
	}
		
	public Scenario getScenario() {
		return scenario;
	}
	
	class ParameterValueBox extends JComboBox implements ActionListener {
		
		private String parameterID;
		
		ParameterValueBox(String parameterID, String[] labels) {
			super(labels);
			this.parameterID = parameterID;
			addActionListener(this);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			getScenario().getParameter(parameterID).setSelectedOption(getSelectedIndex());
		}
		
		public String getParameterID() {
			return parameterID;
		}
	}
	
	class ParameterCheckBox extends JCheckBox implements ActionListener {
		
		private String parameterID;
		private String[] labels;
		
		ParameterCheckBox(String parameterID, String label, String[] labels) {
			super(label);
			this.parameterID = parameterID;
			this.labels = labels;
			addActionListener(this);
			if(labels.length > 0 && isSelectedText(labels[0]))
				setSelected(true);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			int option = -1;
			for (int i = 0; i < labels.length; i++) {
				if(isSelected()) {
					if(isSelectedText(labels[i])) {
						option = i;
						break;
					}
				} else {
					if(isDeselectedText(labels[i])) {
						option = i;
						break;
					}
				}
			}
			if(option > -1)
				getScenario().getParameter(parameterID).setSelectedOption(option);
		}
		
		private boolean isSelectedText(String text) {
			return text.equalsIgnoreCase("yes") 
					|| text.equalsIgnoreCase("true")
						|| text.equalsIgnoreCase("1");
		}
		
		private boolean isDeselectedText(String text) {
			return text.equalsIgnoreCase("no") 
					|| text.equalsIgnoreCase("false")
						|| text.equalsIgnoreCase("0");
		}
		
		public String getParameterID() {
			return parameterID;
		}
	}
}
