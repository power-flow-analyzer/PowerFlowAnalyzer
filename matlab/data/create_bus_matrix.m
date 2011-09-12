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
    busData(i, BUS_I) = parameter_int(jbus, 'BUS_I');
    busData(i, BUS_TYPE) = parameter_int(jbus, 'BUS_TYPE');
    busData(i, PD) = parameter_double(jbus, 'PD');
    busData(i, QD) = parameter_double(jbus, 'QD');
    busData(i, GS) = parameter_double(jbus, 'GS');
    busData(i, BS) = parameter_double(jbus, 'BS');
    busData(i, BUS_AREA) = parameter_int(jbus, 'BUS_AREA');
    busData(i, VM) = parameter_double(jbus, 'VM');
    busData(i, VA) = parameter_double(jbus, 'VA');
    busData(i, BASE_KV) = parameter_double(jbus, 'BASE_KV');
    busData(i, ZONE) = parameter_int(jbus, 'ZONE');
    busData(i, VMAX) = parameter_double(jbus, 'VMAX');
    busData(i, VMIN) = parameter_double(jbus, 'VMIN');
end

end

