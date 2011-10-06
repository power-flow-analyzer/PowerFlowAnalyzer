function [ jbranch ] = create_branch( jnetwork, branchData )
%CREATE_BRANCH Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

% create branch object
jbranch = net.ee.pfanalyzer.model.Branch(jnetwork, 0);

%% add parameters to branch object
if length(branchData) >= T_BUS 
    jbranch.setParameter('F_BUS', branchData(F_BUS));
    jbranch.setParameter('T_BUS', branchData(T_BUS));
end
% add all other parameters and flags
update_branch(jbranch, branchData);

end

