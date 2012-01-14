package net.ee.pfanalyzer.ui.util;

public class MapBoundingBox {

	private double latitudeMin = Double.NaN;
	private double latitudeMax = Double.NaN;
	private double longitudeMin = Double.NaN;
	private double longitudeMax = Double.NaN;
	
	public void add(double latitude, double longitude) {
		if(Double.isNaN(latitude) || Double.isNaN(longitude))
			return;
		if(Double.isNaN(latitudeMin))
			latitudeMin = latitude;
		else
			latitudeMin = Math.min(latitudeMin, latitude);
		if(Double.isNaN(latitudeMax))
			latitudeMax = latitude;
		else
			latitudeMax = Math.max(latitudeMax, latitude);
		if(Double.isNaN(longitudeMin))
			longitudeMin = longitude;
		else
			longitudeMin = Math.min(longitudeMin, longitude);
		if(Double.isNaN(longitudeMax))
			longitudeMax = longitude;
		else
			longitudeMax = Math.max(longitudeMax, longitude);
	}
	
	public void add(MapBoundingBox box) {
		add(box.getLatitudeMin(), box.getLongitudeMin());
		add(box.getLatitudeMax(), box.getLongitudeMax());
	}
	
	public boolean isIncomplete() {
		return Double.isNaN(latitudeMin) || Double.isNaN(latitudeMax)
				|| Double.isNaN(longitudeMin) || Double.isNaN(longitudeMax);
	}
	
	public double getLatitudeMin() {
		return latitudeMin;
	}
	
	public void setLatitudeMin(double latitudeMin) {
		this.latitudeMin = latitudeMin;
	}
	
	public double getLatitudeMax() {
		return latitudeMax;
	}
	
	public void setLatitudeMax(double latitudeMax) {
		this.latitudeMax = latitudeMax;
	}
	
	public double getLongitudeMin() {
		return longitudeMin;
	}
	
	public void setLongitudeMin(double longitudeMin) {
		this.longitudeMin = longitudeMin;
	}
	
	public double getLongitudeMax() {
		return longitudeMax;
	}
	
	public void setLongitudeMax(double longitudeMax) {
		this.longitudeMax = longitudeMax;
	}
}
