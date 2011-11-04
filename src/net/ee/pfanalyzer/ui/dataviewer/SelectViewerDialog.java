package net.ee.pfanalyzer.ui.dataviewer;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import net.ee.pfanalyzer.PowerFlowAnalyzer;
import net.ee.pfanalyzer.model.data.ModelData;
import net.ee.pfanalyzer.ui.dialog.BaseDialog;

public class SelectViewerDialog extends BaseDialog {

	private ModelData selectedViewer;
	
	public SelectViewerDialog(Window frame) {
		super(frame, "Select viewer");
		setText("<html><center><b>Select the viewer type and press OK.");
		
		Box contentPane = Box.createVerticalBox();
		ButtonGroup bg = new ButtonGroup();
		for (final ModelData script : PowerFlowAnalyzer.getConfiguration().getModels("viewer")) {
			JRadioButton button = new JRadioButton(script.getLabel());
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					selectedViewer = script;
				}
			});
			contentPane.add(button);
			bg.add(button);
		}
		addOKButton();
		addCancelButton();
		
		JPanel contentPaneResizer = new JPanel();
		contentPaneResizer.add(contentPane);
		
		getContentPane().add(contentPaneResizer, BorderLayout.CENTER);
	}

	@Override
	protected boolean checkInput() {
		return getSelectedViewer() != null;
	}
	
	public ModelData getSelectedViewer() {
		return selectedViewer;
	}
}
