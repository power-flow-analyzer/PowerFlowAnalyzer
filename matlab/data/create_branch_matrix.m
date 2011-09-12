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
    branchData(i, F_BUS) = parameter_int(jbranch, 'F_BUS');
    branchData(i, T_BUS) = parameter_int(jbranch, 'T_BUS');
    branchData(i, BR_R) = parameter_double(jbranch, 'BR_R');
    branchData(i, BR_X) = parameter_double(jbranch, 'BR_X');
    branchData(i, BR_B) = parameter_double(jbranch, 'BR_B');
    branchData(i, RATE_A) = parameter_double(jbranch, 'RATE_A');
    branchData(i, RATE_B) = parameter_double(jbranch, 'RATE_B');
    branchData(i, RATE_C) = parameter_double(jbranch, 'RATE_C');
    branchData(i, TAP) = parameter_double(jbranch, 'TAP');
    branchData(i, SHIFT) = parameter_double(jbranch, 'SHIFT');
    branchData(i, BR_STATUS) = parameter_int(jbranch, 'BR_STATUS');
    branchData(i, ANGMIN) = parameter_double(jbranch, 'ANGMIN');
    branchData(i, ANGMAX) = parameter_double(jbranch, 'ANGMAX');
end

end

