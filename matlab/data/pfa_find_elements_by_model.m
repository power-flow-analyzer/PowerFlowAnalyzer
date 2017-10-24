function [ jlist ] = pfa_find_elements_by_model( jnetwork, model )
%PFA_FIND_ELEMENTS Summary of this function goes here
%   Detailed explanation goes here

jlist = jnetwork.getElements(model);

end

