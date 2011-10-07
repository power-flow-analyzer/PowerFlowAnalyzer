package net.ee.pfanalyzer.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import net.ee.pfanalyzer.model.PowerFlowCase;
import net.ee.pfanalyzer.model.data.ModelData;

public class SelectScriptDialog extends BaseDialog {

	private ModelData selectedScript;
	
	public SelectScriptDialog(Frame frame, PowerFlowCase caze) {
		super(frame, "Select script");
		setText("<html><center><b>Select a script to be executed and press OK.");
		
		Box contentPane = Box.createVerticalBox();
		for (final ModelData script : caze.getModelDB().getScriptClass().getModel()) {
			JRadioButton button = new JRadioButton(script.getLabel());
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					selectedScript = script;
				}
			});
			if(caze.getModelDB().getScriptClass().getModel().size() == 1)
				button.doClick();
			contentPane.add(button);
		}
		addOKButton();
		addCancelButton();
		
		JPanel contentPaneResizer = new JPanel();
		contentPaneResizer.add(contentPane);
		
		getContentPane().add(contentPaneResizer, BorderLayout.CENTER);
	}

	@Override
	protected boolean checkInput() {
		return getSelectedScript() != null;
	}
	
	public ModelData getSelectedScript() {
		return selectedScript;
	}
}
