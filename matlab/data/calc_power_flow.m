function [  ] = calc_power_flow( jnetwork )
%RUNPFCALCULATION Summary of this function goes here
%   Detailed explanation goes here

% options for Matpower
mpopt = mpoption('VERBOSE', 0, 'OUT_ALL', 0);

% create structure containing case data for Matpower
mpc = network2matpower(jnetwork);

% calculate power flow
mpc = runpf(mpc, mpopt);

% collect data for visualisation
jnetwork = matpower2network(mpc);

% transfer data to visualisation
transfer_update_network(jnetwork);

end

