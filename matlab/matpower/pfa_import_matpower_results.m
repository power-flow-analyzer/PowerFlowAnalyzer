function [ new_network_data ] = pfa_import_matpower_results( network_data, mpc )

% Define MatPower constants
define_constants;

branch_count = size(network_data.branch.F_BUS, 1);
trafo_count = size(network_data.transformer.F_BUS, 1);
branch_indices = [1:branch_count]'; %#ok<*NBRAK>
transformer_indices = [branch_count+1:branch_count+trafo_count]';

new_network_data = network_data;
new_network_data.SUCCESS = mpc.success;
% bus data
new_network_data.bus.VM = mpc.bus(:, VM);
new_network_data.bus.VA = mpc.bus(:, VA);
% branch data
new_network_data.branch.PF = mpc.branch(branch_indices, PF);
new_network_data.branch.QF = mpc.branch(branch_indices, QF);
new_network_data.branch.PT = mpc.branch(branch_indices, PT);
new_network_data.branch.QT = mpc.branch(branch_indices, QT);
% transformer data
new_network_data.transformer.PF = mpc.branch(transformer_indices, PF);
new_network_data.transformer.QF = mpc.branch(transformer_indices, QF);
new_network_data.transformer.PT = mpc.branch(transformer_indices, PT);
new_network_data.transformer.QT = mpc.branch(transformer_indices, QT);
% generator data
new_network_data.gen.PG = mpc.gen(:, PG);
new_network_data.gen.QG = mpc.gen(:, QG);

end