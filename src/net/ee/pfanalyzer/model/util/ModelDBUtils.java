package net.ee.pfanalyzer.model.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.IDisplayConstants;
import net.ee.pfanalyzer.model.ModelDB;
import net.ee.pfanalyzer.model.ParameterException;
import net.ee.pfanalyzer.model.data.AbstractModelElementData;
import net.ee.pfanalyzer.model.data.ModelClassData;
import net.ee.pfanalyzer.model.data.ModelData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.data.NetworkParameterType;
import net.ee.pfanalyzer.model.data.NetworkParameterValueOption;
import net.ee.pfanalyzer.model.data.NetworkParameterValueRestriction;
import net.ee.pfanalyzer.ui.util.HTMLUtil;
import net.ee.pfanalyzer.ui.util.SwingUtils;

public class ModelDBUtils {

	public final static String SCRIPT_PARAMETER = "SCRIPT";
	public final static String CREATE_NETWORK_PARAMETER = "CREATE_NETWORK";
	public final static String CHANGE_PATH_PARAMETER = "CHANGE_PATH";
	
	public final static String ICON_PARAMETER = "ICON";
	public final static String WIDTH_PARAMETER = "WIDTH";
	public final static String HEIGHT_PARAMETER = "HEIGHT";
	
	public static String getFullElementID(AbstractModelElementData element) {
		String id = element.getID();
		if(id == null || id.isEmpty())
			id = "<empty>";
		if(element.getParent() != null && isRootClass(element.getParent()) == false) {
			String parentID = getFullElementID(element.getParent());
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
	
	public static String getParameterDisplayValue(AbstractNetworkElement element, String parameterID, int displayFlags) {
		return getParameterValue(element, parameterID, true, null, "label", false, false, displayFlags);
	}
	
	public static String getParameterValue(AbstractNetworkElement element, String parameterID,
			boolean roundedValues, String decimalSeparator, String exportListValues, 
			boolean removePercentageSymbol, boolean convertBooleanValues, int displayFlags) {
		NetworkParameter param = element.getParameterDefinition(parameterID);
		if(param != null) {
			return getParameterValue(element, param, roundedValues, decimalSeparator, exportListValues, 
					removePercentageSymbol, convertBooleanValues, displayFlags);
		}
		return element.getTextParameter(parameterID);
	}
	
	public static String getParameterDisplayValue(ParameterSupport support, NetworkParameter paramDef, 
			int displayFlags) {
		return getParameterValue(support, paramDef, true, null, "label", false, false, displayFlags);
	}
	
	public static String getParameterDisplayValue(final String value, final NetworkParameter paramDef, 
			int displayFlags) {
		if(paramDef == null)
			return value;
		ParameterSupport support = new SingleParameterSupport(value, paramDef);
		return getParameterDisplayValue(support, paramDef, displayFlags);
	}
	
	public static String getParameterValue(ParameterSupport support, NetworkParameter paramDef, 
			boolean roundedValues, String decimalSeparator, String exportListValues, 
			boolean removePercentageSymbol, boolean convertBooleanValues, int displayFlags) {
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
				if(option.getValue().equals(value)) {
					if("value".equals(exportListValues))
						return option.getValue();
					else if("label".equals(exportListValues))
						return option.getLabel();
					else if("id".equals(exportListValues))
						return option.getID();
				}
			}
		}
		if(type != null) {
			boolean showUnit = (displayFlags & IDisplayConstants.PARAMETER_DISPLAY_UNIT) != 0;
			String unit = getUnit(paramDef, showUnit);
			if(type.equals(NetworkParameterType.INTEGER)) {
				Integer result = support.getIntParameter(parameterID);
				if(result != null) {
					return result.toString() + unit;
				} else
					return "";
			} else if(type.equals(NetworkParameterType.DOUBLE)) {
				Double dvalue = support.getDoubleParameter(parameterID);
				// round value and replace decimal separator
				if(dvalue != null && roundedValues && paramDef.getDisplay() != null) {
					String pattern = paramDef.getDisplay().getDecimalFormatPattern();
					if(removePercentageSymbol)
						pattern = pattern.replace("%", "");
					DecimalFormatSymbols symbols = new DecimalFormatSymbols();
					if(decimalSeparator != null)
						symbols.setDecimalSeparator(decimalSeparator.trim().charAt(0));
					DecimalFormat format = new DecimalFormat(pattern, symbols);
					return format.format(dvalue) + unit;
				}
				// replace decimal separator in exact value
				if(dvalue != null) {
					String value = dvalue.toString();
					if(decimalSeparator == null || decimalSeparator.length() == 0)
						value = value.replace('.', SwingUtils.DEFAULT_DECIMAL_SEPARATOR_CHAR);
					else if(decimalSeparator.trim().charAt(0) != '.')
						value = value.replace('.', decimalSeparator.trim().charAt(0));
					value += unit;
					return value;
				} else
					return "";
			} else if(type.equals(NetworkParameterType.BOOLEAN)) {
				if(convertBooleanValues) {
					Boolean bvalue = support.getBooleanParameter(parameterID);
					if(bvalue != null)
						return bvalue.booleanValue() ? "1" : "0";
					else
						return "";
				}
			}
		}
		return support.getTextParameter(parameterID);
	}
	
	private static String getUnit(NetworkParameter paramDef, boolean showUnit) {
		if(paramDef.getDisplay() == null)
			return "";
		if(showUnit && paramDef.getDisplay().getUnit() != null)
			return " " + paramDef.getDisplay().getUnit();
		else
			return "";
	}
	
