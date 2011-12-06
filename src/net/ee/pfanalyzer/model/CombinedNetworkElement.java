package net.ee.pfanalyzer.model;

import java.util.ArrayList;
import java.util.List;

public abstract class CombinedNetworkElement<TYPE extends AbstractNetworkElement> {

	private List<TYPE> networkElements = new ArrayList<TYPE>();
	private int index;
	private String typeLabel;
	private Boolean isActive = null;
	
	public abstract String getLabel();
	
	public abstract boolean hasWarnings();
	
	public abstract boolean hasFailures();
	
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
	
	public double getSumOfParameters(String parameterID) {
		double result = 0;
		for (TYPE element : getNetworkElements()) {
			result += element.getDoubleParameter(parameterID, 0);
		}
		return result;
	}
	
	public String getDisplayName(int displayFlags) {
		String text = getLabel();
		if(getNetworkElementCount() > 1 && (displayFlags & AbstractNetworkElement.DISPLAY_ELEMENT_COUNT) != 0) {
			String entityLabel = getTypeLabel() != null ? getTypeLabel() : "elements";
			text += " (" + getNetworkElementCount() + " " + entityLabel + ")";
		}
		return text;
	}
	
	public boolean isActive() {
		if(isActive == null) {
			isActive = false;
			for (AbstractNetworkElement element : getNetworkElements()) {
				if(element.isActive()) {
					isActive = true;
					break;
				}
			}
		}
		return isActive;
	}
	
	public String getTypeLabel() {
		return typeLabel;
	}
	
	public void setTypeLabel(String typeLabel) {
		this.typeLabel = typeLabel;
	}
}
