function [ generatorData, genCostData ] = create_matpower_generator_matrices( generator_list )
%CREATE_generator Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

%% create generator matrix


for i=1:generator_list.size()
    % get generator object for current matrix row
    jgenerator = generator_list.get(i - 1);
    
%% check number of columns and create (empty) matrices
    if i == 1
        genDataSize = 0;
        genDataSize = increase_if_exists(jgenerator, 'GEN_BUS', genDataSize);
        genDataSize = increase_if_exists(jgenerator, 'PG', genDataSize);
        genDataSize = increase_if_exists(jgenerator, 'QG', genDataSize);
        genDataSize = increase_if_exists(jgenerator, 'QMAX', genDataSize);
        genDataSize = increase_if_exists(jgenerator, 'QMIN', genDataSize);
        genDataSize = increase_if_exists(jgenerator, 'VG', genDataSize);
        genDataSize = increase_if_exists(jgenerator, 'MBASE', genDataSize);
        genDataSize = increase_if_exists(jgenerator, 'GEN_STATUS', genDataSize);
        genDataSize = increase_if_exists(jgenerator, 'PMAX', genDataSize);
        genDataSize = increase_if_exists(jgenerator, 'PMIN', genDataSize);
        genDataSize = increase_if_exists(jgenerator, 'PC1', genDataSize);
        genDataSize = increase_if_exists(jgenerator, 'PC2', genDataSize);
        genDataSize = increase_if_exists(jgenerator, 'QC1MIN', genDataSize);
        genDataSize = increase_if_exists(jgenerator, 'QC1MAX', genDataSize);
        genDataSize = increase_if_exists(jgenerator, 'QC2MIN', genDataSize);
        genDataSize = increase_if_exists(jgenerator, 'QC2MAX', genDataSize);
        genDataSize = increase_if_exists(jgenerator, 'RAMP_AGC', genDataSize);
        genDataSize = increase_if_exists(jgenerator, 'RAMP_10', genDataSize);
        genDataSize = increase_if_exists(jgenerator, 'RAMP_30', genDataSize);
        genDataSize = increase_if_exists(jgenerator, 'RAMP_Q', genDataSize);
        genDataSize = increase_if_exists(jgenerator, 'APF', genDataSize);

        generatorData = zeros(generator_list.size(), genDataSize);

        genCostSize = 0;
        genCostSize = increase_if_exists(jgenerator, 'MODEL', genCostSize);
        genCostSize = increase_if_exists(jgenerator, 'STARTUP', genCostSize);
        genCostSize = increase_if_exists(jgenerator, 'SHUTDOWN', genCostSize);
        genCostSize = increase_if_exists(jgenerator, 'NCOST', genCostSize);
        genCostSize = increase_if_exists(jgenerator, 'COST', genCostSize);
        genCostSize = increase_if_exists(jgenerator, 'GENCOST6', genCostSize);
        genCostSize = increase_if_exists(jgenerator, 'GENCOST7', genCostSize);
        
        genCostData = zeros(generator_list.size(), genCostSize);
    end
    
%% add parameters to current generator matrix row
    if length(generatorData) >= GEN_BUS 
        generatorData(i, GEN_BUS) = parameter_int(jgenerator, 'GEN_BUS');
    end
    if length(generatorData) >= PG 
        generatorData(i, PG) = parameter_double(jgenerator, 'PG');
    end
    if length(generatorData) >= QG 
        generatorData(i, QG) = parameter_double(jgenerator, 'QG');
    end
    if length(generatorData) >= QMAX 
        generatorData(i, QMAX) = parameter_double(jgenerator, 'QMAX');
    end
    if length(generatorData) >= QMIN 
        generatorData(i, QMIN) = parameter_double(jgenerator, 'QMIN');
    end
    if length(generatorData) >= VG 
        generatorData(i, VG) = parameter_double(jgenerator, 'VG');
    end
    if length(generatorData) >= MBASE 
        generatorData(i, MBASE) = parameter_double(jgenerator, 'MBASE');
    end
    if length(generatorData) >= GEN_STATUS 
        generatorData(i, GEN_STATUS) = parameter_int(jgenerator, 'GEN_STATUS');
    end
    if length(generatorData) >= PMAX 
        generatorData(i, PMAX) = parameter_double(jgenerator, 'PMAX');
    end
    if length(generatorData) >= PMIN 
        generatorData(i, PMIN) = parameter_double(jgenerator, 'PMIN');
    end
    if length(generatorData) >= PC1 
        generatorData(i, PC1) = parameter_double(jgenerator, 'PC1');
    end
    if length(generatorData) >= PC2 
        generatorData(i, PC2) = parameter_double(jgenerator, 'PC2');
    end
    if length(generatorData) >= QC1MIN 
        generatorData(i, QC1MIN) = parameter_double(jgenerator, 'QC1MIN');
    end
    if length(generatorData) >= QC1MAX 
        generatorData(i, QC1MAX) = parameter_double(jgenerator, 'QC1MAX');
    end
    if length(generatorData) >= QC2MIN 
        generatorData(i, QC2MIN) = parameter_double(jgenerator, 'QC2MIN');
    end
    if length(generatorData) >= QC2MAX 
        generatorData(i, QC2MAX) = parameter_double(jgenerator, 'QC2MAX');
    end
    if length(generatorData) >= RAMP_AGC 
        generatorData(i, RAMP_AGC) = parameter_double(jgenerator, 'RAMP_AGC');
    end
    if length(generatorData) >= RAMP_10 
        generatorData(i, RAMP_10) = parameter_double(jgenerator, 'RAMP_10');
    end
    if length(generatorData) >= RAMP_30 
        generatorData(i, RAMP_30) = parameter_double(jgenerator, 'RAMP_30');
    end
    if length(generatorData) >= RAMP_Q 
        generatorData(i, RAMP_Q) = parameter_double(jgenerator, 'RAMP_Q');
    end
    if length(generatorData) >= APF 
        generatorData(i, APF) = parameter_double(jgenerator, 'APF');
    end

%% add parameters to current generator cost matrix row
    if length(genCostData) >= MODEL 
        genCostData(i, MODEL) = parameter_int(jgenerator, 'MODEL');
    end
    if length(genCostData) >= STARTUP 
        genCostData(i, STARTUP) = parameter_double(jgenerator, 'STARTUP');
    end
    if length(genCostData) >= SHUTDOWN 
        genCostData(i, SHUTDOWN) = parameter_double(jgenerator, 'SHUTDOWN');
    end
    if length(genCostData) >= NCOST 
        genCostData(i, NCOST) = parameter_int(jgenerator, 'NCOST');
    end
    if length(genCostData) >= COST 
        genCostData(i, COST) = parameter_double(jgenerator, 'COST');
    end
    if length(genCostData) >= 6 
        genCostData(i, 6) = parameter_double(jgenerator, 'GENCOST6');
    end
    if length(genCostData) >= 7 
        genCostData(i, 7) = parameter_double(jgenerator, 'GENCOST7');
    end
end

end

