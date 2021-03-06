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
function [ transformer_data ] = read_transformer_regulation( transformer_string )
%READ_BUS_DATA Summary of this function goes here
%   Detailed explanation goes here

    transformer_data = struct();
    transformer_data.ltc_transformer_identifier = strtrim(transformer_string(1:19));
    transformer_data.phase_regulation_voltage_change_per_tap = str2double(transformer_string(21:25));
    transformer_data.phase_regulation_number_of_taps = str2double(transformer_string(27:28));
    transformer_data.phase_regulation_current_tap_position = str2double(transformer_string(30:32));
    transformer_data.phase_regulation_target_voltage_for_regulated_winding = str2double(transformer_string(34:38));
    transformer_data.angle_regulation_voltage_change_per_tap = str2double(transformer_string(40:44));
    transformer_data.angle_regulation_angle = str2double(transformer_string(46:50));
    transformer_data.angle_regulation_number_of_taps = str2double(transformer_string(52:53));
    transformer_data.angle_regulation_current_tap_position = str2double(transformer_string(55:57));
    transformer_data.angle_regulation_target_power_flow = str2double(transformer_string(59:63));
    transformer_data.angle_regulation_type = strtrim(transformer_string(65:68));

end

