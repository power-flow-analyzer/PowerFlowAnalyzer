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
function [ output_data ] = pfa_load_dataset_excel( input_file, table_index_or_name, ...
    data_index_column, data_index_row, data_values_row )
%PFA_READ_EXCEL Summary of this function goes here
%   Detailed explanation goes here

cache_version = 0.1;
log = logging;

%% Source File
source_file = fullfile(input_file);
[source_path, source_file_name, source_file_ext] = fileparts(source_file);
sourceFileInfo = dir(source_file);
if isempty(sourceFileInfo)
    error('Input file cannot be found: %s', source_file);
end


%% Table reference
if isnumeric(table_index_or_name)
    table_name = ['number ' num2str(table_index_or_name)];
else
    table_name = table_index_or_name;
end


%% Cache File
cache_file = fullfile(source_path, strcat(source_file_name, source_file_ext, '_cache.mat'));
cachedFileInfo = dir(cache_file);
sheetField = strrep(table_name, ' ', '_');


%% Read from cache

cache_outdated = true;
cache_incomplete = true;
if ~isempty(cachedFileInfo)
    cache_data = load(cache_file);
    cache_data = cache_data.cache_data;
    if isfield(cache_data, 'cache_version') && cache_data.cache_version == cache_version && ...
            cache_data.date == sourceFileInfo.datenum
        cache_outdated = false;
        if isfield(cache_data, sheetField) && ...
                cache_data.(sheetField).source.data_index_column == data_index_column && ...
                cache_data.(sheetField).source.data_index_row == data_index_row && ...
                cache_data.(sheetField).source.data_values_row == data_values_row
            cache_incomplete = false;
            output_data = cache_data.(sheetField).data;
            if log.io.cache || log.io.read
                fprintf('IO: Reading cache for table "%s" from Excel file "%s"\n', table_name, source_file);
            end
        end
    else
        % delete variable, would be reused otherwise
        clear cache_data
    end
end

%% Cache outdated or incomplete

if cache_outdated || cache_incomplete
    if log.io.read
        fprintf('IO: Reading table "%s" from Excel file "%s"\n', table_name, source_file);
    end
    output_data = read_datasource_excel_internal( input_file, table_index_or_name, ...
    data_index_column, data_index_row, data_values_row );
    cache_data.cache_version = cache_version;
    cache_data.date = sourceFileInfo.datenum;
    cache_data.(sheetField).source.data_index_column = data_index_column;
    cache_data.(sheetField).source.data_index_row = data_index_row;
    cache_data.(sheetField).source.data_values_row = data_values_row;
    cache_data.(sheetField).data = output_data;
    if cache_outdated
        % overwrite existing file
        if log.io.cache || log.io.write
            fprintf('IO: Create file cache "%s" with table "%s"\n', cache_file, sheetField);
        end
        save(cache_file, 'cache_data');
    elseif cache_incomplete
        % append cache entry to existing file
        if log.io.cache || log.io.write
            fprintf('IO: Update table "%s" in file cache "%s"\n', sheetField, cache_file);
        end
        save(cache_file, 'cache_data', '-append');
    end
end

end

