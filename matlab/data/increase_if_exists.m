function [ index ] = increase_if_exists( jelement, parameter, lastIndex )
%UNTITLED Summary of this function goes here
%   Detailed explanation goes here

if jelement.hasParameterValue(parameter)
    index = lastIndex + 1;
else
    index = lastIndex;
end

end

