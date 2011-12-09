package net.ee.pfanalyzer.ui.viewer.network;

import java.awt.Window;

import net.ee.pfanalyzer.ui.dialog.ParameterChooserDialog;
import net.ee.pfanalyzer.ui.parameter.ParameterMasterViewer;
import net.ee.pfanalyzer.ui.viewer.element.ModelElementPanel;

public class ExportImageDialog extends ParameterChooserDialog {

	protected ExportImageDialog(Window frame) {
		super(frame, "Export image", true);
		setText("<html><b>" + "Enter the image properties and press OK.");
		
		ModelElementPanel parameterPanel = new ModelElementPanel(null);
//		parameterPanel.setShowNetworkParameters(true);
		parameterPanel.setEditable(true);// default setting
//		parameterPanel.setParameterMaster(new ParameterMasterViewer(caze, viewer, true));
//		parameterPanel.setShowFullParameterInfo(false);
//		addParameters(viewer.getDataDefinition(), parameterPanel);

		addOKButton();
		addCancelButton();
		
		finishLayout();
	}

}
