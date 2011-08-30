function [ busData ] = create_bus_matrix( bus_list )
%CREATE_bus Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

%% create bus matrix

busData = zeros(bus_list.size(), VMIN); % VMIN is last column in matrix
for i=1:bus_list.size()
    % get bus object for current matrix row
    jbus = bus_list.get(i - 1);
    
    % add parameters to current matrix row
    busData(i, BUS_I) = jbus.getIntParameter('BUS_I');
    busData(i, BUS_TYPE) = jbus.getIntParameter('BUS_TYPE');
    busData(i, PD) = jbus.getDoubleParameter('PD');
    busData(i, QD) = jbus.getDoubleParameter('QD');
    busData(i, GS) = jbus.getDoubleParameter('GS');
    busData(i, BS) = jbus.getDoubleParameter('BS');
    busData(i, BUS_AREA) = jbus.getIntParameter('BUS_AREA');
    busData(i, VM) = jbus.getDoubleParameter('VM');
    busData(i, VA) = jbus.getDoubleParameter('VA');
    busData(i, BASE_KV) = jbus.getDoubleParameter('BASE_KV');
    busData(i, ZONE) = jbus.getIntParameter('ZONE');
    busData(i, VMAX) = jbus.getDoubleParameter('VMAX');
    busData(i, VMIN) = jbus.getDoubleParameter('VMIN');
end

end

