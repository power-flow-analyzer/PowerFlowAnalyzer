package net.ee.pfanalyzer.model.db;

import net.ee.pfanalyzer.model.Bus;
import net.ee.pfanalyzer.model.data.BaseElementType;
import net.ee.pfanalyzer.model.data.ModelClassData;
import net.ee.pfanalyzer.model.data.ModelDBData;
import net.ee.pfanalyzer.model.data.ModelData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.data.NetworkParameterType;
import net.ee.pfanalyzer.model.data.NetworkParameterValueDisplay;
import net.ee.pfanalyzer.model.data.NetworkParameterValueOption;
import net.ee.pfanalyzer.model.data.NetworkParameterValueRestriction;
import net.ee.pfanalyzer.model.matpower.BusData;
import net.ee.pfanalyzer.model.matpower.IBusDataConstants;

public class DefaultModelDB extends ModelDBData {

	public DefaultModelDB() {
		getModelClass().add(createBusData());
		ModelClassData c1 = new ModelClassData();
		c1.setLabel("Branch");
		c1.setBaseType(BaseElementType.BRANCH);
		
		NetworkParameter p = new NetworkParameter();
		p.setType(NetworkParameterType.INTEGER);
		p.setRestriction(NetworkParameterValueRestriction.BUS_NUMBER);
		p.setID("F_BUS");
		p.setLabel("F_BUS");
		p.setDescription("From Bus");
		c1.getParameter().add(p);
		p = new NetworkParameter();
		p.setType(NetworkParameterType.INTEGER);
		p.setRestriction(NetworkParameterValueRestriction.BUS_NUMBER);
		p.setID("T_BUS");
		p.setLabel("T_BUS");
		p.setDescription("To Bus");
		c1.getParameter().add(p);
		
		p = new NetworkParameter();
		p.setType(NetworkParameterType.DOUBLE);
		p.setID("R");
		p.setLabel("Resistance");
		NetworkParameterValueDisplay display = new NetworkParameterValueDisplay();
//		display.setMin(0.0);
//		display.setMax(Double.MAX_VALUE);
		display.setIncrement(0.0001);
		p.setDisplay(display);
		c1.getParameter().add(p);
		
//		p = new ModelProperty();
//		p.setType(ModelPropertyType.DOUBLE);
//		p.setID("BR_R");
//		p.setLabel("BR_R");
//		c1.getProperty().add(p);
//		
//		p = new ModelProperty();
//		p.setID("BR_X");
//		p.setLabel("BR_X");
//		c1.getProperty().add(p);
		
		NetworkParameter p1 = new NetworkParameter();
		p1.setType(NetworkParameterType.INTEGER);
		p1.setRestriction(NetworkParameterValueRestriction.LIST);
		p1.setID("BR_STATUS");
		p1.setLabel("Initial branch status");
		NetworkParameterValueOption o1 = new NetworkParameterValueOption();
		o1.setID("1");
		o1.setLabel("in-service");
		o1.setValue("1");
		p1.getOption().add(o1);
		o1 = new NetworkParameterValueOption();
		o1.setID("0");
		o1.setLabel("out-of-service");
		o1.setValue("0");
		p1.setDefaultValue("1");
		p1.getOption().add(o1);
		c1.getParameter().add(p1);
		NetworkParameter p2 = new NetworkParameter();
		p2.setID("susceptance");
		p2.setValue("1.5e-8");
		c1.getParameter().add(p2);
//		p1.set
		ModelClassData c2 = new ModelClassData();
		c2.setLabel("Cable");
		c1.getModelClass().add(c2);
		
		ModelData m1 = new ModelData();
		m1.setLabel("Unknown Branch");
		c1.getModel().add(m1);
		getModelClass().add(c1);
	}
	
	private static ModelClassData createBusData() {
		ModelClassData clazz = new ModelClassData();
		clazz.setLabel("Bus");
		clazz.setBaseType(BaseElementType.BUS);
		
		NetworkParameter busNumber = createBusProperty(IBusDataConstants.PROPERTY_BUS_NUMBER, IBusDataConstants.BUS_NUMBER, true);
		busNumber.setType(NetworkParameterType.INTEGER);
		busNumber.setRestriction(NetworkParameterValueRestriction.BUS_NUMBER);
		clazz.getParameter().add(busNumber);
		
		NetworkParameter type = createBusProperty(IBusDataConstants.PROPERTY_BUS_TYPE, IBusDataConstants.BUS_TYPE, true);
		type.setType(NetworkParameterType.INTEGER);
		type.setRestriction(NetworkParameterValueRestriction.LIST);
		type.getOption().add(createOption("PQ", "PQ", ""+IBusDataConstants.BUS_TYPE_PQ));
		type.getOption().add(createOption("PV", "PV", ""+IBusDataConstants.BUS_TYPE_PV));
		type.getOption().add(createOption("REF", "Reference", ""+IBusDataConstants.BUS_TYPE_REFERENCE));
		type.getOption().add(createOption("ISO", "Isolated", ""+IBusDataConstants.BUS_TYPE_ISOLATED));
		clazz.getParameter().add(type);
		
		NetworkParameter voltage = createBusProperty(IBusDataConstants.PROPERTY_BASE_VOLTAGE, IBusDataConstants.BASE_VOLTAGE, true);
		voltage.setType(NetworkParameterType.DOUBLE);
		clazz.getParameter().add(voltage);
		
		NetworkParameter lattitude = createProperty(Bus.PROPERTY_LATTITUDE, "Lattitude", "", true);
		lattitude.setType(NetworkParameterType.DOUBLE);
		clazz.getParameter().add(lattitude);
		
		NetworkParameter longitude = createProperty(Bus.PROPERTY_LONGITUDE, "Longitude", "", true);
		longitude.setType(NetworkParameterType.DOUBLE);
		clazz.getParameter().add(longitude);
		
		return clazz;
	}
	
	private static NetworkParameterValueOption createOption(String id, String label, String value) {
		NetworkParameterValueOption option = new NetworkParameterValueOption();
		option.setID(id);
		option.setLabel(label);
		option.setValue(value);
		return option;
	}
	
	private static NetworkParameter createBusProperty(String propertyName, int propertyIndex, boolean empty) {
		return createProperty(propertyName, BusData.getName(propertyIndex), BusData.getDescription(propertyIndex), empty);
	}
	
	private static NetworkParameter createProperty(String propertyName, String label, String description, boolean empty) {
		NetworkParameter p = new NetworkParameter();
		p.setID(propertyName);
		p.setLabel(label);
		p.setDescription(description);
		if(empty)
			p.setEmpty(Boolean.TRUE);
		return p;
	}
}
