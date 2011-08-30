package net.ee.pfanalyzer.model;

import java.io.File;

import net.ee.pfanalyzer.model.data.CaseData;
import net.ee.pfanalyzer.model.data.ModelData;
import net.ee.pfanalyzer.model.data.NetworkData;

public class PowerFlowCase {

	private File caseFile;
	private CaseData pfCase;
	private Network network;
	private ModelDB modelDB;
	
	public PowerFlowCase() {
		pfCase = new CaseData();
		network = new Network();
		modelDB = new ModelDB();
		pfCase.setNetwork(network.getData());
		pfCase.setModelDb(modelDB.getData());
	}
	
	public PowerFlowCase(File caseFile) {
		this.caseFile = caseFile;
		try {
			CaseSerializer serializer = new CaseSerializer();
			pfCase = serializer.readCase(caseFile);
			network = new Network(pfCase.getNetwork());
			modelDB = new ModelDB(pfCase.getModelDb());
			updateNetworkData();
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
	
	public void setNetworkData(NetworkData networkData) {
		getNetwork().setData(networkData);
		pfCase.setNetwork(networkData);
		updateNetworkData();
	}
	
	private void updateNetworkData() {
		System.out.println("case: setting network data with " + getNetwork().getElements().size() + " elements");
		for (AbstractNetworkElement element : getNetwork().getElements()) {
			ModelData model = getModelDB().getModel(element.getModelID());
//			System.out.println("    model id: " + element.getModelID());
//			if(model != null)
//				System.out.println("    setting model: " + element.getModelID());
			element.setModel(model);
		}
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
