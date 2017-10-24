function [ pfa_element ] = pfa_import_element( pfa_network, data_type, data_entry)
%PFA_IMPORT_ELEMENT Summary of this function goes here
%   Detailed explanation goes here

switch data_type
    case 'BUS'
        pfa_element = pfa_import_bus(pfa_network, data_entry);
    case 'BRANCH'
        pfa_element = pfa_import_branch(pfa_network, data_entry);
    case 'GENERATOR'
        pfa_element = pfa_import_generator(pfa_network, data_entry);
    case 'TRANSFORMER'
        pfa_element = pfa_import_branch(pfa_network, data_entry);
    case 'AREA'
        pfa_element = pfa_import_area(pfa_network, data_entry);
    otherwise
            fprintf('    Info: Create new unknown element for row %i\n', ...
                data_entry.index);
            pfa_element = net.ee.pfanalyzer.model.NetworkElement(pfa_network);
end

if ~isempty(pfa_element)
    %% Write parameters
    import_parameters_from_data(pfa_network, pfa_element, data_type, data_entry);
end

end

