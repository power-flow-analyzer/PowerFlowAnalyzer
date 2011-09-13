function [  ] = add_flags_branch( jbranch, branchData )
%ADD_FLAGS_BUS Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

%% apparent power
Pmax = max(abs(branchData(PF)), abs(branchData(PT)));
Qmax = max(abs(branchData(QF)), abs(branchData(QT)));
S = sqrt(Pmax^2 + Qmax^2);
rate_A_percentage = S / branchData(RATE_A) * 100;
rate_A_failure = S > branchData(RATE_A);
rate_B_percentage = S / branchData(RATE_B) * 100;
rate_B_failure = S > branchData(RATE_B);
rate_C_percentage = S / branchData(RATE_C) * 100;
rate_C_failure = S > branchData(RATE_C);

jflag = net.ee.pfanalyzer.model.NetworkFlag('Long term rating');
jflag.addParameter('PF');
jflag.addParameter('QF');
jflag.addParameter('PT');
jflag.addParameter('QT');
jflag.setWarning(rate_A_percentage >= 90);
jflag.setFailure(rate_A_failure);
jflag.setPercentage(rate_A_percentage);
jbranch.addFlag(jflag);

jflag = net.ee.pfanalyzer.model.NetworkFlag('Short term rating');
jflag.addParameter('PF');
jflag.addParameter('QF');
jflag.addParameter('PT');
jflag.addParameter('QT');
jflag.setWarning(rate_B_percentage >= 90);
jflag.setFailure(rate_B_failure);
jflag.setPercentage(rate_B_percentage);
jbranch.addFlag(jflag);

jflag = net.ee.pfanalyzer.model.NetworkFlag('Emergency term rating');
jflag.addParameter('PF');
jflag.addParameter('QF');
jflag.addParameter('PT');
jflag.addParameter('QT');
jflag.setWarning(rate_C_percentage >= 90);
jflag.setFailure(rate_C_failure);
jflag.setPercentage(rate_C_percentage);
jbranch.addFlag(jflag);

end

