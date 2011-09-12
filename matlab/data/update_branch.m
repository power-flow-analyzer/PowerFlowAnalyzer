function [ ] = update_branch( jbranch, branchData )
%CREATE_BRANCH Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

%% add parameters to branch object

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

end

