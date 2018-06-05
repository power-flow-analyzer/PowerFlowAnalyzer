function [ jlist_new_elements, jlist_updated_elements ] = ...
    pfa_import_network_elements( pfa_network, data, data_type )
%IMPORT_NETWORK_ELEMENTS Summary of this function goes here
%   Detailed explanation goes here

parameters = pfa_data_field_names(data);
if ~isstruct(data) || isempty(parameters)
    jlist_new_elements = java.util.ArrayList();
    jlist_updated_elements = java.util.ArrayList();
    return;
end

%% Process all elements from data

% length of first column will be taken as element count
element_count = length(data.(parameters{1}));
jlist_new_elements = java.util.ArrayList();
jlist_updated_elements = java.util.ArrayList();

for element_i = 1:element_count
    try
        %% Check name and model

        data_entry = pfa_data_entry(data, element_i);

        pfa_element = pfa_import_element(pfa_network, data_type, data_entry);

        if isempty(pfa_element)
            continue;
        end
        % model is not empty for exisiting elements/empty for new elements
        is_element_update = ~isempty(pfa_element.getModel());


        %% Postprocessing
        if is_element_update
            % add network element to list of updated elements
            jlist_updated_elements.add(pfa_element);
        else
            %  jarea.getParameterList(createParameter('OUTLINE', id));
            % add network element to list of new elements
            jlist_new_elements.add(pfa_element);
            % add new element to network
            pfa_network.addElement(pfa_element);
        end
    catch ME
        warning('Cannot import element in row %i: %s', element_i, getReport(ME));
%         disp(getReport(ME));
%         disp(ME.stack);
%         disp(ME.cause{:});
%         rethrow(ME);
    end % end try

end % end for

% pfa_network.fireNetworkChanged(true);

end

function [ field_names ] = pfa_data_field_names( data )
%PFA_DATA_FIELD_NAMES Summary of this function goes here
%   Detailed explanation goes here

field_names = fieldnames(data);
field_names = setxor(field_names, {'fields'});

end