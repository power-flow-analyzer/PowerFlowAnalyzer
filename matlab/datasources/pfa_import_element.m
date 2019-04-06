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
function [ pfa_element ] = pfa_import_element( pfa_network, data_type, data_entry)
%PFA_IMPORT_ELEMENT Summary of this function goes here
%   Detailed explanation goes here

switch upper(data_type)
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

if ~isempty(pfa_element) && isempty(pfa_element.getModel())
    %% Write parameters
    import_parameters_from_data(pfa_network, pfa_element, data_type, data_entry);
end

end

