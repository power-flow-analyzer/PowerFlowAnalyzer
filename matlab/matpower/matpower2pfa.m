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
function [ network_data ] = matpower2pfa( mpc )
%MATPOWER2PFA Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

network_data = struct();

network_data.BASE_MVA = mpc.baseMVA;
network_data.bus = struct();
network_data.branch = struct();
network_data.transformer = struct();
network_data.gen = struct();
if isfield(mpc, 'success')
    network_data.SUCCESS = mpc.success;
end
if isfield(mpc, 'gencost')
    network_data.gencost = struct();
end

%% bus nodes
network_data.bus.IDENTIFIER = strcat('bus_', strtrim(cellstr(num2str(mpc.bus(:, BUS_I)))), '_ref');
network_data.bus.BUS_I = mpc.bus(:, BUS_I);
network_data.bus.BUS_TYPE = mpc.bus(:, BUS_TYPE);
network_data.bus.PD = mpc.bus(:, PD);
network_data.bus.QD = mpc.bus(:, QD);
network_data.bus.GS = mpc.bus(:, GS);
network_data.bus.BS = mpc.bus(:, BS);
network_data.bus.BUS_AREA = mpc.bus(:, BUS_AREA);
network_data.bus.VM = mpc.bus(:, VM);
network_data.bus.VA = mpc.bus(:, VA);
network_data.bus.BASE_KV = mpc.bus(:, BASE_KV);
network_data.bus.ZONE = mpc.bus(:, ZONE);
network_data.bus.VMAX = mpc.bus(:, VMAX);
network_data.bus.VMIN = mpc.bus(:, VMIN);


%% branches and transformers

[branch_indices, transformer_indices] = is_branch_or_transformer(mpc);

% branches
network_data.branch.F_BUS = mpc.branch(branch_indices, F_BUS);
network_data.branch.T_BUS = mpc.branch(branch_indices, T_BUS);
network_data.branch.BR_R = mpc.branch(branch_indices, BR_R);
network_data.branch.BR_X = mpc.branch(branch_indices, BR_X);
network_data.branch.BR_B = mpc.branch(branch_indices, BR_B);
network_data.branch.RATE_A = mpc.branch(branch_indices, RATE_A);
network_data.branch.RATE_B = mpc.branch(branch_indices, RATE_B);
network_data.branch.RATE_C = mpc.branch(branch_indices, RATE_C);
network_data.branch.TAP = mpc.branch(branch_indices, TAP);
network_data.branch.SHIFT = mpc.branch(branch_indices, SHIFT);
network_data.branch.BR_STATUS = mpc.branch(branch_indices, BR_STATUS);
network_data.branch.ANGMIN = mpc.branch(branch_indices, ANGMIN);
network_data.branch.ANGMAX = mpc.branch(branch_indices, ANGMAX);

% transformers
network_data.transformer.F_BUS = mpc.branch(transformer_indices, F_BUS);
network_data.transformer.T_BUS = mpc.branch(transformer_indices, T_BUS);
network_data.transformer.BR_R = mpc.branch(transformer_indices, BR_R);
network_data.transformer.BR_X = mpc.branch(transformer_indices, BR_X);
network_data.transformer.BR_B = mpc.branch(transformer_indices, BR_B);
network_data.transformer.RATE_A = mpc.branch(transformer_indices, RATE_A);
network_data.transformer.RATE_B = mpc.branch(transformer_indices, RATE_B);
network_data.transformer.RATE_C = mpc.branch(transformer_indices, RATE_C);
network_data.transformer.TAP = mpc.branch(transformer_indices, TAP);
network_data.transformer.SHIFT = mpc.branch(transformer_indices, SHIFT);
network_data.transformer.BR_STATUS = mpc.branch(transformer_indices, BR_STATUS);
network_data.transformer.ANGMIN = mpc.branch(transformer_indices, ANGMIN);
network_data.transformer.ANGMAX = mpc.branch(transformer_indices, ANGMAX);


%% generators

network_data.gen.GEN_BUS = mpc.gen(:, GEN_BUS);
network_data.gen.PG = mpc.gen(:, PG);
network_data.gen.QG = mpc.gen(:, QG);
network_data.gen.QMAX = mpc.gen(:, QMAX);
network_data.gen.QMIN = mpc.gen(:, QMIN);
network_data.gen.VG = mpc.gen(:, VG);
network_data.gen.MBASE = mpc.gen(:, MBASE);
network_data.gen.GEN_STATUS = mpc.gen(:, GEN_STATUS);
network_data.gen.PMAX = mpc.gen(:, PMAX);
network_data.gen.PMIN = mpc.gen(:, PMIN);


%% generator costs

if isfield(mpc, 'gencost')
    network_data.gencost.MODEL = mpc.gencost(:, MODEL);
    network_data.gencost.STARTUP = mpc.gencost(:, STARTUP);
    network_data.gencost.SHUTDOWN = mpc.gencost(:, SHUTDOWN);
    network_data.gencost.NCOST = mpc.gencost(:, NCOST);
    network_data.gencost.COST = mpc.gencost(:, COST);
    for gencost_i = 6:length(mpc.gencost(1, :))
        network_data.gencost.(['GENCOST' num2str(gencost_i)]) = mpc.gencost(:, gencost_i);
    end
end

end

