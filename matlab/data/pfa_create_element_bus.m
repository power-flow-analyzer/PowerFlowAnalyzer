function [ pfa_element ] = pfa_create_element_bus( pfa_network )
%PFA_CREATE_BUS Summary of this function goes here
%   Detailed explanation goes here

pfa_element = net.ee.pfanalyzer.model.Bus(pfa_network, 0);
% pfa_element = net.ee.pfanalyzer.model.NetworkElement(pfa_network);

end

