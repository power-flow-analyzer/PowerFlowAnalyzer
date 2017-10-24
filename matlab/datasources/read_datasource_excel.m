function [ data ] = read_datasource_excel( input_file, table_name, ...
    data_index_column, data_index_row, data_values_row )
%PFA_READ_EXCEL Summary of this function goes here
%   Detailed explanation goes here

% Source File
[source_path, source_file_name, source_file_ext] = fileparts(fullfile(input_file));
input_file_full = fullfile(pwd, source_path, strcat(source_file_name, source_file_ext));
sourceFileInfo = dir(input_file_full);
if isempty(sourceFileInfo)
    error('Input file cannot be found: %s', input_file_full);
end

exl = actxserver('excel.application');
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
last_row = size(all_values, data_index_column);

parameters = all_values(data_index_row, :);
find_nan = cellfun(@(V) any(isnan(V(:))), parameters);
parameters(find_nan)={''};
parameters(strcmp(parameters, '-'))={''};

for column_i = 1:length(parameters)
    % normalize parameter name
    % replace spaces with underscores
    field_name = strrep(upper(parameters{column_i}), ' ', '_');
    % use built-in function for all other characters
    field_name = matlab.lang.makeValidName(field_name);
    % skip columns with empty parameter
    if isempty(field_name) || isempty(strtrim(field_name))
        continue;
    end
%     fprintf('Parameter %s\n', field_name);
    column_values = all_values(data_values_row:last_row, column_i);
    if isnumeric(column_values{1,1})
        data.(field_name) = cell2mat(column_values);
    elseif ischar(column_values{1,1})
        column_values(strcmp(column_values, '-'))={''};
        data.(field_name) = column_values;
    end
end

% exlFile.Save
exl_workbook.Close;
exl.Quit;

end
