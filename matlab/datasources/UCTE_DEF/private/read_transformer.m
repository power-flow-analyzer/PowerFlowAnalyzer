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
function [ transformer_data ] = read_transformer( transformer_string )
%READ_BUS_DATA Summary of this function goes here
%   Detailed explanation goes here

    transformer_data = struct();
    transformer_data.nrw_transformer_connectivity_node = strtrim(transformer_string(1:8));
    transformer_data.rw_transformer_connectivity_node = strtrim(transformer_string(10:17));
    transformer_data.transformer_order_code = transformer_string(19);
    transformer_data.transformer_status = str2double(transformer_string(21));
    transformer_data.rated_voltage_1 = str2double(transformer_string(23:27));
    transformer_data.rated_voltage_2 = str2double(transformer_string(29:33));
    transformer_data.transformer_snom = str2double(transformer_string(35:39));
    transformer_data.transformer_resistance = str2double(transformer_string(41:46));
    transformer_data.transformer_reactance = str2double(transformer_string(48:53));
    transformer_data.transformer_susceptance = str2double(transformer_string(55:62));
    transformer_data.transformer_conductance = str2double(transformer_string(64:69));
    transformer_data.transformer_rating = str2double(transformer_string(71:76));
    transformer_data.transformer_identifier = strtrim(transformer_string(78:89));

end

