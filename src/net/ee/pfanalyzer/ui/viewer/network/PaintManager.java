package net.ee.pfanalyzer.ui.viewer.network;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.DatabaseChangeEvent;
import net.ee.pfanalyzer.ui.timer.DisplayTimer;
import net.ee.pfanalyzer.ui.viewer.IPaintListener;
import net.ee.pfanalyzer.ui.viewer.network.painter.AreaPainter;
import net.ee.pfanalyzer.ui.viewer.network.painter.BranchPainter;
import net.ee.pfanalyzer.ui.viewer.network.painter.BusPainter;
import net.ee.pfanalyzer.ui.viewer.network.painter.MarkerPainter;

public class PaintManager {

	private NetworkMapViewer viewer;
	private BufferedImage image;
	private BackgroundThread backgroundThread;
	private List<IPaintListener> asyncPaintListeners = new ArrayList<IPaintListener>();
	private List<IPaintListener> syncPaintListeners = new ArrayList<IPaintListener>();
	private boolean isOffscreenPainting = false;
	
	private BusPainter busPainter;
	private BranchPainter branchPainter;
	private MarkerPainter markerPainter;
	private AreaPainter areaPainter;

	public PaintManager(NetworkMapViewer viewer) {
		this.viewer = viewer;
		busPainter = new BusPainter(viewer.getNetwork(), viewer);
		branchPainter = new BranchPainter(viewer.getNetwork(), viewer);
		markerPainter = new MarkerPainter(viewer.getNetwork(), viewer);
		areaPainter = new AreaPainter(viewer.getNetwork(), viewer);
		// paint network elements synchronously
		addSyncPaintListener(busPainter);
		addSyncPaintListener(branchPainter);
		addSyncPaintListener(markerPainter);
		// paint areas asynchronously 
		addAsyncPaintListener(areaPainter);
	}
	
	public void addAsyncPaintListener(IPaintListener listener) {
		asyncPaintListeners.add(listener);
		Collections.sort(asyncPaintListeners, new PaintListenerComparator());
	}
	
	public void removeAsyncPaintListener(IPaintListener listener) {
		asyncPaintListeners.remove(listener);
	}
	
	private void paintAsyncListeners(Graphics2D g2d) {
		for (IPaintListener listener : asyncPaintListeners) {
			if(listener.isActive())
				listener.paint(g2d);
		}
	}
	
	public void addSyncPaintListener(IPaintListener listener) {
		syncPaintListeners.add(listener);
		Collections.sort(syncPaintListeners, new PaintListenerComparator());
	}
	
	public void removeSyncPaintListener(IPaintListener listener) {
		syncPaintListeners.remove(listener);
	}
	
	private void paintSyncListeners(Graphics2D g2d) {
		for (IPaintListener listener : syncPaintListeners) {
			if(listener.isActive())
				listener.paint(g2d);
		}
	}
	
	public void checkUpdateListeners(DatabaseChangeEvent event) {
		boolean needsUpdate = false;
		for (IPaintListener listener : asyncPaintListeners) {
			if(listener.isActive() && listener.needsUpdate(event)) {
				needsUpdate = true;
				listener.update();
				break;
			}
		}
		if(needsUpdate)
			update();
	}
	
	public void setActive(String paintID, boolean flag) {
		for (IPaintListener listener : asyncPaintListeners) {
			if(listener.getPaintID().equals(paintID))
				listener.setActive(flag);
		}
	}
	
	public List<IPaintListener> getActivePaintListeners() {
		List<IPaintListener> activeListeners = new ArrayList<IPaintListener>();
		for (IPaintListener listener : asyncPaintListeners) {
			if(listener.isActive())
				activeListeners.add(listener);
		}
		return activeListeners;
	}
	
