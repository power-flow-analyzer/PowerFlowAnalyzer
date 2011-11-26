package net.ee.pfanalyzer.ui.viewer.network;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.Branch;
import net.ee.pfanalyzer.model.CombinedBranch;
import net.ee.pfanalyzer.model.CombinedBus;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.ui.viewer.DataViewerConfiguration;

public class BusBarViewer extends NetworkMapViewer { //JComponent implements INetworkDataViewer {
	
	public final static String VIEWER_ID = "viewer.network.busbar";

	protected boolean horizontalBusBars = true;
	
	private List<BusBarInfo> busBarInfos = new ArrayList<BusBarInfo>();
	private List<BranchInfo> branchInfos = new ArrayList<BranchInfo>();
	
	private int busBarSize = 50;
	private int branchOffset = 10;
	
	public BusBarViewer(Network network, DataViewerConfiguration configuration, Component parent) {
		super(network, configuration, parent);
		analyzeNetwork();
	}
	
	private void analyzeNetwork() {
		for (int i = 0; i < getNetwork().getCombinedBusCount(); i++) {
			CombinedBus cbus = getNetwork().getCombinedBus(i);
			BusBarInfo info = new BusBarInfo();
			info.cbus = cbus;
			info.latitude = cbus.getLatitude();
			info.longitude = cbus.getLongitude();
//			if(cbus.getGenerators().size() > 0) {
//				BusBarConnection conn = new BusBarConnection();
//				conn.element = cbus.getGenerators().get(0);
//				info.connections.add(conn);
//			}
			busBarInfos.add(info);
		}
//		for (Branch branch : getNetwork().getcom) {
//		}
		for (int i = 0; i < getNetwork().getCombinedBranchCount(); i++) {
			CombinedBranch cbranch = getNetwork().getCombinedBranch(i);
			BusBarInfo fromBusInfo = getBusBarInfo(cbranch.getFromBus());
			BusBarInfo toBusInfo = getBusBarInfo(cbranch.getToBus());
			if(fromBusInfo == null || toBusInfo == null)
				continue;
//			for (Branch branch : cbranch.getBranches()) {
			for (int j = 0; j < cbranch.getBranchCount(); j++) {
				Branch branch = cbranch.getBranch(j);
				BusBarConnection fromBusConnection = new BusBarConnection();
				fromBusConnection.element = branch;
				fromBusConnection.otherLatitude = branch.getToBus().getLatitude();
				fromBusConnection.otherLongitude = branch.getToBus().getLongitude();
				fromBusInfo.addConnection(fromBusConnection);
				
				BusBarConnection toBusConnection = new BusBarConnection();
				toBusConnection.element = branch;
				toBusConnection.otherLatitude = branch.getFromBus().getLatitude();
				toBusConnection.otherLongitude = branch.getFromBus().getLongitude();
//				toBusInfo.addConnection(toBusConnection);
				
				BranchInfo info = new BranchInfo();
				info.element = branch;
				info.paralellBranchesCount = cbranch.getBranchCount();
				info.branchIndex = j;
				info.fromBus = fromBusConnection;
				info.toBus = toBusConnection;
				branchInfos.add(info);
			}
		}
		for (BusBarInfo info : busBarInfos) {
			ConnectionSorter sorter = new ConnectionSorter();
			Collections.sort(info.topConnections, sorter);
			Collections.sort(info.bottomConnections, sorter);
//			System.out.println("Busbar " + info.cbus.getLabel());
//			System.out.println("    top connections: " + info.topConnections.size());
//			System.out.println("    down connections: " + info.bottomConnections.size());
		}
	}

	protected void paintNetwork(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		// enable anti aliasing
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Stroke defaultStroke = g2d.getStroke();
		// fill background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		// real painting
		if(internalBusCoords != null) {
			double width = internalMaxX - internalMinX;
			horizontalScale = ((double) getWidth() - 2 * HORIZONTAL_GAP) / width;
			double height = internalMaxY - internalMinY;
			verticalScale = ((double) getHeight() - 2 * VERTICAL_GAP) / height;
			if(respectAspectRatio) {
				if(verticalScale < horizontalScale) {
					horizontalScale = verticalScale;
				} else {
					verticalScale = horizontalScale;
				}
			}
			// draw bus nodes
			if(drawBusNodes) {
				for (BusBarInfo info : busBarInfos) {
					paintBusBar(g2d, info);
					// draw branches
					if(drawBranches) {
						paintBranches(g2d, info);
					}
				}
			}
//			// draw branches
//			if(drawBranches) {
//				for (BranchInfo info : branchInfos) {
//					paintBranches(g2d, info);
//				}
//			}
		}
	}
	
	private void paintBusBar(Graphics2D g2d, BusBarInfo info) {
		double fontYOffset = g2d.getFontMetrics().getAscent() / 2.0;
		CombinedBus cbus = info.cbus;//getNetwork().getCombinedBus(i);
		double[] coords = getBusXYDouble(cbus.getFirstBus().getBusNumber());
		double x = coords[0];
		double y = coords[1];
		g2d.setStroke(strokesBold[0]);
		g2d.setColor(Color.BLACK);
		g2d.draw(new Line2D.Double(x - busBarSize / 2, y, x + busBarSize / 2, y));
		if(drawBusNames) {
			String locationName = cbus.getLabel();
			if(locationName != null)
				g2d.drawString(locationName, (int) x + busBarSize / 2 + 5, (int) (y + fontYOffset));
		}
	}
	
