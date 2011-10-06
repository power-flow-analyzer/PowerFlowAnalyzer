function [ ] = update_generator( jgenerator, genData, genCostData )
%CREATE_generator Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

%% add parameters to generator object
if length(genData) >= PG 
    jgenerator.setParameter('PG', genData(PG));
end
if length(genData) >= QG 
    jgenerator.setParameter('QG', genData(QG));
end
if length(genData) >= QMAX 
    jgenerator.setParameter('QMAX', genData(QMAX));
end
if length(genData) >= QMIN 
    jgenerator.setParameter('QMIN', genData(QMIN));
end
if length(genData) >= VG 
    jgenerator.setParameter('VG', genData(VG));
end
if length(genData) >= MBASE 
    jgenerator.setParameter('MBASE', genData(MBASE));
end
if length(genData) >= GEN_STATUS 
    jgenerator.setParameter('GEN_STATUS', genData(GEN_STATUS));
end
if length(genData) >= PMAX 
    jgenerator.setParameter('PMAX', genData(PMAX));
end
if length(genData) >= PMIN 
    jgenerator.setParameter('PMIN', genData(PMIN));
end
if length(genData) >= PC1 
    jgenerator.setParameter('PC1', genData(PC1));
end
if length(genData) >= PC2 
    jgenerator.setParameter('PC2', genData(PC2));
end
if length(genData) >= QC1MIN 
    jgenerator.setParameter('QC1MIN', genData(QC1MIN));
end
if length(genData) >= QC1MAX 
    jgenerator.setParameter('QC1MAX', genData(QC1MAX));
end
if length(genData) >= QC2MIN 
    jgenerator.setParameter('QC2MIN', genData(QC2MIN));
end
if length(genData) >= QC2MAX 
    jgenerator.setParameter('QC2MAX', genData(QC2MAX));
end
if length(genData) >= RAMP_AGC 
    jgenerator.setParameter('RAMP_AGC', genData(RAMP_AGC));
end
if length(genData) >= RAMP_10 
    jgenerator.setParameter('RAMP_10', genData(RAMP_10));
end
if length(genData) >= RAMP_30 
    jgenerator.setParameter('RAMP_30', genData(RAMP_30));
end
if length(genData) >= RAMP_Q 
    jgenerator.setParameter('RAMP_Q', genData(RAMP_Q));
end
if length(genData) >= APF 
    jgenerator.setParameter('APF', genData(APF));
end
% the following parameters are only included in OPF output
if length(genData) >= MU_PMAX 
    jgenerator.setParameter('MU_PMAX', genData(MU_PMAX));
end
if length(genData) >= MU_PMIN 
    jgenerator.setParameter('MU_PMIN', genData(MU_PMIN));
end
if length(genData) >= MU_QMAX 
    jgenerator.setParameter('MU_QMAX', genData(MU_QMAX));
end
if length(genData) >= MU_QMIN 
    jgenerator.setParameter('MU_QMIN', genData(MU_QMIN));
end

% add generator cost parameters to object
if exist('genCostData', 'var')
    if length(genCostData) >= MODEL 
        jgenerator.setParameter('MODEL', genCostData(MODEL));
    end
    if length(genCostData) >= STARTUP 
        jgenerator.setParameter('STARTUP', genCostData(STARTUP));
    end
    if length(genCostData) >= SHUTDOWN 
        jgenerator.setParameter('SHUTDOWN', genCostData(SHUTDOWN));
    end
    if length(genCostData) >= NCOST 
        jgenerator.setParameter('NCOST', genCostData(NCOST));
    end
    if length(genCostData) >= COST 
        jgenerator.setParameter('COST', genCostData(COST));
    end
    if length(genCostData) >= 6 
        jgenerator.setParameter('GENCOST6', genCostData(6));
    end
    if length(genCostData) >= 7 
        jgenerator.setParameter('GENCOST7', genCostData(7));
    end
end

%% add flags

% remove old flags
jgenerator.clearFlags();

% add flags
if genData(GEN_STATUS) == 1 % check if generator is running
    add_flags_generator(jgenerator, genData);
end

end

