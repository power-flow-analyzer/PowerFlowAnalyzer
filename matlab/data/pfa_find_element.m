function [ pfa_element ] = pfa_find_element( pfa_network, ...
    element_model, param_name, param_value, warningsOn )
%PFA_FIND_ELEMENT Summary of this function goes here
%   Detailed explanation goes here

jlist = pfa_network.getElements(element_model, param_name, param_value);
if jlist.size() == 0 % no element found
    if warningsOn
        fprintf('Error during import: no network element found with parameter %s and value %s\n', char(param_name), char(param_value));
    end
    pfa_element = '';
elseif jlist.size() == 1 % one single element exists
    % return this element
    pfa_element = jlist.get(0);
    return;
else
    if warningsOn
        fprintf('Error during import: More than one network element with parameter %s and value %s\n', char(param_name), char(param_value));
    end
    pfa_element = '';
end

end

