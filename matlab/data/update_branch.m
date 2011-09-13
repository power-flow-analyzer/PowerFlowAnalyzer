function [ ] = update_branch( jbranch, branchData )
%CREATE_BRANCH Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

%% add parameters to branch object
jbranch.setParameter('BR_R', branchData(BR_R));
jbranch.setParameter('BR_X', branchData(BR_X));
jbranch.setParameter('BR_B', branchData(BR_B));
jbranch.setParameter('RATE_A', branchData(RATE_A));
jbranch.setParameter('RATE_B', branchData(RATE_B));
jbranch.setParameter('RATE_C', branchData(RATE_C));
jbranch.setParameter('TAP', branchData(TAP));
jbranch.setParameter('SHIFT', branchData(SHIFT));
jbranch.setParameter('BR_STATUS', branchData(BR_STATUS));
jbranch.setParameter('ANGMIN', branchData(ANGMIN));
jbranch.setParameter('ANGMAX', branchData(ANGMAX));
% the following parameters are only included in (O)PF output
if length(branchData) > PF 
    jbranch.setParameter('PF', branchData(PF));
    jbranch.setParameter('QF', branchData(QF));
    jbranch.setParameter('PT', branchData(PT));
    jbranch.setParameter('QT', branchData(QT));
end
% the following parameters are only included in OPF output
if length(branchData) > MU_SF 
    jbranch.setParameter('MU_SF', branchData(MU_SF));
    jbranch.setParameter('MU_ST', branchData(MU_ST));
    jbranch.setParameter('MU_ANGMIN', branchData(MU_ANGMIN));
    jbranch.setParameter('MU_ANGMAX', branchData(MU_ANGMAX));
end

%% add flags

% remove old flags
jbranch.clearFlags();

% add flags
if length(branchData) > PF 
    add_flags_branch(jbranch, branchData);
end

end

