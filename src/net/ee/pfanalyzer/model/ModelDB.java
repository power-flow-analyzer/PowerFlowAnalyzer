package net.ee.pfanalyzer.model;

import java.util.HashMap;
import java.util.Map;

import net.ee.pfanalyzer.model.data.AbstractModelElementData;
import net.ee.pfanalyzer.model.data.ModelClassData;
import net.ee.pfanalyzer.model.data.ModelDBData;
import net.ee.pfanalyzer.model.data.ModelData;
import net.ee.pfanalyzer.model.util.ModelDBUtils;

public class ModelDB {

	public final static String DEFAULT_BUS_MODEL = "bus.unknown";
	public final static String DEFAULT_BRANCH_MODEL = "branch.unknown";
	public final static String DEFAULT_GENERATOR_MODEL = "generator.unknown";
	
	private ModelDBData db;
	
	private Map<String, ModelData> models = new HashMap<String, ModelData>();
	
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
			addModelsRecursive(clazz);
		}
//		System.out.println("    " + models.size() + " models found");
	}
	
	private void addModelsRecursive(AbstractModelElementData element) {
		if(element instanceof ModelData) {
			ModelData model = (ModelData) element;
			models.put(ModelDBUtils.getParameterID(model), model);
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
}
