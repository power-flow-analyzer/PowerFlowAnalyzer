function pfa_convert_qgis_shapes(qgis_config_file)
% input_path QGIS_INPUT_PATH
% output_file output file name for converted area configuration

tic

qgis_config = read_datasource_excel(qgis_config_file, 'AREAS', 1, 1, 2);

file_pattern = '*.csv';
    
for conf_index = 1 : length(qgis_config.NAME)
    % data structure holding area configuration
    area_config = struct();
    area_config.ID = cell(0, 1);
    area_config.NAME = cell(0, 1);
    area_config.MODEL = cell(0, 1);
    area_config.OUTLINE_FILE = cell(0, 1);
    % read QGIS file
    input_dir  = cell2mat(qgis_config.INPUT_FOLDER(conf_index));
    fprintf('Starting conversion of QGIS shapes in directory: %s ...\n', input_dir);
    output_path_rel = cell2mat(qgis_config.OUTPUT_FOLDER(conf_index));
    model_id = cell2mat(qgis_config.AREA_MODEL(conf_index));
    metainfo_field = qgis_config.NAMING_MODE(conf_index);
    output_file_prefix = cell2mat(qgis_config.NAME_PREFIX(conf_index));
    output_path_full = fullfile(pwd, output_path_rel);
    % retrieve list of input files
    input_files = dir(fullfile(input_dir, file_pattern));
    % convert all input files
    for csv = 1 : length(input_files)
        % convert QGIS file and update area config
        area_config = convert_qgis_file(area_config, fullfile(input_dir, input_files(csv).name), ...
            output_path_rel, output_path_full, metainfo_field, output_file_prefix, model_id);
    end
    if ~isempty(area_config)
        % save area configuration to Excel file
        output_file = qgis_config.OUTPUT_DATA_SOURCE{conf_index};
        % write table configuration
        data_sources = struct();
        data_sources.SOURCE_NAME = {'QGIS Areas'};
        data_sources.SOURCE_TYPE = {'Excel'};
        data_sources.SOURCE_LOCATION = {'-'};
        data_sources.DATA_NAME = {'Areas'};
        data_sources.DATA_TYPE = {'AREA'};
        data_sources.DATA_INDEX_COLUMN = {1};
        data_sources.DATA_INDEX_ROW = {1};
        data_sources.DATA_VALUES_ROW = {2};
        write_datasource_excel(data_sources, output_file, 'Data Sources', 1, 4, 6);
        % write table data
        write_datasource_excel(area_config, output_file, 'Areas', 1, 1, 2);
    end
end


%% THE END

fclose('all');
fprintf('Conversion terminated successfully (took %s seconds)\n', num2str(toc, 5));

end

function [ area_config ] = convert_qgis_file(area_config, input_file, output_folder_rel, ...
    output_folder_full, metainfo_field, output_file_prefix, model_id)

% open CSV input file
[file_handle, error_msg] = fopen(input_file, 'r','n','UTF-8'); % read-only in UTF-8
% check if file does not exist
if ~isempty(error_msg)
    warning(['  Cannot open file: ' input_file]);
    return;
end

% create output directory if necessary
[~,~,~] = mkdir(output_folder_full);
count = 0;
while ~feof(file_handle)
    % read next line
    line = strtrim(fgets(file_handle));
    count = count + 1;
    % skip first line
    if count == 1 %|| count > 5
       continue; 
    end

    % parse meta information from current line
    metainfo = parse_qgis_metainfo(line);
    metainfostring = '';
    for info = 1:length(metainfo)
        metainfovalue = char(metainfo(info));
        metainfostring = [metainfostring ' ' metainfovalue]; %#ok<AGROW>
    end

    % parse polygon/coordinates from current line
    polygon = parse_qgis_multipolygon(line);
    % write polygon if list of coordinates is not empty
    if ~isempty(polygon)
        [~, input_filename, input_fileext] = fileparts(input_file);
        if metainfo_field == 0
            % output file name must be input file name
            output_filename = [output_file_prefix input_filename];
        else
            % output file name must be read from meta info
            output_filename = [output_file_prefix char(metainfo(metainfo_field))];
        end
        output_fileext = input_fileext;
        output_file_name = getOutputFileName(output_folder_full, output_filename, output_fileext);
        output_fullfile = fullfile(output_folder_full, [output_file_name output_fileext]);
        output_file_rel = fullfile(output_folder_rel, [output_file_name output_fileext]);
        disp(['  Writing shape ' output_file_name ' with ' ...
            num2str(length(polygon)) ' vertices and ' ...
            num2str(length(metainfo)) ' attributes [' metainfostring ']']);
        dlmwrite(output_fullfile, polygon, 'precision', 13);
        area_config.ID{end+1} = output_file_name;
        area_config.NAME{end+1} = output_file_name;
        area_config.MODEL{end+1} = model_id;
        area_config.OUTLINE_FILE{end+1} = output_file_rel;
    end

end

% close CSV input file
fclose(file_handle);

end

function [output_file_name] = getOutputFileName(output_folder, fileName, fileExt)

    output_file_name = fileName;
    index = 1;
    while exist(fullfile(output_folder, [output_file_name fileExt]), 'file') > 0
        output_file_name = [fileName '-' num2str(index)];
        index = index + 1;
    end
end