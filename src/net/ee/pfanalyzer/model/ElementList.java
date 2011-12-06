package net.ee.pfanalyzer.model;

import net.ee.pfanalyzer.ui.viewer.element.ElementAttributes;

public class ElementList extends CombinedNetworkElement<AbstractNetworkElement> {

	private Boolean hasFailures = null;
	private Boolean hasWarnings = null;
	private ElementAttributes attributes;

	public ElementList() {
	}
	
	@Override
	public String getLabel() {
		if(getNetworkElementCount() == 1)
			return getFirstNetworkElement().getDisplayName(AbstractNetworkElement.DISPLAY_DEFAULT);
		return getNetworkElementCount() + " elements";
	}

	@Override
	public boolean hasFailures() {
		if(hasFailures == null) {
			hasFailures = false;
			for (AbstractNetworkElement element : getNetworkElements()) {
				if(element.hasFailures()) {
					hasFailures = true;
					break;
				}
			}
		}
		return hasFailures;
	}

	@Override
	public boolean hasWarnings() {
		if(hasWarnings == null) {
			hasWarnings = false;
			for (AbstractNetworkElement element : getNetworkElements()) {
				if(element.hasWarnings()) {
					hasWarnings = true;
					break;
				}
			}
		}
		return hasWarnings;
	}

	public ElementAttributes getAttributes() {
		return attributes;
	}

	public void setAttributes(ElementAttributes attributes) {
		this.attributes = attributes;
	}
}
