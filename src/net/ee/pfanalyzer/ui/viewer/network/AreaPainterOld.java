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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.ee.pfanalyzer.model.DatabaseChangeEvent;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.ui.CaseViewer;
import net.ee.pfanalyzer.ui.util.MapBoundingBox;
import net.ee.pfanalyzer.ui.viewer.INetworkDataViewer;
import net.ee.pfanalyzer.ui.viewer.IPaintListener;

public class AreaPainterOld implements IPaintListener {

	public final static String PAINT_ID = "paint.network.outline";
	
	private NetworkMapViewer viewer;
	private boolean isActive = true;
	private List<Outline> outlines;
	private Collection<Outline> lastOutlineList;
	private MapBoundingBox boundingBox = new MapBoundingBox();

	public AreaPainterOld(NetworkMapViewer viewer) {
		this.viewer = viewer;
	}
	
	@Override
	public void paint(Graphics2D g2d) {
		List<Outline> outlines = getOutlines();
		if(outlines == null)
			return;
		for (Outline outline : getOutlines()) {
			Color outlineColor = outline.getBorderColor();
			Color backgroundColor = Color.LIGHT_GRAY;//outline.getBackgroundColor();
			int[][] coords = outline.getScreenPoints();
			if(coords.length == 0)
				continue;
			double lastX = viewer.getX(coords[0]);
			double lastY = viewer.getY(coords[0]);
			GeneralPath polygon = new GeneralPath();
			polygon.moveTo(lastX, lastY);
			for (int i = 1; i < coords.length; i++) {
				// check if polygon must be closed
				if(coords[i][0] == -1 && coords[i][1] == -1) {
					if(polygon != null) {
						polygon.closePath();
						drawOutline(g2d, polygon, outlineColor, backgroundColor);
						// new polygon will be created next round
						polygon = null;
					}
					continue;
				}
				double x = viewer.getX(coords[i]);
				double y = viewer.getY(coords[i]);
				if(polygon == null) {
					polygon = new GeneralPath();
					polygon.moveTo(x, y);
					
					continue;
				}
//				g2d.draw(new Line2D.Double(lastX, lastY, x, y));
//				lastX = x;
//				lastY = y;
				polygon.lineTo(x, y);
			}
			if(polygon != null) {
				polygon.closePath();
				drawOutline(g2d, polygon, outlineColor, backgroundColor);
			}
		}
	}
	
	private void drawOutline(Graphics2D g2d, GeneralPath polygon, Color outlineColor, Color backgroundColor) {
		if(backgroundColor != null) {
			g2d.setColor(backgroundColor);
			g2d.fill(polygon);
		}
		if(outlineColor != null)
			g2d.setColor(outlineColor);
		else
			g2d.setColor(Color.LIGHT_GRAY);
		g2d.draw(polygon);
	}
	
	@Override
	public void update() {
//		System.out.println("updateAreas");
		outlines = new ArrayList<Outline>();
		CaseViewer container = viewer.getNetworkContainer();
		lastOutlineList = container.getOutlines();
		for (Outline outline : lastOutlineList) {
			String paramID = "OUTLINE." + outline.getOutlineID();
			NetworkParameter param = getViewer().getViewerConfiguration().getParameterValue(paramID);
			if(param == null)
				param = ModelDBUtils.getParameterValue(outline.getOutlineData(), "ENABLED");
			boolean defaultEnabled = param == null ? false : Boolean.parseBoolean(param.getValue());
			if(getViewer().getViewerConfiguration().getBooleanParameter(paramID, defaultEnabled))
				addOutlineInternal(outline);
		}
//		repaint();
	}
	
	private void addOutlineInternal(Outline outline) {
		outlines.add(outline);
		if(outline.getBoundingBox() != null)
			boundingBox.add(outline.getBoundingBox());
	}
	
	private List<Outline> getOutlines() {
		CaseViewer container = viewer.getNetworkContainer();
		if(outlines == null && container != null)
			update();
		if(container != null && container.getOutlines() != lastOutlineList)
			update();
		return outlines;
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
		return event.getParameterID().startsWith("OUTLINE.");
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
		return LAYER_OUTLINES;
	}

	@Override
	public MapBoundingBox getBoundingBox() {
		return boundingBox;
	}
}
