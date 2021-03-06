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
function [  ] = pfa_write_datasource_excel( data, output_file, table_name, ...
    data_index_column, data_index_row, data_values_row)
%pfa_write_datasource_excel Summary of this function goes here
%   Detailed explanation goes here

warning('off','MATLAB:xlswrite:AddSheet')

EXCEL_COLUMN_NAMES = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

parameters = fieldnames(data);
if isempty(parameters)
    return;
end
% write parameters
xlswrite(output_file, parameters', table_name, ...
    sprintf('%s%i:%s%i', EXCEL_COLUMN_NAMES{data_index_column}, data_index_row, ...
    EXCEL_COLUMN_NAMES{data_index_column+length(parameters)}, data_index_row));

% write values
for field_i = 1:length(parameters)
    values = data.(parameters{field_i}); % was transposed before
    xlswrite(output_file, values, table_name, ...
        sprintf('%s%i:%s%i', EXCEL_COLUMN_NAMES{field_i}, data_values_row, ...
        EXCEL_COLUMN_NAMES{field_i}, data_values_row + length(values) - 1));
end

% ensure that the first cell is not empty (write an arbitrary in this case)
if data_index_row > 1
    xlswrite(output_file, 'Data Source', table_name, ...
        sprintf('%s1:%s1', EXCEL_COLUMN_NAMES{data_index_column}, ...
        EXCEL_COLUMN_NAMES{data_index_column}));    
end

end

