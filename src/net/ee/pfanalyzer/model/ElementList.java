package net.ee.pfanalyzer.model;

import net.ee.pfanalyzer.ui.viewer.element.ElementAttributes;

public class ElementList extends CombinedNetworkElement<AbstractNetworkElement> {

	private ElementAttributes attributes;

	public ElementList() {
	}
	
	@Override
	public String getLabel() {
		if(getNetworkElementCount() == 1)
			return getFirstNetworkElement().getDisplayName(AbstractNetworkElement.DISPLAY_DEFAULT);
		return getNetworkElementCount() + " elements";
	}

	public ElementAttributes getAttributes() {
		return attributes;
	}

	public void setAttributes(ElementAttributes attributes) {
		this.attributes = attributes;
	}
}
