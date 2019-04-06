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
function pfaui_delete_elements( pfa_network )
%PFAUI_DELETE_ELEMENTS Summary of this function goes here
%   Detailed explanation goes here

delete_nodes = pfa_param_boolean(pfa_network, 'DELETE_NODES', false);
delete_branches = pfa_param_boolean(pfa_network, 'DELETE_BRANCHES', false);
delete_transformers = pfa_param_boolean(pfa_network, 'DELETE_TRANSFORMERS', false);
delete_generators = pfa_param_boolean(pfa_network, 'DELETE_GENERATORS', false);
delete_custom_pattern = pfa_param_boolean(pfa_network, 'DELETE_CUSTOM_PATTERN', false);

if delete_custom_pattern
    model_pattern = pfa_param_text(pfa_network, 'MODEL_PATTERN');
    pfa_delete_elements(pfa_network, model_pattern);
end
if delete_generators
   pfa_delete_elements(pfa_network, 'generator'); 
end
if delete_transformers
    pfa_delete_elements(pfa_network, 'branch.transformer');
end
if delete_branches
   pfa_delete_elements(pfa_network, 'branch'); 
end
if delete_nodes
   pfa_delete_elements(pfa_network, 'bus'); 
end

pfa_update_network(pfa_network);

end

