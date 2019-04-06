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
function [ pfa_element ] = pfa_import_bus( pfa_network, data_entry)
%PFA_IMPORT_BUS Summary of this function goes here
%   Detailed explanation goes here

pfa_element = pfa_find_bus_from_data(pfa_network, data_entry);
if isempty(pfa_element)
%     is_element_update = false;
%                 fprintf('    Info: Create new bus for row %i\n', ...
%                     element_i);
    pfa_element = pfa_create_element_bus(pfa_network);
else
%     is_element_update = true;
%                 fprintf('    Info: Update bus for row %i\n', ...
%                     element_i);
end

% pfa_element = pfa_create_element_bus(pfa_network);

end

