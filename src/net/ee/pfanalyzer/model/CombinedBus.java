package net.ee.pfanalyzer.model;

import java.util.ArrayList;
import java.util.List;

public class CombinedBus extends CombinedNetworkElement<Bus> {

	public final static double COORDINATE_EPSILON = 0.0001;
	
	private List<Generator> generators = new ArrayList<Generator>();
	private List<Transformer> transformers = new ArrayList<Transformer>();
	private double longitude, lattitude;
	private Boolean isCorrect = null;
	
	public CombinedBus(Bus bus, double longitude, double lattitude) {
		this.longitude = longitude;
		this.lattitude = lattitude;
		addBus(bus);
	}
	
	public void addBus(Bus bus) {
		addNetworkElement(bus);
	}
	
	public Bus getFirstBus() {
		return getFirstNetworkElement();
	}
	
//	public int getFirstBusIndex() {
//		return getFirstNetworkElementIndex();
//	}
	
	public List<Bus> getBusNodes() {
		return getNetworkElements();
	}
	
	public void addGenerator(Generator gen) {
		generators.add(gen);
	}
	
	public List<Generator> getGenerators() {
		return generators;
	}
	
	public void addTransformer(Transformer t) {
		transformers.add(t);
	}
	
	public List<Transformer> getTransformers() {
		return transformers;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLattitude() {
		return lattitude;
	}
	
	public boolean hasLongitude(double longitude) {
		return Math.abs(getLongitude() - longitude) <= COORDINATE_EPSILON;
	}
	
	public boolean hasLattitude(double lattitude) {
		return Math.abs(getLattitude() - lattitude) <= COORDINATE_EPSILON;
	}
	
	public boolean contains(AbstractNetworkElement data) {
		return super.contains(data) || generators.contains(data);
	}

	@Override
	public String getLabel() {
		String locationName = getFirstBus().getName();
		if(locationName != null)
			return locationName;// + " (Area " + (getIndex() + 1) + ")";
		else if(getNetworkElementCount() == 1)
			return "Bus " + (getIndex() + 1);
		else
			return "Area " + (getIndex() + 1);
	}
	
	@Override
	public boolean isCorrect() {
		if(isCorrect == null) {
			isCorrect = true;
			for (Bus bus : getBusNodes()) {
				if(bus.isCorrect() == false) {
					isCorrect = false;
					break;
				}
			}
			for (Generator gen : getGenerators()) {
				if(gen.isCorrect() == false) {
					isCorrect = false;
					break;
				}
			}
			for (Transformer t : getTransformers()) {
				if(t.isCorrect() == false) {
					isCorrect = false;
					break;
				}
			}
		}
		return isCorrect;
	}
}
