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
function [ mpc ] = mp_check_mpc( mpc, correctErrors )
%MP_CHECK_MPC Summary of this function goes here
%   Detailed explanation goes here

%% Check branch data

if correctErrors
    % correct missing TAP data
    mpc.branch(find(isnan(mpc.branch(:, 9))), 9) = 1;
end


%% Check for NaN values in MatPower matrices

if any(any(isnan(mpc.bus)))
   error('Bus matrix contains NaN!'); 
end

if any(any(isnan(mpc.branch)))
   error('Branch matrix contains NaN!'); 
end

if any(any(isnan(mpc.gen)))
   error('Generator matrix contains NaN!'); 
end

if isfield(mpc, 'gencost') && any(any(isnan(mpc.gencost)))
   error('Generator cost matrix contains NaN!'); 
end

end

