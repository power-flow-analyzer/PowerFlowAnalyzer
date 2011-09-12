function [ value ] = parameter_double( ...
    network_element, parameter_name )
%PARAMETER_DOUBLE Summary of this function goes here
%   Detailed explanation goes here

value = network_element.getDoubleParameter(parameter_name);
end

