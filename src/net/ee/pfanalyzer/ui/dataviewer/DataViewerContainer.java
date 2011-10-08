package net.ee.pfanalyzer.ui.dataviewer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import net.ee.pfanalyzer.PowerFlowAnalyzer;
import net.ee.pfanalyzer.model.PowerFlowCase;

public class DataViewerContainer extends JPanel {
	
	private INetworkDataViewer viewer;
	private PowerFlowCase powerFlowCase;

	public DataViewerContainer(INetworkDataViewer viewer, PowerFlowCase caze) {
		super(new BorderLayout());
		this.viewer = viewer;
		this.powerFlowCase = caze;
		
		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		final JButton editButton = PowerFlowAnalyzer.createButton("Edit viewer properties", 
				"pencil.png", "Edit viewer properties", false);
		editButton.setMargin(new Insets(2, 2, 1, 1));
		editButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showPropertiesDialog();
			}
		});
		buttonPane.add(editButton);

		add(buttonPane, BorderLayout.EAST);
		add(new JScrollPane(viewer.getComponent()), BorderLayout.CENTER);
	}
	
	private void showPropertiesDialog() {
		DataViewerDialog dialog = new DataViewerDialog(SwingUtilities.getWindowAncestor(this), 
				"Viewer Properties", getViewer().getViewerData(), powerFlowCase);
		dialog.showDialog(-1, -1);
		if(dialog.isOkPressed())
			getViewer().refresh();
	}

	public INetworkDataViewer getViewer() {
		return viewer;
	}
}