	private void paintBranches(Graphics2D g2d, BranchInfo info) {
//		for (int i = 0; i < info.bottomConnections.size(); i++) {
			AbstractNetworkElement element = info.element;
			int offset = busBarSize / (info.paralellBranchesCount + 1);
			int relativePosition = offset + offset * info.branchIndex - busBarSize / 2;
			if(element instanceof Branch)
				paintBranch(g2d, (Branch) element, relativePosition, info.branchIndex);
//		}
	}
	
	private void paintBranches(Graphics2D g2d, BusBarInfo info) {
		for (int i = 0; i < info.bottomConnections.size(); i++) {
			AbstractNetworkElement element = info.bottomConnections.get(i).element;
			int offset = busBarSize / (info.bottomConnections.size() + 1);
			int relativePosition = offset + offset * i - busBarSize / 2;
			if(element instanceof Branch)
				paintBranch(g2d, (Branch) element, relativePosition, i);
		}
//		for (BusBarConnection connection : info.bottomConnections) {
//			if(connection.element instanceof Branch)
//				paintBranch(g2d, (Branch) connection.element);
//		}
//		for (int i = 0; i < getNetwork().getCombinedBranchCount(); i++) {
//			CombinedBranch cbranch = getNetwork().getCombinedBranch(i);
//			for (Branch branch : cbranch.getBranches()) {
//				paintBranch(g2d, branch);
//			}
//		}
	}
	
	private void paintBranch(Graphics2D g2d, Branch branch, int relativePosition, int offsetFactor) {
//		for (int i = 0; i < getNetwork().getCombinedBranchCount(); i++) {
//			CombinedBranch cbranch = getNetwork().getCombinedBranch(i);
//			int fromBus = cbranch.getFirstBranch().getFromBusNumber();
//			int toBus = cbranch.getFirstBranch().getToBusNumber();
		boolean isSelected = isSelection(branch);
		boolean isHovered = isHovered(branch);
		g2d.setStroke(getBranchStroke(branch, isSelected || isHovered));
		int fromBus = branch.getFromBusNumber();
		int toBus = branch.getToBusNumber();
		double[] coords1 = getBusXYDouble(fromBus);
		double x1 = coords1[0] + relativePosition;
		double y1 = coords1[1];
		if(x1 == -1 || y1 == -1)
			return;
		double[] coords2 = getBusXYDouble(toBus);
		double x4 = coords2[0] + relativePosition;
		double y4 = coords2[1];
		if(x4 == -1 || y4 == -1)
			return;
		
		double x2 = x1;
		double y2 = y1 + (y4 - y1) / 2.0;
//		if(Math.abs(y4 - y1) < 20)
//			y2 = Math.max(y1, y4) + 20;
		double x3 = x4;
		double y3 = y2;
		
		// 
		double yOffset = offsetFactor * branchOffset;
		if(y4 > y1)
			yOffset *= -1;
		y2 += yOffset;
		y3 += yOffset;
		
		g2d.setColor(Color.BLACK);
		g2d.draw(new Line2D.Double(x1, y1, x2, y2));
		g2d.draw(new Line2D.Double(x2, y2, x3, y3));
		g2d.draw(new Line2D.Double(x3, y3, x4, y4));
//		}
	}
	
	private BusBarInfo getBusBarInfo(CombinedBus cbus) {
		for (BusBarInfo info : busBarInfos) {
			if(info.cbus == cbus)
				return info;
		}
		return null;
	}
	
	private class BusBarInfo {
		
		double latitude, longitude;
		CombinedBus cbus;
		List<BusBarConnection> topConnections = new ArrayList<BusBarConnection>();
		List<BusBarConnection> bottomConnections = new ArrayList<BusBarConnection>();
		
		private void addConnection(BusBarConnection connection) {
			if(connection.otherLatitude == -1 || connection.otherLongitude == -1) {
				// e.g. generators do not have "other" locations
				bottomConnections.add(connection);
				return;
			}
			if(latitude >= connection.otherLatitude
					|| connection.otherLatitude - latitude < 0.3)
				bottomConnections.add(connection);
			else
				topConnections.add(connection);
		}
	}
	
	private class BranchInfo {
		AbstractNetworkElement element;
		BusBarConnection fromBus, toBus;
		int paralellBranchesCount;
		int branchIndex;
	}
	
	private class BusBarConnection {
		double otherLatitude = -1, otherLongitude = -1;
		AbstractNetworkElement element;
	}
	
	private static class ConnectionSorter implements Comparator<BusBarConnection> {
		@Override
		public int compare(BusBarConnection c1, BusBarConnection c2) {
			if(c1.otherLongitude == c2.otherLongitude)
				return 0;
			return c1.otherLongitude < c2.otherLongitude ? -1 : 1;
		}
	}
}
