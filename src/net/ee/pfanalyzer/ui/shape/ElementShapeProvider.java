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
package net.ee.pfanalyzer.ui.shape;



public class ElementShapeProvider {
	
	private double networkMaxSize = 25;
	private double networkMinSize = 16;
	private double busMaxSize = 16;
	private double busMinSize = 4;
	private double transformerMaxSize = 20;
	private double transformerMinSize = 4;
	
	private double shapeSizeFactor = 1;
	private boolean limitSize = true;
	
//	private Map<String, IElementShape> shapes = new HashMap<String, IElementShape>();
	
//	public ElementShapeProvider() {
//		shapes.put(DefaultBusShape.ID, value)
//	}

	public IElementShape getShape(String shapeID) {
		if(NetworkShape.ID.equals(shapeID))
			return new NetworkShape(getShapeSize(networkMaxSize, networkMinSize));
		if(DefaultBusShape.ID.equals(shapeID))
			return new DefaultBusShape(getShapeSize(busMaxSize, busMinSize));
		if(DefaultBranchShape.ID.equals(shapeID))
			return new DefaultBranchShape(-1);
		if(TransformerShape.ID.equals(shapeID))
			return new TransformerShape(getShapeSize(transformerMaxSize, transformerMinSize));
		return null;
	}
	
	private double getShapeSize(double maxSize, double minSize) {
		if(limitSize)
			return Math.max(Math.min(maxSize, shapeSizeFactor * maxSize), minSize);
		else
			return Math.max(shapeSizeFactor * maxSize, minSize);
	}
	
	public void setShapeSizeFactor(double factor) {
		shapeSizeFactor = factor;
	}

	public double getShapeSizeFactor() {
		return shapeSizeFactor;
	}

	public void setLimitSize(boolean limitSize) {
		this.limitSize = limitSize;
	}

	public boolean isLimitSize() {
		return limitSize;
	}

//	public IElementShape getShape(AbstractNetworkElement element, int size, boolean selected) {
//		return null;
//	}
}
