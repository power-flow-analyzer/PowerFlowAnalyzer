function [ jbranch ] = create_branch( jnetwork, branchData, index )
%CREATE_BRANCH Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

% create branch object
jbranch = net.ee.pfanalyzer.model.Branch(jnetwork, index);

%% add parameters to branch object
jbranch.setParameter('F_BUS', branchData(F_BUS));
jbranch.setParameter('T_BUS', branchData(T_BUS));

% add all other parameters and flags
update_branch(jbranch, branchData);

end

