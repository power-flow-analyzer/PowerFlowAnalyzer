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
function [ jmarker ] = add_marker( jbus, modelID, name, latitude, longitude )
%ADD_LOAD Summary of this function goes here
%   Detailed explanation goes here

jnetwork = jbus.getNetwork();
bus_number = jbus.getBusNumber();

% create network element for marker
jmarker = create_marker(jnetwork);
jmarker.setModelID(modelID);
% % % jmarker.setParameter('BUS', bus_number);
jmarker.setParameter('PARENT_BUS', bus_number);
jmarker.setParameter('NAME', name);
jmarker.setParameter('LATITUDE', latitude);
jmarker.setParameter('LONGITUDE', longitude);

jnetwork.addElement(jmarker);

end

