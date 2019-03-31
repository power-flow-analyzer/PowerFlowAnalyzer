function write_dacf_excel( network_data, output_file )
%WRITE_DACF_TO_EXCEL Summary of this function goes here
%   Detailed explanation goes here

data_sources = struct();
data_sources.SOURCE_NAME = {'Netzknoten'; 'Leitungen'; 'Transformatoren'; 'PSTs'};
data_sources.SOURCE_TYPE = {'Excel'; 'Excel'; 'Excel'; 'Excel'};
data_sources.SOURCE_LOCATION = {'-'; '-'; '-'; '-'};
data_sources.DATA_NAME = {'Nodes'; 'Lines'; 'Transformers'; 'Transformer Regulation'};
data_sources.DATA_TYPE = {'BUS'; 'BRANCH'; 'TRANSFORMER'; 'TRANSFORMER'};
data_sources.DATA_INDEX_COLUMN = {1; 1; 1; 1};
data_sources.DATA_INDEX_ROW = {1; 1; 1; 1};
data_sources.DATA_VALUES_ROW = {3; 3; 3; 3};
pfa_write_datasource_excel(data_sources, output_file, 'Data Sources', 1, 4, 6);
% for data_source_i = 1:length(data_sources)
%     pfa_write_datasource_excel(network_data.nodes, output_file, 'Nodes', 1, 1, 2);
% end
pfa_write_datasource_excel(network_data.nodes, output_file, 'Nodes', 1, 1, 2);
pfa_write_datasource_excel(network_data.lines, output_file, 'Lines', 1, 1, 2);
pfa_write_datasource_excel(network_data.transformers, output_file, 'Transformers', 1, 1, 2);
pfa_write_datasource_excel(network_data.transformer_regulation, output_file, 'Transformer Regulation', 1, 1, 2);
end

