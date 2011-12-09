package net.ee.pfanalyzer.ui.viewer.network;

import java.awt.Window;

import net.ee.pfanalyzer.ui.dialog.ParameterChooserDialog;
import net.ee.pfanalyzer.ui.parameter.ParameterFileFilter;
import net.ee.pfanalyzer.ui.parameter.ParameterMasterViewerDialog;
import net.ee.pfanalyzer.ui.viewer.DataViewerConfiguration;
import net.ee.pfanalyzer.ui.viewer.DataViewerDialog;
import net.ee.pfanalyzer.ui.viewer.element.ModelElementPanel;

public class ExportImageDialog extends ParameterChooserDialog {
	
	public final static String DIALOG_ID = "dialog.viewer.network.export_image";
	
	private static ParameterFileFilter JPEG_FILE_FILTER = new ParameterFileFilter(
			"JPEG Images", new String[] { "jpg", "jpeg" });	
	private DataViewerDialog dialog;

	protected ExportImageDialog(Window frame, DataViewerConfiguration configuration) {
		super(frame, "Export image", true);
		setText("<html><b>" + "Enter the image properties and press OK.");
		
		dialog = configuration.getDialogData(DIALOG_ID, true);
		
		ModelElementPanel parameterPanel = new ModelElementPanel(null) {
			@Override
			protected ParameterFileFilter getFileFilter(String parameterID) {
				return JPEG_FILE_FILTER;
			}
		};
		parameterPanel.setEditable(true);// default setting
		parameterPanel.setParameterMaster(new ParameterMasterViewerDialog(dialog));
		parameterPanel.setShowFullParameterInfo(false);
		addParameters(dialog.getDialogModel(), parameterPanel);

		addOKButton();
		addCancelButton();
		
		finishLayout();
	}
	
	@Override
	protected boolean checkInput() {
		Integer imageWidth = getDialogData().getIntParameter("IMAGE_WIDTH");
		Integer imageHeight = getDialogData().getIntParameter("IMAGE_HEIGHT");
		Double quality = getDialogData().getDoubleParameter("IMAGE_COMPRESSION_QUALITY");
		String imagePath = getDialogData().getTextParameter("IMAGE_FILE");
		if(imageWidth == null || imageHeight == null || quality == null || imagePath == null)
			return false;
		return true;
	}

	public DataViewerDialog getDialogData() {
		return dialog;
	}
}
