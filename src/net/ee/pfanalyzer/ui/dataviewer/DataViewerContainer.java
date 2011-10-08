package net.ee.pfanalyzer.ui.dataviewer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.ee.pfanalyzer.PowerFlowAnalyzer;
import net.ee.pfanalyzer.ui.util.SwingUtils;

public class DataViewerContainer extends JPanel {
	
	private INetworkDataViewer viewer;

	public DataViewerContainer(INetworkDataViewer viewer) {
		super(new BorderLayout());
		this.viewer = viewer;
		
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
		DataViewerDialog dialog = new DataViewerDialog(SwingUtils.getTopLevelFrame(this), 
				"Viewer Properties", getViewer().getViewerData());
		dialog.showDialog(-1, -1);
		if(dialog.isOkPressed())
			getViewer().refresh();
	}

	public INetworkDataViewer getViewer() {
		return viewer;
	}
}
