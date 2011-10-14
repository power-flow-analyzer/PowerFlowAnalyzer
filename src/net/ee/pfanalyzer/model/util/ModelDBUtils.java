package net.ee.pfanalyzer.model.util;

import java.text.DecimalFormat;

import net.ee.pfanalyzer.model.ModelDB;
import net.ee.pfanalyzer.model.data.AbstractModelElementData;
import net.ee.pfanalyzer.model.data.ModelClassData;
import net.ee.pfanalyzer.model.data.ModelData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.data.NetworkParameterType;
import net.ee.pfanalyzer.model.data.NetworkParameterValueOption;
import net.ee.pfanalyzer.model.data.NetworkParameterValueRestriction;

public class ModelDBUtils {

	public final static String SCRIPT_PARAMETER = "SCRIPT";
	public final static String CREATE_NETWORK_PARAMETER = "CREATE_NETWORK";
	
	public static String getParameterID(AbstractModelElementData element) {
		String id = element.getID();
		if(id == null || id.isEmpty())
			id = "<empty>";
		if(element.getParent() != null && isRootClass(element.getParent()) == false) {
			String parentID = getParameterID(element.getParent());
			if("<empty>".equals(parentID))
				return id;
			return parentID + "." + id;
		}
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
	
	public static String getParameterDisplayValue(ParameterSupport support, NetworkParameter paramDef) {
		if(support == null)
			return "";
		String parameterID = paramDef.getID();
		NetworkParameterType type = paramDef.getType();
		if(NetworkParameterValueRestriction.LIST.equals(paramDef.getRestriction())) {
			String value = support.getParameterValue(parameterID).getValue();
			if(type != null && type.equals(NetworkParameterType.INTEGER)) {
				Integer result = support.getIntParameter(parameterID);
				if(result != null)
					value = result.toString();
			}
			for (NetworkParameterValueOption option: paramDef.getOption()) {
				if(option.getValue().equals(value))
					return option.getLabel() + " (" + option.getValue() + ")";
			}
		}
		if(type != null) {
			if(type.equals(NetworkParameterType.INTEGER)) {
				Integer result = support.getIntParameter(parameterID);
				if(result != null)
					return result.toString();
				return "";
			} else if(type.equals(NetworkParameterType.DOUBLE)) {
				Double dvalue = support.getDoubleParameter(parameterID);
				if(dvalue != null && paramDef.getDisplay() != null) {
					String pattern = paramDef.getDisplay().getDecimalFormatPattern();
					DecimalFormat format = new DecimalFormat(pattern);
					return format.format(dvalue);
				}
				if(dvalue != null)
					return dvalue.toString();
				return "";
			}
		}
		return support.getTextParameter(parameterID);
	}
	
	public static boolean isNetworkClass(AbstractModelElementData element) {
		return ModelDB.ROOT_NETWORK_CLASS.equals(getRootClass(element).getID());
	}
	
	public static boolean isScriptClass(AbstractModelElementData element) {
		return ModelDB.ROOT_SCRIPT_CLASS.equals(getRootClass(element).getID());
	}
	
	public static boolean isInternalScriptParameter(String parameterName) {
		return SCRIPT_PARAMETER.equals(parameterName) || CREATE_NETWORK_PARAMETER.equals(parameterName);
	}
	
	public static boolean isNetworkCreatingScript(ModelData script) {
		for (NetworkParameter parameter : script.getParameter()) {
			if(ModelDBUtils.CREATE_NETWORK_PARAMETER.equals(parameter.getID())) {
				NetworkParameter propertyValue = ModelDBUtils.getParameterValue(script, parameter.getID());
				if(propertyValue != null)
					return Boolean.valueOf(propertyValue.getValue());
			}
		}
		return false;
	}
	
	public static boolean isRootClass(AbstractModelElementData element) {
		return getRootClass(element) == element;
	}
	
	public static AbstractModelElementData getRootClass(AbstractModelElementData element) {
		// get parent of element
		if(element.getParent() != null)
			return getRootClass(element.getParent());
		// element is root
		return element;
	}
	
	public static NetworkParameter findChildParameterDefinition(ModelClassData clazz, String id) {
		NetworkParameter p = getOwnParameter(clazz, id);
		if(p != null)
			return p;
		for (ModelData model : clazz.getModel()) {
			p = getOwnParameter(model, id);
			if(p != null)
				return p;
		}
		return null;
	}
}
