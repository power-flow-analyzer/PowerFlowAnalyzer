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
function [ mpc ] = pfa2matpower( network_data )
%PFA2MATPOWER Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

mpc = struct();
% mpc.version = '2';

mpc.baseMVA = 100;
mpc.bus = struct();
mpc.branch = struct();
mpc.gen = struct();
% mpc.gencost = struct();

%% bus nodes
bus_count = size(network_data.bus.BUS_I, 1);
mpc.bus = zeros(bus_count, VMIN);
mpc.bus(:, BUS_I) = network_data.bus.BUS_I;
mpc.bus(:, BUS_TYPE) = network_data.bus.BUS_TYPE;
mpc.bus(:, PD) = network_data.bus.PD;
mpc.bus(:, QD) = network_data.bus.QD;
mpc.bus(:, GS) = network_data.bus.GS;
mpc.bus(:, BS) = network_data.bus.BS;
mpc.bus(:, BUS_AREA) = network_data.bus.BUS_AREA;
mpc.bus(:, VM) = network_data.bus.VM;
mpc.bus(:, VA) = network_data.bus.VA;
mpc.bus(:, BASE_KV) = network_data.bus.BASE_KV;
mpc.bus(:, ZONE) = network_data.bus.ZONE;
mpc.bus(:, VMAX) = network_data.bus.VMAX;
mpc.bus(:, VMIN) = network_data.bus.VMIN;


%% branches and transformers
line_count = size(network_data.branch.F_BUS, 1);
trafo_count = size(network_data.transformer.F_BUS, 1);
branch_count = line_count + trafo_count;
mpc.branch  = zeros(branch_count, ANGMAX);
% branches
mpc.branch(1:line_count, F_BUS) = network_data.branch.F_BUS;
mpc.branch(1:line_count, T_BUS) = network_data.branch.T_BUS;
mpc.branch(1:line_count, BR_R) = network_data.branch.BR_R;
mpc.branch(1:line_count, BR_X) = network_data.branch.BR_X;
mpc.branch(1:line_count, BR_B) = network_data.branch.BR_B;
mpc.branch(1:line_count, RATE_A) = network_data.branch.RATE_A;
mpc.branch(1:line_count, RATE_B) = network_data.branch.RATE_A;
mpc.branch(1:line_count, RATE_C) = network_data.branch.RATE_A;
% mpc.branch(1:line_count, TAP) = network_data.branch.TAP;
% mpc.branch(1:line_count, SHIFT) = network_data.branch.SHIFT;
mpc.branch(1:line_count, BR_STATUS) = network_data.branch.BR_STATUS;
mpc.branch(1:line_count, ANGMIN) = network_data.branch.ANGMIN;
mpc.branch(1:line_count, ANGMAX) = network_data.branch.ANGMAX;

% transformers
trafo_indices = line_count+1:line_count+trafo_count;
mpc.branch(trafo_indices, F_BUS) = network_data.transformer.F_BUS;
mpc.branch(trafo_indices, T_BUS) = network_data.transformer.T_BUS;
mpc.branch(trafo_indices, BR_R) = network_data.transformer.BR_R;
mpc.branch(trafo_indices, BR_X) = network_data.transformer.BR_X;
mpc.branch(trafo_indices, BR_B) = network_data.transformer.BR_B;
mpc.branch(trafo_indices, RATE_A) = network_data.transformer.RATE_A;
mpc.branch(trafo_indices, RATE_B) = network_data.transformer.RATE_A;
mpc.branch(trafo_indices, RATE_C) = network_data.transformer.RATE_A;
mpc.branch(trafo_indices, TAP) = network_data.transformer.TAP;
mpc.branch(trafo_indices, SHIFT) = network_data.transformer.SHIFT;
mpc.branch(trafo_indices, BR_STATUS) = network_data.transformer.BR_STATUS;
mpc.branch(trafo_indices, ANGMIN) = network_data.transformer.ANGMIN;
mpc.branch(trafo_indices, ANGMAX) = network_data.transformer.ANGMAX;

%% generators
gen_count = size(network_data.gen.GEN_BUS, 1);
mpc.gen = zeros(gen_count, PMIN);
mpc.gen(:, GEN_BUS) = network_data.gen.GEN_BUS;
mpc.gen(:, PG) = network_data.gen.PG;
mpc.gen(:, QG) = network_data.gen.QG;
mpc.gen(:, QMAX) = network_data.gen.QMAX;
mpc.gen(:, QMIN) = network_data.gen.QMIN;
mpc.gen(:, VG) = network_data.gen.VG;
mpc.gen(:, MBASE) = network_data.gen.MBASE;
mpc.gen(:, GEN_STATUS) = network_data.gen.GEN_STATUS;
mpc.gen(:, PMAX) = network_data.gen.PMAX;
mpc.gen(:, PMIN) = network_data.gen.PMIN;
% mpc.gencost = zeros(gen_count, 7);



end

