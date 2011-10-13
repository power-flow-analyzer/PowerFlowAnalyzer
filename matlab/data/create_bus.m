function [ jbus ] = create_bus( jnetwork, busData )
%CREATE_bus Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

%% create bus object
jbus = net.ee.pfanalyzer.model.Bus(jnetwork, 0);

%% add parameters to bus object
if exist('busData', 'var')
    % add bus number parameter
    if length(busData) >= BUS_I 
        jbus.setParameter('BUS_I', busData(BUS_I));
    end

    % add all other parameters and flags
    update_bus(jbus, busData);
end

end

