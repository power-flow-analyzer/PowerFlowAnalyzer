package net.ee.pfanalyzer.ui.shape;



public class ElementShapeProvider {
	
	private double networkSize = 25;
	private double busSize = 16;
	private double transformerSize = 20;
	
	private double shapeSizeFactor = 1;
	
//	private Map<String, IElementShape> shapes = new HashMap<String, IElementShape>();
	
//	public ElementShapeProvider() {
//		shapes.put(DefaultBusShape.ID, value)
//	}

	public IElementShape getShape(String shapeID) {
		if(NetworkShape.ID.equals(shapeID))
			return new NetworkShape(getShapeSize(networkSize));
		if(DefaultBusShape.ID.equals(shapeID))
			return new DefaultBusShape(getShapeSize(busSize));
		if(DefaultBranchShape.ID.equals(shapeID))
			return new DefaultBranchShape(-1);
		if(TransformerShape.ID.equals(shapeID))
			return new TransformerShape(getShapeSize(transformerSize));
		return null;
	}
	
	private double getShapeSize(double maxSize) {
		return Math.max(Math.min(maxSize, shapeSizeFactor * maxSize), 4);
	}
	
	public void setShapeSizeFactor(double factor) {
		shapeSizeFactor = factor;
	}

//	public IElementShape getShape(AbstractNetworkElement element, int size, boolean selected) {
//		return null;
//	}
}
