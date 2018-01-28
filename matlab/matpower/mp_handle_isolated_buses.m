function [ mpc ] = mp_handle_isolated_buses( mpc )
%MP_HANDLE_ISOLATED_BUSES Summary of this function goes here
%   Detailed explanation goes here

% find islands and isolated buses
connected_buses = find_islands(mpc);
if length(connected_buses) > 1
    error('Error: %i islands were detected in network', length(connected_buses));
end
connected_buses = connected_buses{1};% make it a double vector
all_buses = 1:size(mpc.bus(:, 1), 1);
isolated_buses = setxor(all_buses, connected_buses);
for bus_i = isolated_buses
    mpc.bus(bus_i, 2) = 4; % set bus type to isolated
end

end

