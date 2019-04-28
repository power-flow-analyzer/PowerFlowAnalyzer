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
function [ branch_indices, transformer_indices ] = is_branch_or_transformer( mpc )
%IS_BRANCH_OR_TRANSFORMER Returns indices of branches and transformers
%   Detailed explanation goes here

% Import Matpower constants
define_constants;

branch_indices = [];
transformer_indices = [];

for branch_i=1:length(mpc.branch(:,1))
    f_bus = mpc.branch(branch_i, F_BUS);
    t_bus = mpc.branch(branch_i, T_BUS);
    f_bus_index = find(mpc.bus(:, BUS_I) == f_bus);
    t_bus_index = find(mpc.bus(:, BUS_I) == t_bus);
    if length(f_bus_index) ~= 1 || length(t_bus_index) ~= 1
       disp(''); 
    end
    if mpc.bus(f_bus_index, BASE_KV) == mpc.bus(t_bus_index, BASE_KV)
        branch_indices(end+1, 1) = branch_i; %#ok<AGROW>
    else
        transformer_indices(end+1, 1) = branch_i; %#ok<AGROW>
    end
end

end

