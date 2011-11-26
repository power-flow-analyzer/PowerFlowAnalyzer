package net.ee.pfanalyzer.ui.viewer.network.contour;

import java.awt.PaintContext;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class ValueGradientContext implements PaintContext {
	
	public final static int ACTION_CUT_VALUES = 0;
	public final static int ACTION_OMIT_VALUES = 1;
	
	private int outOfBoundsAction = ACTION_CUT_VALUES;
	
	// TODO examples
	private double maxValue = 1.1;
	private double middleValue = 1;
	private double minValue = 0.9;
	private double maxDistance = 70;
	private double maxRelDistance = 0.93;
	
	private ColorProvider colorProvider;
	private ValuePoint[] points;

	public ValueGradientContext(ValuePoint[] points, ColorProvider colorProvider) {
		this.points = points;
		this.colorProvider = colorProvider;
	}

	public void dispose() {
	}

	public ColorModel getColorModel() {
		return ColorModel.getRGBdefault();
	}

	public Raster getRaster(int x, int y, int width, int height) {
		WritableRaster raster = getColorModel().createCompatibleWritableRaster(width, height);

		int[] pixels = new int[width * height * 4];
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				double ratio = 0;
				for (int p = 0; p < points.length; p++) {
					if(points[p] == null)
						continue;
					double distance = points[p].distance(x + i, y + j);
					double value = points[p].getValue();
					if(outOfBoundsAction == ACTION_CUT_VALUES) {
						if(value > maxValue)
							value = maxValue;
						else if(value < minValue)
							value = minValue;
					} else if(outOfBoundsAction == ACTION_OMIT_VALUES) {
						if(value > maxValue || value < minValue)
							continue;
					}
					
					double relValue;
					if(value >= middleValue)
						relValue = Math.abs((value - middleValue) / (maxValue - middleValue));
					else
						relValue = -Math.abs((middleValue - value) / (middleValue - minValue));
					
					if(distance > maxDistance)
						distance = maxDistance;
					
					double relDistance = ((maxDistance - distance) / maxDistance);
					double newRatio = relDistance * relValue;
					
					// draw small values at least in direct sourroundings of point
					if(relDistance > maxRelDistance) {
						ratio = newRatio;
						break;
					}
					
					if(Math.abs(newRatio) > Math.abs(ratio)) {
						ratio = newRatio;
					}
				}

				int[] argb = getColorProvider().getARGB(0, null, 1, null, ratio);
				int pixelIndex = (j * width + i) * 4;
				pixels[pixelIndex + 0] = argb[0];// red
				pixels[pixelIndex + 1] = argb[1];// green
				pixels[pixelIndex + 2] = argb[2];// blue
				pixels[pixelIndex + 3] = argb[3];// alpha
			}
		}
		raster.setPixels(0, 0, width, height, pixels);

		return raster;
	}
	
	public ColorProvider getColorProvider() {
		return colorProvider;
	}
}
