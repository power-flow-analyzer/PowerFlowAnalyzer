function [  ] = import_parameters_from_data( jnetwork, jelement, data_type, data_entry )
%COPY_PARAMETERS_FROM_DATA Summary of this function goes here
%   Detailed explanation goes here

parameters = fieldnames(data_entry);
if ~isstruct(data_entry) || isempty(parameters)
    return;
end

    %% Write parameters
%     is_name_set = false;
    is_model_set = false;
    is_parameter_set = false;
    
    %% copy all element's parameters into data model
    for p = 1:length(parameters)
        %% Quality check
        param_name = char(parameters(p));
%         if data_entry.index > length(data.(param_name))
%             fprintf('    Warning: Missing value in row %i in column "%s"\n', ...
%                 char(data.(param_name)(data_entry.index)), param_name);
%             continue;
%         end
        % skip "fields" parameter
        if strcmp(param_name, 'fields')
            continue;
        end
        param_value = data_entry.(param_name);
        
        %% determine parameter value depending on Matlab data type
        % TODO: better use cell2mat?
        % strings are contained in a cell in contrast to numbers
        if isnumeric(param_value)
%             fprintf('  Parameter: %s=%d\n', param_name, param_value);
            if isnan(param_value)
                % ignore empty parameters
%                 fprintf('    Warning: Empty numeric value in row %i in column "%s"\n', ...
%                     data_entry.index, param_name);
                continue;
            end
        elseif iscell(param_value)
%             fprintf('  Parameter: %s=%s\n', param_name, param_value);
            param_value = param_value{1,1};
            % check if text was converted into number
            if isnumeric(param_value)
                if isnan(param_value)
                    % 'NaN' texts were converted to double NaNs
%                     fprintf('    Warning: Empty text value in row %i in column "%s"\n', ...
%                         data_entry.index, param_name);
                    continue;
                else
                    param_value = num2str(param_value);
%                     error('Numeric data in text content in row %i in column "%s"\n', ...
%                         data_entry.index, param_name);
                end
            end
            if isempty(param_value)
                % ignore empty parameters
%                 fprintf('    Warning: Empty text value in row %i in column "%s"\n', ...
%                     data_entry.index, param_name);
                continue;
            end
        elseif isempty(param_value)
%             fprintf('    Warning: Empty value in row %i in column "%s"\n', ...
%                 data_entry.index, param_name);
            continue;
        end
        
        %% Write parameter value into data model
        switch param_name
%             case 'NAME'
%                 if isnumeric(param_value)
%                     param_value = num2str(param_value);
%                 end
%                 jelement.setName(param_value);
%                 is_name_set = true;
            case 'MODEL'
                if isnumeric(param_value)
                    param_value = num2str(param_value);
                end
                jelement.setModelID(param_value);
                is_model_set = true;
            otherwise
                % use uppercase names for parameters
                jelement.setParameter(param_name, param_value);
                is_parameter_set = true;
        end
    end
    if ~is_model_set
        model_id = pfa_get_model(jnetwork, data_type, data_entry);
        if ~isempty(model_id)
            jelement.setModelID(model_id);
            is_model_set = true;
        end
    end
    
    %% Quality check
    if ~is_parameter_set && ~is_model_set
        fprintf('    Warning: Skipping row %i since it does not contain any data\n', ...
            data_entry.index);
       return;
    end
%     if ~is_name_set
%        warning('Name is not set for network element %i', data_entry.index);
%     end
    if ~is_model_set
       warning('Model is not set for %s element %i', data_type, data_entry.index);
    end
end

