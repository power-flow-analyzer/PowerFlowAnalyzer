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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.ee.pfanalyzer.math.coordinate.ICoordinateConverter;
import net.ee.pfanalyzer.model.data.ModelData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.model.util.ParameterUtils;
import net.ee.pfanalyzer.ui.util.MapBoundingBox;

public class Outline {

	private ModelData outlineData;
	private File dataFile;
//	private double[][] points;
	private int[][] screenPoints;
	private MapBoundingBox boundingBox;
	private ICoordinateConverter converter;
	private Color borderColor, backgroundColor;
	private boolean isLoaded = false;
	
	public Outline(ICoordinateConverter converter, ModelData outlineData, File dataFile) {
		this.converter = converter;
		this.outlineData = outlineData;
		this.dataFile = dataFile;
	}
	
	public String getOutlineID() {
		return outlineData.getID();
	}
	
	public ModelData getOutlineData() {
		return outlineData;
	}
	
	public Color getBorderColor() {
		if(borderColor == null) {
			NetworkParameter param = ModelDBUtils.getParameterValue(outlineData, "BORDER_COLOR");
			if(param == null || param.getValue() == null)
				return null;
			borderColor = ParameterUtils.parseColor(param.getValue());
		}
		return borderColor;
	}
	
	public Color getBackgroundColor() {
		if(backgroundColor == null) {
			NetworkParameter param = ModelDBUtils.getParameterValue(outlineData, "BACKGROUND_COLOR");
			if(param == null || param.getValue() == null)
				return null;
			backgroundColor = ParameterUtils.parseColor(param.getValue());
		}
		return backgroundColor;
	}
	
	Object lock = new Object();
	
	public int[][] getScreenPoints() {
		synchronized(lock) {
			if(isLoaded == false)
				loadFromCSV(dataFile);
		}
		return screenPoints;
	}

	public MapBoundingBox getBoundingBox() {
		synchronized(lock) {
			if(isLoaded == false)
				loadFromCSV(dataFile);
		}
		return boundingBox;
	}
	
	private void loadFromCSV(File csvFile) {
		try {
			List<double[]> allCoords = new ArrayList<double[]>();
			FileReader reader = new FileReader(csvFile);
			BufferedReader buff = new BufferedReader(reader);
			String line;
			int lineIndex = 1;
			while( (line = buff.readLine()) != null) {
				double[] coords = parseValues(line, lineIndex);
				lineIndex++;
				if(coords != null)
					allCoords.add(coords);
			}
			reader.close();
			// determine min/max coordinates of outline
			boundingBox = new MapBoundingBox();
			for (double[] coords : allCoords) {
				boundingBox.add(coords[1], coords[0]);
			}
			int[][] points = new int[allCoords.size()][2];
			for (int i = 0; i < points.length; i++) {
				points[i] = transformValues(allCoords.get(i));
			}
			screenPoints =  points;
		} catch (FileNotFoundException e) {
			System.err.println("Cannot find outline file " + csvFile);
			screenPoints =  new int[0][2];
		} catch (IOException e) {
			System.err.println("Cannot read outline file " + csvFile + ": " + e);
			screenPoints =  new int[0][2];
		} finally {
			isLoaded = true;
		}
	}
	
	private double[] parseValues(String text, int lineIndex) {
		try {
			int separatorIndex = text.indexOf(',');
			double d1 = Double.valueOf(text.substring(0, separatorIndex));
			double d2 = Double.valueOf(text.substring(separatorIndex + 1));
			if(d1 == 0 && d2 == 0)
				System.out.println("zero coords at line " + lineIndex);
			return new double[] { d1, d2 };
		} catch(Exception e) {
			System.err.println("Could not read coordinates in line " + lineIndex + ": " + e);
			return null;
		}
	}
	
	private int[] transformValues(double[] coords) {
		if(Double.isNaN(coords[0]) || Double.isNaN(coords[1]))
			return new int[] { -1, -1 };
		int x = converter.getX(coords[0]);
		int y = converter.getY(coords[1]);
		return new int[] { x, y };
	}
}
