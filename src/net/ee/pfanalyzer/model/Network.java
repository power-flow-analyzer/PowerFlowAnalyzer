package net.ee.pfanalyzer.model;

import java.util.ArrayList;
import java.util.List;

import net.ee.pfanalyzer.io.IllegalDataException;
import net.ee.pfanalyzer.model.data.AbstractNetworkElementData;
import net.ee.pfanalyzer.model.data.ModelClassData;
import net.ee.pfanalyzer.model.data.ModelData;
import net.ee.pfanalyzer.model.data.NetworkData;
import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.model.data.NetworkParameterPurposeRestriction;
import net.ee.pfanalyzer.model.util.ElementGroupingUtils;
import net.ee.pfanalyzer.model.util.ListUtils;
import net.ee.pfanalyzer.model.util.ModelDBUtils;
import net.ee.pfanalyzer.model.util.ParameterSupport;
import net.ee.pfanalyzer.model.util.ParameterUtils;

public class Network extends ParameterSupport {

	private List<Bus> busses = new ArrayList<Bus>();
	private List<Branch> branches = new ArrayList<Branch>();
	private List<Generator> generators = new ArrayList<Generator>();
	private List<CombinedBus> combinedBusList = new ArrayList<CombinedBus>();
	private List<CombinedBranch> combinedBranchList = new ArrayList<CombinedBranch>();
	private List<MarkerElement> markers = new ArrayList<MarkerElement>();
	
	private NetworkData networkData;
	private PowerFlowCase caze;
	private Network parentNetwork;
	private ModelClassData globalParameterClass, scriptParameterClass;
	private List<Network> scenarios = new ArrayList<Network>();
	private List<AbstractNetworkElement> elements = new ArrayList<AbstractNetworkElement>();
	private List<INetworkChangeListener> listeners = new ArrayList<INetworkChangeListener>();
	private Boolean hasFailures = null;
	private Boolean wasCalculated = null;
	private boolean isDirty = false;
	
	private double time;
	
	private int voltageLevel;
	
	private boolean successful;
	
	public Network() {
		networkData = new NetworkData();	
	}
	
	public Network(long caseID) {
		networkData = new NetworkData();	
		setCaseID(caseID);
	}
	
	public Network(NetworkData networkData) {
		this.networkData = networkData;
		updateNetworkData();
		findCombinedElements();
	}
	
	public NetworkData getData() {
		return networkData;
	}
	
	void setData(NetworkData data) {
		networkData = data;
		updateNetworkData();
		findCombinedElements();
	}
	
	public PowerFlowCase getPowerFlowCase() {
		return caze;
	}

	void setPowerFlowCase(PowerFlowCase caze) {
		this.caze = caze;
	}
	
	public ModelDB getModelDB() {
		if(getPowerFlowCase() != null)
			return getPowerFlowCase().getModelDB();
		return null;
	}

	Network copy() throws Exception {
		NetworkData copiedData = (NetworkData) CaseSerializer.copy(getData());
		copiedData.getScenario().clear();// remove all scenarios
		copiedData.setInternalID(-1L);// clear internal ID
		Network newNetwork = new Network(copiedData);
		return newNetwork;
	}
	
	public long getInternalID() {
		return getData().getInternalID();
	}
	
	public void setInternalID(long id) {
		getData().setInternalID(id);
	}
	
	public long getCaseID() {
		return getData().getCaseID();
	}
	
	public void setCaseID(long id) {
		getData().setCaseID(id);
	}
	
	void addScenario(Network scenario) {
		getData().getScenario().add(scenario.getData());
		getScenarios().add(scenario);
		scenario.setParentNetwork(this);
	}
	
	public void removeScenario(Network scenario) {
		for (int i = 0; i < getScenarios().size(); i++) {
			if(getScenarios().get(i).getInternalID() == scenario.getInternalID()) {
				getScenarios().remove(i);
				break;
			}
		}
		removeScenario(scenario.getData());
//		Object removed = null;
//		for (int i = 0; i < getScenarios().size(); i++) {
//			if(getScenarios().get(i).getData() == scenario.getData()) {
//				removed = getData().getScenario().remove(i);
//				break;
//			}
//		}
//		if(removed == null)
//			throw new RuntimeException("Scenario data could not be deleted from network");
//		removed = null;
//		for (int i = 0; i < getScenarios().size(); i++) {
//			if(getScenarios().get(i) == scenario) {
//				removed = getScenarios().remove(i);
//				break;
//			}
//		}
//		if(removed == null)
//			throw new RuntimeException("Scenario could not be deleted from network");
	}
	
