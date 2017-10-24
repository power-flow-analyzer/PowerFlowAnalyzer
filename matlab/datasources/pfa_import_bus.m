function [ pfa_element ] = pfa_import_bus( pfa_network, data_entry)
%PFA_IMPORT_BUS Summary of this function goes here
%   Detailed explanation goes here

% pfa_element = pfa_find_bus_from_data(pfa_network, data_entry);
% if isempty(pfa_element)
% %     is_element_update = false;
% %                 fprintf('    Info: Create new bus for row %i\n', ...
% %                     element_i);
%     pfa_element = pfa_create_element_bus(pfa_network);
% else
% %     is_element_update = true;
% %                 fprintf('    Info: Update bus for row %i\n', ...
% %                     element_i);
% end

pfa_element = pfa_create_element_bus(pfa_network);

end

