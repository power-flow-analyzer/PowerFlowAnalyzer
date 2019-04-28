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
function pfa_import_network_data( pfa_network, network_data )
%PFA_IMPORT_NETWORK_DATA Import network data into network
%   Detailed explanation goes here

% save('network_data.mat', 'network_data_debug');

pfa_network.setParameter('BASE_MVA', network_data.BASE_MVA);
import_elements(pfa_network, network_data.bus, 'BUS');
import_elements(pfa_network, network_data.branch, 'BRANCH');
import_elements(pfa_network, network_data.gen, 'GENERATOR');
import_elements(pfa_network, network_data.transformer, 'TRANSFORMER');
if isfield(network_data, 'SUCCESS')
    pfa_network.setParameter('SUCCESS', network_data.SUCCESS);
end

% update Model DB in PFA
pfa_update_model_db(pfa_network);
% update network in PFA
pfa_update_network(pfa_network);

end

function import_elements(pfa_network, data, data_type)
    [new_elements, updated_elements] = ...
        pfa_import_network_elements(pfa_network, data, data_type);
    modified_files_count = new_elements.size() + updated_elements.size();
    if modified_files_count > 0
        fprintf('    %i %s elements modified (%i added, %i updated)\n', ...
            modified_files_count, data_type, new_elements.size(), updated_elements.size());
    end
end