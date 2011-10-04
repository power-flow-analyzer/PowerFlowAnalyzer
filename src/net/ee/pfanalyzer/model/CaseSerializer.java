package net.ee.pfanalyzer.model;

import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.namespace.QName;
import javax.xml.transform.sax.SAXSource;

import net.ee.pfanalyzer.io.IllegalDataException;
import net.ee.pfanalyzer.model.data.AbstractModelElementData;
import net.ee.pfanalyzer.model.data.CaseData;
import net.ee.pfanalyzer.model.data.ModelClassData;
import net.ee.pfanalyzer.model.data.ModelDBData;
import net.ee.pfanalyzer.model.data.ModelData;
import net.ee.pfanalyzer.model.data.NetworkData;
import net.ee.pfanalyzer.model.data.ObjectFactory;

import org.xml.sax.InputSource;

public class CaseSerializer {

	public final static String NAMESPACE = "http://www.mehg.net/schema/PowerFlowAnalyzer";
	
	public final static String INTERNAL_MODEL_DB_INPUT_FILE = "/net/ee/pfanalyzer/default_model_db.xml";
	
	private JAXBContext context;
	
	public CaseSerializer() {
		try {
			context = JAXBContext.newInstance(ObjectFactory.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Object copy(Object o) throws Exception {
		JAXBContext c = JAXBContext.newInstance(o.getClass());
		JAXBElement<?> originalJAXBElement = new JAXBElement(new QName("root"), o.getClass(), o);
		JAXBSource source = new JAXBSource(c, originalJAXBElement);
		Unmarshaller unmarshaller = c.createUnmarshaller();
		JAXBElement<?> copiedJAXBElement = unmarshaller.unmarshal(source, o.getClass());
		Object copy = copiedJAXBElement.getValue();
		return copy;
	}
	
	public CaseData readCase(File caseFile) throws Exception {
		Unmarshaller unmarschaller = context.createUnmarshaller();
		Object data = ((JAXBElement<?>) unmarschaller.unmarshal(caseFile)).getValue();
		CaseData c = (CaseData) data;
		updateParents(c);
		return c;
	}
	
	public CaseData readCase(InputStream caseFile) throws Exception {
		Unmarshaller unmarschaller = context.createUnmarshaller();
		Object data = ((JAXBElement<?>) unmarschaller.unmarshal(caseFile)).getValue();
		CaseData c = (CaseData) data;
		updateParents(c);
		return c;
	}
	
	public static void updateParents(CaseData c) {
		if(c.getModelDb() != null) {
			for (ModelClassData clazz : c.getModelDb().getModelClass()) {
				setParents(clazz, null);
			}
		}
	}
	
	private static void setParents(ModelClassData clazz, AbstractModelElementData parent) {
		clazz.setParent(parent);
		for (ModelClassData subClass : clazz.getModelClass())
			setParents(subClass, clazz);
		for (ModelData model : clazz.getModel())
			model.setParent(clazz);
	}
	
	private void removeParents(ModelClassData clazz) {
		clazz.setParent(null);
		for (ModelClassData subClass : clazz.getModelClass())
			removeParents(subClass);
		for (ModelData model : clazz.getModel())
			model.setParent(null);
	}
	
	public void writeCase(CaseData c, File caseFile) throws Exception {
		if(c.getModelDb() != null) {
			for (ModelClassData clazz : c.getModelDb().getModelClass()) {
				removeParents(clazz);
			}
		}
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "US-ASCII");
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.marshal(new JAXBElement<CaseData>(
				new QName(NAMESPACE, "power_flow_case", "mpui"), CaseData.class, c), caseFile);
		updateParents(c);
	}
	
	public static NetworkData readNetwork(String network) throws Exception {
		JAXBContext c = JAXBContext.newInstance(NetworkData.class);
		SAXSource source = new SAXSource(new InputSource(new StringReader(network)));
		Unmarshaller unmarschaller = c.createUnmarshaller();
		Object data = ((JAXBElement<?>) unmarschaller.unmarshal(source, NetworkData.class)).getValue();
		return (NetworkData) data;
	}
	
	public static String writeNetwork(NetworkData network) throws Exception {
		StringWriter writer = new StringWriter();
		JAXBContext c = JAXBContext.newInstance(NetworkData.class);
		Marshaller marshaller = c.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "US-ASCII");
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.marshal(new JAXBElement<NetworkData>(
				new QName(NAMESPACE, "network", "mpui"), NetworkData.class, network), writer);
		return writer.toString();
	}
	
	public static ModelDBData readModelDB(File f) throws IllegalDataException {
		try {
			CaseSerializer serializer = new CaseSerializer();
			CaseData pfCase = serializer.readCase(f);
			updateParents(pfCase);
			return pfCase.getModelDb();
		} catch (Exception e) {
			throw new IllegalDataException("Cannot load internal model DB", e);
		}
	}
	
	public static ModelDBData readInternalModelDB() throws IllegalDataException {
		try {
			CaseSerializer serializer = new CaseSerializer();
			CaseData pfCase = serializer.readCase(CaseSerializer.class.getResourceAsStream(
					INTERNAL_MODEL_DB_INPUT_FILE));
			updateParents(pfCase);
			return pfCase.getModelDb();
		} catch (Exception e) {
			throw new IllegalDataException("Cannot load internal model DB", e);
		}
	}
}
