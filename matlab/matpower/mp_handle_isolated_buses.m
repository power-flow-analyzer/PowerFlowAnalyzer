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
function [ mpc ] = mp_handle_isolated_buses( mpc, ignore_islands )
%MP_HANDLE_ISOLATED_BUSES Summary of this function goes here
%   Detailed explanation goes here

if exist('ignore_islands', 'var') == 0
   ignore_islands = false; 
end

% find islands and isolated buses
connected_buses = find_islands(mpc);
if length(connected_buses) > 1 && ignore_islands == false
    error('Error: %i islands were detected in network', length(connected_buses) - 1);
end
connected_buses = connected_buses{1};% make it a double vector
all_buses = 1:size(mpc.bus(:, 1), 1);
isolated_buses = setxor(all_buses, connected_buses);
for bus_i = isolated_buses
    mpc.bus(bus_i, 2) = 4; % set bus type to isolated
end

end

