package net.ee.pfanalyzer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ee.pfanalyzer.io.IllegalDataException;
import net.ee.pfanalyzer.model.data.AbstractNetworkElementData;
import net.ee.pfanalyzer.model.data.NetworkData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.model.util.ParameterSupport;

public class Network extends ParameterSupport {

	private double[][] busData, generatorData, branchData, coordinateData;
	
	private List<Bus> busses = new ArrayList<Bus>();
	private List<Branch> branches = new ArrayList<Branch>();
	private List<Generator> generators = new ArrayList<Generator>();
//	private Coordinates[] coordinates;
//	private String[] locationNames;
	private List<CombinedBus> combinedBusList;
	private List<CombinedBranch> combinedBranchList;
	private Map<String, double[]> customData = new HashMap<String, double[]>();
	
	private NetworkData networkData;
	private List<AbstractNetworkElement> elements = new ArrayList<AbstractNetworkElement>();
	
	private double time;
	
	private int voltageLevel;
	
	private boolean successful;
	
	public Network() {
		networkData = new NetworkData();	
	}
	
	public Network(NetworkData networkData) {
		this.networkData = networkData;
		updateNetworkData();
	}
	
	public NetworkData getData() {
		return networkData;
	}
	
	void setData(NetworkData data) {
		networkData = data;
		updateNetworkData();
	}

	private void updateNetworkData() {
		// update elements
		getElements().clear();
		getBusses().clear();
//		System.out.println("network: update data");
		for (int i = 0; i < networkData.getElement().size(); i++) {
			AbstractNetworkElementData element = networkData.getElement().get(i);
			if(element.getModelID().startsWith("bus.")) {
				addElementInternal(new Bus(this, element, i));
//				System.out.println("    adding bus: " + element.getModelID());
			} else if(element.getModelID().startsWith("branch.")) {
					addElementInternal(new Branch(this, element, i));
//					System.out.println("    adding branch: " + element.getModelID());
			} else if(element.getModelID().startsWith("generator.")) {
				addElementInternal(new Generator(this, element, i));
//				System.out.println("    adding generator: " + element.getModelID());
			}
		}
//		System.out.println("    " + getElements().size() + " elements added");
		// set bus references in branches
		for (Branch branch : getBranches()) {
			branch.setFromBus(getBus(branch.getFromBusNumber()));
			branch.setToBus(getBus(branch.getToBusNumber()));
		}
		// set bus references in generators
		for (Generator generator : getGenerators()) {
			generator.setBus(getBus(generator.getBusNumber()));
		}
	}
	
	@Override
	public List<NetworkParameter> getParameterList() {
		return networkData.getParameter();
	}
	
	public void addElement(AbstractNetworkElement element) {
		networkData.getElement().add(element.getElementData());
		addElementInternal(element);
	}
	
	private void addElementInternal(AbstractNetworkElement element) {
		getElements().add(element);
		if(element instanceof Bus)
			getBusses().add((Bus) element);
		if(element instanceof Branch)
			branches.add((Branch) element);
		if(element instanceof Generator)
			generators.add((Generator) element);
	}
	
	public List<AbstractNetworkElement> getElements() {
		return elements;
	}
	
	public List<AbstractNetworkElement> getElements(String idPrefix) {
		List<AbstractNetworkElement> list = new ArrayList<AbstractNetworkElement>();
		for (AbstractNetworkElement element : getElements()) {
			if(element.getModel() == null)
				continue;
			if(ModelDBUtils.getParameterID(element.getModel()).startsWith(idPrefix))
				list.add(element);
		}
		return list;
	}
	
