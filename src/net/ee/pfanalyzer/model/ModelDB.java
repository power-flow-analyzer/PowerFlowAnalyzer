package net.ee.pfanalyzer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ee.pfanalyzer.model.data.AbstractModelElementData;
import net.ee.pfanalyzer.model.data.ModelClassData;
import net.ee.pfanalyzer.model.data.ModelDBData;
import net.ee.pfanalyzer.model.data.ModelData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ModelDBUtils;

public class ModelDB {

	public final static String DEFAULT_MODEL = "unknown";
	public final static String DEFAULT_BUS_MODEL = "bus.unknown";
	public final static String DEFAULT_BRANCH_MODEL = "branch.unknown";
	public final static String DEFAULT_GENERATOR_MODEL = "generator.unknown";
	public final static String DEFAULT_MARKER_MODEL = "marker.unknown";
	public final static String MARKER_CONNECTION_MODEL = "marker.connection";
	
	public final static String ROOT_NETWORK_CLASS = "network";
	public final static String ROOT_SCRIPT_CLASS = "script";
	public final static String ROOT_CONFIGURATION_CLASS = "conf";
	
	private ModelDBData db;
	
	private Map<String, ModelData> models = new HashMap<String, ModelData>();
	private ModelClassData networkClass, scriptClass, configurationClass;
	
	private List<IDatabaseChangeListener> listeners = new ArrayList<IDatabaseChangeListener>();
	
	public ModelDB() {
		this.db = CaseSerializer.readInternalModelDB();
		refreshModels();
	}
	
	public ModelDB(ModelDBData db) {
		this.db = db;
		refreshModels();
	}
	
	public ModelDBData getData() {
		return db;
	}
	
	public void refreshModels() {
		// remove old items
		models.clear();
//		System.out.println("model db: refresh model index");
		// add models recursively
		for (ModelClassData clazz : getData().getModelClass()) {
			if(ROOT_NETWORK_CLASS.equals(clazz.getID()))
				networkClass = clazz;
			else if(ROOT_SCRIPT_CLASS.equals(clazz.getID()))
				scriptClass = clazz;
			else if(ROOT_CONFIGURATION_CLASS.equals(clazz.getID()))
				configurationClass = clazz;
		}
		if(networkClass != null)
			addModelsRecursive(networkClass);
		if(configurationClass != null)
			addModelsRecursive(configurationClass);
//		for (ModelClassData clazz : getData().getModelClass()) {
//			addModelsRecursive(clazz);
//		}
//		System.out.println("    " + models.size() + " models found");
	}
	
	private void addModelsRecursive(AbstractModelElementData element) {
		if(element instanceof ModelData) {
			ModelData model = (ModelData) element;
			models.put(ModelDBUtils.getFullElementID(model), model);
//			System.out.println("   add " + ModelDBUtils.getParameterID(model));
		} else if(element instanceof ModelClassData) {
			for (ModelClassData clazz : ((ModelClassData) element).getModelClass())
				addModelsRecursive(clazz);
			for (ModelData childModel : ((ModelClassData) element).getModel())
				addModelsRecursive(childModel);
		}
	}
	
	public ModelData getModel(String modelID) {
		return models.get(modelID);
	}
	
	public List<ModelData> getModels(String modelIDPrefix) {
		List<ModelData> list = new ArrayList<ModelData>();
		for (String modelID : models.keySet()) {
			if(modelID.startsWith(modelIDPrefix))
				list.add(models.get(modelID));
		}
		return list;
	}
	
	public NetworkParameter getParameterValue(ModelData model, String parameterID) {
		return ModelDBUtils.getParameterValue(model, parameterID);
	}
	
	public String getModelID(ModelData model) {
		return ModelDBUtils.getFullElementID(model);
	}
	
	public ModelClassData getNetworkClass() {
		return networkClass;
	}
	
	public ModelClassData getScriptClass() {
		return scriptClass;
	}
	
	public ModelClassData getConfigurationClass() {
		return configurationClass;
	}
	
	public void fireElementChanged(DatabaseChangeEvent event) {
		for (IDatabaseChangeListener listener : listeners) {
			listener.elementChanged(event);
		}
	}
	
	public void fireParameterChanged(DatabaseChangeEvent event) {
		for (IDatabaseChangeListener listener : listeners) {
			listener.parameterChanged(event);
		}
	}
	
	public void addDatabaseChangeListener(IDatabaseChangeListener listener) {
		listeners.add(listener);
	}
	
	public void removeDatabaseChangeListener(IDatabaseChangeListener listener) {
		listeners.remove(listener);
	}
}
