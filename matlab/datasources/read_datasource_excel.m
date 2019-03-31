function [ output_data ] = read_datasource_excel( input_file, table_index_or_name, ...
    data_index_column, data_index_row, data_values_row )

warning('Function is deprecated, use "pfa_load_dataset_excel" instead');

output_data = pfa_load_dataset_excel(input_file, table_index_or_name, ...
    data_index_column, data_index_row, data_values_row);

end