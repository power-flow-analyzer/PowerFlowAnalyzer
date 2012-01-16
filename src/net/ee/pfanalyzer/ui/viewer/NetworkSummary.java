package net.ee.pfanalyzer.ui.viewer;

import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.Generator;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.matpower.IGeneratorDataConstants;
import net.ee.pfanalyzer.ui.CaseViewer;
import net.ee.pfanalyzer.ui.NetworkElementSelectionManager;
import net.ee.pfanalyzer.ui.NetworkViewer;
import net.ee.pfanalyzer.ui.util.IObjectAction;
import net.ee.pfanalyzer.ui.viewer.element.ModelElementPanel;
import net.miginfocom.swing.MigLayout;

public class NetworkSummary extends JPanel implements IGeneratorDataConstants {

	private final static DecimalFormat FORMAT = new DecimalFormat("#,###.#");
	
	private JLabel numberBus, numberGenerators, numberActiveGenerators;
	private JLabel totalGenCapacityP, totalGenCapacityQ, onLineCapacityP, onLineCapacityQ, actualGenerationP, actualGenerationQ;
	private JPanel flagPanel;
	private CaseViewer parent;
	
	public NetworkSummary(CaseViewer parentContainer) {
		super(new MigLayout("wrap 1", "[left]"));
		this.parent = parentContainer;
		
		numberBus = new JLabel();
		numberGenerators = new JLabel();
		numberActiveGenerators = new JLabel();
		
		totalGenCapacityP = new JLabel();
		totalGenCapacityQ = new JLabel();
		onLineCapacityP = new JLabel();
		onLineCapacityQ = new JLabel();
		actualGenerationP = new JLabel();
		actualGenerationQ = new JLabel();
		
		flagPanel = new JPanel();
		flagPanel.setLayout(new MigLayout("", "[]20[]20[]"));
		
		JPanel summaryPanel = new JPanel(new MigLayout("", "[left]20[right]40[left]20[right]"));
		summaryPanel.add(new JLabel("<html><u>How many?"), "span 2"); 
		summaryPanel.add(new JLabel("<html><u>How much?")); 
		summaryPanel.add(new JLabel("<html><u>P (MW)"), "center"); 
		summaryPanel.add(new JLabel("<html><u>Q (MVAr)"), "wrap, center");
		
		summaryPanel.add(new JLabel("Buses")); 
		summaryPanel.add(numberBus);
		summaryPanel.add(new JLabel("Total Gen Capacity")); 
		summaryPanel.add(totalGenCapacityP);  
		summaryPanel.add(totalGenCapacityQ, "wrap");
		
		summaryPanel.add(new JLabel("Generators")); 
		summaryPanel.add(numberGenerators);
		summaryPanel.add(new JLabel("On-line Capacity")); 
		summaryPanel.add(onLineCapacityP); 
		summaryPanel.add(onLineCapacityQ, "wrap");
		
		summaryPanel.add(new JLabel("Committed Gens")); 
		summaryPanel.add(numberActiveGenerators);
		summaryPanel.add(new JLabel("Generation (actual)")); 
		summaryPanel.add(actualGenerationP);  
		summaryPanel.add(actualGenerationQ, "wrap");
		
		add(summaryPanel);
		
		add(new JLabel("<html><u>Highest operating grades"), "wrap, span");
		add(flagPanel, "wrap, span");
		
	}
	
	public void updateNetwork(Network network) {
		if(network != null) {
			numberBus.setText(network.getBusses().size() + "");
			numberGenerators.setText(network.getGeneratorsCount() + "");
			numberActiveGenerators.setText(getCommittedGeneratorsCount(network) + "");
			totalGenCapacityP.setText(FORMAT.format(getTotalGenCapacityP(network, false)));
			totalGenCapacityQ.setText(FORMAT.format(getTotalGenCapacityQMin(network, false)) + " to "
					+ FORMAT.format(getTotalGenCapacityQMax(network, false)));
			onLineCapacityP.setText(FORMAT.format(getTotalGenCapacityP(network, true)));
			onLineCapacityQ.setText(FORMAT.format(getTotalGenCapacityQMin(network, true)) + " to "
					+ FORMAT.format(getTotalGenCapacityQMax(network, true)));
			actualGenerationP.setText(FORMAT.format(getActualGenerationP(network)));
			actualGenerationQ.setText(FORMAT.format(getActualGenerationQ(network)));
			flagPanel.removeAll();
			ModelElementPanel.addFlags(network.getWorstFlags(), flagPanel, new FlagSelectionAction());
		} else {
		}
	}
	
	private int getCommittedGeneratorsCount(Network network) {
		int count = 0;
		for (Generator generator : network.getGenerators()) {
			if(generator.isActive())
				count++;
		}
		return count;
	}
	
	private double getTotalGenCapacityP(Network network, boolean onlyOnline) {
		return getGeneratorSum(network, onlyOnline, PROPERTY_MAXIMUM_REAL_POWER_OUTPUT);
	}
	
	private double getTotalGenCapacityQMax(Network network, boolean onlyOnline) {
		return getGeneratorSum(network, onlyOnline, PROPERTY_MAXIMUM_REACTIVE_POWER_OUTPUT);
	}
	
	private double getTotalGenCapacityQMin(Network network, boolean onlyOnline) {
		return getGeneratorSum(network, onlyOnline, PROPERTY_MINIMUM_REACTIVE_POWER_OUTPUT);
	}
	
	private double getActualGenerationP(Network network) {
		return getGeneratorSum(network, true, PROPERTY_REAL_POWER_OUTPUT);
	}
	
	private double getActualGenerationQ(Network network) {
		return getGeneratorSum(network, true, PROPERTY_REACTIVE_POWER_OUTPUT);
	}
	
	private double getGeneratorSum(Network network, boolean onlyOnline, String parameterID) {
		double value = 0;
		for (Generator generator : network.getGenerators()) {
			if(onlyOnline && generator.isActive() == false)
				continue;
			value += generator.getDoubleParameter(parameterID, 0);
		}
		return value;
	}
	
	class FlagSelectionAction implements IObjectAction {
		@Override
		public void actionPerformed(Object object, String mode) {
			AbstractNetworkElement element = (AbstractNetworkElement) object;
			Network network = element.getNetwork();
			parent.openNetwork(network);
			NetworkViewer viewer = parent.getViewer(network);
			NetworkElementSelectionManager.selectionChanged(viewer, element);
		}
	}
}
