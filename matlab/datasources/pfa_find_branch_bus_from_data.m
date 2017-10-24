function [ pfa_from_bus, pfa_to_bus ] = pfa_find_branch_bus_from_data( pfa_network, data )
%PFA_FIND_FROM_BUS_FROM_DATA Summary of this function goes here
%   Detailed explanation goes here

if isfield(data, 'F_BUS')
    pfa_from_bus = pfa_network.getBus(data.F_BUS);
elseif isfield(data, 'F_BUS_NAME')
    pfa_from_bus = pfa_find_element(pfa_network, ...
        'bus', 'NAME_INTERNAL', data.F_BUS_NAME, true);
else
    error('No F_BUS or F_BUS_NAME parameter set for branch');
end

if isfield(data, 'T_BUS')
    pfa_to_bus = pfa_network.getBus(data.T_BUS);
elseif isfield(data, 'T_BUS_NAME')
    pfa_to_bus = pfa_find_element(pfa_network, ...
        'bus', 'NAME_INTERNAL', data.T_BUS_NAME, true);
else
    error('No T_BUS or T_BUS_NAME parameter set for branch');
end

% if (isfield(data, 'F_BUS') || isfield(data, 'F_BUS_NAME')) && ...
%         (isfield(data, 'T_BUS') || isfield(data, 'T_BUS_NAME'))
%     from_bus_name = data.F_BUS_NAME;
%     to_bus_name = data.T_BUS_NAME;
%     element_model = 'bus';
%     pfa_from_bus = pfa_find_element(pfa_network, element_model, 'NODE', from_bus_name, true);
%     pfa_to_bus = pfa_find_element(pfa_network, element_model, 'NODE', to_bus_name, true);
% else
%     pfa_from_bus = {};
%     pfa_to_bus = {};
% end

end

