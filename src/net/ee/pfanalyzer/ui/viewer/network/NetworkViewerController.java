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
package net.ee.pfanalyzer.ui.viewer.network;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import net.ee.pfanalyzer.model.DatabaseChangeEvent;
import net.ee.pfanalyzer.model.IDatabaseChangeListener;

public class NetworkViewerController extends JPanel implements IDatabaseChangeListener {

	
//	private NetworkMapViewer viewer;
	private NetworkViewerLegend legend;
	
	public NetworkViewerController(NetworkMapViewer viewer) {
		super(new BorderLayout());
//		this.viewer = viewer;
		legend = new NetworkViewerLegend(viewer);
		add(legend, BorderLayout.SOUTH);
		add(viewer, BorderLayout.CENTER);
//		initializeSettings();
//		viewer.getViewerConfiguration().addDatabaseChangeListener(this);
	}
//	
//	private void initializeSettings() {
//		setSetting(PROPERTY_ZOOM_CHOICE);
//		setSetting(PROPERTY_RESPECT_ASPECT_RATIO);
//		setSetting(PROPERTY_DRAW_BUSSES);
//		setSetting(PROPERTY_DRAW_BUS_NAMES);
//		setSetting(PROPERTY_DRAW_BRANCHES);
//		setSetting(PROPERTY_DRAW_POWER_DIRECTION);
//		setSetting(PROPERTY_DRAW_GENERATORS);
//		setSetting(PROPERTY_DRAW_OUTLINE);
//		setSetting(PROPERTY_DRAW_LEGEND);
//		setSetting(PROPERTY_INTERACTION_ZOOM);
//		setSetting(PROPERTY_INTERACTION_MOVE);
//		setSetting(PROPERTY_SHOW_TOOLTIPS);
//	}
//	
//	private void setSetting(String parameterID) {
//		NetworkParameter value = viewer.getViewerConfiguration().getParameterValue(parameterID);
//		if(value != null && value.getValue() != null)
//			setViewerProperty(parameterID, value.getValue());
//	}
	
	public void dispose() {
//		viewer.getViewerConfiguration().removeDatabaseChangeListener(this);
	}
	
//	private void setViewerProperty(String property, String value) {
//		if(property.equals(PROPERTY_ZOOM_CHOICE)) {
//			if(value == null)
//				value = "0";
//			int intvalue = Integer.valueOf(value);
//			if(intvalue == 2)
//				viewer.setView(ZOOM_GERMANY_COORDINATES);
//			viewer.setPerfectFit(intvalue == 0);
//		} else if(property.equals(PROPERTY_RESPECT_ASPECT_RATIO))
//			viewer.setRespectAspectRation(Boolean.valueOf(value));
//		else if(property.equals(PROPERTY_DRAW_BUSSES))
//			viewer.setDrawBusNodes(Boolean.valueOf(value));
//		else if(property.equals(PROPERTY_DRAW_BUS_NAMES))
//			viewer.setDrawBusNames(Boolean.valueOf(value));
//		else if(property.equals(PROPERTY_DRAW_BRANCHES))
//			viewer.setDrawBranches(Boolean.valueOf(value));
//		else if(property.equals(PROPERTY_DRAW_POWER_DIRECTION))
//			viewer.setDrawPowerDirection(Boolean.valueOf(value));
//		else if(property.equals(PROPERTY_DRAW_GENERATORS))
//			viewer.setDrawGeneratorsNodes(Boolean.valueOf(value));
//		else if(property.equals(PROPERTY_DRAW_OUTLINE))
//			viewer.setDrawOutline(Boolean.valueOf(value));
//		else if(property.equals(PROPERTY_DRAW_LEGEND)) {
//			legend.setVisible(Boolean.valueOf(value));
//			revalidate();
//		}
//		else if(property.equals(PROPERTY_INTERACTION_ZOOM))
//			viewer.setAllowZooming(Boolean.valueOf(value));
//		else if(property.equals(PROPERTY_INTERACTION_MOVE))
//			viewer.setAllowDragging(Boolean.valueOf(value));
//		else if(property.equals(PROPERTY_SHOW_TOOLTIPS))
//			viewer.setShowTooltips(Boolean.valueOf(value));
//		legend.repaint();
//	}

	@Override
	public void elementChanged(DatabaseChangeEvent event) {
	}

	@Override
	public void parameterChanged(DatabaseChangeEvent event) {
//		String property = event.getParameterID();
//		setViewerProperty(property, event.getNewValue());
	}
}