	public void paint(Graphics2D g2d) {
		if(isOffscreenPainting()) {
			// draw image directly in offscreen mode
			// do not create image -> save memory
			paintAsyncListeners(g2d);
			paintSyncListeners(g2d);
		} else {
			// determine what to draw
			if(image == null) {
				update();
				paintSyncListeners(g2d);
			} else if(isRunning()) {
				g2d.setColor(Color.BLACK);
				String text = "Calculating...";
				Rectangle2D textBounds = g2d.getFontMetrics().getStringBounds(text, g2d);
				g2d.drawString(text, (int) (viewer.getWidth() - textBounds.getWidth()) - 5, 
						(int) (viewer.getHeight() - textBounds.getHeight()));
				paintSyncListeners(g2d);
			} else {
				if(image != null)
					g2d.drawImage(image, 0, 0, Color.WHITE, null);
//				paintAsyncListeners(g2d);
				paintSyncListeners(g2d);
			}
		}
	}
	
	public AbstractNetworkElement getObjectFromScreen(int x, int y) {
		AbstractNetworkElement element = null;
		element = busPainter.getObjectFromScreen(x, y);
		if(element != null)
			return element;
		element = markerPainter.getObjectFromScreen(x, y);
		if(element != null)
			return element;
		element = branchPainter.getObjectFromScreen(x, y);
		if(element != null)
			return element;
		return null;
	}
	
	public boolean isRunning() {
		return backgroundThread != null;
	}
	
	public void update() {
		updateSyncListeners();
		// only update something when not in offscreen mode
		if(isOffscreenPainting() == false) {
			if(isRunning())
				backgroundThread.fireUpdateImage();
			else {
				backgroundThread = new BackgroundThread();
				backgroundThread.start();
			}
		}
	}
	
	public void fireDisplayTimeChanged(DisplayTimer timer) {
//		// update sync listeners
//		for (IPaintListener painter : syncPaintListeners)
//			painter.updateDisplayTime(timer);
		createBufferedImage();
	}
	
	private void updateSyncListeners() {
		for (IPaintListener painter : syncPaintListeners) {
			painter.update();
		}
	}
	
	private void updateAsyncListeners() {
		for (IPaintListener painter : asyncPaintListeners) {
			painter.update();
		}
	}
	
	private synchronized void createBufferedImage() {
		if(viewer.getWidth() == 0 || viewer.getHeight() == 0)
			return;
		// TODO nicht jedes mal eine neue Instanz erstellen
		BufferedImage newImage = new BufferedImage(viewer.getWidth(), viewer.getHeight(), BufferedImage.TYPE_INT_ARGB);
		// set max acceleration priority for background image
		newImage.setAccelerationPriority(1f);// TODO acceleration runterschalten?
		Graphics2D g2d = newImage.createGraphics();
		paintAsyncListeners(g2d);
//		paintSyncListeners(g2d); FIXME necessary?
		image = newImage;
	}

	public boolean isOffscreenPainting() {
		return isOffscreenPainting;
	}

	public boolean isOnscreenPainting() {
		return ! isOffscreenPainting;
	}

	public void setOffscreenPainting(boolean isOffscreenPainting) {
		this.isOffscreenPainting = isOffscreenPainting;
		if(isOffscreenPainting) {
			backgroundThread = null;
			image = null;
		}
	}

	public BranchPainter getBranchPainter() {
		return branchPainter;
	}

	class BackgroundThread extends Thread {
		
		private boolean updateImage = true;
		private Object lock = new Object();
		
		BackgroundThread() {
			setPriority(MIN_PRIORITY);
		}
		
		@Override
		public void run() {
			while (needsUpdate()) {
				setRunning();
				// halt this thread for a moment to give the gui a chance to update
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// do nothing
				}
//				updateImage = false; FIXME redundant; same as setRunning()?
				updateAsyncListeners();
				if(isOnscreenPainting())
					createBufferedImage();
			}
			backgroundThread = null;
			if(isOnscreenPainting())
				viewer.repaint();
		}
		
		private void setRunning() {
			synchronized(lock) {
				updateImage = false;
			}
		}
		
		private boolean needsUpdate() {
			synchronized(lock) {
				return updateImage;
			}
		}
		
		public void fireUpdateImage() {
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