	public void removeScenario(NetworkData scenario) {
		for (int i = 0; i < getData().getScenario().size(); i++) {
			if(getData().getScenario().get(i).getInternalID() == scenario.getInternalID()) {
				getData().getScenario().remove(i);
				break;
			}
		}
	}
	
	public Network getParentNetwork() {
		return parentNetwork;
	}

	public void setParentNetwork(Network parentNetwork) {
		this.parentNetwork = parentNetwork;
	}

	public ModelClassData getGlobalParameterClass() {
		return globalParameterClass;
	}

	public void setGlobalParameterClass(ModelClassData globalParameterClass) {
		this.globalParameterClass = globalParameterClass;
	}
	
	public ModelClassData getScriptParameterClass() {
		return scriptParameterClass;
	}

	public void setScriptParameterClass(ModelClassData scriptParameterClass) {
		this.scriptParameterClass = scriptParameterClass;
	}

	public void setDefaultCoordinates() {
		int columnCount = (int) Math.floor(Math.sqrt(getBusses().size()));
		int rowCount = (int) Math.ceil(((double) getBusses().size()) / ((double) columnCount));
		int row = 0;
		int col = 0;
		for (int i = 0; i < getBusses().size(); i++) {
			Bus bus = getBusses().get(i);
			double lattitudeSign = (row + 1) % 2 * -1;
			double longitudeSign = (col + 1) % 2 * -1;
			double lattitude = col + 1 + lattitudeSign * (row + 1) % columnCount * 0.2;
			double longitude = row + 1 + longitudeSign * (col + 1) % rowCount * 0.2;
			col++;
			if(col == columnCount) {
				col = 0;
				row++;
			}
			bus.setLongitude(longitude);
			bus.setLatitude(lattitude);
		}
		findCombinedElements();
	}

	@Override
	public NetworkParameter getParameterValue(String id) {
		NetworkParameter parameter = super.getParameterValue(id);
		if(parameter != null)
			return parameter;
		if(getGlobalParameterClass() != null) {
			parameter = ModelDBUtils.getParameterValue(getGlobalParameterClass(), id);
			if(parameter != null)
				return parameter;
		}
		if(getScriptParameterClass() != null)
			return ModelDBUtils.getParameterValue(getScriptParameterClass(), id);
		return null;
	}
	
	public List<Network> getScenarios() {
		return scenarios;
	}
	
	public List<NetworkParameter> getScenarioParameters() {
		List<NetworkParameter> parameters = new ArrayList<NetworkParameter>();
		for (NetworkParameter parameter : getGlobalParameterClass().getParameter()) {
			if(NetworkParameterPurposeRestriction.SCENARIO.equals(parameter.getPurpose()))
				parameters.add(parameter);
		}
		return parameters;
	}

	private void updateNetworkData() {
		// update elements
		getElements().clear();
		getBusses().clear();
		getBranches().clear();
		getGenerators().clear();
		getMarkers().clear();
//		getScenarios().clear();
//		System.out.println("network: update data");
		for (int i = 0; i < networkData.getElement().size(); i++) {
			AbstractNetworkElementData element = networkData.getElement().get(i);
			if(element.getModelID().startsWith("bus.")) {
				addElementInternal(new Bus(this, element, getBusses().size()));
//				System.out.println("    adding bus: " + element.getModelID());
			} else if(element.getModelID().startsWith("branch.")) {
				addElementInternal(new Branch(this, element, getBranches().size()));
//					System.out.println("    adding branch: " + element.getModelID());
			} else if(element.getModelID().startsWith("generator.")) {
				addElementInternal(new Generator(this, element, getGenerators().size()));
//				System.out.println("    adding generator: " + element.getModelID());
			} else if(element.getModelID().startsWith("marker.")) {
				addElementInternal(new MarkerElement(this, element, getMarkers().size()));
//				System.out.println("    adding marker: " + element.getModelID());
			} else {
				addElementInternal(new NetworkElement(this, element, getElements().size()));
//				System.out.println("    adding element: " + element.getModelID());
			}
		}
//		System.out.println("    " + getElements().size() + " elements added");
		// set bus references in elements
		for (AbstractNetworkElement element : getElements()) {
			updateElement(element);
		}
//		for (NetworkData data : networkData.getScenario()) {
//			Network scenario = new Network(data);
//			getScenarios().add(scenario);
//		}
		hasFailures = null;
		wasCalculated = null;
	}
	
