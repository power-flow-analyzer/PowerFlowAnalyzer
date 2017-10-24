function [ value ] = parameter_int( ...
    network_element, parameter_name, default_value )
%PARAMETER_INT Summary of this function goes here
%   Detailed explanation goes here

if exist('default_value', 'var') == 0
    default_value = 0;
end

value = network_element.getIntParameter(parameter_name, default_value);
end

