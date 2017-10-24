function [ mpc ] = network2matpower( jnetwork )
%LOADPFDATA Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

%% create Matpower case (as a structure)
mpc.version = '2';

% collect data from network
mpc.baseMVA = pfa_param_int(jnetwork, 'BASE_MVA');

mpc.bus    = create_matpower_bus_matrix(jnetwork.getBusses());
mpc.branch = create_matpower_branch_matrix(jnetwork.getBranches());

[gen, gencost] = create_matpower_generator_matrices(jnetwork.getGenerators());
mpc.gen = gen;
mpc.gencost = gencost;

end

