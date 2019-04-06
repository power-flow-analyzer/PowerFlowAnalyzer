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
function [ model_id ] = pfa_get_model( jnetwork, data_type, data )
%GET_MODEL Summary of this function goes here
%   Detailed explanation goes here

model_id = {};
data_type = upper(data_type);

switch data_type
    case 'BUS'
        model_id = 'bus.unknown';
    case 'BRANCH'
        model_id = 'branch.unknown';
    case 'GENERATOR'
        model_id = 'generator.unknown';
    case 'TRANSFORMER'
        model_id = 'branch.transformer.unknown';
end

end
