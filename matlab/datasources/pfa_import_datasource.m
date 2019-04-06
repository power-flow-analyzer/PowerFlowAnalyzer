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
function pfa_import_datasource( pfa_network, input_excel )
%PFA_IMPORT_DATASOURCE Summary of this function goes here
%   Detailed explanation goes here

fprintf('Importing %s\n', input_excel);
network_data = pfa_load_datasource(input_excel);
data_types = fieldnames(network_data);

for data_type_i = 1:length(data_types)
    
    data_type = data_types{data_type_i};
    
    % import network data
    [new_elements, updated_elements] = ...
        pfa_import_network_elements(pfa_network, network_data.(data_type), data_type);
    modified_files_count = new_elements.size() + updated_elements.size();
    if modified_files_count > 0
        fprintf('    %i network elements modified (%i added, %i updated)\n', ...
            modified_files_count, new_elements.size(), updated_elements.size());
    end
    
    % TODO:
    % import area data
    % import model DB
end

% update Model DB in PFA
pfa_update_model_db(pfa_network);
% update network in PFA
pfa_update_network(pfa_network);

end
