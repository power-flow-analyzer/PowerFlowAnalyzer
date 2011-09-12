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
    generatorData(i, GEN_BUS) = parameter_int(jgenerator, 'GEN_BUS');
    generatorData(i, PG) = parameter_double(jgenerator, 'PG');
    generatorData(i, QG) = parameter_double(jgenerator, 'QG');
    generatorData(i, QMAX) = parameter_double(jgenerator, 'QMAX');
    generatorData(i, QMIN) = parameter_double(jgenerator, 'QMIN');
    generatorData(i, VG) = parameter_double(jgenerator, 'VG');
    generatorData(i, MBASE) = parameter_double(jgenerator, 'MBASE');
    generatorData(i, GEN_STATUS) = parameter_int(jgenerator, 'GEN_STATUS');
    generatorData(i, PMAX) = parameter_double(jgenerator, 'PMAX');
    generatorData(i, PMIN) = parameter_double(jgenerator, 'PMIN');
    generatorData(i, PC1) = parameter_double(jgenerator, 'PC1');
    generatorData(i, PC2) = parameter_double(jgenerator, 'PC2');
    generatorData(i, QC1MIN) = parameter_double(jgenerator, 'QC1MIN');
    generatorData(i, QC1MAX) = parameter_double(jgenerator, 'QC1MAX');
    generatorData(i, QC2MIN) = parameter_double(jgenerator, 'QC2MIN');
    generatorData(i, QC2MAX) = parameter_double(jgenerator, 'QC2MAX');
    generatorData(i, RAMP_AGC) = parameter_double(jgenerator, 'RAMP_AGC');
    generatorData(i, RAMP_10) = parameter_double(jgenerator, 'RAMP_10');
    generatorData(i, RAMP_30) = parameter_double(jgenerator, 'RAMP_30');
    generatorData(i, RAMP_Q) = parameter_double(jgenerator, 'RAMP_Q');
    generatorData(i, APF) = parameter_double(jgenerator, 'APF');

    % add parameters to current generator cost matrix row
    genCostData(i, MODEL) = parameter_int(jgenerator, 'MODEL');
    genCostData(i, STARTUP) = parameter_double(jgenerator, 'STARTUP');
    genCostData(i, SHUTDOWN) = parameter_double(jgenerator, 'SHUTDOWN');
    genCostData(i, NCOST) = parameter_int(jgenerator, 'NCOST');
    genCostData(i, COST) = parameter_double(jgenerator, 'COST');
    genCostData(i, 6) = parameter_double(jgenerator, 'GENCOST6');
    genCostData(i, 7) = parameter_double(jgenerator, 'GENCOST7');
end

end

