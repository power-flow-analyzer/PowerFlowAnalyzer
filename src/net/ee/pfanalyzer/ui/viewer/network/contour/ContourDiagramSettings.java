package net.ee.pfanalyzer.ui.viewer.network.contour;

public class ContourDiagramSettings {

	private double maxValue = Double.NaN;
	private double middleValue = Double.NaN;
	private double minValue = Double.NaN;
	private double maxDistance = Double.NaN;
	private double maxRelDistance = Double.NaN;
	
	private ColorProvider colorProvider = new ColorProvider.SimpleColorProvider();
	private int outOfBoundsAction = -1;
	
	public boolean isIncomplete() {
		return Double.isNaN(maxValue) || Double.isNaN(minValue) || Double.isNaN(middleValue) 
			|| Double.isNaN(maxDistance) || Double.isNaN(maxRelDistance) || outOfBoundsAction == -1
			|| getColorProvider().getTransparency() == -1
			|| getOutOfBoundsAction() == -1;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	public double getMiddleValue() {
		return middleValue;
	}

	public void setMiddleValue(double middleValue) {
		this.middleValue = middleValue;
	}

	public double getMinValue() {
		return minValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public double getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(double maxDistance) {
		this.maxDistance = maxDistance;
	}

	public double getMaxRelDistance() {
		return maxRelDistance;
	}

	public void setMaxRelDistance(double maxRelDistance) {
		this.maxRelDistance = maxRelDistance;
	}

	public ColorProvider getColorProvider() {
		return colorProvider;
	}

	public void setColorProvider(ColorProvider colorProvider) {
		this.colorProvider = colorProvider;
	}

	public int getOutOfBoundsAction() {
		return outOfBoundsAction;
	}

	public void setOutOfBoundsAction(int outOfBoundsAction) {
		this.outOfBoundsAction = outOfBoundsAction;
	}
}
