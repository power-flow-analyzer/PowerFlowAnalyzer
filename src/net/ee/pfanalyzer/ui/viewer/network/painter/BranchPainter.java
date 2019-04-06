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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.Branch;
import net.ee.pfanalyzer.model.CombinedBranch;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.preferences.Preferences;
import net.ee.pfanalyzer.ui.shape.IElementShape;
import net.ee.pfanalyzer.ui.viewer.network.NetworkMapViewer;

public class BranchPainter extends AbstractShapePainter {
	
	public final static String PAINT_ID = "paint.network.branch";

	protected List<CombinedBranch> visibleBranches = new ArrayList<CombinedBranch>();
	
	private List<Integer> voltageLevels;
	
	private GeneralPath arrow_pos, arrow_neg;
	private int arrowSize = 10;
	
	public BranchPainter(Network network, NetworkMapViewer viewer) {
		super(network, viewer);
		updateArrowSize(arrowSize);
	}

	@Override
	public String getPaintID() {
		return PAINT_ID;
	}

	@Override
	public int getLayer() {
		return LAYER_BRANCHES;
	}

	@Override
	public void paint(Graphics2D g2d) {
		if(viewer.isDrawBranches()) {
			synchronized (visibleBranches) {
				for (int i = 0; i < visibleBranches.size(); i++) {
					CombinedBranch cbranch = visibleBranches.get(i);
					boolean isSelected = viewer.isSelection(cbranch);
					boolean isHovered = viewer.isHovered(cbranch);
					for (int b = 0; b < cbranch.getBranchCount(); b++) {
						Branch branch = cbranch.getBranch(b);
						if( ! isSelected && viewer.isSelection(branch))
							isSelected = true;
						if( ! isHovered && viewer.isHovered(branch))
							isHovered = true;
					}
					int fromBus = cbranch.getFirstBranch().getFromBusNumber();
					int toBus = cbranch.getFirstBranch().getToBusNumber();
					double[] coords1 = viewer.getBusXYDouble(fromBus);
					double x1 = coords1[0];//getBusXDouble(fromBus, horizontalScale);
					double y1 = coords1[1];//getBusYDouble(fromBus, verticalScale);
					double[] coords2 = viewer.getBusXYDouble(toBus);
					double x2 = coords2[0];//getBusXDouble(toBus, horizontalScale);
					double y2 = coords2[1];//getBusYDouble(toBus, verticalScale);
					if(viewer.isDrawPowerDirection()) {
						boolean highlighted = isSelected || isHovered;
						boolean fadeOut = viewer.isFadeOutUnselected() && viewer.hasSelection() && highlighted == false;
						// draw first branch
						Branch branch = cbranch.getFirstBranch();
						if(viewer.isDrawFlags() == false || branch.isCorrect()) {
							if(fadeOut)
								g2d.setColor(Color.LIGHT_GRAY);
							else if(branch.isActive())
								g2d.setColor(Color.BLUE);
							else
								g2d.setColor(Color.GRAY);
						} else {
							if(fadeOut)
								g2d.setColor(Preferences.getFlagFailureColorBright());
							else
								g2d.setColor(Preferences.getFlagFailureColor());
						}
						IElementShape branchShape = drawShape(branch, g2d, x1, y1, x2, y2, highlighted, null, 
								getBranchStroke(branch, highlighted));
						double realInjectionSumFrom = cbranch.getFromBusRealInjectionSum();
						double realInjectionSumTo = cbranch.getToBusRealInjectionSum();
						if(realInjectionSumFrom != realInjectionSumTo && branchShape != null) {
							double[][] decorationPlaces = branchShape.getAdditionalDecorationsPlace();
							if(decorationPlaces != null && decorationPlaces.length >= 1 && decorationPlaces[0].length >= 4) {
								g2d.fill(getArrowShape(g2d, decorationPlaces[0][0], decorationPlaces[0][1], 
										decorationPlaces[0][2], decorationPlaces[0][3], 
										realInjectionSumFrom > realInjectionSumTo));
							}
						}
					} else {
						double branch_space = 5;
						double alpha = Math.atan((y2-y1)/(x2-x1));
						double beta1 = alpha + Math.PI / 2.0;
						double xFactor = branch_space * Math.cos(beta1);
						double yFactor = branch_space * Math.sin(beta1);
						double nrbranches = cbranch.getBranchCount() - 1;
						double xOffset = xFactor * nrbranches / 2.0;
						double yOffset = yFactor * nrbranches / 2.0;
						for (int b = 0; b < cbranch.getBranchCount(); b++) {
							double bd = b;
							double x1l = x1 - xOffset + xFactor * bd;
							double y1l = y1 - yOffset + yFactor * bd;
							double x2l = x2 - xOffset + xFactor * bd;
							double y2l = y2 - yOffset + yFactor * bd;
							Branch branch = cbranch.getBranch(b);
							if(viewer.isDrawFlags() == false || branch.isCorrect()) {
								if(branch.isActive())
									g2d.setColor(Color.BLUE);
								else
									g2d.setColor(Color.GRAY);
							} else
								g2d.setColor(Color.RED);
							boolean highlighted = isSelected || isHovered;
							drawShape(branch, g2d, x1l, y1l, x2l, y2l, false, null, getBranchStroke(branch, highlighted));
						}
					}
				}
			}
		}
	}

	@Override
	public void update() {
		updateVisibleBranches();
		updateVoltageLevels();
		updateArrowSize(arrowSize);
	}
	
