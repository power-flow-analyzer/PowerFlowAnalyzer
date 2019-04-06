%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Copyright 2019 Markus Gronau
% 
% This file is part of PowerFlowAnalyzer.
% 
% Licensed under the Apache License, Version 2.0 (the "License");
% you may not use this file except in compliance with the License.
% You may obtain a copy of the License at
% 
%     http://www.apache.org/licenses/LICENSE-2.0
% 
% Unless required by applicable law or agreed to in writing, software
% distributed under the License is distributed on an "AS IS" BASIS,
% WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
% See the License for the specific language governing permissions and
% limitations under the License.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
function [  ] = add_flags_branch( jbranch, branchData )
%ADD_FLAGS_BUS Summary of this function goes here
%   Detailed explanation goes here

if pfa_param_int(jbranch, 'BR_STATUS') == 0
   return; 
end

% define Matpower constants
define_constants;

%% apparent power
Pmax = max(abs(branchData(PF)), abs(branchData(PT)));
Qmax = max(abs(branchData(QF)), abs(branchData(QT)));
S = sqrt(Pmax^2 + Qmax^2);

if branchData(RATE_A) > 0
    rate_A_percentage = S / branchData(RATE_A) * 100;
    jflag = net.ee.pfanalyzer.model.NetworkFlag('branch.long_term_rating');
    jflag.setValue(S, 'RATE_A');
    jflag.addParameter('RATE_A');
    jflag.addParameter('PF');
    jflag.addParameter('QF');
    jflag.addParameter('PT');
    jflag.addParameter('QT');
    jflag.setPercentage(rate_A_percentage);
    jbranch.addFlag(jflag);
end

if branchData(RATE_A) ~= branchData(RATE_B) && ...
        branchData(RATE_B) > 0
    rate_B_percentage = S / branchData(RATE_B) * 100;
    jflag = net.ee.pfanalyzer.model.NetworkFlag('branch.short_term_rating');
    jflag.setValue(S, 'RATE_B');
    jflag.addParameter('RATE_B');
    jflag.addParameter('PF');
    jflag.addParameter('QF');
    jflag.addParameter('PT');
    jflag.addParameter('QT');
    jflag.setPercentage(rate_B_percentage);
    jbranch.addFlag(jflag);
end

if branchData(RATE_A) ~= branchData(RATE_B) && ...
        branchData(RATE_B) ~= branchData(RATE_C) && ...
        branchData(RATE_C) > 0
    rate_C_percentage = S / branchData(RATE_C) * 100;
    jflag = net.ee.pfanalyzer.model.NetworkFlag('branch.emergency_term_rating');
    jflag.setValue(S, 'RATE_C');
    jflag.addParameter('RATE_C');
    jflag.addParameter('PF');
    jflag.addParameter('QF');
    jflag.addParameter('PT');
    jflag.addParameter('QT');
    jflag.setPercentage(rate_C_percentage);
    jbranch.addFlag(jflag);
end

end

