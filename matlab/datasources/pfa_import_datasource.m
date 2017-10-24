function pfa_import_datasource( pfa_network, input_excel )
%PFA_IMPORT_DATASOURCE Summary of this function goes here
%   Detailed explanation goes here

fprintf('Importing %s\n', input_excel);
datasources = read_datasource_excel(input_excel, 'Data Sources', 1, 4, 6);

for ds = 1:length(datasources.DATA_NAME)
    
    % skip empty rows and rows without "data_name" values
    if isempty(datasources.DATA_NAME{ds}) || any(isnan(datasources.DATA_NAME{ds}))
        continue;
    end
    
    data_type = datasources.DATA_TYPE{ds};
    if strcmpi(data_type, 'FILTER')
       continue;
    end
    
    location = datasources.SOURCE_LOCATION{ds};
    if isempty(location)
       location = input_excel;
    end
    table_name = datasources.DATA_NAME{ds};
    fprintf('  Loading %s: %s\n', location, table_name);
    %TODO: ACTIVEX_VT_ERROR in letzter Spalte
    source_data = read_datasource_excel(location, table_name, ...
        datasources.DATA_INDEX_COLUMN(ds), ...
        datasources.DATA_INDEX_ROW(ds), ...
        datasources.DATA_VALUES_ROW(ds));
    
    if isfield(datasources, 'INCLUDE_FILTER_NAME')
        filter_name = datasources.INCLUDE_FILTER_NAME{ds};
        if ~isempty(filter_name)
            fprintf('    Applying filter "%s"\n', filter_name);
            filter = load_filter(location, datasources, filter_name);
            % overwrite variable source_data
            source_data = pfa_filter_dataset(pfa_network, source_data, filter);
        end
    end
    
%     model_filter = {};
%     if isfield(datasources, 'MODEL_FILTER_NAME')
%         model_filter_name = datasources.MODEL_FILTER_NAME{ds};
%         if ~isempty(model_filter_name)
%             fprintf('    Applying models "%s"\n', model_filter_name);
%             model_filter = load_filter(location, datasources, model_filter_name);
%         end
%     end
    
    % import network data
    
%     if strcmpi(data_type, 'BUS') || strcmpi(data_type, 'BRANCH')
        [new_elements, updated_elements] = ...
            pfa_import_network_elements(pfa_network, source_data, data_type);
        modified_files_count = new_elements.size() + updated_elements.size();
        if modified_files_count > 0
            fprintf('    %i network elements modified (%i added, %i updated)\n', ...
                modified_files_count, new_elements.size(), updated_elements.size());
        end
%     end
    
    % TODO:
    % import area data
    % import model DB
end

% update Model DB in PFA
pfa_update_model_db(pfa_network);
% update network in PFA
pfa_update_network(pfa_network);

end

function [filter] = load_filter(location, datasources, filter_name)
for ds = 1:length(datasources.DATA_NAME)
    if strcmpi(datasources.DATA_TYPE{ds}, 'FILTER') && ...
            strcmp(datasources.SOURCE_NAME{ds}, filter_name)
                table_name = char(datasources.DATA_NAME(ds));
                filter = read_datasource_excel(location, table_name, ...
                    datasources.DATA_INDEX_COLUMN(ds), ...
                    datasources.DATA_INDEX_ROW(ds), ...
                    datasources.DATA_VALUES_ROW(ds));
                return;
    end
end
error('Filter cannot be found: %s', filter_name);
end