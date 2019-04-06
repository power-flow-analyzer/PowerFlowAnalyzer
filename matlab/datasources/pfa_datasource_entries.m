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
function [ output_data ] = pfa_datasource_entries( input_data )
%PFA_DATASOURCE_ENTRIES Summary of this function goes here
%   Detailed explanation goes here

parameters = fieldnames(input_data);
if isempty(parameters)
    return;
end

% length of first column will be taken as element count
element_count = length(input_data.(parameters{1}));
for element_i = 1:element_count
    for param_i = 1:length(parameters)
        param = parameters{param_i};        
        output_data(element_i).(param) = input_data.(param)(element_i);
    end
end

end

