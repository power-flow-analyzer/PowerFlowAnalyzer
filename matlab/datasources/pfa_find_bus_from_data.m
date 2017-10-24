function [ jelement ] = pfa_find_bus_from_data( jnetwork, data )
%FIND_BUS_FROM_DATA Summary of this function goes here
%   Detailed explanation goes here

jelement = {};

if isfield(data, 'NAME') && isfield(data, 'MODEL')
    element_name = data.NAME;
    element_model = data.MODEL;
    jelement = pfa_find_single_element(jnetwork, element_name, element_model, false);
end

end

