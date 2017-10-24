function pfaui_import_datasource( jnetwork )
%PFAUI_IMPORT_EXCEL Summary of this function goes here
%   Detailed explanation goes here

% 
% %% remove existing elements from network, if selected
% 
% if pfa_param_boolean(jnetwork, 'REMOVE_ALL_ELEMENTS', false)
%     jnetwork.removeElements(jnetwork.getElements('bus'));
%     jnetwork.removeElements(jnetwork.getElements('branch'));
%     jnetwork.removeElements(jnetwork.getElements('generator'));
%     jnetwork.removeElements(jnetwork.getElements('unknown'));
    jnetwork.removeAllElements();
    % remove all outline definitions for areas from PFA-DB
    jnetwork.getModelDB().getOutlineClass().getModel().clear();

%     
% end

if pfa_param_boolean(jnetwork, 'LOAD_1ST_DATA_SOURCE', false)
    input_excel = pfa_param_text(jnetwork, '1ST_DATA_SOURCE_LOCATION');
    pfa_import_datasource(jnetwork, input_excel);
    
end
if pfa_param_boolean(jnetwork, 'LOAD_2ND_DATA_SOURCE', false)
    input_excel = pfa_param_text(jnetwork, '2ND_DATA_SOURCE_LOCATION');
    pfa_import_datasource(jnetwork, input_excel);
end

% fprintf('  Updating user interface\n');
% transfer_update_network(jnetwork);
% cancelpf;

fprintf('Import completed successfully\n');

end
