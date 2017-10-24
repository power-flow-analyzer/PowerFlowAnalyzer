function [ pfa_element ] = pfa_find_single_element( pfa_network, ...
    element_name, element_model, warningsOn )
%PFA_FIND_SINGLE_BUS Summary of this function goes here
%   Detailed explanation goes here

jlist = pfa_network.getElements(element_model, 'NAME', element_name);
if jlist.size() == 0 % bus node does not exist
    if warningsOn
        warning('Error during import: no network element found with name %s and type %s', char(element_name), char(element_model));
    end
    pfa_element = '';
elseif jlist.size() == 1 % bus node exists
    % use existing bus node
    pfa_element = jlist.get(0);
    return;
else
    if warningsOn
        warning('Error during import: More than one network element with name %s and type %s', char(element_name), char(element_model));
    end
    pfa_element = '';
end

end

