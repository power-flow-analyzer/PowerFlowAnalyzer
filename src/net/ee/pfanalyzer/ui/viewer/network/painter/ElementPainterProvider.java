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
package net.ee.pfanalyzer.ui.viewer.network.painter;

import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.ui.viewer.network.NetworkMapViewer;

public class ElementPainterProvider {
	
	BusPainter busPainter;
	BranchPainter branchPainter;
	MarkerPainter markerPainter;
	AreaPainter areaPainter;
	
	public ElementPainterProvider(Network network, NetworkMapViewer viewer) {
		busPainter = new BusPainter(network, viewer);
		branchPainter = new BranchPainter(network, viewer);
		markerPainter = new MarkerPainter(network, viewer);
		areaPainter = new AreaPainter(network, viewer);
	}
	
	public void paint(Graphics2D g2d) {
		Stroke defaultStroke = g2d.getStroke();
//		areaPainter.paint(g2d);
//		markerPainter.paint(g2d);
		branchPainter.paint(g2d);
		g2d.setStroke(defaultStroke);
		busPainter.paint(g2d);
	}
	
	public void update() {
		busPainter.update();
		branchPainter.update();
		markerPainter.update();
//		areaPainter.update();
		
		// determine factor for size of element shapes
		Rectangle2D.Double r = busPainter.visibleRect;
		double minDim = Math.max(r.getMaxX() - r.getMinX(), r.getMaxY() - r.getMinY());
		int busCount = busPainter.getVisibleBusNodes().size();
		double shapeSizeFactor;
		if(busCount > 0 && minDim > 0)
			shapeSizeFactor = ((r.getMaxX() - r.getMinX()) * (r.getMaxY() - r.getMinY())) / (5000 * busCount);//minDim / (10.0 * busCount);
		else
			shapeSizeFactor = 1;
		busPainter.shapeProvider.setShapeSizeFactor(shapeSizeFactor);
//		System.out.println("shapeSizeFactor="+shapeSizeFactor);
	}
	
	public AbstractNetworkElement getObjectFromScreen(int x, int y) {
		AbstractNetworkElement element = null;
		element = busPainter.getObjectFromScreen(x, y);
		if(element != null)
			return element;
		element = markerPainter.getObjectFromScreen(x, y);
		if(element != null)
			return element;
		element = branchPainter.getObjectFromScreen(x, y);
		if(element != null)
			return element;
		return null;
	}

	public void setShapeFactor(double factor) {
//		createStrokes((float) (1.0 * factor), (float) (2.5 * factor)); FIXME
//		shapeProvider.setLimitSize(factor < 1.0);
//		fontSize = (float) (this.getFont().getSize2D() * factor);
////		shapeProvider.setShapeSizeFactor(factor);
//		updateArrowSize((int) (this.arrowSize * factor));
//		networkMarkerSize = (int) (this.networkMarkerSize * factor);
    	
    }

	public BranchPainter getBranchPainter() {
		return branchPainter;
	}
}