	void updateModels() {
		if(getModelDB() == null)
			return;
//		System.out.println("case: setting network data with " + network.getElements().size() + " elements");
		// setting network class containing global parameters
		setGlobalParameterClass(getModelDB().getNetworkClass());
		setScriptParameterClass(getModelDB().getScriptClass());
		// setting model references in network elements
		for (AbstractNetworkElement element : getElements()) {
			updateModel(element);
		}
//		for (Network scenario : network.getScenarios()) {
//			updateModels(scenario);
//		}
	}
	
	private void updateModel(AbstractNetworkElement element) {
		if(getModelDB() == null)
			return;
		ModelData model = getModelDB().getModel(element.getModelID());
//		System.out.println("    model id: " + element.getModelID());
//		if(model != null)
//			System.out.println("    setting model: " + element.getModelID());
		element.setModel(model);
	}

	
	private void updateElement(AbstractNetworkElement element) {
		if(element instanceof Branch) {
			Branch branch = (Branch) element;
			branch.setFromBus(getBus(branch.getFromBusNumber()));
			branch.setToBus(getBus(branch.getToBusNumber()));
		} else if(element instanceof Generator) {
			Generator generator = (Generator) element;
			generator.setBus(getBus(generator.getBusNumber()));
		} else if(element instanceof NetworkElement) {
			NetworkElement ne = (NetworkElement) element;
			ne.setParentBus(getBus(ne.getParentBusNumber()));
		}
	}
	
	public void addNetworkChangeListener(INetworkChangeListener listener) {
		listeners.add(listener);
	}
	
	public void removeNetworkChangeListener(INetworkChangeListener listener) {
		listeners.remove(listener);
	}
	
	public void fireNetworkChanged() {
//		System.out.println("fireNetworkChanged");
		setDirty(true);
//		updateNetworkData();
//		findCombinedElements();
		NetworkChangeEvent event = new NetworkChangeEvent(this);
		for (INetworkChangeListener listener : listeners) {
			listener.networkChanged(event);
		}
	}
	
	public void fireNetworkElementAdded(AbstractNetworkElement element) {
//		System.out.println("fireNetworkElementAdded");
		setDirty(true);
		findCombinedElements();
		NetworkChangeEvent event = new NetworkChangeEvent(this);
		event.setNetworkElement(element);
		for (INetworkChangeListener listener : listeners) {
			listener.networkElementAdded(event);
		}
	}
	
	public void fireNetworkElementRemoved(AbstractNetworkElement element) {
//		System.out.println("fireNetworkElementRemoved");
		setDirty(true);
//		updateNetworkData();
		findCombinedElements();
		NetworkChangeEvent event = new NetworkChangeEvent(this);
		event.setNetworkElement(element);
		for (INetworkChangeListener listener : listeners) {
			listener.networkElementRemoved(event);
		}
	}
	
	public void fireNetworkElementChanged(NetworkChangeEvent event) {
		// don't fire the event if old value is equal to new value
		if(event.getNewValue() == null && event.getOldValue() == null
				|| event.getNewValue() != null && event.getNewValue().equals(event.getOldValue()))
			return;
		setDirty(true);
		if(event.getNetworkElement() != null)
			updateElement(event.getNetworkElement());
		// check if combined busses and branches must be updated
		if(IInternalParameters.LONGITUDE.equals(event.getParameterID())
				|| IInternalParameters.LATITUDE.equals(event.getParameterID())
				|| IInternalParameters.FROM_BUS.equals(event.getParameterID())
				|| IInternalParameters.TO_BUS.equals(event.getParameterID())
				|| IInternalParameters.GEN_BUS.equals(event.getParameterID()))
			findCombinedElements();
		for (INetworkChangeListener listener : listeners) {
			listener.networkElementChanged(event);
		}
	}
	
	@Override
	public List<NetworkParameter> getParameterList() {
		return networkData.getParameter();
	}
	
