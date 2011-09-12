function [ jbus ] = create_bus( jnetwork, busData, index )
%CREATE_bus Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

%% create bus object
jbus = net.ee.pfanalyzer.model.Bus(jnetwork, index);

%% add parameters to bus object
jbus.setParameter('BUS_I', busData(BUS_I));

% add all other parameters and flags
update_bus(jbus, busData);

end

