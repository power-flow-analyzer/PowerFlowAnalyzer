function [ ] = update_branch( jbranch, branchData )
%CREATE_BRANCH Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

%% add parameters to branch object
if length(branchData) >= BR_R 
    jbranch.setParameter('BR_R', branchData(BR_R));
end
if length(branchData) >= BR_X 
    jbranch.setParameter('BR_X', branchData(BR_X));
end
if length(branchData) >= BR_B 
    jbranch.setParameter('BR_B', branchData(BR_B));
end
if length(branchData) >= RATE_A 
    jbranch.setParameter('RATE_A', branchData(RATE_A));
end
if length(branchData) >= RATE_B 
    jbranch.setParameter('RATE_B', branchData(RATE_B));
end
if length(branchData) >= RATE_C 
    jbranch.setParameter('RATE_C', branchData(RATE_C));
end
if length(branchData) >= TAP 
    jbranch.setParameter('TAP', branchData(TAP));
end
if length(branchData) >= SHIFT 
    jbranch.setParameter('SHIFT', branchData(SHIFT));
end
if length(branchData) >= BR_STATUS 
    jbranch.setParameter('BR_STATUS', branchData(BR_STATUS));
end
if length(branchData) >= ANGMIN 
    jbranch.setParameter('ANGMIN', branchData(ANGMIN));
end
if length(branchData) >= ANGMAX 
    jbranch.setParameter('ANGMAX', branchData(ANGMAX));
end
% the following parameters are only included in (O)PF output
if length(branchData) >= PF 
    jbranch.setParameter('PF', branchData(PF));
end
if length(branchData) >= QF 
    jbranch.setParameter('QF', branchData(QF));
end
if length(branchData) >= PT 
    jbranch.setParameter('PT', branchData(PT));
end
if length(branchData) >= QT 
    jbranch.setParameter('QT', branchData(QT));
end
% the following parameters are only included in OPF output
if length(branchData) >= MU_SF 
    jbranch.setParameter('MU_SF', branchData(MU_SF));
end
if length(branchData) >= MU_ST 
    jbranch.setParameter('MU_ST', branchData(MU_ST));
end
if length(branchData) >= MU_ANGMIN 
    jbranch.setParameter('MU_ANGMIN', branchData(MU_ANGMIN));
end
if length(branchData) >= MU_ANGMAX 
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

