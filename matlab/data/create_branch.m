function [ branch ] = create_branch( network, branchData, index )
%CREATE_BRANCH Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

% create branch object
branch = net.ee.pfanalyzer.model.Branch(network, index);

% add parameters to branch object
branch.setParameter('F_BUS', branchData(F_BUS));
branch.setParameter('T_BUS', branchData(T_BUS));
branch.setParameter('BR_R', branchData(BR_R));
branch.setParameter('BR_X', branchData(BR_X));
branch.setParameter('BR_B', branchData(BR_B));
branch.setParameter('RATE_A', branchData(RATE_A));
branch.setParameter('RATE_B', branchData(RATE_B));
branch.setParameter('RATE_C', branchData(RATE_C));
branch.setParameter('TAP', branchData(TAP));
branch.setParameter('SHIFT', branchData(SHIFT));
branch.setParameter('BR_STATUS', branchData(BR_STATUS));
branch.setParameter('ANGMIN', branchData(ANGMIN));
branch.setParameter('ANGMAX', branchData(ANGMAX));
% the following parameters are only included in (O)PF output
if length(branchData) > QT 
    branch.setParameter('PF', branchData(PF));
    branch.setParameter('QF', branchData(QF));
    branch.setParameter('PT', branchData(PT));
    branch.setParameter('QT', branchData(QT));
end
% the following parameters are only included in OPF output
if length(branchData) > MU_SF 
    branch.setParameter('MU_SF', branchData(MU_SF));
    branch.setParameter('MU_ST', branchData(MU_ST));
    branch.setParameter('MU_ANGMIN', branchData(MU_ANGMIN));
    branch.setParameter('MU_ANGMAX', branchData(MU_ANGMAX));
end

end

