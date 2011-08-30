function [ branchData ] = create_branch_matrix( branch_list )
%CREATE_BRANCH Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

%% create branch matrix

branchData = zeros(branch_list.size(), ANGMAX); % ANGMAX is last column in matrix

for i=1:branch_list.size()
    % get branch object for current matrix row
    jbranch = branch_list.get(i - 1);
    
    % add parameters to current matrix row
    branchData(i, F_BUS) = jbranch.getIntParameter('F_BUS');
    branchData(i, T_BUS) = jbranch.getIntParameter('T_BUS');
    branchData(i, BR_R) = jbranch.getIntParameter('BR_R');
    branchData(i, BR_X) = jbranch.getIntParameter('BR_X');
    branchData(i, BR_B) = jbranch.getIntParameter('BR_B');
    branchData(i, RATE_A) = jbranch.getIntParameter('RATE_A');
    branchData(i, RATE_B) = jbranch.getIntParameter('RATE_B');
    branchData(i, RATE_C) = jbranch.getIntParameter('RATE_C');
    branchData(i, TAP) = jbranch.getIntParameter('TAP');
    branchData(i, SHIFT) = jbranch.getIntParameter('SHIFT');
    branchData(i, BR_STATUS) = jbranch.getIntParameter('BR_STATUS');
    branchData(i, ANGMIN) = jbranch.getIntParameter('ANGMIN');
    branchData(i, ANGMAX) = jbranch.getIntParameter('ANGMAX');
end

end

