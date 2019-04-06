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
function [ output_data ] = pfa_data_entry( input_data, index )
%PFA_DATA_ENTRY Summary of this function goes here
%   Detailed explanation goes here

if isstruct(input_data)
    parameters = fieldnames(input_data);
    output_data.index = index;

    for param_i = 1:length(parameters)
        param = parameters{param_i};
        value = input_data.(param)(index);
        if iscell(value)
            value = value{1};
        end
        output_data.(param) = value;
    end
else
    error('Must only be called on structured input data');
end

end

