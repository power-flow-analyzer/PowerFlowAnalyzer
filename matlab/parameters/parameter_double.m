function [ value ] = parameter_double( ...
    network_element, parameter_name, default_value )
%PARAMETER_DOUBLE Summary of this function goes here
%   Detailed explanation goes here

if exist('default_value', 'var') == 0
    default_value = 0;
end

value = network_element.getDoubleParameter(parameter_name, default_value);
end

