function [ value ] = parameter_int( ...
    network_element, parameter_name )
%PARAMETER_INT Summary of this function goes here
%   Detailed explanation goes here

value = network_element.getIntParameter(parameter_name, 0);
end