	public static String getParameterDescription(AbstractNetworkElement element, String parameterID, String value, boolean showFullParameterInfo) {
		NetworkParameter propertyDefinition = element.getParameterDefinition(parameterID);
		if(propertyDefinition == null)
			return "Undefined Parameter: " + parameterID;
		else
			return getParameterDescription(propertyDefinition, value, showFullParameterInfo);
	}
	
	public static String getParameterDescription(NetworkParameter propertyDefinition, 
			NetworkParameter parameterValue, boolean showFullParameterInfo) {
		String value = null;
		if(parameterValue == null)
			value = "";
		else if(parameterValue.getValue() != null)
			value = parameterValue.getValue();
		if(value != null)
			value = ParameterUtils.getNormalizedParameterValue(propertyDefinition, value);
		return getParameterDescription(propertyDefinition, value, showFullParameterInfo);
	}
	
	public static String getParameterDescription(NetworkParameter propertyDefinition, String value, boolean showFullParameterInfo) {
		String tooltipText = "<html>";
		if(showFullParameterInfo && propertyDefinition.getLabel() != null 
				&& propertyDefinition.getLabel().length() > 0)
			tooltipText += "<b>" + propertyDefinition.getLabel() + "</b><br>";
		if(propertyDefinition.getDescription() != null && propertyDefinition.getDescription().length() > 0)
			tooltipText += HTMLUtil.removeHTMLTags(propertyDefinition.getDescription()) + "<br>";
		if(showFullParameterInfo) {
			if(tooltipText.length() > 6)
				tooltipText += "<br>";
			if(value != null)
				tooltipText += "Value: " + value + "<br><br>";
			tooltipText += "Parameter ID: " + propertyDefinition.getID();
			if(propertyDefinition.getDisplay() != null)
				tooltipText += "<br>Unit: " + (propertyDefinition.getDisplay().getUnit() == null ? 
						"&lt;none&gt;" : propertyDefinition.getDisplay().getUnit());
		}
		if(tooltipText.length() > 6)
			return tooltipText;
		else
			return null;
	}
	
	public static String setParameterValue(AbstractNetworkElement element, String parameterID, String value, 
			String decimalSeparator, boolean overwriteWithEmpty) throws ParameterException {
		if(element == null)
			return value;
		if(value != null && value.isEmpty())
			value = null;
		NetworkParameter paramDef = element.getParameterDefinition(parameterID);
		if(paramDef != null && value != null) {
			NetworkParameterType type = paramDef.getType();
			if(type != null) {
				if(type.equals(NetworkParameterType.INTEGER) || type.equals(NetworkParameterType.DOUBLE)) {
					if(decimalSeparator == null || decimalSeparator.length() == 0)
						value = value.replace(SwingUtils.DEFAULT_DECIMAL_SEPARATOR_CHAR, '.');
					else if(decimalSeparator.trim().charAt(0) != '.')
						value = value.replace(decimalSeparator.trim().charAt(0), '.');
					try {
						Double.parseDouble(value);
					} catch(NumberFormatException ex) {
						throw new ParameterException(parameterID, value, type.name());
					}
				} else if(type.equals(NetworkParameterType.BOOLEAN)) {
					value = value.toLowerCase();
					if( !("true".equals(value) || "false".equals(value))) {
						if("yes".equals(value))
							value = "true";
						else if("no".equals(value))
							value = "false";
						else if(value.endsWith(".0")) { // value is a double
							if("1.0".equals(value))
								value = "true";
							else if("0.0".equals(value))
								value = "false";
							else
								throw new ParameterException(parameterID, value, type.name());
						} else if("1".equals(value)) // value is an integer
							value = "true";
						else if("0".equals(value)) // value is an integer
							value = "false";
						else
							throw new ParameterException(parameterID, value, type.name());
					}
				}
			}
		}
		if(value != null || overwriteWithEmpty)
			element.setParameter(parameterID, value);
		return value;
	}
	
	public static boolean isNetworkClass(AbstractModelElementData element) {
		return ModelDB.ROOT_NETWORK_CLASS.equals(getRootClass(element).getID());
	}
	
	public static boolean isScriptClass(AbstractModelElementData element) {
		return ModelDB.ROOT_SCRIPT_CLASS.equals(getRootClass(element).getID());
	}
	
	public static boolean isInternalScriptParameter(String parameterName) {
		return SCRIPT_PARAMETER.equals(parameterName) 
				|| CREATE_NETWORK_PARAMETER.equals(parameterName)
				|| CHANGE_PATH_PARAMETER.equals(parameterName);
	}
	
	public static boolean isOutlineClass(AbstractModelElementData element) {
		return ModelDB.ROOT_OUTLINE_CLASS.equals(getRootClass(element).getID());
	}
	
	public static boolean isInternalViewerParameter(String parameterName) {
		return ICON_PARAMETER.equals(parameterName) 
		|| WIDTH_PARAMETER.equals(parameterName)
		|| HEIGHT_PARAMETER.equals(parameterName);
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
	
	static class SingleParameterSupport extends ParameterSupport {
		
		List<NetworkParameter> list;
		
		SingleParameterSupport(String value, NetworkParameter paramDef) {
			list = new ArrayList<NetworkParameter>(1);
			NetworkParameter paramValue = new NetworkParameter();
			paramValue.setID(paramDef.getID());
			paramValue.setValue(value);
			list.add(paramValue);
		}
		@Override
		public List<NetworkParameter> getParameterList() {
			return list;
		}
	}
}
