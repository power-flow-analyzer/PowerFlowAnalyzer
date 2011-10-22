function [ value ] = parameter_boolean( ...
    network_element, parameter_name, default_value )
%PARAMETER_DOUBLE Summary of this function goes here
%   Detailed explanation goes here

if exist('default_value', 'var') == 0
    default_value = false;
end
value = network_element.getBooleanParameter(parameter_name, default_value);
end

