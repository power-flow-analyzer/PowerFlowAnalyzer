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
function [ transformer_data ] = read_exchange_power( exchange_power_string )
%READ_BUS_DATA Summary of this function goes here
%   Detailed explanation goes here

    transformer_data = struct();
    transformer_data.country1 = strtrim(exchange_power_string(1:2));
    transformer_data.country2 = str2double(exchange_power_string(4:5));
    transformer_data.scheduled_exchange = str2double(exchange_power_string(7:13));
    transformer_data.comments = str2double(exchange_power_string(15:26));

end

