function [ value ] = parameter_text( ...
    network_element, parameter_name )
%PARAMETER_INT Summary of this function goes here
%   Detailed explanation goes here

value = network_element.getTextParameter(parameter_name);
end

