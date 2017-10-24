function [ data_output ] = pfa_read_datasource( input_excel )
%PFA_READ_DATASOURCE Summary of this function goes here
%   Detailed explanation goes here

%% OBSOLETE !!!

datasources = read_datasource_excel(input_excel, 'Data Sources', 1, 4, 6);
% save('datasources.mat', 'datasources');
% data_output = struct();

for ds = 1:length(datasources.data_name)
    location = char(datasources.source_location(ds));
    if isempty(location)
       location = input_excel;
    end
    table_name = char(datasources.data_name(ds));
    fprintf('  Loading %s: %s\n', location, table_name);
    source_data = read_datasource_excel(location, table_name, 1, ...
        datasources.data_index_row(ds), ...
        datasources.data_values_row(ds));
%     source_fields = fieldnames(source_data);
    
%     data_output(ds) = source_data;
end

end

