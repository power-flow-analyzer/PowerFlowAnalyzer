function [  ] = calc_power_flow( jnetwork )
%RUNPFCALCULATION Summary of this function goes here
%   Detailed explanation goes here

% options for Matpower
mpopt = mpoption('VERBOSE', 0, 'OUT_ALL', 0);

% create structure containing case data for Matpower
mpc = network2matpower(jnetwork);

% calculate power flow
algo = jnetwork.getTextParameter('POWER_FLOW_ALGO');
if strcmp(algo, 'PF')
    mpc2 = runpf(mpc, mpopt);
elseif strcmp(algo, 'OPF')
    mpc2 = runopf(mpc, mpopt);
elseif strcmp(algo, 'UOPF')
    mpc2 = runuopf(mpc, mpopt);
elseif strcmp(algo, 'DCPF')
    mpc2 = rundcpf(mpc, mpopt);
elseif strcmp(algo, 'DCOPF')
    mpc2 = rundcopf(mpc, mpopt);
elseif strcmp(algo, 'DUOPF')
    mpc2 = runduopf(mpc, mpopt);
end

% collect data for visualisation
update_network(mpc2, jnetwork);

% transfer data to visualisation
transfer_update_network(jnetwork);

end

