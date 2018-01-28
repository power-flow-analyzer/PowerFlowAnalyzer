function [  ] = calc_power_flow( jnetwork )
%RUNPFCALCULATION Summary of this function goes here
%   Detailed explanation goes here

% options for Matpower
%mpopt = mpoption('VERBOSE', 3,'OUT_SYS_SUM',1,'OUT_BUS',0,'OUT_BRANCH',0,'OUT_GEN',0,'OUT_ALL_LIM',0);
% mpopt = mpoption('VERBOSE', 0, 'OUT_ALL', 0);
mpopt = mpoption('VERBOSE', 3,'OUT_SYS_SUM',1,'OUT_BUS',0,'OUT_BRANCH',0,'OUT_GEN',0,'OUT_ALL_LIM',1);

% create structure containing case data for Matpower
mpc = network2matpower(jnetwork);
%save('matpower_case.mat', 'mpc');

mpc = mp_handle_isolated_buses(mpc);

% calculate power flow
algo = pfa_param_text(jnetwork, 'POWER_FLOW_ALGO');
if strcmp(algo, 'PF')
    mpc2 = runpf(mpc, mpopt);
elseif strcmp(algo, 'OPF')
    check_gencost(mpc.gencost);
    mpc2 = runopf(mpc, mpopt);
elseif strcmp(algo, 'UOPF')
    check_gencost(mpc.gencost);
    mpc2 = runuopf(mpc, mpopt);
elseif strcmp(algo, 'DCPF')
    mpc2 = rundcpf(mpc, mpopt);
elseif strcmp(algo, 'DCOPF')
    check_gencost(mpc.gencost);
    mpc2 = rundcopf(mpc, mpopt);
elseif strcmp(algo, 'DUOPF')
    check_gencost(mpc.gencost);
    mpc2 = runduopf(mpc, mpopt);
end

% collect data for visualisation
update_network(mpc2, jnetwork);

% transfer data to visualisation
pfa_update_network(jnetwork);

end