	public void initializeData() {
		if(combinedBusList != null)
			return;
		combinedBusList = new ArrayList<CombinedBus>();
		// combine bus nodes
		for (int i = 0; i < busses.size(); i++) {
			Bus bus = busses.get(i);
			double longitude = bus.getLongitude();
			double lattitude = bus.getLattitude();
			if(Double.isNaN(lattitude) || Double.isNaN(longitude)) // no coords set
				continue;
			boolean found = false;
//			System.out.println("Bus " + i);
			for (CombinedBus cbus : combinedBusList) {
				if(cbus.hasLongitude(longitude) && cbus.hasLattitude(lattitude)) {
//					System.out.println("  added to combined bus " + cbus.getIndex());
					cbus.addBus(bus);
					found = true;
					break;
				}
			}
			if(found == false) {
				CombinedBus cbus = new CombinedBus(bus, longitude, lattitude);
				cbus.setIndex(combinedBusList.size());
				combinedBusList.add(cbus);
//				System.out.println("  created new combined bus " + cbus.getIndex());
			}
		}
//		// add bus nodes with no coordinates (e.g. generator busses) TODO remove this
//		for (int i = coords.length; i < busses.size(); i++) {
//			Bus bus = busses.get(i);
//			Bus realBus = getBus(bus.getRealBusIndex());
//			if(realBus == null)
//				continue;
//			for (CombinedBus cbus : combinedBusList) {
//				if(cbus.contains(realBus)) {
//					cbus.addBus(bus);
//					break;
//				}
//			}
//		}
		int busCount = 0;
		for (CombinedBus cbus : combinedBusList) {
			busCount += cbus.getNetworkElementCount();
		}
//		System.out.println(combinedBusList.size() + " combined bus nodes found with " + busCount + " elements");
		// combine generators
		for (int i = 0; i < getGeneratorsCount(); i++) {
			Generator generator = generators.get(i);
			Bus bus = generator.getBus();
			CombinedBus cbus = getCombinedBus(bus);
			if(cbus != null)
				cbus.addGenerator(generator);
		}
		combinedBranchList = new ArrayList<CombinedBranch>();
		// combine branches
		for (int i = 0; i < getBranchesCount(); i++) {
			Branch branch = branches.get(i);
			Bus fromBus = branch.getFromBus();
			Bus toBus = branch.getToBus();
			CombinedBus cFromBus = getCombinedBus(fromBus);
			CombinedBus cToBus = getCombinedBus(toBus);
			if(cFromBus == null || cToBus == null)
				continue;
			if(cFromBus == cToBus) {// combined start and end busses are the same -> branch is a transformer!
				cFromBus.addTransformer(new Transformer(branch));
				continue;
			}
//			System.out.println("Branch " + i);
//			System.out.println("  from bus: " + fromBus.getIndex() + "(" + fromBusIndex + ")");
//			System.out.println("    combined bus: " + cFromBus.getIndex());
//			System.out.println("  to bus  : " + toBus.getIndex() + "(" + toBusIndex + ")");
//			System.out.println("    combined bus: " + cToBus.getIndex());
			boolean found = false;
			for (CombinedBranch cbranch : combinedBranchList) {
				if(cbranch.hasBusNodes(cFromBus, cToBus)) {
//					System.out.println("  added to combined branch " + cbranch.getIndex());
					cbranch.addBranch(branch);
					found = true;
					break;
				}
			}
			if(found == false) {
				CombinedBranch cbranch = new CombinedBranch(cFromBus, cToBus, branch);
				cbranch.setIndex(combinedBranchList.size());
				combinedBranchList.add(cbranch);
//				System.out.println("  created new combined branch " + cbranch.getIndex());
			}
		}
//		System.out.println(combinedBranchList.size() + " combined branches found");
	}
	
	public int getCombinedBusCount() {
		return combinedBusList == null ? 0 : combinedBusList.size();
	}
	
	public CombinedBus getCombinedBus(int index) {
		return combinedBusList.get(index);
	}
	
	public CombinedBus getCombinedBus(AbstractNetworkElement data) {
		for (CombinedBus cbus : combinedBusList) {
			if(cbus.contains(data))
				return cbus;
		}
		return null;
	}
	
	public int getCombinedBranchCount() {
		return combinedBranchList == null ? 0 : combinedBranchList.size();
	}
	
	public CombinedBranch getCombinedBranch(int index) {
		return combinedBranchList.get(index);
	}
	
	public CombinedBranch getCombinedBranch(AbstractNetworkElement data) {
		for (CombinedBranch cbranch : combinedBranchList) {
			if(cbranch.contains(data))
				return cbranch;
		}
		return null;
	}

	/**
	 * @deprecated
	 */
	public double[][] getBusData() {
		return busData;
	}
	
	public List<Bus> getBusses() {
		return busses;
	}

	/**
	 * @deprecated
	 */
	public void setBusData(double[][] busData) {
		throw new RuntimeException("Must not be called");
//		this.busData = busData;
//		busses.clear();
//		for (int i = 0; i < busData.length; i++) {
//			busses.add(new Bus(this, i));
//		}
	}
	
