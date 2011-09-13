function [ jgenerator ] = create_generator( jnetwork, genData, genCostData, index )
%CREATE_generator Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

% create generator object
jgenerator = net.ee.pfanalyzer.model.Generator(jnetwork, index);

%% add parameters to generator object
jgenerator.setParameter('GEN_BUS', genData(GEN_BUS));

% add all other parameters and flags
update_generator(jgenerator, genData, genCostData);

end

