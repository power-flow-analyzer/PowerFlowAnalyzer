function [ jgenerator ] = create_generator( jnetwork, genData, genCostData )
%CREATE_generator Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

% create generator object
jgenerator = net.ee.pfanalyzer.model.Generator(jnetwork, 0);

%% add parameters to generator object
if length(genData) >= GEN_BUS 
    jgenerator.setParameter('GEN_BUS', genData(GEN_BUS));
end

% add all other parameters and flags
if exist('genCostData', 'var')
    update_generator(jgenerator, genData, genCostData);
else
    update_generator(jgenerator, genData);
end

end

