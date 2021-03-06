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
function [ pfa_area ] = pfa_import_area( pfa_network, area_data )
%IMPORT_AREA Summary of this function goes here
%   Detailed explanation goes here

% create new PFA object for input data
pfa_area = net.ee.pfanalyzer.model.NetworkElement(pfa_network);

%% Create outline with coordinates
joutline = net.ee.pfanalyzer.model.data.ModelData();
joutline.setID(area_data.ID);
joutline.setLabel(area_data.NAME);
joutline.getParameter().add(createParameter('CSV_DATA_FILE', area_data.OUTLINE_FILE));
pfa_network.getModelDB().getOutlineClass().getModel().add(joutline);
joutline.setParent(pfa_network.getModelDB().getOutlineClass());
pfa_network.getModelDB().fireElementChanged(...
    net.ee.pfanalyzer.model.DatabaseChangeEvent(0, joutline)); % 0 means ADDED

%% Register outline in area
pfa_area.setParameter('OUTLINE', area_data.ID);

end

function [jparam] = createParameter(id, value)
jparam = net.ee.pfanalyzer.model.data.NetworkParameter;
jparam.setID(id);
jparam.setValue(value);
end