	public Bus getBus(int busNumber) {
		for (int i = 0; i < busses.size(); i++) {
			Bus bus = busses.get(i);
			if(bus.getBusNumber() == busNumber)
				return bus;
		}
		return null;
	}
	
//	public Bus getBus(int index) {
//		if(index < busses.size())
//			return busses.get(index);
//		// bus must be searched manually
//		for (int i = 0; i < busses.size(); i++) {
//			Bus bus = busses.get(i);
//			if(bus.getBusNumber() == index)
//				return bus;
//		}
//		return null;
//	}
	
//	public void addBus(int index, Bus bus) {
//		busses.add(index, bus);
//		for (int i = index; i < busses.size(); i++) {
//			getBus(i).setIndex(i);
//		}
//	}
//	
//	public void removeBus(Bus bus) {
//		busses.remove(bus);
//		for (int i = bus.getIndex(); i < busses.size(); i++) {
//			getBus(i).setIndex(i);
//		}
//	}
//	
//	void updateBusData() {
//		busData = new double[busses.size()][];
//		for (int i = 0; i < busData.length; i++) {
//			busData[i] = busses.get(i).getData();
//		}
//	}

	/**
	 * @deprecated
	 */
	public double[][] getGeneratorData() {
		return generatorData;
	}
	
	public List<Generator> getGenerators() {
		return generators;
	}

	/**
	 * @deprecated
	 */
	public void setGeneratorData(double[][] generatorData) {
		throw new RuntimeException("Must not be called");
//		this.generatorData = generatorData;
//		generators = new Generator[generatorData.length];
//		for (int i = 0; i < generatorData.length; i++) {
//			generators[i] = new Generator(this, i);
//		}
	}
	
	public int getGeneratorsCount() {
		return generators.size();
	}
	
//	public Generator getGenerator(int index) {
//		return generators.get(index);
//	}

	/**
	 * @deprecated
	 */
	public double[][] getBranchData() {
		return branchData;
	}

	/**
	 * @deprecated
	 */
	public void setBranchData(double[][] branchData) {
		throw new RuntimeException("Must not be called");
//		this.branchData = branchData;
//		branches = new Branch[branchData.length];
//		for (int i = 0; i < branchData.length; i++) {
//			branches[i] = new Branch(this, i);
//		}
	}
	
	public List<Branch> getBranches() {
		return branches;
	}
	
	public int getBranchesCount() {
		return branches.size();
	}
	
//	public Branch getBranch(int index) {
//		return branches.get(index);
////		return branches[index];
//	}
	
	/**
	 * @deprecated
	 */
	public double[][] getCoordinateData() {
		return coordinateData;
	}

	/**
	 * @deprecated
	 */
	public void setCoordinateData(double[][] coordinateData) {
		throw new RuntimeException("Must not be called");
//		this.coordinateData = coordinateData;
//		coordinates = new Coordinates[coordinateData.length];
//		for (int i = 0; i < coordinateData.length; i++) {
//			coordinates[i] = new Coordinates(this, i);
//		}
//		for (int i = 0; i < busses.size(); i++) {
//			Bus bus = getBus(i);
//			if(i < coordinateData.length) {
//				bus.setCoordinates(coordinateData[i]);
//			} else
//				bus.setCoordinates(getBus(bus.getRealBusIndex()).getCoordinates());
//		}
	}
	
//	/**
//	 * @deprecated
//	 */
//	public void setLocationNames(String[] names) {
//		for (int i = 0; i < busses.size(); i++) {
//			Bus bus = getBus(i);
//			if(i < names.length)
//				bus.setName(names[i]);
//			else
//				bus.setName(getBus(bus.getRealBusIndex()).getName());
//		}
//		this.locationNames = names;
//	}
	
//	public String getLocationName(int index) {
//		if(locationNames != null)
//			return locationNames[index];
//		return null;
//	}
//	
//	/**
//	 * @deprecated
//	 */
//	public String[] getLocationNames() {
////		String locationNames = new String[]
//		return locationNames;
//	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public int getVoltageLevel() {
		return voltageLevel;
	}

	public void setVoltageLevel(int voltageLevel) {
		this.voltageLevel = voltageLevel;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}
	
	public void setData(String name, double[] data) {
		customData.put(name, data);
	}
	
	public double[] getData(String name) {
		return customData.get(name);
	}
	
	public String toXML() throws IllegalDataException {
		try {
			return CaseSerializer.writeNetwork(getData());
		} catch (Exception e) {
			throw new IllegalDataException("Cannot write network data", e);
		}
	}
}
