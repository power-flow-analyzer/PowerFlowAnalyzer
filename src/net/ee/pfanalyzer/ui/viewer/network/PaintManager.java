package net.ee.pfanalyzer.ui.viewer.network;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import net.ee.pfanalyzer.model.DatabaseChangeEvent;
import net.ee.pfanalyzer.ui.viewer.IPaintListener;

public class PaintManager {

	private NetworkMapViewer viewer;
	private BufferedImage image;
	private BackgroundThread backgroundThread;
	private List<IPaintListener> paintListeners = new ArrayList<IPaintListener>();
	
	public PaintManager(NetworkMapViewer viewer) {
		this.viewer = viewer;
	}
	
	public void addPaintListener(IPaintListener listener) {
		paintListeners.add(listener);
	}
	
	public void removePaintListener(IPaintListener listener) {
		paintListeners.remove(listener);
	}
	
	private void paintListeners(Graphics2D g2d) {
		for (IPaintListener listener : paintListeners) {
			if(listener.isActive())
				listener.paint(g2d);
		}
	}
	
	public void updateListeners(DatabaseChangeEvent event) {
		boolean needsUpdate = false;
		for (IPaintListener listener : paintListeners) {
			if(listener.isActive() && listener.needsUpdate(event)) {
				needsUpdate = true;
				listener.update();
				break;
			}
		}
		if(needsUpdate)
			updateBackgroundImage();
	}
	
	public void setActive(String paintID, boolean flag) {
		for (IPaintListener listener : paintListeners) {
			if(listener.getPaintID().equals(paintID))
				listener.setActive(flag);
		}
	}
	
	public void drawBackground(Graphics2D g2d) {
//		long startTime = System.currentTimeMillis();
		if(image == null)
			updateBackgroundImage();
		else
			g2d.drawImage(image, 0, 0, Color.WHITE, null);// TODO middleColor

//		long stopTime = System.currentTimeMillis();
//		g2d.drawString("Time: " + (stopTime - startTime) + "ms", 10, viewer.getHeight() - 20);
	}
	
	public void updateBackgroundImage() {
		if(backgroundThread != null)
			backgroundThread.updateImage();
		else {
			backgroundThread = new BackgroundThread();
			backgroundThread.start();
		}
	}
	
	private synchronized void createBackgroundImage() {
		if(viewer.getWidth() == 0 || viewer.getHeight() == 0)
			return;
		BufferedImage newImage = new BufferedImage(viewer.getWidth(), viewer.getHeight(), BufferedImage.TYPE_INT_ARGB);
		// set max acceleration priority for background image
		newImage.setAccelerationPriority(1f);// TODO acceleration runterschalten?
		Graphics2D g2d = newImage.createGraphics();
		paintListeners(g2d);
		image = newImage;
	}

	class BackgroundThread extends Thread {
		
		private boolean updateImage = true;
		private Object lock = new Object();
		
		BackgroundThread() {
			setPriority(MIN_PRIORITY);
		}
		
		@Override
		public void run() {
			while (shouldRun()) {
				isRunning();
				// halt this thread for a moment to give the gui a chance to update
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// do nothing
				}
				updateImage = false;
				createBackgroundImage();
				viewer.repaint();
			}
			backgroundThread = null;
		}
		
		private void isRunning() {
			synchronized(lock) {
				updateImage = false;
			}
		}
		
		private boolean shouldRun() {
			synchronized(lock) {
				return updateImage;
			}
		}
		
		public void updateImage() {
			synchronized(lock) {
				updateImage = true;
			}
		}
	}
}
