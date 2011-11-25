package net.ee.pfanalyzer.ui.shape;



public class ElementShapeProvider {
	
	private int networkSize = 20;
	private int busSize = 16;
	private int transformerSize = 16;
	
//	private Map<String, IElementShape> shapes = new HashMap<String, IElementShape>();
	
//	public ElementShapeProvider() {
//		shapes.put(DefaultBusShape.ID, value)
//	}

	public IElementShape getShape(String shapeID) {
		if(NetworkShape.ID.equals(shapeID))
			return new NetworkShape(networkSize);
		if(DefaultBusShape.ID.equals(shapeID))
			return new DefaultBusShape(busSize);
		if(DefaultBranchShape.ID.equals(shapeID))
			return new DefaultBranchShape(-1);
		if(TransformerShape.ID.equals(shapeID))
			return new TransformerShape(transformerSize);
		return null;
	}

//	public IElementShape getShape(AbstractNetworkElement element, int size, boolean selected) {
//		return null;
//	}
}
