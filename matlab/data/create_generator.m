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
function [ jgenerator ] = create_generator( jnetwork, genData, genCostData )
%CREATE_generator Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

% create generator object
jgenerator = net.ee.pfanalyzer.model.Generator(jnetwork, 0);

%% add parameters to generator object
if length(genData) >= GEN_BUS 
    jgenerator.setParameter('GEN_BUS', genData(GEN_BUS));
end

% add all other parameters and flags
if exist('genCostData', 'var')
    update_generator(jgenerator, genData, genCostData);
else
    update_generator(jgenerator, genData);
end

end

