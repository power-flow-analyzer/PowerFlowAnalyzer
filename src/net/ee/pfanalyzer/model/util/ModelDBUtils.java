package net.ee.pfanalyzer.model.util;

import net.ee.pfanalyzer.model.data.AbstractModelElementData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.data.NetworkParameterType;

public class ModelDBUtils {

	public static String getParameterID(AbstractModelElementData element) {
		String id = element.getID();
		if(id == null || id.isEmpty())
			id = "<empty>";
		if(element.getParent() != null)
			return getParameterID(element.getParent()) + "." + id;
		return id;
	}
	
	public static boolean hasParameterDefinition(AbstractModelElementData element, String id) {
		if(getOwnParameter(element, id) == null)
			return false;
		if(element.getParent() == null)
			return true;
		return getParameterValue(element.getParent(), id) == null;
	}
	
	public static NetworkParameter getOwnParameter(AbstractModelElementData element, String id) {
		for (NetworkParameter p : element.getParameter()) {
			if(p.getID() != null && p.getID().equals(id))
				return p;
		}
		return null;
	}
	
//	public static ModelParameter getOwnProperty(AbstractNetworkElementData element, String id) {
//		for (ModelParameter p : element.getParameter()) {
//			if(p.getID() != null && p.getID().equals(id))
//				return p;
//		}
//		return null;
//	}
	
	public static NetworkParameter getParameterValue(AbstractModelElementData element, String id) {
		NetworkParameter p = getOwnParameter(element, id);
		if(p == null && element.getParent() != null)
			return getParameterValue(element.getParent(), id);
		return p;
	}
	
	public static NetworkParameter getParameterDefinition(AbstractModelElementData element, String id) {
		NetworkParameter p = null;
		if(element.getParent() != null)
			p = getParameterDefinition(element.getParent(), id);
		if(p != null)
			return p;
		return getOwnParameter(element, id);
	}
	
	public static NetworkParameterType getParameterType(AbstractModelElementData element, String id) {
		NetworkParameter p = getOwnParameter(element, id);
		if(p != null && p.getType() != null)
			return p.getType();
		if(element.getParent() != null)
			return getParameterType(element.getParent(), id);
		return null;
	}
	
//	public static ModelParameter getPropertyValue(AbstractNetworkElement element, String id) {
//		ModelParameter p = getOwnProperty(element.getElementData(), id);
////		if(p == null && element.getModel() != null)
////			return getPropertyValue(element.getParent(), id);
//		return p;
//	}
	
	public static void removeOwnParameter(AbstractModelElementData element, String id) {
//		for (int i = 0; i < element.getProperty().size(); i++) {
//			if(element.getProperty().get(i).getID().equals(id)) {
//				element.getProperty().remove(i);
//				break;
//			}
//		}
		NetworkParameter p = ModelDBUtils.getOwnParameter(element, id);
		if(p != null)
			element.getParameter().remove(p);
	}
	
	public static NetworkParameter getParameter(AbstractModelElementData element, String id, boolean create) {
		NetworkParameter p = ModelDBUtils.getOwnParameter(element, id);
		if(p != null)
			return p;
		if(create == false)
			return null;
		p = ModelDBUtils.getParameterValue(element, id);
//		if(p != null) {
//			ModelProperty copy = copyProperty(p);
//			element.getProperty().add(copy);
//			return copy;
//		}
		if(p != null) {
			NetworkParameter reference = new NetworkParameter();
			reference.setID(id);
//			reference.setTextValue(new String(p.getTextValue()));
			element.getParameter().add(reference);
			return reference;
		}
//		if(create) {
//			ModelProperty p = new ModelProperty();
//			p.setID(id);
//			element.getProperty().add(p);
//			properties.put(id, p);
//			return p;
//		}
		return null;
	}
}
