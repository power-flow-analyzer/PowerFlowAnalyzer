package temp;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GradientTest extends JFrame {

	public final static int ACTION_CUT_VALUES = 0;
	public final static int ACTION_OMIT_VALUES = 1;
	
	int outOfBoundsAction = ACTION_CUT_VALUES;
	
//	double maxValue = 1.1;
//	double middleValue = 1;
//	double minValue = 0.9;
//	double displayMaxValue = 1.2;
//	double displayMiddleValue = 1;
//	double displayMinValue = 0.8;
	// TODO examples
	double maxValue = 100;
	double middleValue = 0;
	double minValue = -100;
//	double displayMaxValue = 200;
//	double displayMiddleValue = 0;
//	double displayMinValue = -200;
	
	Color maxColor = new Color(255, 0, 0);
	Color upperHalfColor = new Color(255, 255, 0);
	Color middleColor = new Color(255, 255, 255);
	Color lowerHalfColor = new Color(0, 255, 255);
	Color minColor = new Color(0, 0, 255);
	double maxDistance = 70;
	double colorSteps = 0.0;
	int transparency = 185;
	double maxRelDistance = 0.93;
	ColorProvider colorProvider = new SimpleColorProvider();
	BufferedImage image;
	BackgroundThread backgroundThread;
	
	ValuePoint[] points;
	ValueGradientPaint paint;

	private void generatePoints() {
		points = new ValuePoint[120];
		points[0] = new ValuePoint(50, 150, 100);// TODO examples
//		points[1] = new ValuePoint(getWidth() - 100, getHeight() / 2, 100);
//		points[2] = new ValuePoint(150, 200, 70);
//		points[3] = new ValuePoint(70, 250, 52);
//		points[4] = new ValuePoint(100, 230, 20);
//		points[5] = new ValuePoint(120, 200, 88);
//		points[6] = new ValuePoint(200, 100, 20);
//		points[7] = new ValuePoint(180, 80, 50);
//		points[8] = new ValuePoint(230, 50, 36);
//		points[9] = new ValuePoint(220, 120, 15);
		for (int i = 2; i < points.length; i++) {
			double x = 50 + Math.random() * 300;
			double y = 50 + Math.random() * 300;
			
//			double value = Math.random() * 0.2 - 0.1 +1;//(Math.random() >= 0.9 ? -100 : -50);
			// TODO examples
			double value = Math.random() * 200 - 100;//(Math.random() >= 0.9 ? -100 : -50);
			
			points[i] = new ValuePoint(x, y, value);
		}
		paint = new ValueGradientPaint(points);
	}
	
	private synchronized void createBackgroundImage() {
		if(points == null || paint == null)
			return;
		BufferedImage newImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		// set max acceleration priority for background image
		newImage.setAccelerationPriority(1f);
		Graphics2D g2d = newImage.createGraphics();
		g2d.setPaint(paint);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		image = newImage;
	}
	
	class BackgroundThread extends Thread {
		
		private boolean updateImage = true;
		private Object lock = new Object();
		
		BackgroundThread() {
			setPriority(MIN_PRIORITY);
		}
		
		@Override
		public void run() {
			while (shouldRun()) {
				isRunning();
				// halt this thread for a moment to give the gui a chance to update
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// do nothing
				}
				updateImage = false;
				createBackgroundImage();
				repaint();
			}
			backgroundThread = null;
		}
		
		private void isRunning() {
			synchronized(lock) {
				updateImage = false;
			}
		}
		
		private boolean shouldRun() {
			synchronized(lock) {
				return updateImage;
			}
		}
		
		public void updateImage() {
			synchronized(lock) {
				updateImage = true;
			}
		}
	}
	
	private void updateBackgroundImage() {
		if(backgroundThread != null)
			backgroundThread.updateImage();
		else {
			backgroundThread = new BackgroundThread();
			backgroundThread.start();
		}
	}

	class Board extends JComponent {
		
		Board() {
			setPreferredSize(new Dimension(400, 400));
			generatePoints();
			addComponentListener(new ComponentAdapter() {
				public void componentResized(ComponentEvent e) {
					updateBackgroundImage();
				}
			});
		}

		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			
			points[1] = new ValuePoint(getWidth() - 100, getHeight() / 2, 100);
			// TODO examples
//			points[1] = new ValuePoint(getWidth() - 100, getHeight() / 2, 1);

			drawBackground(g2d);
		}
		
		private void drawBackground(Graphics2D g2d) {
			long startTime = System.currentTimeMillis();
			if(image == null)
				updateBackgroundImage();
			else
				g2d.drawImage(image, 0, 0, middleColor, null);

			g2d.setColor(Color.BLACK);
			g2d.draw(new Line2D.Double(points[0].getX(), points[0].getY(), points[1].getX(), points[1].getY()));
			int radius = 2;
			for (int i = 0; i < points.length; i++) {
				Ellipse2D.Double circle = new Ellipse2D.Double(points[i].getX() - radius, points[i].getY() - radius, 2 * radius, 2 * radius);
				g2d.fill(circle);
			}
			long stopTime = System.currentTimeMillis();
			g2d.drawString("Distance: " + (int) maxDistance + 
					", Color steps: " + (int) colorSteps + 
					", Time: " + (stopTime - startTime) + "ms", 10, getHeight() - 20);
		}
	}

	class ValueGradientPaint implements Paint {
		private ValuePoint[] points;
		private ValueGradientContext context;

		public ValueGradientPaint(ValuePoint[] points) {
			this.points = points;
		}

		public PaintContext createContext(ColorModel cm,
				Rectangle deviceBounds, Rectangle2D userBounds,
				AffineTransform at, RenderingHints hints) {
//			if(context == null) {
				ValuePoint[] transformedPoints = new ValuePoint[points.length];
				for (int i = 0; i < points.length; i++) {
					if(points[i] == null)
						continue;
					transformedPoints[i] = new ValuePoint(points[i], points[i].getValue());
					at.transform(points[i], transformedPoints[i]);
				}
				context = new ValueGradientContext(transformedPoints);
//			}
			return context;
		}

		public int getTransparency() {
			return TRANSLUCENT;
		}
	}

	class ValueGradientContext implements PaintContext {
		private ValuePoint[] points;

		public ValueGradientContext(ValuePoint[] points) {
			this.points = points;
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

					int[] argb = colorProvider.getARGB(0, null, 1, null, ratio);
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
	}
	
	abstract class ColorProvider {
		public int[] getARGB(double min, Color minColor, double max, Color maxColor, double value) {
			double ratio = getRatio(value / max);
			if (ratio > 1.0)
				ratio = 1.0;
			return getARGB(ratio);
		}
		
		protected abstract int[] getARGB(double ratio);
		
		public Color getColor(double ratio) {
			int[] data = getARGB(getRatio(ratio));
			return new Color(data[0], data[1], data[2], data[3]);
		}
		
		public double getRatio(double ratio) {
			if(colorSteps <= 0)
				return ratio;
			int temp = (int) Math.round(ratio * colorSteps);
			return temp / colorSteps;
		}
	}
	
	class ComplexColorProvider extends ColorProvider {
		protected int[] getARGB(double ratio) {
			int[] result = new int[4];
			if(ratio >= 0) {
				if(ratio > 0.5) {
					result[0] = (int) (upperHalfColor.getRed() + (ratio - 0.5) * 2 * (maxColor.getRed() - upperHalfColor.getRed()));
					result[1] = (int) (upperHalfColor.getGreen() + (ratio - 0.5) * 2 * (maxColor.getGreen() - upperHalfColor.getGreen()));
					result[2] = (int) (upperHalfColor.getBlue() + (ratio - 0.5) * 2 * (maxColor.getBlue() - upperHalfColor.getBlue()));
				} else {
					result[0] = (int) (middleColor.getRed() + ratio * 2.0 * (upperHalfColor.getRed() - middleColor.getRed()));
					result[1] = (int) (middleColor.getGreen() + ratio * 2.0 * (upperHalfColor.getGreen() - middleColor.getGreen()));
					result[2] = (int) (middleColor.getBlue() + ratio * 2.0 * (upperHalfColor.getBlue() - middleColor.getBlue()));
				}
			} else {
				if(ratio < -0.5) {
					result[0] = (int) (lowerHalfColor.getRed() + (-ratio - 0.5) * 2 * (minColor.getRed() - lowerHalfColor.getRed()));
					result[1] = (int) (lowerHalfColor.getGreen() + (-ratio - 0.5) * 2 * (minColor.getGreen() - lowerHalfColor.getGreen()));
					result[2] = (int) (lowerHalfColor.getBlue() + (-ratio - 0.5) * 2 * (minColor.getBlue() - lowerHalfColor.getBlue()));
				} else {
					result[0] = (int) (middleColor.getRed() - ratio * 2.0 * (lowerHalfColor.getRed() - middleColor.getRed()));
					result[1] = (int) (middleColor.getGreen() - ratio * 2.0 * (lowerHalfColor.getGreen() - middleColor.getGreen()));
					result[2] = (int) (middleColor.getBlue() - ratio * 2.0 * (lowerHalfColor.getBlue() - middleColor.getBlue()));
				}
			}
			result[3] = transparency;
			return result;
		}
	}
	
	class SimpleColorProvider extends ColorProvider {
		protected int[] getARGB(double ratio) {
			int[] result = new int[4];
			if(ratio >= 0) {
				result[0] = (int) (middleColor.getRed() + ratio * (maxColor.getRed() - middleColor.getRed()));
				result[1] = (int) (middleColor.getGreen() + ratio * (maxColor.getGreen() - middleColor.getGreen()));
				result[2] = (int) (middleColor.getBlue() + ratio * (maxColor.getBlue() - middleColor.getBlue()));
			} else {
				result[0] = (int) (middleColor.getRed() - ratio * (minColor.getRed() - middleColor.getRed()));
				result[1] = (int) (middleColor.getGreen() - ratio * (minColor.getGreen() - middleColor.getGreen()));
				result[2] = (int) (middleColor.getBlue() - ratio * (minColor.getBlue() - middleColor.getBlue()));
			}
			result[3] = transparency;
			return result;
		}
	}
	
	class Legend extends JComponent {
		Legend() {
			setPreferredSize(new Dimension(50, 200));
			setBorder(new LineBorder(Color.BLACK));
		}
		
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			int height = getHeight();
			int half = (int) (height / 2.0);
			int width = getWidth();
			for (int i = 0; i < half; i++) {
				if(i == half/2.0)
					g2d.setColor(Color.BLACK);
				else
					g2d.setColor(colorProvider.getColor((double) (half - i) / (double) half));
				g2d.drawLine(0, i, width, i);
			}
			for (int i = 0; i < half; i++) {
				if(i == 0 || i == half/2.0)
					g2d.setColor(Color.BLACK);
				else
					g2d.setColor(colorProvider.getColor((double) (-i) / (double) half));
				g2d.drawLine(0, i + half, width, i + half);
			}
		}
	}

	class ValuePoint extends Point2D.Double {
		double value;

		ValuePoint(Point2D.Double point, double value) {
			this(point.getX(), point.getY(), value);
		}

		ValuePoint(double x, double y, double value) {
			super(x, y);
			this.value = value;
		}

		public double getValue() {
			return value;
		}

		public void setValue(double value) {
			this.value = value;
		}
	}
	
	class OptionPanel extends JPanel {
		OptionPanel() {
			super(new GridLayout(0, 2));
			
			final JSlider distanceSlider = new JSlider(0, 500, (int) maxDistance);
			add(new JLabel("Highlight distance:"));
			add(distanceSlider);
			final JSlider minRelDistanceSlider = new JSlider(0, 100, (int) (maxRelDistance * 100.0));
			add(new JLabel("Factor for small values:"));
			add(minRelDistanceSlider);
			final JSlider colorStepSlider = new JSlider(0, 50, (int) colorSteps);
			add(new JLabel("Color steps:"));
			add(colorStepSlider);
			final JFormattedTextField maxValueField = new JFormattedTextField(new DecimalFormat("#.##"));
			maxValueField.setValue(maxValue);
			add(new JLabel("Maximum:"));
			add(maxValueField);
			final JFormattedTextField middleValueField = new JFormattedTextField(new DecimalFormat("#.##"));
			middleValueField.setValue(middleValue);
			add(new JLabel("Middle:"));
			add(middleValueField);
			final JFormattedTextField minValueField = new JFormattedTextField(new DecimalFormat("#.##"));
			minValueField.setValue(minValue);
			add(new JLabel("Minimum:"));
			add(minValueField);
			final JSlider transparencySlider = new JSlider(0, 255, transparency);
			add(new JLabel("Contrast:"));
			add(transparencySlider);
			final JComboBox outOfBoundsActionBox = new JComboBox(new String[] { "Cut values", "Omit values" });
			add(new JLabel("Action for values out of bounds:"));
			add(outOfBoundsActionBox);
			final JComboBox colorProviderBox = new JComboBox(new String[] { "3 colors", "5 colors" });
			add(new JLabel("Colors:"));
			add(colorProviderBox);
			final JButton repaintButton = new JButton("Repaint Network");
			add(repaintButton);
			final JButton randomButton = new JButton("New Network");
			add(randomButton);
			
			colorStepSlider.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					colorSteps = colorStepSlider.getValue();
					updateBackgroundImage();
				}
			});
			distanceSlider.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					maxDistance = distanceSlider.getValue();
					updateBackgroundImage();
				}
			});
			minRelDistanceSlider.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					maxRelDistance = minRelDistanceSlider.getValue() / 100.0;
					updateBackgroundImage();
				}
			});
			maxValueField.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if("value".equals(evt.getPropertyName()) && maxValueField.getValue() instanceof Number) {
						maxValue = ((Number) maxValueField.getValue()).doubleValue();
						updateBackgroundImage();
					}
				}
			});
			middleValueField.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if("value".equals(evt.getPropertyName()) && middleValueField.getValue() instanceof Number) {
						middleValue = ((Number) middleValueField.getValue()).doubleValue();
						updateBackgroundImage();
					}
				}
			});
			minValueField.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if("value".equals(evt.getPropertyName()) && minValueField.getValue() instanceof Number) {
						minValue = ((Number) minValueField.getValue()).doubleValue();
						updateBackgroundImage();
					}
				}
			});
			transparencySlider.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					transparency = transparencySlider.getValue();
					updateBackgroundImage();
				}
			});
			outOfBoundsActionBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					outOfBoundsAction = outOfBoundsActionBox.getSelectedIndex();
					updateBackgroundImage();
				}
			});
			colorProviderBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					switch (colorProviderBox.getSelectedIndex()) {
					case 0:
						colorProvider = new SimpleColorProvider();
						break;
					case 1:
						colorProvider = new ComplexColorProvider();
						break;
					}
					updateBackgroundImage();
				}
			});
			randomButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					generatePoints();
					updateBackgroundImage();
				}
			});
			repaintButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					GradientTest.this.repaint();
				}
			});
		}
	}

	GradientTest() {
		super("Gradient Test");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		getContentPane().add(new Board(), BorderLayout.CENTER);
		getContentPane().add(new OptionPanel(), BorderLayout.SOUTH);
		getContentPane().add(new Legend(), BorderLayout.EAST);

		pack();
		setVisible(true);
	}

	public static void main(String[] args) {
		new GradientTest();
	}
}