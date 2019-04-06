/*******************************************************************************
 * Copyright 2019 Markus Gronau
 * 
 * This file is part of PowerFlowAnalyzer.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
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
