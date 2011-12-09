package net.ee.pfanalyzer.ui.viewer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import net.ee.pfanalyzer.PowerFlowAnalyzer;
import net.ee.pfanalyzer.ui.dialog.ProgressDialog;
import net.ee.pfanalyzer.ui.viewer.network.NetworkMapViewer;

public class DataViewerImageExport implements Runnable {

	private NetworkMapViewer networkViewer;
	private int width, height;
	private float quality;
	private File imageFile;
	private boolean isExporting = false;
	
	public DataViewerImageExport(NetworkMapViewer viewer, int width, int height, float quality, File imageFile) {
		this.networkViewer = viewer;
		this.width = width;
		this.height = height;
		this.quality = quality;
		this.imageFile = imageFile;
	}
	
	public void exportImage() {
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		exportViewerAsImage();
	}
	
	private void exportViewerAsImage() {
		// show progress dialog
		isExporting = true;
		new ProgressDialog() {
			@Override
			protected boolean showProgressDialog() {
				return isExporting;
			}
		};
		// wait a little to give the gui the chance to update
		// otherwise the viewer will not be properly initialized
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
		// create offscreen viewer
		INetworkDataViewer viewer = networkViewer.createOffscreenViewer();
		try {
			// initialize it
			networkViewer.initializeOffscreenViewer(viewer, width, height);
			// create image to draw onto
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = img.createGraphics();
			// paint the image
			viewer.paintViewer(g2d);
			// write image file
			Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
			ImageWriter writer = writers.next();
			// set compression quality
			ImageWriteParam param = writer.getDefaultWriteParam();
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(quality);
			// write image to file
			FileImageOutputStream out = new FileImageOutputStream(imageFile);
			writer.setOutput(out);
			writer.write(null, new IIOImage(img, null, null), param);
			writer.dispose();
			out.close();
		} catch (IOException e) {
			PowerFlowAnalyzer.getInstance().setError("Cannot write image file: " + e.getMessage());
			e.printStackTrace();
		} finally {
			isExporting = false;
			viewer.dispose();
		}
	}
}
