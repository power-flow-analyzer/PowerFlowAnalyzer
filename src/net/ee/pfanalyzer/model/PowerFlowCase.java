package net.ee.pfanalyzer.model;

import java.io.File;

import net.ee.pfanalyzer.model.data.CaseData;
import net.ee.pfanalyzer.model.data.ModelData;
import net.ee.pfanalyzer.model.data.NetworkData;
import net.ee.pfanalyzer.ui.PowerFlowContainer;

public class PowerFlowCase {

	private File caseFile;
	private CaseData pfCase;
	private Network network;
	private ModelDB modelDB;
	private PowerFlowContainer viewer;
	
	public PowerFlowCase() {
		pfCase = new CaseData();
		network = new Network();
		modelDB = new ModelDB();
		pfCase.setNetwork(network.getData());
		pfCase.setModelDb(modelDB.getData());
		updateNetworkData(getNetwork());
	}
	
	public PowerFlowCase(File caseFile) {
		this.caseFile = caseFile;
		try {
			CaseSerializer serializer = new CaseSerializer();
			pfCase = serializer.readCase(caseFile);
			network = new Network(pfCase.getNetwork());
			modelDB = new ModelDB(pfCase.getModelDb());
			updateNetworkData(getNetwork());
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
	
	public Network getNetwork() {
		return network;
	}
	
	public PowerFlowContainer getViewer() {
		return viewer;
	}

	public void setViewer(PowerFlowContainer viewer) {
		this.viewer = viewer;
	}

	public void setNetworkData(NetworkData networkData) {
		setNetworkData(getNetwork(), networkData);
	}

	public void setNetworkData(Network network, NetworkData networkData) {
		if(getNetwork() == network)
			pfCase.setNetwork(networkData);
		network.setData(networkData);
		updateNetworkData(network);
//		getNetwork().fireNetworkChanged();
	}
	
	private void updateNetworkData(Network network) {
		// setting network class containing global parameters
		network.setGlobalParameterClass(getModelDB().getNetworkClass());
		// setting model references in network elements
//		System.out.println("case: setting network data with " + getNetwork().getElements().size() + " elements");
		for (AbstractNetworkElement element : network.getElements()) {
			ModelData model = getModelDB().getModel(element.getModelID());
//			System.out.println("    model id: " + element.getModelID());
//			if(model != null)
//				System.out.println("    setting model: " + element.getModelID());
			element.setModel(model);
		}
		for (Network scenario : network.getScenarios()) {
			updateNetworkData(scenario);
		}
	}
	
	public Network createScenario(Network network) throws Exception {
		Network scenario = network.copy();
		network.addScenario(scenario);
		updateNetworkData(scenario);
		return scenario;
	}
	
	public void save() {
		try {
			CaseSerializer serializer = new CaseSerializer();
			serializer.writeCase(pfCase, caseFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
