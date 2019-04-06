%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Copyright 2019 Markus Gronau
% 
% This file is part of PowerFlowAnalyzer.
% 
% Licensed under the Apache License, Version 2.0 (the "License");
% you may not use this file except in compliance with the License.
% You may obtain a copy of the License at
% 
%     http://www.apache.org/licenses/LICENSE-2.0
% 
% Unless required by applicable law or agreed to in writing, software
% distributed under the License is distributed on an "AS IS" BASIS,
% WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
% See the License for the specific language governing permissions and
% limitations under the License.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
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

