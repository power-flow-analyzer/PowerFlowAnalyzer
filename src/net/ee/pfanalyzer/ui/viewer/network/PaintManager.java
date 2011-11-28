package net.ee.pfanalyzer.ui.viewer.network;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
		Collections.sort(paintListeners, new PaintListenerComparator());
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
		if(image == null)
			updateBackgroundImage();
		else if(isRunning()) {
			g2d.setColor(Color.BLACK);
			String text = "Calculating...";
			Rectangle2D textBounds = g2d.getFontMetrics().getStringBounds(text, g2d);
			g2d.drawString(text, (int) (viewer.getWidth() - textBounds.getWidth()) - 5, 
					(int) (viewer.getHeight() - textBounds.getHeight()));
		} else
			g2d.drawImage(image, 0, 0, Color.WHITE, null);// TODO middleColor
	}
	
	public boolean isRunning() {
		return backgroundThread != null;
	}
	
	public void updateBackgroundImage() {
		if(isRunning())
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
//				viewer.repaint();
			}
			backgroundThread = null;
			viewer.repaint();
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
	
	class PaintListenerComparator implements Comparator<IPaintListener> {
		@Override
		public int compare(IPaintListener l1, IPaintListener l2) {
			if(l1.getLayer() == l2.getLayer())
				return 0;
			if(l1.getLayer() < l2.getLayer())
				return -1;
			return 1;
		}
	}
}
