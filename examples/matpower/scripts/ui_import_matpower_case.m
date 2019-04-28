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
function ui_import_matpower_case( pfa_network )
%UI_IMPORT_MATPOWER_CASE Summary of this function goes here
%   Detailed explanation goes here

% read configuration from network
config = pfa_params2struct(pfa_network);

% do the import
network_data = import_matpower_case(config);

%% Import data into PFA
% delete topology elements from PFA network
pfa_delete_elements(pfa_network, 'generator');
pfa_delete_elements(pfa_network, 'branch.transformer');
pfa_delete_elements(pfa_network, 'branch');
pfa_delete_elements(pfa_network, 'bus');
% update data in PFA network
pfa_import_network_data(pfa_network, network_data);

end

