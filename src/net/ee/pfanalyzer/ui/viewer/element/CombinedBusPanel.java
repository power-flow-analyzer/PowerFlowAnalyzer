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
package net.ee.pfanalyzer.ui.viewer.element;

import net.ee.pfanalyzer.model.CombinedBus;
import net.ee.pfanalyzer.model.Network;
import net.ee.pfanalyzer.model.util.ElementGroupingUtils;

public class CombinedBusPanel extends AbstractElementPanel {

//	private Group busGroup, generatorGroup;//, transformerGroup;
//	private JLabel sumRealPowerDemandLabel, sumReactivePowerDemandLabel, sumRealPowerGenerationLabel, sumReactivePowerGenerationLabel;
//	private DecimalFormat format = new DecimalFormat("#.###");
////	private Font labelFont;
	
	public CombinedBusPanel(ElementViewer viewer, Network data) {
		super(viewer);
////		labelFont = getFont().deriveFont(Font.BOLD).deriveFont(11f);
////		summaryGroup = addElementGroup("Summary");
//		busGroup = addElementGroup("Bus Overview");
//		generatorGroup = addElementGroup("Generator Overview");
////		transformerGroup = addElementGroup("Transformer Overview");
//		
//		sumRealPowerDemandLabel = new JLabel();
//		sumReactivePowerDemandLabel = new JLabel();
//		sumRealPowerGenerationLabel = new JLabel();
//		sumReactivePowerGenerationLabel = new JLabel();
	}

	public void setCombinedBus(CombinedBus cbus) {
		removeAllElements();
		// set title
		setTitle(cbus.getLabel());
		addBusElements(cbus);
		addElements(ElementGroupingUtils.convertGeneratorList(cbus.getGenerators()), "Generators",
				GENERATOR_ATTRIBUTES);
		finishLayout();
	}

//	public void setCombinedBus(CombinedBus data) {
//		// remove old elements
//		busGroup.removeAll();
//		generatorGroup.removeAll();
////		transformerGroup.removeAll();
//		// set title
//		setTitle(data.getLabel());
//		
//		// add bus nodes
////		JLabel label = new JLabel("\u2211 Real Power Demand: ");
//////		label.setFont(labelFont);
////		busGroup.add(label);
////		busGroup.add(sumRealPowerDemandLabel);
////		label = new JLabel("\u2211 Reactive Power Demand: ");
//////		label.setFont(labelFont);
////		busGroup.add(label);
////		busGroup.add(sumReactivePowerDemandLabel);
//		busGroup.add(new JLabel("Busses (" + data.getNetworkElementCount() + "):"));
//		busGroup.add(new JPanel());// add vertical space
//		double realPowerSum = 0;
//		double reactivePowerSum = 0;
//		for (int i = 0; i < data.getNetworkElementCount(); i++) {
//			Bus bus = data.getNetworkElement(i);
//			busGroup.addElementLink(bus, AbstractNetworkElement.DISPLAY_DEFAULT);
////			realPowerSum += bus.getData()[IBusDataConstants.REAL_POWER_DEMAND];
////			reactivePowerSum += bus.getData()[IBusDataConstants.REACTIVE_POWER_DEMAND];
//		}
//		sumRealPowerDemandLabel.setText(format.format(realPowerSum) + " MW");
//		sumReactivePowerDemandLabel.setText(format.format(reactivePowerSum) + " MVAr");
//		
//		// add generators
////		label = new JLabel("\u2211 Real Power Output: ");
//////		label.setFont(labelFont);
////		generatorGroup.add(label);
////		generatorGroup.add(sumRealPowerGenerationLabel);
////		label = new JLabel("\u2211 Reactive Power Output: ");
//////		label.setFont(labelFont);
////		generatorGroup.add(label);
////		generatorGroup.add(sumReactivePowerGenerationLabel);
//		generatorGroup.add(new JLabel("Generators (" + data.getGenerators().size() + "):"));
//		generatorGroup.add(new JPanel());// add vertical space
//		realPowerSum = 0;
//		reactivePowerSum = 0;
//		for (Generator gen : data.getGenerators()) {
//			generatorGroup.addElementLink(gen, AbstractNetworkElement.DISPLAY_DEFAULT);// TODO
////			realPowerSum += gen.getData()[IGeneratorDataConstants.REAL_POWER_OUTPUT];
////			reactivePowerSum += gen.getData()[IGeneratorDataConstants.REACTIVE_POWER_OUTPUT];
//		}
//		sumRealPowerGenerationLabel.setText(format.format(realPowerSum) + " MW");
//		sumReactivePowerGenerationLabel.setText(format.format(reactivePowerSum) + " MVAr");
//		
////		// add transformers
////		for (Transformer t : data.getTransformers()) {
////			transformerGroup.addElementLink(t, AbstractNetworkElement.DISPLAY_DEFAULT);
////		}
//		
//		finishLayout();
//	}
}
