function [ busData ] = create_bus_matrix( bus_list )
%CREATE_bus Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

%% create bus matrix

for i=1:bus_list.size()
    % get bus object for current matrix row
    jbus = bus_list.get(i - 1);
    
%% check number of columns and create (empty) matrices
    if i == 1
        busDataSize = 0;
        busDataSize = increase_if_exists(jbus, 'BUS_I', busDataSize);
        busDataSize = increase_if_exists(jbus, 'BUS_TYPE', busDataSize);
        busDataSize = increase_if_exists(jbus, 'PD', busDataSize);
        busDataSize = increase_if_exists(jbus, 'QD', busDataSize);
        busDataSize = increase_if_exists(jbus, 'GS', busDataSize);
        busDataSize = increase_if_exists(jbus, 'BS', busDataSize);
        busDataSize = increase_if_exists(jbus, 'BUS_AREA', busDataSize);
        busDataSize = increase_if_exists(jbus, 'VM', busDataSize);
        busDataSize = increase_if_exists(jbus, 'VA', busDataSize);
        busDataSize = increase_if_exists(jbus, 'BASE_KV', busDataSize);
        busDataSize = increase_if_exists(jbus, 'ZONE', busDataSize);
        busDataSize = increase_if_exists(jbus, 'VMAX', busDataSize);
        busDataSize = increase_if_exists(jbus, 'VMIN', busDataSize);
        
        busData = zeros(bus_list.size(), busDataSize);
    end

%% add parameters to current matrix row
    if length(busData) >= BUS_I 
        busData(i, BUS_I) = parameter_int(jbus, 'BUS_I');
    end
    if length(busData) >= BUS_TYPE 
        busData(i, BUS_TYPE) = parameter_int(jbus, 'BUS_TYPE');
    end
    if length(busData) >= PD 
        busData(i, PD) = parameter_double(jbus, 'PD');
    end
    if length(busData) >= QD 
    	busData(i, QD) = parameter_double(jbus, 'QD');
    end
    if length(busData) >= GS 
        busData(i, GS) = parameter_double(jbus, 'GS');
    end
    if length(busData) >= BS 
        busData(i, BS) = parameter_double(jbus, 'BS');
    end
    if length(busData) >= BUS_AREA 
        busData(i, BUS_AREA) = parameter_int(jbus, 'BUS_AREA');
    end
    if length(busData) >= VM 
        busData(i, VM) = parameter_double(jbus, 'VM');
    end
    if length(busData) >= VA 
        busData(i, VA) = parameter_double(jbus, 'VA');
    end
    if length(busData) >= BASE_KV 
        busData(i, BASE_KV) = parameter_double(jbus, 'BASE_KV');
    end
    if length(busData) >= ZONE 
        busData(i, ZONE) = parameter_int(jbus, 'ZONE');
    end
    if length(busData) >= VMAX 
        busData(i, VMAX) = parameter_double(jbus, 'VMAX');
    end
    if length(busData) >= VMIN 
        busData(i, VMIN) = parameter_double(jbus, 'VMIN');
    end
end

end

