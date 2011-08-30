package net.ee.pfanalyzer.model;

import java.util.ArrayList;
import java.util.List;

public abstract class CombinedNetworkElement<TYPE extends AbstractNetworkElement> {

	private List<TYPE> networkElements = new ArrayList<TYPE>();
	private int index;
	
	public abstract String getLabel();
	
	public abstract boolean isCorrect();
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	public void addNetworkElement(TYPE element) {
		networkElements.add(element);
	}
	
	public List<TYPE> getNetworkElements() {
		return networkElements;
	}
	
	public int getNetworkElementCount() {
		return networkElements.size();
	}
	
	public TYPE getNetworkElement(int index) {
		return networkElements.get(index);
	}
	
	public TYPE getFirstNetworkElement() {
		return getNetworkElement(0);
	}
	
	public boolean contains(AbstractNetworkElement data) {
		return networkElements.contains(data);
	}
}
