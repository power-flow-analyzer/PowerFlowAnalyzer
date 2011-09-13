package net.ee.pfanalyzer.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import net.ee.pfanalyzer.ui.util.IActionUpdater;

public class NetworkElementSelectionManager implements INetworkElementSelectionListener {

	public static NetworkElementSelectionManager getInstance(JComponent component) {
		if(component instanceof PowerFlowViewer)
			return ((PowerFlowViewer) component).getSelectionManager();
		if(component.getParent() != null && component.getParent() instanceof JComponent)
			return getInstance((JComponent) component.getParent());
		return null;
	}
	
	public static void selectionChanged(JComponent component, Object data) {
		NetworkElementSelectionManager manager = getInstance(component);
		if(manager != null)
			manager.selectionChanged(data);
	}
	
	private List<INetworkElementSelectionListener> networkElementListeners = new ArrayList<INetworkElementSelectionListener>();
	private List<IActionUpdater> actionUpdater = new ArrayList<IActionUpdater>();
	private ArrayList<Object> viewHistory = new ArrayList<Object>();
	private int currentView = 0;
	private Object oldSelection;
	
	public NetworkElementSelectionManager() {
		clearHistory();
	}
	
	public void clearHistory() {
		currentView = 0;
		viewHistory.clear();
		viewHistory.add(null);// add the whole network as first entry in history
	}
	
	@Override
	public void selectionChanged(Object data) {
		selectionChanged(data, true);
	}
	
	private void selectionChanged(Object selection, boolean addToHistory) {
		if(oldSelection == selection)
			return;
		oldSelection = selection;
		if(addToHistory) {
			for (int i = 0, count = viewHistory.size() - currentView - 1; i < count; i++) {
				viewHistory.remove(currentView + 1);
//				System.out.println("remove view from history: " + (currentView + 1 + i));
			}
			viewHistory.add(selection);
			currentView++;
//			System.out.println("added to history, now is at index " + currentView);
		}
		for (INetworkElementSelectionListener listener : networkElementListeners) {
			listener.selectionChanged(selection);
		}
		fireActionUpdate();
	}
	
	public void addActionUpdateListener(IActionUpdater listener) {
		actionUpdater.add(listener);
	}
	
	public void removeActionUpdateListener(IActionUpdater listener) {
		actionUpdater.remove(listener);
	}
	
	private void fireActionUpdate() {
		for (IActionUpdater listener : actionUpdater) {
			listener.updateActions();
		}
	}

	public void addNetworkElementSelectionListener(INetworkElementSelectionListener listener) {
		networkElementListeners.add(listener);
	}
	
	public void removeNetworkElementSelectionListener(INetworkElementSelectionListener listener) {
		networkElementListeners.remove(listener);
	}
	
	public boolean hasPreviousElement() {
		return currentView > 0;
	}
	
	public void showPreviousElement() {
		if(currentView <= 0)
			return;
		currentView--;
		selectionChanged(viewHistory.get(currentView), false);
//		System.out.println("previous, now is at index " + currentView);
	}
	
	public boolean hasNextElement() {
		return currentView < viewHistory.size() - 1;
	}
	
	public void showNextElement() {
		if(currentView >= viewHistory.size() - 1)
			return;
		currentView++;
		selectionChanged(viewHistory.get(currentView), false);
//		System.out.println("next, now is at index " + currentView);
	}
}
