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
function [ mpc ] = network2matpower( jnetwork )
%LOADPFDATA Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

%% create Matpower case (as a structure)
mpc.version = '2';

% collect data from network
mpc.baseMVA = pfa_param_int(jnetwork, 'BASE_MVA');

mpc.bus    = create_matpower_bus_matrix(jnetwork.getBusses());
mpc.branch = create_matpower_branch_matrix(jnetwork.getBranches());

[gen, gencost] = create_matpower_generator_matrices(jnetwork.getGenerators());
mpc.gen = gen;
mpc.gencost = gencost;

end

