package net.ee.pfanalyzer.model;

import java.util.ArrayList;
import java.util.List;

public class CombinedBus extends CombinedNetworkElement<Bus> {

	public final static double COORDINATE_EPSILON = 0.0001;
	
	private List<Generator> generators = new ArrayList<Generator>();
//	private List<Transformer> transformers = new ArrayList<Transformer>();
	private double longitude, latitude;
	private Boolean hasFailures = null;
	private Boolean hasWarnings = null;
	
	public CombinedBus(Bus bus) {
		this.longitude = bus.getLongitude();
		this.latitude  = bus.getLatitude();
		addBus(bus);
	}
	
	public CombinedBus(Bus bus, double longitude, double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
		addBus(bus);
	}
	
	public String getTypeLabel() {
		return "Busses";
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
	
//	public void addTransformer(Transformer t) {
//		transformers.add(t);
//	}
//	
//	public List<Transformer> getTransformers() {
//		return transformers;
//	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}
	
	public boolean hasLongitude(double longitude) {
		return Math.abs(getLongitude() - longitude) <= COORDINATE_EPSILON;
	}
	
	public boolean hasLatitude(double latitude) {
		return Math.abs(getLatitude() - latitude) <= COORDINATE_EPSILON;
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
			return getFirstBus().getDisplayName(AbstractNetworkElement.DISPLAY_NAME);//"Bus " + (getIndex() + 1);
		else
			return "Combined bus " + (getIndex() + 1);
	}
	
	@Override
	public boolean hasFailures() {
		if(hasFailures == null) {
			hasFailures = false;
			for (Bus bus : getBusNodes()) {
				if(bus.hasFailures()) {
					hasFailures = true;
					break;
				}
			}
			for (Generator gen : getGenerators()) {
				if(gen.hasFailures()) {
					hasFailures = true;
					break;
				}
			}
//			for (Transformer t : getTransformers()) {
//				if(t.hasFailures()) {
//					hasFailures = true;
//					break;
//				}
//			}
		}
		return hasFailures;
	}
	
	@Override
	public boolean hasWarnings() {
		if(hasWarnings == null) {
			hasWarnings = false;
			for (Bus bus : getBusNodes()) {
				if(bus.hasWarnings()) {
					hasWarnings = true;
					break;
				}
			}
			for (Generator gen : getGenerators()) {
				if(gen.hasWarnings()) {
					hasWarnings = true;
					break;
				}
			}
//			for (Transformer t : getTransformers()) {
//				if(t.hasWarnings()) {
//					hasWarnings = true;
//					break;
//				}
//			}
		}
		return hasWarnings;
	}
}
