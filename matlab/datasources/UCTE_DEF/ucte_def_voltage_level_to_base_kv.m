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
function [ base_kv ] = ucte_def_voltage_level_to_base_kv(voltage_level_code)
    switch voltage_level_code
        case 0
            base_kv = 750;
        case 1
            base_kv = 380;
        case 2
            base_kv = 220;
        case 3
            base_kv = 150;
        case 4
            base_kv = 120;
        case 5
            base_kv = 110;
        case 6
            base_kv = 70;
        case 7
            base_kv = 27;
        case 8
            base_kv = 330;
        case 9
            base_kv = 500;
        otherwise
            error('Unknown voltage level code: %i', voltage_level_code);
    end
end