	public AbstractNetworkElement createElement(String modelID) {
		AbstractNetworkElement element = new Bus(this, getBusses().size());
		if(modelID.startsWith("bus.")) {
			element = new Bus(this, getBusses().size());
//			System.out.println("    adding bus: " + modelID);
		} else if(modelID.startsWith("branch.")) {
			element = new Branch(this, getBranches().size());
//				System.out.println("    adding branch: " + modelID);
		} else if(modelID.startsWith("generator.")) {
			element = new Generator(this, getGenerators().size());
//			System.out.println("    adding generator: " + modelID);
		} else if(modelID.startsWith("marker.")) {
			element = new MarkerElement(this, getMarkers().size());
//			System.out.println("    adding marker: " + modelID);
		} else {
			element = new NetworkElement(this, getElements().size());
//			System.out.println("    adding element: " + element.getModelID());
		}
		element.setModelID(modelID);
		if(getModelDB() != null)
			updateModel(element);
		return element;
	}
	
	public void addElement(AbstractNetworkElement element) {
		networkData.getElement().add(element.getElementData());
		addElementInternal(element);
		updateModel(element);
	}
	
	private void addElementInternal(AbstractNetworkElement element) {
		getElements().add(element);
		if(element instanceof Bus)
			getBusses().add((Bus) element);
		else if(element instanceof Branch)
			getBranches().add((Branch) element);
		else if(element instanceof Generator)
			getGenerators().add((Generator) element);
		else if(element instanceof MarkerElement)
			getMarkers().add((MarkerElement) element);
	}
	
	public void removeElement(AbstractNetworkElement element) {
//		System.out.println("remove element from network");
		networkData.getElement().remove(ListUtils.getIndexOf(networkData.getElement(), element.getElementData()));
		getElements().remove(ListUtils.getIndexOf(getElements(), element));
		if(element instanceof Bus)
			getBusses().remove(ListUtils.getIndexOf(getBusses(), (Bus) element));
		else if(element instanceof Branch)
			getBranches().remove(ListUtils.getIndexOf(getBranches(), (Branch) element));
		else if(element instanceof Generator)
			getGenerators().remove(ListUtils.getIndexOf(getGenerators(), (Generator) element));
		else if(element instanceof MarkerElement)
			getMarkers().remove(ListUtils.getIndexOf(getMarkers(), (MarkerElement) element));
	}
	
	public void removeElements(List<AbstractNetworkElement> elements) {
		for (AbstractNetworkElement element : elements) {
			removeElement(element);
		}
	}
	
	public void removeAllFlags() {
		for (AbstractNetworkElement element : getElements()) {
			element.clearFlags();
		}
	}
		
	public boolean hasFailures() {
		if(hasFailures == null) {
			// first check if calculation converged
			hasFailures = getIntParameter("SUCCESS", 1) == 0;
			if(hasFailures)
				return hasFailures;// no need to check flags of elements
			for (AbstractNetworkElement element : getElements()) {
				if(element.hasFailures()) {
					hasFailures = true;
					break;
				}
			}
		}
		return hasFailures;
	}
	
	public boolean wasCalculated() {
		if(wasCalculated == null) {
			wasCalculated = hasParameterValue("SUCCESS");
		}
		return wasCalculated;
	}
	
	public boolean isDirty() {
		return isDirty;
	}
	
	void setDirty(boolean dirty) {
		isDirty = dirty;
		getPowerFlowCase().dirtyStateChanged();
	}
	
	public void removeAllElements() {
		networkData.getElement().clear();
		getElements().clear();
		getBusses().clear();
		getBranches().clear();
		getGenerators().clear();
		getMarkers().clear();
	}
	
	public boolean isEmpty() {
		return networkData.getElement().isEmpty();
	}
	
	public List<AbstractNetworkElement> getElements() {
		return elements;
	}
	
	public List<AbstractNetworkElement> getElements(String idPrefix) {
		List<AbstractNetworkElement> list = new ArrayList<AbstractNetworkElement>();
		for (AbstractNetworkElement element : getElements()) {
			if(idPrefix == null || idPrefix.isEmpty()) {
				list.add(element);// just copy all elements to new list
				continue;
			}
			if(element.getModel() == null) {
//				System.err.println("model is null");
				continue;
			}
			if(ModelDBUtils.getFullElementID(element.getModel()).startsWith(idPrefix))
				list.add(element);
		}
		return list;
	}
	
