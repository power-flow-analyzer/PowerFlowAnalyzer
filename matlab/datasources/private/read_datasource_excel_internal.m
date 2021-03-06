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
function [ data ] = read_datasource_excel_internal( input_file, table_name, ...
    data_index_column, data_index_row, data_values_row )
%PFA_READ_EXCEL Summary of this function goes here
%   Detailed explanation goes here

data = struct;

% Source File
[source_path, source_file_name, source_file_ext] = fileparts(fullfile(input_file));
input_file_full = fullfile(pwd, source_path, strcat(source_file_name, source_file_ext));
sourceFileInfo = dir(input_file_full);
if isempty(sourceFileInfo)
    error('Input file cannot be found: %s', input_file_full);
end

exl = actxserver('excel.application');
exl.DisplayAlerts = false; % do not block UI with invisible prompts from Excel (e.g. saving before closing)
exl_workbook = exl.Workbooks;
exl_file = exl_workbook.Open(input_file_full);
    
try
    exl_table = exl_file.Sheets.Item(table_name);
catch
    exl_workbook.Close;
    exl.Quit;
    error('Sheet "%s" is not contained in Excel file "%s"', table_name, input_file_full);
end

all_values = exl_table.UsedRange.Value;
all_values = delete_empty_rows(all_values, data_values_row);
last_row = size(all_values, data_index_column);

% check for empty table -> return empty structure
if data_index_row >= size(all_values, 1)
    exl_workbook.Close;
    exl.Quit;
    fprintf('Empty sheet "%s" in Excel file "%s"\n', table_name, input_file_full);
    return;
end
parameters = all_values(data_index_row, :);
find_nan = cellfun(@(V) any(isnan(V(:))), parameters);
parameters(find_nan)={''};
parameters(strcmp(parameters, '-'))={''};

fields = parameters(isempty(parameters));
data.fields = cell(2, length(fields));
delete_fields = cell(0, 1);

field_i = 1;
for column_i = 1:length(parameters)
    param_name = parameters{column_i};
    % normalize parameter name
    % skip columns with empty parameter
    if isempty(param_name) || isempty(strtrim(param_name))
        continue;
    end
    % replace spaces with underscores
    field_name = strrep(upper(param_name), ' ', '_');
    % use built-in function for all other characters
    field_name = matlab.lang.makeValidName(field_name);
    data.fields{1, field_i} = param_name;
    data.fields{2, field_i} = field_name;
%     fprintf('Parameter %s\n', field_name);
    column_values = all_values(data_values_row:last_row, column_i);
    if strcmp(field_name, 'TIME')
        data.time = column_values;
        delete_fields = {param_name};
    elseif isnumeric(column_values{1,1})
        data.(field_name) = cell2mat(column_values);
    elseif ischar(column_values{1,1})
        column_values(strcmp(column_values, '-'))={''};
        data.(field_name) = column_values;
    end
    field_i = field_i + 1;
end

% delete fields if necessary
for field_i = 1:length(delete_fields)
    index_i = find(strcmp(data.fields(1, :), delete_fields(field_i)));
    data.fields = data.fields(:, [1:index_i-1, index_i+1:end]);
end

% exlFile.Save

exl_workbook.Close;
exl.Quit;

end

%  Remove empty rows filled with numeric NaNs
function [ output_data ] = delete_empty_rows(input_data, first_data_row)
row_count = size(input_data, 1);
col_count = size(input_data, 2);
data_rows = zeros(row_count, 1);
data_rows(1:first_data_row) = true;
for row_i = first_data_row:row_count
    for col_i = 1:col_count
        if (isnumeric(input_data{row_i, col_i}) ...
                && isnan(input_data{row_i, col_i})) == false
            data_rows(row_i) = true;
            break;
        end
    end
%     fprintf('Row %i contains data (not empty): %i\n', row_i, data_rows(row_i));
end
output_data = cell(sum(data_rows), col_count);
data_row_indices = find(data_rows == 1);
output_data(data_row_indices, :) = input_data(data_row_indices, :);
end
