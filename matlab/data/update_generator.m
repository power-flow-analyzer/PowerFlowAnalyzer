function [ ] = update_generator( jgenerator, genData, genCostData )
%CREATE_generator Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

%% add parameters to generator object
jgenerator.setParameter('PG', genData(PG));
jgenerator.setParameter('QG', genData(QG));
jgenerator.setParameter('QMAX', genData(QMAX));
jgenerator.setParameter('QMIN', genData(QMIN));
jgenerator.setParameter('VG', genData(VG));
jgenerator.setParameter('MBASE', genData(MBASE));
jgenerator.setParameter('GEN_STATUS', genData(GEN_STATUS));
jgenerator.setParameter('PMAX', genData(PMAX));
jgenerator.setParameter('PMIN', genData(PMIN));
jgenerator.setParameter('PC1', genData(PC1));
jgenerator.setParameter('PC2', genData(PC2));
jgenerator.setParameter('QC1MIN', genData(QC1MIN));
jgenerator.setParameter('QC1MAX', genData(QC1MAX));
jgenerator.setParameter('QC2MIN', genData(QC2MIN));
jgenerator.setParameter('QC2MAX', genData(QC2MAX));
jgenerator.setParameter('RAMP_AGC', genData(RAMP_AGC));
jgenerator.setParameter('RAMP_10', genData(RAMP_10));
jgenerator.setParameter('RAMP_30', genData(RAMP_30));
jgenerator.setParameter('RAMP_Q', genData(RAMP_Q));
jgenerator.setParameter('APF', genData(APF));
% the following parameters are only included in OPF output
if length(genData) > MU_PMAX 
    jgenerator.setParameter('MU_PMAX', genData(MU_PMAX));
    jgenerator.setParameter('MU_PMIN', genData(MU_PMIN));
    jgenerator.setParameter('MU_QMAX', genData(MU_QMAX));
    jgenerator.setParameter('MU_QMIN', genData(MU_QMIN));
end

% add generator cost parameters to object
jgenerator.setParameter('MODEL', genCostData(MODEL));
jgenerator.setParameter('STARTUP', genCostData(STARTUP));
jgenerator.setParameter('SHUTDOWN', genCostData(SHUTDOWN));
jgenerator.setParameter('NCOST', genCostData(NCOST));
jgenerator.setParameter('COST', genCostData(COST));
jgenerator.setParameter('GENCOST6', genCostData(6));
jgenerator.setParameter('GENCOST7', genCostData(7));

%% add flags

% remove old flags
jgenerator.clearFlags();

% add flags
if genData(GEN_STATUS) == 1 % check if generator is running
    add_flags_generator(jgenerator, genData);
end

end