	public List<AbstractNetworkElement> getElements(String idPrefix, String parameterID, int parameterValue) {
		return getElements(idPrefix, parameterID, Integer.toString(parameterValue));
	}
	
	public List<AbstractNetworkElement> getElements(String idPrefix, String parameterID, String parameterValue) {
		List<AbstractNetworkElement> result = new ArrayList<AbstractNetworkElement>();
		List<AbstractNetworkElement> list = getElements(idPrefix);
		for (AbstractNetworkElement element : list) {
			NetworkParameter parameter = element.getParameterValue(parameterID);
			if(parameter != null && parameter.getValue() != null) {
				String value1 = parameter.getValue();
				String value2 = parameterValue;
				NetworkParameter paramDef = element.getParameterDefinition(parameterID);
				if(paramDef != null) {
					value1 = ParameterUtils.getNormalizedParameterValue(paramDef, value1);
					value2 = ParameterUtils.getNormalizedParameterValue(paramDef, value2);
				}
				if(value1.equals(value2))
					result.add(element);
			}
		}
		return result;
	}
	
	private void findCombinedElements() {
		// combine bus nodes
		combinedBusList = ElementGroupingUtils.getCombinedBussesByCoordinates(busses);
		// combine branches
		combinedBranchList = ElementGroupingUtils.getCombinedBranchesByCoordinates(branches, combinedBusList);
		
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
//		System.out.println(combinedBusList.size() + " combined bus nodes found with " + busCount + " elements");
		// combine generators
		for (int i = 0; i < getGeneratorsCount(); i++) {
			Generator generator = generators.get(i);
			Bus bus = generator.getBus();
			CombinedBus cbus = getCombinedBus(bus);
			if(cbus != null)
				cbus.addGenerator(generator);
		}
	}
	
	public int getCombinedBusCount() {
		return combinedBusList.size();
	}
	
	public CombinedBus getCombinedBus(int index) {
		return combinedBusList.get(index);
	}
	
	public List<CombinedBus> getCombinedBusses() {
		return combinedBusList;
	}
	
	public CombinedBus getCombinedBus(AbstractNetworkElement data) {
		return ElementGroupingUtils.getCombinedBus(combinedBusList, data);
	}
	
	public int getCombinedBranchCount() {
		return combinedBranchList.size();
	}
	
	public List<CombinedBranch> getCombinedBranches() {
		return combinedBranchList;
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

	public List<Bus> getBusses() {
		return busses;
	}

	public Bus getBus(int busNumber) {
		for (int i = 0; i < busses.size(); i++) {
			Bus bus = busses.get(i);
			if(bus.getBusNumber() == busNumber)
				return bus;
		}
		return null;
	}
	
	public int getNextBusNumber(int busNumber) {
		while(getBus(busNumber) != null)
			busNumber++;
		return busNumber;
	}
	
	public List<Generator> getGenerators() {
		return generators;
	}

	public int getGeneratorsCount() {
		return generators.size();
	}
	
	public List<Branch> getBranches() {
		return branches;
	}
	
	public int getBranchesCount() {
		return branches.size();
	}
	
	public List<MarkerElement> getMarkers() {
		return markers;
	}

	public int getMarkersCount() {
		return markers.size();
	}
	
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
    
    public String getName() {
    	if(getData().getName() == null)
    		return "";
    	return getData().getName();
    }
    
    public String getDisplayName() {
    	String name = getName();
		if(name.isEmpty())
			return "Untitled Network " + getInternalID();
    	return name;
    }
    
    public void setName(String value) {
    	if(value == null || value.trim().isEmpty())
    		getData().setName(null);
    	else
    		getData().setName(value);
    }
    
    public String getDescription() {
    	if(getData().getDescription() == null)
    		return "";
    	return getData().getDescription();
    }
    
    public void setDescription(String value) {
    	if(value == null || value.trim().isEmpty())
    		getData().setDescription(null);
    	else
    		getData().setDescription(value);
    }
	
	public String toXML() throws IllegalDataException {
		try {
			return CaseSerializer.writeNetwork(getData());
		} catch (Exception e) {
			throw new IllegalDataException("Cannot write network data", e);
		}
	}
}