	@Override
	public AbstractNetworkElement getObjectFromScreen(int x, int y) {
		if(viewer.isDrawBranches()) {
			for (int i = 0; i < visibleBranches.size(); i++) {
				Branch branch = visibleBranches.get(i).getFirstBranch();
				int fromBus = branch.getFromBusNumber();
				int toBus = branch.getToBusNumber();
				double[] coords1 = viewer.getBusXYDouble(fromBus);
				double x1 = coords1[0];//getBusX(fromBus, horizontalScale);
				double y1 = coords1[1];//getBusY(fromBus, verticalScale);
				if(x1 == -1 || y1 == -1)
					continue;
				double[] coords2 = viewer.getBusXYDouble(toBus);
				double x2 = coords2[0];//getBusX(toBus, horizontalScale);
				double y2 = coords2[1];//getBusY(toBus, verticalScale);
				if(x2 == -1 || y2 == -1)
					continue;
				double minX = Math.min(x1, x2);
				double maxX = Math.max(x1, x2);
				if(x < minX - 2 || x > maxX + 2)
					continue;
				double minY = Math.min(y1, y2);
				double maxY = Math.max(y1, y2);
				if(y < minY - 2 || y > maxY + 2)
					continue;
				double m = (y2 - y1) / (x2 - x1);
				// check if m is infinity -> x2 = x1 -> both bus points on a vertical line
				if(Double.isInfinite(m) && Math.abs(x1 - x) <= 5)
					return branch;
				double n = y1 - m * x1;
				if(Math.abs((m * x + n - y)/(Math.sqrt(m * m + 1))) <= 5)
					return branch;
			}
		}
		return null;
	}

	private Shape getArrowShape(Graphics2D g2d, double x1, double y1, double x2, double y2, boolean directionOneToTwo) {
		double x = x1 + (x2 - x1) / 2.0;
		double y = y1 + (y2 - y1) / 2.0;
		double angle = Math.atan((y2 - y1) / (x2 - x1));
		if(x1 > x2) {
			angle += Math.PI;
		}
		AffineTransform transformation = AffineTransform
				.getTranslateInstance(x, y);
		transformation
				.concatenate(AffineTransform.getRotateInstance(angle));
		return directionOneToTwo ? 
				arrow_pos.createTransformedShape(transformation) : 
				arrow_neg.createTransformedShape(transformation);
	}
	
	protected Stroke getBranchStroke(Branch branch, boolean bold) {
		return getBranchStroke(viewer.getBaseVoltage(branch), bold);
	}
	
	public Stroke getBranchStroke(int baseVoltage, boolean bold) {
		if(viewer.isDrawPowerDirection())
			return bold ? strokesBold[0] : strokesNormal[0];
		for (int i = 0; i < getVoltageLevels().size(); i++) {
			Integer voltageLevel = getVoltageLevels().get(i);
			if(voltageLevel.intValue() == baseVoltage)
				return bold ? strokesBold[i] : strokesNormal[i];
		}
		return bold ? otherStrokeBold : otherStrokeNormal;
	}
	
	List<Integer> getVoltageLevels() {
		if(voltageLevels == null)
			updateVoltageLevels();
		return voltageLevels;
	}
	
	private void updateVisibleBranches() {
		synchronized (visibleBranches) {
			visibleBranches.clear();
			for (int i = 0; i < getNetwork().getCombinedBranchCount(); i++) {
				CombinedBranch cbranch = getNetwork().getCombinedBranch(i);
				int fromBus = cbranch.getFirstBranch().getFromBusNumber();
				int toBus = cbranch.getFirstBranch().getToBusNumber();
				double[] coords1 = viewer.getBusXYDouble(fromBus);
				double x1 = coords1[0];//getBusXDouble(fromBus, horizontalScale);
				double y1 = coords1[1];//getBusYDouble(fromBus, verticalScale);
				if(x1 == -1 || y1 == -1)
					continue;
				double[] coords2 = viewer.getBusXYDouble(toBus);
				double x2 = coords2[0];//getBusXDouble(toBus, horizontalScale);
				double y2 = coords2[1];//getBusYDouble(toBus, verticalScale);
				if(x2 == -1 || y2 == -1)
					continue;
				if(isOutsideView(x1, y1) && isOutsideView(x2, y2) && isOutsideView(x1, y1, x2, y2, 6))
					continue;
				visibleBranches.add(cbranch);
			}
	//		System.out.println("Visible combined branches: " + visibleBranches.size());
		}
	}
	
	private void updateVoltageLevels() {
		voltageLevels = new ArrayList<Integer>();
//		for (int i = 0; i < getNetwork().getCombinedBranchCount(); i++) {
		for (CombinedBranch cbranch : visibleBranches) {
//			CombinedBranch cbranch = getNetwork().getCombinedBranch(i);
			int voltage = viewer.getBaseVoltage(cbranch.getFirstBranch());
			if(voltageLevels.contains(voltage) == false)
				voltageLevels.add(voltage);
		}
		// sort voltage levels in descending order
		Collections.sort(voltageLevels, new Comparator<Integer>() {
			@Override
			public int compare(Integer int1, Integer int2) {
				return int2.compareTo(int1);
			}
		});
	}
	
	public List<Integer> getVisibleVoltageLevels() {
		return voltageLevels;
	}
	
	private void updateArrowSize(int size) {
		arrow_pos = new GeneralPath();
		arrow_pos.moveTo(-size / 2, -size / 2);
		arrow_pos.lineTo(-size / 2, size / 2);
		arrow_pos.lineTo(size / 2, 0);
		arrow_pos.lineTo(-size / 2, -size / 2);

		arrow_neg = new GeneralPath();
		arrow_neg.moveTo(-size / 2, 0);
		arrow_neg.lineTo(size / 2, size / 2);
		arrow_neg.lineTo(size / 2, -size / 2);
		arrow_neg.lineTo(-size / 2, 0);
	}
}
