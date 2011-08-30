function [ generatorData, genCostData ] = create_generator_matrices( generator_list )
%CREATE_generator Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

%% create generator matrix

generatorData = zeros(generator_list.size(), APF); % APF is last column in matrix
genCostData = zeros(generator_list.size(), COST); % COST is last column in matrix

for i=1:generator_list.size()
    % get generator object for current matrix row
    jgenerator = generator_list.get(i - 1);
    
    % add parameters to current generator matrix row
    generatorData(i, GEN_BUS) = jgenerator.getIntParameter('GEN_BUS');
    generatorData(i, PG) = jgenerator.getDoubleParameter('PG');
    generatorData(i, QG) = jgenerator.getDoubleParameter('QG');
    generatorData(i, QMAX) = jgenerator.getDoubleParameter('QMAX');
    generatorData(i, QMIN) = jgenerator.getDoubleParameter('QMIN');
    generatorData(i, VG) = jgenerator.getDoubleParameter('VG');
    generatorData(i, MBASE) = jgenerator.getDoubleParameter('MBASE');
    generatorData(i, GEN_STATUS) = jgenerator.getIntParameter('GEN_STATUS');
    generatorData(i, PMAX) = jgenerator.getDoubleParameter('PMAX');
    generatorData(i, PMIN) = jgenerator.getDoubleParameter('PMIN');
    generatorData(i, PC1) = jgenerator.getDoubleParameter('PC1');
    generatorData(i, PC2) = jgenerator.getDoubleParameter('PC2');
    generatorData(i, QC1MIN) = jgenerator.getDoubleParameter('QC1MIN');
    generatorData(i, QC1MAX) = jgenerator.getDoubleParameter('QC1MAX');
    generatorData(i, QC2MIN) = jgenerator.getDoubleParameter('QC2MIN');
    generatorData(i, QC2MAX) = jgenerator.getDoubleParameter('QC2MAX');
    generatorData(i, RAMP_AGC) = jgenerator.getDoubleParameter('RAMP_AGC');
    generatorData(i, RAMP_10) = jgenerator.getDoubleParameter('RAMP_10');
    generatorData(i, RAMP_30) = jgenerator.getDoubleParameter('RAMP_30');
    generatorData(i, RAMP_Q) = jgenerator.getDoubleParameter('RAMP_Q');
    generatorData(i, APF) = jgenerator.getDoubleParameter('APF');

    % add parameters to current generator cost matrix row
    genCostData(i, MODEL) = jgenerator.getIntParameter('MODEL');
    genCostData(i, STARTUP) = jgenerator.getDoubleParameter('STARTUP');
    genCostData(i, SHUTDOWN) = jgenerator.getDoubleParameter('SHUTDOWN');
    genCostData(i, NCOST) = jgenerator.getDoubleParameter('NCOST');
    genCostData(i, COST) = jgenerator.getDoubleParameter('COST');
    genCostData(i, 6) = jgenerator.getDoubleParameter('GENCOST6');
    genCostData(i, 7) = jgenerator.getDoubleParameter('GENCOST7');
end

end

