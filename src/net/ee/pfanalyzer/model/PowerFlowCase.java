package net.ee.pfanalyzer.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.ee.pfanalyzer.model.data.CaseData;
import net.ee.pfanalyzer.model.data.ModelData;
import net.ee.pfanalyzer.model.data.NetworkData;
import net.ee.pfanalyzer.ui.NetworkContainer;

public class PowerFlowCase implements IDatabaseChangeListener {

	private File caseFile;
	private CaseData pfCase;
	private List<Network> networks = new ArrayList<Network>();
	private ModelDB modelDB;
	private NetworkContainer viewer;
	private long maxNetworkID = 0;
	
	public PowerFlowCase(ModelDB modelDB) {
		pfCase = new CaseData();
		this.modelDB = modelDB;
		pfCase.setModelDb(modelDB.getData());
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
	
	public ModelDB getModelDB() {
		return modelDB;
	}
	
	public List<Network> getNetworks() {
		return networks;
	}
	
	public NetworkContainer getViewer() {
		return viewer;
	}

	public void setViewer(NetworkContainer viewer) {
		this.viewer = viewer;
	}
	
	private Network addNetworkInternal(NetworkData netData) {
		return addNetworkInternal(new Network(netData));
	}
	
	private Network addNetworkInternal(Network network) {
		network.setPowerFlowCase(this);
		networks.add(network);
		if(network.getInternalID() == -1) {
			maxNetworkID++;
			while(getNetwork(maxNetworkID) != null) {
				maxNetworkID++;
			}
			network.setInternalID(maxNetworkID);
		} else
			maxNetworkID = Math.max(maxNetworkID, network.getInternalID());
		updateNetworkData(network);
		return network;
	}
	
	public Network addNetwork(Network network) {
		addNetworkInternal(network);
		pfCase.getNetwork().add(network.getData());
		return network;
	}
	
	public Network addNetwork(NetworkData netData) {
		Network network = addNetworkInternal(netData);
		pfCase.getNetwork().add(netData);
		return network;
	}
	
	public void removeNetwork(Network network) {
		for (int i = 0; i < getNetworks().size(); i++) {
			if(getNetworks().get(i).getInternalID() == network.getInternalID()) {
				getNetworks().remove(i);
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
	
	public Network getNetwork(long networkID) {
		for (Network network : getNetworks()) {
			if(network.getInternalID() == networkID)
				return network;
		}
		return null;
	}

	public void setNetworkData(Network network, NetworkData networkData) {
		removeNetwork(networkData);
		pfCase.getNetwork().add(networkData);
		network.setData(networkData);
		updateNetworkData(network);
//		getNetwork().fireNetworkChanged();
	}
	
	private void updateAllNetworkData() {
		for (Network net : getNetworks())
			updateNetworkData(net);
	}
	
	private void updateNetworkData(Network network) {
//		System.out.println("case: setting network data with " + network.getElements().size() + " elements");
		// setting network class containing global parameters
		network.setGlobalParameterClass(getModelDB().getNetworkClass());
		network.setScriptParameterClass(getModelDB().getScriptClass());
		// setting model references in network elements
		for (AbstractNetworkElement element : network.getElements()) {
			updateNetworkElement(element);
		}
		for (Network scenario : network.getScenarios()) {
			updateNetworkData(scenario);
		}
	}
	
	void updateNetworkElement(AbstractNetworkElement element) {
		ModelData model = getModelDB().getModel(element.getModelID());
//		System.out.println("    model id: " + element.getModelID());
//		if(model != null)
//			System.out.println("    setting model: " + element.getModelID());
		element.setModel(model);
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
		for (Network network : getNetworks()) {
			network.fireNetworkChanged();
		}
	}

	public Network createNetworkCopy(Network network) throws Exception {
		Network newNet = network.copy();
		updateNetworkData(newNet);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
