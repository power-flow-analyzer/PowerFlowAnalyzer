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
package net.ee.pfanalyzer.ui.viewer;

import java.awt.Graphics2D;

import net.ee.pfanalyzer.model.DatabaseChangeEvent;
import net.ee.pfanalyzer.ui.util.MapBoundingBox;

public interface IPaintListener {
	
	public final static int LAYER_BUS_NODES = 10;
	public final static int LAYER_MARKERS = 9;
	public final static int LAYER_BRANCHES = 8;
	public final static int LAYER_AREAS = 7;
	public final static int LAYER_OUTLINES = 6;
	public final static int LAYER_CONTOUR = 5;

	void paint(Graphics2D g2d);
	
	INetworkDataViewer getViewer();
	
	boolean needsUpdate(DatabaseChangeEvent event);
	
	void update();
	
//	void updateDisplayTime(DisplayTimer timer);
	
	void setActive(boolean flag);
	
	boolean isActive();
	
	String getPaintID();
	
	int getLayer();
	
	MapBoundingBox getBoundingBox();
}
