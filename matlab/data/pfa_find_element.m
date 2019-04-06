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

