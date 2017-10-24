function [ pfa_branch ] = pfa_import_branch( pfa_network, branch_data)
%PFA_IMPORT_BRANCH Summary of this function goes here
%   Detailed explanation goes here

pfa_branch = {};

% find from and to bus nodes
[ pfa_from_bus, pfa_to_bus ] = pfa_find_branch_bus_from_data(pfa_network, branch_data);
if isempty(pfa_from_bus) || isempty(pfa_to_bus)
    return;
end

% create new PFA object for input data
pfa_branch = net.ee.pfanalyzer.model.Branch(pfa_network, 0);
% fprintf('    Info: Create new branch for row %i\n', element_i);

% % set from and to bus numbers as separate parameters
% from_bus_index = pfa_from_bus.getBusNumber();
% pfa_set_param(pfa_branch, 'F_BUS', from_bus_index);
% to_bus_index = pfa_to_bus.getBusNumber();
% pfa_set_param(pfa_branch, 'T_BUS', to_bus_index);

% set branch name
from_bus_name = pfa_param_text(pfa_from_bus, 'NAME');
to_bus_name = pfa_param_text(pfa_to_bus, 'NAME');
pfa_set_param(pfa_branch, 'NAME', [from_bus_name ' - ' to_bus_name]);

end