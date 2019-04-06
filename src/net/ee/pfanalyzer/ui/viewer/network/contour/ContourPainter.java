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
package net.ee.pfanalyzer.ui.viewer.network.contour;

import java.awt.Graphics2D;
import java.util.List;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.Bus;
import net.ee.pfanalyzer.model.DatabaseChangeEvent;
import net.ee.pfanalyzer.model.Generator;
import net.ee.pfanalyzer.model.NetworkElement;
import net.ee.pfanalyzer.ui.util.MapBoundingBox;
import net.ee.pfanalyzer.ui.viewer.INetworkDataViewer;
import net.ee.pfanalyzer.ui.viewer.IPaintListener;
import net.ee.pfanalyzer.ui.viewer.network.NetworkMapViewer;

public class ContourPainter implements IPaintListener {

	public final static String PAINT_ID = "paint.network.contour";
	
	private NetworkMapViewer viewer;
	private boolean isActive = true;
	private ContourDiagramSettings settings;

	public ContourPainter(NetworkMapViewer viewer, ContourDiagramSettings settings) {
		this.viewer = viewer;
		this.settings = settings;
	}
	
	private ValuePoint[] generatePoints() {
		String elementPrefix = settings.getElementIDPrefix();
		String propName = settings.getParameterName();
		List<AbstractNetworkElement> elements = viewer.getNetwork().getElements(elementPrefix);
		ValuePoint[] points = new ValuePoint[elements.size()];
		for (int i = 0; i < points.length; i++) {
			AbstractNetworkElement element = elements.get(i);
			Double value = element.getDoubleParameter(propName);
			if(value == null)
				continue;
			int busNumber = getBusNumber(element);
			if(busNumber == -1)
				continue;
			double[] coords = viewer.getBusXYDouble(busNumber);
			double x = coords[0];
			double y = coords[1];
			points[i] = new ValuePoint(x, y, value);
		}
		return points;
	}
	
	private int getBusNumber(AbstractNetworkElement element) {
		if(element instanceof Bus)
			return ((Bus) element).getBusNumber();
		if(element instanceof Generator)
			return ((Generator) element).getBusNumber();
		if(element instanceof NetworkElement)
			return ((NetworkElement) element).getParentBusNumber();
		return -1;
	}
	
	@Override
	public void paint(Graphics2D g2d) {
		ValuePoint[] points = generatePoints();
		g2d.setPaint(new ValueGradientPaint(points, settings));
		g2d.fillRect(0, 0, viewer.getWidth(), viewer.getHeight());
	}
	
	@Override
	public void update() {
	}
	
	@Override
	public INetworkDataViewer getViewer() {
		return viewer;
	}
	
	@Override
	public String getPaintID() {
		return PAINT_ID;
	}

	@Override
	public boolean needsUpdate(DatabaseChangeEvent event) {
		return false;
	}

	@Override
	public boolean isActive() {
		return isActive;
	}

	@Override
	public void setActive(boolean flag) {
		isActive = flag;
	}

	@Override
	public int getLayer() {
		return LAYER_CONTOUR;
	}

	@Override
	public MapBoundingBox getBoundingBox() {
		return null;// no bounding box
	}
}
