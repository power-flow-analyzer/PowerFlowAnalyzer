function pfaui_import_areas(jnetwork)
clc;

% load configuration for areas from MAT file
area_config = load(pfa_param_text(jnetwork, 'AREA_CONFIG'));
area_config = area_config.area_config;

% remove all outline definitions for areas from PFA-DB
jnetwork.getModelDB().getOutlineClass().getModel().clear();
% remove all area elements from network
jnetwork.removeElements(jnetwork.getElements('area'));

% add areas to network
for conf_index = 1 : length(area_config)
    add_area( jnetwork, area_config{conf_index}.ID, area_config{conf_index}.NAME, ...
        area_config{conf_index}.MODEL, area_config{conf_index}.OUTLINE_FILE);
end

% update network in PFA
net.ee.pfanalyzer.PowerFlowAnalyzer.getInstance().updateModelDBDialog();

% close progress dialog in PFA
cancelpf;

% THE END

% % input_path = char(parameter_text(jnetwork, 'QGIS_INPUT_PATH'));
% % assignin('base', 'input_path', input_path);
% % if isempty(input_path)
% %     error('Input path must not be empty');
% % end
% % 
% % % load configuration for areas
% % area_config = area_configuration();
% 
% file_pattern = '*.csv';
% 
% % delete output folder
% % rmdir(output_path_full, 's');
% 
% % remove all outline definitions for areas from DB
% jnetwork.getModelDB().getOutlineClass().getModel().clear();
% % remove all area elements from network
% jnetwork.removeElements(jnetwork.getElements('area'));
% 
% for conf_index = 1 : length(area_config)
%     input_dir  = [input_path area_config{conf_index, 1}{1, 1}];
%     output_path_rel = area_config{conf_index, 1}{2, 1};
%     model_id = area_config{conf_index, 1}{3, 1};
%     metainfo_field = area_config{conf_index, 1}{4, 1};
%     output_file_prefix = area_config{conf_index, 1}{5, 1};
%     output_path_full = fullfile(pwd, output_path_rel);
%     % retrieve list of input files
%     input_files = dir(fullfile(input_dir, file_pattern));
%     % convert all input files
%     for csv = 1 : length(input_files)
%         % convert QGIS file and add it to PowerFlowAnalyzer
%         convert_qgis_shapes(jnetwork, fullfile(input_dir, input_files(csv).name), ...
%             output_path_rel, output_path_full, metainfo_field, output_file_prefix, model_id);
%     end
% end
% 
% % update model DB window
% net.ee.pfanalyzer.PowerFlowAnalyzer.getInstance().updateModelDBDialog();
% 
% fclose('all');
% disp('Conversion terminated successfully');
% cancelpf;

end