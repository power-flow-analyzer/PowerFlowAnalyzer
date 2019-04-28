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
function [ network_data ] = pfa_add_dataset( network_data, additional_data, data_type )
%pfa_add_dataset Enriches a network data set with additional data of a specific type
%   Detailed explanation goes here

%% Preliminary checks
source_row_count = numel(additional_data.IDENTIFIER);
if source_row_count == 0
    % nothing to copy
    return;
end
if ~isfield(additional_data, 'IDENTIFIER')
   error('Data source does not contain an "IDENTIFIER" column!');
end
target_row_count = numel(network_data.(data_type).IDENTIFIER);


%% Gather fields to be copied
source_fields = fieldnames(additional_data);
% exclude some columns from being copied
fields2copy = setxor(source_fields, {'fields', 'IDENTIFIER'});
% check if first row contains default values
has_default_values = strcmp(additional_data.IDENTIFIER{1}, 'DEFAULT');


%% Create fields in target data set if necessary
for field_i = 1:length(fields2copy)
    field_name = fields2copy{field_i};
    % check if field exists in target data set
    if ~isfield(network_data.(data_type), field_name)
        % create field
        if isnumeric(additional_data.(field_name))
            if has_default_values
                default_value = additional_data.(field_name)(1);
                if isnan(default_value)
                    % fill empty cells with NaN
                    values = nan(target_row_count, 1);
                else
                    % fill empty cells with default value
                    values = ones(target_row_count, 1) * default_value;
                end
            else
                % fill empty cells with NaN
                values = nan(target_row_count, 1);
            end
            network_data.(data_type).(field_name) = values;
        elseif iscell(additional_data.(field_name))
            % create empty cells
            values = cell(target_row_count, 1);
            if has_default_values
                % fill empty cells with default value
                default_value = additional_data.(field_name){1};
                values(:, 1) = { default_value };
            end
            network_data.(data_type).(field_name) = values;
        else
            error('Data in field "%s" is neither numeric nor cell', field_name);
        end 
    end
end

%% Copy values
for row_i = 1:source_row_count
    identifier = additional_data.IDENTIFIER{row_i};
    indices = find(strncmp(network_data.(data_type).IDENTIFIER, identifier, length(identifier)));
    if isempty(indices)
%         warning('No match for bus "%s"', identifier);
        continue;
    end

    for field_i = 1:length(fields2copy)
        field_name = fields2copy{field_i};
        if isnumeric(additional_data.(field_name))
            value2copy = additional_data.(field_name)(row_i);
            % copy values into target data set
            network_data.(data_type).(field_name)(indices, 1) = value2copy;
        elseif iscell(additional_data.(field_name))
            value2copy = additional_data.(field_name){row_i};
            % copy values into target data set
            for index_i = 1:length(indices)
                network_data.(data_type).(field_name){indices(index_i), 1} = value2copy;
            end
        else
            error('Data in field "%s" is neither numeric nor cell', field_name);
        end
    end
end

end