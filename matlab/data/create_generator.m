function [ generator ] = create_generator( network, genData, genCostData, index )
%CREATE_generator Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

% create generator object
generator = net.ee.pfanalyzer.model.Generator(network, index);

% add parameters to generator object
generator.setParameter('GEN_BUS', genData(GEN_BUS));
generator.setParameter('PG', genData(PG));
generator.setParameter('QG', genData(QG));
generator.setParameter('QMAX', genData(QMAX));
generator.setParameter('QMIN', genData(QMIN));
generator.setParameter('VG', genData(VG));
generator.setParameter('MBASE', genData(MBASE));
generator.setParameter('GEN_STATUS', genData(GEN_STATUS));
generator.setParameter('PMAX', genData(PMAX));
generator.setParameter('PMIN', genData(PMIN));
generator.setParameter('PC1', genData(PC1));
generator.setParameter('PC2', genData(PC2));
generator.setParameter('QC1MIN', genData(QC1MIN));
generator.setParameter('QC1MAX', genData(QC1MAX));
generator.setParameter('QC2MIN', genData(QC2MIN));
generator.setParameter('QC2MAX', genData(QC2MAX));
generator.setParameter('RAMP_AGC', genData(RAMP_AGC));
generator.setParameter('RAMP_10', genData(RAMP_10));
generator.setParameter('RAMP_30', genData(RAMP_30));
generator.setParameter('RAMP_Q', genData(RAMP_Q));
generator.setParameter('APF', genData(APF));
% the following parameters are only included in OPF output
if length(genData) > MU_PMAX 
    generator.setParameter('MU_PMAX', genData(MU_PMAX));
    generator.setParameter('MU_PMIN', genData(MU_PMIN));
    generator.setParameter('MU_QMAX', genData(MU_QMAX));
    generator.setParameter('MU_QMIN', genData(MU_QMIN));
end

% add generator cost parameters to object
generator.setParameter('MODEL', genCostData(MODEL));
generator.setParameter('STARTUP', genCostData(STARTUP));
generator.setParameter('SHUTDOWN', genCostData(SHUTDOWN));
generator.setParameter('NCOST', genCostData(NCOST));
generator.setParameter('COST', genCostData(COST));
generator.setParameter('GENCOST6', genCostData(6));
generator.setParameter('GENCOST7', genCostData(7));

end

