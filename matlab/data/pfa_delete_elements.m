function pfa_delete_elements( jnetwork, model_pattern )
%PFA_DELETE_ELEMENTS Deletes elements from a network in PFA
%   Detailed explanation goes here

jlist = pfa_find_elements_by_model(jnetwork, model_pattern);
element_count = jlist.size();

if element_count > 0
    jnetwork.removeElements(jlist);
    fprintf('%i "%s" elements deleted from network\n', element_count, model_pattern);
end

end

