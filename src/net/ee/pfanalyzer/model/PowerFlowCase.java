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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.ee.pfanalyzer.model.data.CaseData;
import net.ee.pfanalyzer.model.data.DataViewerData;
import net.ee.pfanalyzer.model.data.NetworkData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.ui.CaseViewer;
import net.ee.pfanalyzer.ui.timer.DisplayTimer;

public class PowerFlowCase implements IDatabaseChangeListener {

	private File caseFile;
	private CaseData pfCase;
	private List<Network> networks = new ArrayList<Network>();
	private List<Network> networksAndScenarios = new ArrayList<Network>();
	private ModelDB modelDB;
	private CaseViewer viewer;
	private DisplayTimer timer;
	private long maxNetworkID = 0;
	private long caseID = -1;
	private List<IPowerFlowCaseListener> listeners = new ArrayList<IPowerFlowCaseListener>();
	private boolean isDirty = false;
	
	public PowerFlowCase(ModelDB modelDB) {
		pfCase = new CaseData();
		this.modelDB = modelDB;
		pfCase.setModelDb(modelDB.getData());
		timer = new DisplayTimer();
		addDefaultTableViewers();
		updateAllNetworkData();
		modelDB.addDatabaseChangeListener(this);
	}
	
	public PowerFlowCase(File caseFile) {
		this.caseFile = caseFile;
		try {
			CaseSerializer serializer = new CaseSerializer();
			pfCase = serializer.readCase(caseFile);
			modelDB = new ModelDB(pfCase.getModelDb());
			for (NetworkData netData : pfCase.getNetwork()) {
				addNetworkInternal(netData);
			}
			timer = new DisplayTimer();
			addDefaultTableViewers();
			modelDB.addDatabaseChangeListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public File getCaseFile() {
		return caseFile;
	}
	
	public void setCaseFile(File caseFile) {
		this.caseFile = caseFile;
	}
	
	public File getAbsolutePath(String relativePath) {
		if(getCaseFile() == null)
			return null;
		return new File(getCaseFile().getParentFile(), relativePath);
	}
	
	public ModelDB getModelDB() {
		return modelDB;
	}
	
	public void changeModelDB(ModelDB modelDB) {
		this.modelDB.removeDatabaseChangeListener(this);
		this.modelDB = modelDB;
		pfCase.setModelDb(modelDB.getData());
		updateAllNetworkData();
		modelDB.addDatabaseChangeListener(this);		
	}
	
	public List<Network> getNetworks(boolean includeScenarios) {
		return includeScenarios ? networksAndScenarios : networks;
	}
	
//	public List<Network> getAllNetworks() {
//		return networksAndScenarios;
//	}
	
	public boolean isDirty() {
		if(isDirty)
			return true;
		if(getModelDB().isDirty())
			return true;
		for (Network network : networksAndScenarios) {
			if(network.isDirty())
				return true;
		}
		return false;
	}
	
	public void addPowerFlowCaseListener(IPowerFlowCaseListener listener) {
		listeners.add(listener);
	}
	
	public void removePowerFlowCaseListener(IPowerFlowCaseListener listener) {
		listeners.remove(listener);
	}
	
	void dirtyStateChanged() {
		isDirty = isDirty();
		for (IPowerFlowCaseListener listener : listeners) {
			listener.caseChanged(this);
		}
	}
	
	public DisplayTimer getTimer() {
		return timer;
	}
	
	public CaseViewer getViewer() {
		return viewer;
	}
	
	public List<DataViewerData> getDataViewerData() {
		return pfCase.getDataViewer();
	}

	public void setViewer(CaseViewer viewer) {
		this.viewer = viewer;
	}
	
	private void addDefaultTableViewers() {
		// backward compatibility
		removeOldViewers();
		if(pfCase.getDataViewer().size() > 0)
			return;
		pfCase.getDataViewer().add(createNetworkViewerData("Network"));
		pfCase.getDataViewer().add(createElementViewerData("Network Navigator"));
		pfCase.getDataViewer().add(createTableViewerData("Bus Data", "bus"));
		pfCase.getDataViewer().add(createTableViewerData("Branch Data", "branch"));
		pfCase.getDataViewer().add(createTableViewerData("Generator Data", "generator"));
	}
	
	private void removeOldViewers() {
		for (int i = 0; i < pfCase.getDataViewer().size(); i++) {
			if(pfCase.getDataViewer().get(i).getModelID() == null) {
				pfCase.getDataViewer().remove(i);
				removeOldViewers();
				break;
			}
		}
	}
	
	private DataViewerData createNetworkViewerData(String title) {
		DataViewerData viewerData = new DataViewerData();
		viewerData.setModelID("viewer.network.map");
		NetworkParameter p = new NetworkParameter();
		p.setID("TITLE");
		p.setValue(title);
		viewerData.getParameter().add(p);
		p = new NetworkParameter();
		p.setID("POSITION");
		p.setValue("left");
		viewerData.getParameter().add(p);
		return viewerData;
	}
	
	private DataViewerData createElementViewerData(String title) {
		DataViewerData viewerData = new DataViewerData();
		viewerData.setModelID("viewer.element.viewer");
		NetworkParameter p = new NetworkParameter();
		p.setID("TITLE");
		p.setValue(title);
		viewerData.getParameter().add(p);
		p = new NetworkParameter();
		p.setID("POSITION");
		p.setValue("right");
		viewerData.getParameter().add(p);
		return viewerData;
	}
	
	private DataViewerData createTableViewerData(String title, String filter) {
		DataViewerData viewerData = new DataViewerData();
		viewerData.setModelID("viewer.table.type_filter");
		NetworkParameter p = new NetworkParameter();
		p.setID("TITLE");
		p.setValue(title);
		viewerData.getParameter().add(p);
		p = new NetworkParameter();
		p.setID("ELEMENT_FILTER");
		p.setValue(filter);
		viewerData.getParameter().add(p);
		p = new NetworkParameter();
		p.setID("POSITION");
		p.setValue("bottom");
		viewerData.getParameter().add(p);
		return viewerData;
	}
	
	private Network addNetworkInternal(NetworkData netData) {
		return addNetworkInternal(new Network(netData), false);
	}

	private Network addNetworkInternal(Network network, boolean isScenario) {
		network.setPowerFlowCase(this);
		if(isScenario == false)
			getNetworks(false).add(network);
		getNetworks(true).add(network);
		if(network.getInternalID() == -1) {
			maxNetworkID++;
			while(getNetwork(maxNetworkID) != null) {
				maxNetworkID++;
			}
			network.setInternalID(maxNetworkID);
		} else
			maxNetworkID = Math.max(maxNetworkID, network.getInternalID());
		network.updateModels();
		network.setCaseID(getCaseID());
		return network;
	}
	
	public long getCaseID() {
		return caseID;
	}

	public void setCaseID(long caseID) {
		this.caseID = caseID;
		for (NetworkData netData : pfCase.getNetwork()) {
			netData.setCaseID(caseID);
		}
	}
	
	public Network addNetwork(Network network) {
		addNetworkInternal(network, false);
		pfCase.getNetwork().add(network.getData());
		return network;
	}
	
	public Network addNetwork(NetworkData netData) {
		Network network = addNetworkInternal(netData);
		pfCase.getNetwork().add(netData);
		network.setDirty(true);
		return network;
	}
	
	public void removeNetwork(Network network) {
		if(network.getParentNetwork() == null) {
			for (int i = 0; i < getNetworks(false).size(); i++) {
				if(getNetworks(false).get(i).getInternalID() == network.getInternalID()) {
					getNetworks(false).remove(i);
					break;
				}
			}
		} else
			network.getParentNetwork().removeScenario(network);
		for (int i = 0; i < getNetworks(true).size(); i++) {
			if(getNetworks(true).get(i).getInternalID() == network.getInternalID()) {
				getNetworks(true).remove(i);
				break;
			}
		}
		removeNetwork(network.getData());
	}
	
	private void removeNetwork(NetworkData network) {
		for (int i = 0; i < pfCase.getNetwork().size(); i++) {
			if(pfCase.getNetwork().get(i).getInternalID() == network.getInternalID()) {
				pfCase.getNetwork().remove(i);
				break;
			}
		}
	}
	
	public void addScenario(Network parent, Network scenario) {
		addNetworkInternal(scenario, true);
		scenario.setPowerFlowCase(this);
		parent.addScenario(scenario);
		scenario.updateModels();
	}
	
	public Network getNetwork(long networkID) {
		for (Network network : getNetworks(true)) {
			if(network.getInternalID() == networkID)
				return network;
		}
		return null;
	}

	public void setNetworkData(Network network, NetworkData networkData) {
		removeNetwork(networkData);
		pfCase.getNetwork().add(networkData);
		network.setData(networkData);
		network.updateModels();
		network.setDirty(true);
//		getNetwork().fireNetworkChanged();
	}
	
	private void updateAllNetworkData() {
		for (Network net : getNetworks(true))
			net.updateModels();
	}
	
	@Override
	public void elementChanged(DatabaseChangeEvent event) {
//		System.out.println("elementChanged: " + event.getParameterID());
//		System.out.println("  old: " + event.getOldValue());
//		System.out.println("  new: " + event.getNewValue());
	}

	@Override
	public void parameterChanged(DatabaseChangeEvent event) {
//		System.out.println("parameterChanged: " + event.getParameterID());
//		System.out.println("  old: " + event.getOldValue());
//		System.out.println("  new: " + event.getNewValue());
		for (Network network : getNetworks(true)) {
			network.updateModels();
			network.fireNetworkChanged();
		}
	}

	public Network createNetworkCopy(Network network) throws Exception {
		Network newNet = network.copy();
		newNet.updateModels();
		return newNet;
	}
	
//	public Network createScenario(Network network) throws Exception {
//		Network scenario = network.copy();
//		network.addScenario(scenario);
//		updateNetworkData(scenario);
//		return scenario;
//	}
	
	public void save() {
		try {
			CaseSerializer serializer = new CaseSerializer();
			serializer.writeCase(pfCase, caseFile);
			getModelDB().setDirty(false);
			for (Network network : networksAndScenarios) {
				network.setDirty(false);
			}
			isDirty = false;
			dirtyStateChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
