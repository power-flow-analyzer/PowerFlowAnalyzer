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
function [ data_output ] = pfa_read_datasource( input_excel )
%PFA_READ_DATASOURCE Summary of this function goes here
%   Detailed explanation goes here

%% OBSOLETE !!!

datasources = pfa_load_dataset_excel(input_excel, 'Data Sources', 1, 4, 6);
% save('datasources.mat', 'datasources');
% data_output = struct();

for ds = 1:length(datasources.data_name)
    location = char(datasources.source_location(ds));
    if isempty(location)
       location = input_excel;
    end
    table_name = char(datasources.data_name(ds));
    fprintf('  Loading %s: %s\n', location, table_name);
    source_data = pfa_load_dataset_excel(location, table_name, 1, ...
        datasources.data_index_row(ds), ...
        datasources.data_values_row(ds));
%     source_fields = fieldnames(source_data);
    
%     data_output(ds) = source_data;
end

end

