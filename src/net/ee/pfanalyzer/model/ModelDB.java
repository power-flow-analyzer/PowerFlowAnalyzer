/*******************************************************************************
 * Copyright 2019 Markus Gronau
 * 
 * This file is part of PowerFlowAnalyzer.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
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
	public final static String ROOT_FLAG_CLASS = "flag";
	public final static String ROOT_CONFIGURATION_CLASS = "conf";
	public final static String ROOT_OUTLINE_CLASS = "outline";
	
	private ModelDBData db;
	
	private Map<String, ModelData> models = new HashMap<String, ModelData>();
	private Map<String, ModelData> flags = new HashMap<String, ModelData>();
	private Map<String, ModelData> configs = new HashMap<String, ModelData>();
	private ModelClassData networkClass, scriptClass, flagClass, configurationClass, outlineClass;
	
	private List<IDatabaseChangeListener> listeners = new ArrayList<IDatabaseChangeListener>();
	private boolean isDirty = false;
	
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
	
	public boolean isDirty() {
		return isDirty;
	}
	
	public void setDirty(boolean dirty) {
		isDirty = dirty;
	}
	
	public void refreshModels() {
		// remove old items
		models.clear();
		flags.clear();
		configs.clear();
//		System.out.println("model db: refresh model index");
		// add models recursively
		for (ModelClassData clazz : getData().getModelClass()) {
			if(ROOT_NETWORK_CLASS.equals(clazz.getID()))
				networkClass = clazz;
			else if(ROOT_SCRIPT_CLASS.equals(clazz.getID()))
				scriptClass = clazz;
			else if(ROOT_FLAG_CLASS.equals(clazz.getID()))
				flagClass = clazz;
			else if(ROOT_CONFIGURATION_CLASS.equals(clazz.getID()))
				configurationClass = clazz;
			else if(ROOT_OUTLINE_CLASS.equals(clazz.getID()))
				outlineClass = clazz;
		}
		if(networkClass != null)
			addModelsRecursive(networkClass, models);
		if(configurationClass != null)
			addModelsRecursive(configurationClass, configs);
		if(flagClass != null)
			addModelsRecursive(flagClass, flags);
//		for (ModelClassData clazz : getData().getModelClass()) {
//			addModelsRecursive(clazz);
//		}
//		System.out.println("    " + models.size() + " models found");
	}
	
	public void replaceTopClass(ModelClassData clazz) {
		if(ROOT_NETWORK_CLASS.equals(clazz.getID()))
			replaceTopClass(networkClass, clazz);
		else if(ROOT_SCRIPT_CLASS.equals(clazz.getID()))
			replaceTopClass(scriptClass, clazz);
		else if(ROOT_FLAG_CLASS.equals(clazz.getID()))
			replaceTopClass(flagClass, clazz);
		else if(ROOT_CONFIGURATION_CLASS.equals(clazz.getID()))
			replaceTopClass(configurationClass, clazz);
		else if(ROOT_OUTLINE_CLASS.equals(clazz.getID()))
			replaceTopClass(outlineClass, clazz);
//		getData().getModelClass().add(clazz);
	}
	
	private void replaceTopClass(ModelClassData oldClazz, ModelClassData newClazz) {
		if(oldClazz == null) {
			getData().getModelClass().add(newClazz);
		} else {
			for (int i = 0; i < getData().getModelClass().size(); i++) {
				if(getData().getModelClass().get(i) == oldClazz) {
					getData().getModelClass().set(i, newClazz);
					break;
				}
			}
		}
	}
	
	private void addModelsRecursive(AbstractModelElementData element, Map<String, ModelData> map) {
		if(element instanceof ModelData) {
			ModelData model = (ModelData) element;
			map.put(ModelDBUtils.getFullElementID(model), model);
//			System.out.println("   add " + ModelDBUtils.getParameterID(model));
		} else if(element instanceof ModelClassData) {
			for (ModelClassData clazz : ((ModelClassData) element).getModelClass())
				addModelsRecursive(clazz, map);
			for (ModelData childModel : ((ModelClassData) element).getModel())
				addModelsRecursive(childModel, map);
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
	
	public ModelData getFlag(String modelID) {
		return flags.get(modelID);
	}
	
	public ModelData getConfiguration(String modelID) {
		return configs.get(modelID);
	}
	
	public List<ModelData> getConfigurations(String modelIDPrefix) {
		List<ModelData> list = new ArrayList<ModelData>();
		for (String modelID : configs.keySet()) {
			if(modelID.startsWith(modelIDPrefix))
				list.add(configs.get(modelID));
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
	
	public ModelClassData getFlagClass() {
		return flagClass;
	}
	
	public ModelClassData getOutlineClass() {
		return outlineClass;
	}
	
	public ModelClassData getConfigurationClass() {
		return configurationClass;
	}
	
	public void fireElementChanged(DatabaseChangeEvent event) {
		setDirty(true);
		for (IDatabaseChangeListener listener : listeners) {
			listener.elementChanged(event);
		}
	}
	
	public void fireParameterChanged(DatabaseChangeEvent event) {
		setDirty(true);
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
