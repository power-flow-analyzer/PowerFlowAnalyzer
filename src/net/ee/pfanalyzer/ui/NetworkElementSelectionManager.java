package net.ee.pfanalyzer.ui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import net.ee.pfanalyzer.model.util.ListUtils;
import net.ee.pfanalyzer.ui.NetworkViewer.ViewerFrame;
import net.ee.pfanalyzer.ui.util.IActionUpdater;

public class NetworkElementSelectionManager implements INetworkElementSelectionListener {

	public static NetworkElementSelectionManager getInstance(Component component) {
		if(component instanceof NetworkViewer)
			return ((NetworkViewer) component).getSelectionManager();
		if(component instanceof ViewerFrame)
			return ((ViewerFrame) component).getPowerFlowViewer().getSelectionManager();
		if(component.getParent() != null && component.getParent() instanceof Component)
			return getInstance((Component) component.getParent());
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
	
	public Object getSelection() {
		return oldSelection;
	}
	
	@Override
	public void selectionChanged(Object data) {
		selectionChanged(data, true);
	}
	
	public void removeFromHistory(Object data) {
		int index;
		while( ( index = ListUtils.getIndexOf(viewHistory, data)) > -1) {
			viewHistory.remove(index);
			if(index <= currentView)
				currentView--;
//			System.out.println("removed from history, currentView is " + currentView);
		}
		oldSelection = viewHistory.get(currentView);
		fireActionUpdate();
	}
	
	private void selectionChanged(Object selection, boolean addToHistory) {
		if(oldSelection == selection)
			return;
//		System.out.println("select " + selection);
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
