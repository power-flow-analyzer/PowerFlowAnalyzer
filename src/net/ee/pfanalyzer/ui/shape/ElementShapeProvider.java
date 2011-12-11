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
