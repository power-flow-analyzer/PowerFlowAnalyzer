function [  ] = calc_power_flow( jnetwork )
%RUNPFCALCULATION Summary of this function goes here
%   Detailed explanation goes here

% options for Matpower
mpopt = mpoption('VERBOSE', 0, 'OUT_ALL', 0);

% create structure containing case data for Matpower
mpc = network2matpower(jnetwork);

% calculate power flow
mpc2 = runpf(mpc, mpopt);

% collect data for visualisation
update_network(mpc2, jnetwork);

% transfer data to visualisation
transfer_update_network(jnetwork);

end

