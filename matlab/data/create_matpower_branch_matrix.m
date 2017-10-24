function [ branchData ] = create_matpower_branch_matrix( branch_list )
%CREATE_BRANCH Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

%% create branch matrix

for i=1:branch_list.size()
    % get branch object for current matrix row
    jbranch = branch_list.get(i - 1);
    
%% check number of columns and create (empty) matrices
    if i == 1
        branchDataSize = 0;
        branchDataSize = increase_if_exists(jbranch, 'F_BUS', branchDataSize);
        branchDataSize = increase_if_exists(jbranch, 'T_BUS', branchDataSize);
        branchDataSize = increase_if_exists(jbranch, 'BR_R', branchDataSize);
        branchDataSize = increase_if_exists(jbranch, 'BR_X', branchDataSize);
        branchDataSize = increase_if_exists(jbranch, 'BR_B', branchDataSize);
        branchDataSize = increase_if_exists(jbranch, 'RATE_A', branchDataSize);
        branchDataSize = increase_if_exists(jbranch, 'RATE_B', branchDataSize);
        branchDataSize = increase_if_exists(jbranch, 'RATE_C', branchDataSize);
        branchDataSize = increase_if_exists(jbranch, 'TAP', branchDataSize);
        branchDataSize = increase_if_exists(jbranch, 'SHIFT', branchDataSize);
        branchDataSize = increase_if_exists(jbranch, 'BR_STATUS', branchDataSize);
        branchDataSize = increase_if_exists(jbranch, 'ANGMIN', branchDataSize);
        branchDataSize = increase_if_exists(jbranch, 'ANGMAX', branchDataSize);

        branchData = zeros(branch_list.size(), branchDataSize);
    end
    
%% add parameters to current matrix row
    if length(branchData) >= F_BUS 
        branchData(i, F_BUS) = pfa_param_int(jbranch, 'F_BUS');
    end
    if length(branchData) >= T_BUS 
        branchData(i, T_BUS) = pfa_param_int(jbranch, 'T_BUS');
    end
    if length(branchData) >= BR_R 
        branchData(i, BR_R) = pfa_param(jbranch, 'BR_R');
    end
    if length(branchData) >= BR_X 
        branchData(i, BR_X) = pfa_param(jbranch, 'BR_X');
    end
    if length(branchData) >= BR_B 
        branchData(i, BR_B) = pfa_param(jbranch, 'BR_B');
    end
    if length(branchData) >= RATE_A 
        branchData(i, RATE_A) = pfa_param(jbranch, 'RATE_A');
    end
    if length(branchData) >= RATE_B 
        branchData(i, RATE_B) = pfa_param(jbranch, 'RATE_B');
    end
    if length(branchData) >= RATE_C 
        branchData(i, RATE_C) = pfa_param(jbranch, 'RATE_C');
    end
    if length(branchData) >= TAP 
        branchData(i, TAP) = pfa_param(jbranch, 'TAP');
    end
    if length(branchData) >= SHIFT 
        branchData(i, SHIFT) = pfa_param(jbranch, 'SHIFT');
    end
    if length(branchData) >= BR_STATUS 
        branchData(i, BR_STATUS) = pfa_param_int(jbranch, 'BR_STATUS');
    end
    if length(branchData) >= ANGMIN 
        branchData(i, ANGMIN) = pfa_param(jbranch, 'ANGMIN');
    end
    if length(branchData) >= ANGMAX 
        branchData(i, ANGMAX) = pfa_param(jbranch, 'ANGMAX');
    end
end

end

