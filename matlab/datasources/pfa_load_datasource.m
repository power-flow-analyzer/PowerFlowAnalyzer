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
function [ data ] = pfa_load_datasource( input_excel )
%pfa_load_datasource Summary of this function goes here
%   Detailed explanation goes here

data = struct();

fprintf('Importing %s\n', input_excel);
datasources = pfa_load_dataset_excel(input_excel, 'Data Sources', 1, 4, 6);

for ds = 1:length(datasources.DATA_NAME)
    
    % skip empty rows and rows without "data_name" values
    if isempty(datasources.DATA_NAME{ds}) || any(isnan(datasources.DATA_NAME{ds}))
        continue;
    end
    
    data_type = datasources.DATA_TYPE{ds};
    if strcmpi(data_type, 'FILTER')
       continue;
    end
    data_type = lower(data_type);
    
    location = datasources.SOURCE_LOCATION{ds};
    if isempty(location)
       location = input_excel;
    end
    table_name = datasources.DATA_NAME{ds};
    fprintf('  Loading %s: %s\n', location, table_name);
    %TODO: ACTIVEX_VT_ERROR in letzter Spalte
    data.(data_type) = pfa_load_dataset_excel(location, table_name, ...
        datasources.DATA_INDEX_COLUMN(ds), ...
        datasources.DATA_INDEX_ROW(ds), ...
        datasources.DATA_VALUES_ROW(ds));
    
%     if isfield(datasources, 'INCLUDE_FILTER_NAME')
%         filter_name = datasources.INCLUDE_FILTER_NAME{ds};
%         if ~isempty(filter_name)
%             fprintf('    Applying filter "%s"\n', filter_name);
%             filter = load_filter(location, datasources, filter_name);
%             % overwrite variable source_data
%             source_data = pfa_filter_dataset(pfa_network, source_data, filter);
%         end
%     end
    
%     model_filter = {};
%     if isfield(datasources, 'MODEL_FILTER_NAME')
%         model_filter_name = datasources.MODEL_FILTER_NAME{ds};
%         if ~isempty(model_filter_name)
%             fprintf('    Applying models "%s"\n', model_filter_name);
%             model_filter = load_filter(location, datasources, model_filter_name);
%         end
%     end
    

end

end

% function [filter] = load_filter(location, datasources, filter_name)
% for ds = 1:length(datasources.DATA_NAME)
%     if strcmpi(datasources.DATA_TYPE{ds}, 'FILTER') && ...
%             strcmp(datasources.SOURCE_NAME{ds}, filter_name)
%                 table_name = char(datasources.DATA_NAME(ds));
%                 filter = pfa_load_dataset_excel(location, table_name, ...
%                     datasources.DATA_INDEX_COLUMN(ds), ...
%                     datasources.DATA_INDEX_ROW(ds), ...
%                     datasources.DATA_VALUES_ROW(ds));
%                 return;
%     end
% end
% error('Filter cannot be found: %s', filter_name);
% end
