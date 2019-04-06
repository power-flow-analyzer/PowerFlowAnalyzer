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
function [ node_data ] = read_node( node_string )
%READ_BUS_DATA Summary of this function goes here
%   Detailed explanation goes here

    node_data = struct();
    node_data.node = strtrim(node_string(1:8));
    node_data.substation_name = strtrim(node_string(10:21));
    node_data.status = str2double(node_string(23));
    node_data.node_type = str2double(node_string(25));
    node_data.voltage_set_point = str2double(node_string(27:32));
    node_data.p_load = str2double(node_string(34:40));
    node_data.q_load = str2double(node_string(42:48));
    node_data.p_gen  = str2double(node_string(50:56));
    node_data.q_gen  = str2double(node_string(58:64));
    node_data.p_min  = str2double(node_string(66:72));
    node_data.p_max  = str2double(node_string(74:80));
    node_data.q_min  = str2double(node_string(82:88));
    node_data.q_max  = str2double(node_string(90:96));
    node_data.static  = str2double(node_string(98:102));
    node_data.p_nom  = str2double(node_string(104:110));
    node_data.sk  = str2double(node_string(112:118));
    node_data.x_r_ratio  = str2double(node_string(120:126));
    node_data.pp_type  = str2double(node_string(128));

end

