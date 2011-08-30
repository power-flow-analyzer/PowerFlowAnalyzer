function [ jnetwork ] = import_matpower_case( caze )
%LOADPFDATA Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

% collect data for visualisation
jnetwork = matpower2network(caze);

% transfer data to visualisation
transfer_new_network(jnetwork);

end

