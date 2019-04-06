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
package net.ee.pfanalyzer.ui.viewer.element;

import java.util.List;

import net.ee.pfanalyzer.model.CombinedBranch;
import net.ee.pfanalyzer.model.CombinedBus;
import net.ee.pfanalyzer.model.Network;

public class CombinedBranchPanel extends AbstractElementPanel {

	public CombinedBranchPanel(ElementViewer viewer, Network data) {
		super(viewer);
		
	}

	public void setCombinedBranch(CombinedBranch cbranch, List<CombinedBus> combinedBusList) {
		removeAllElements();
		// set title
		setTitle(cbranch.getLabel());
		addBranchElements(cbranch.getBranches(), combinedBusList);
		finishLayout();
	}
}
