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
function savepfdata( file, pfdata )
%SAVEPFDATA Summary of this function goes here
%   Detailed explanation goes here

baseMVA = pfdata.getVoltageLevel(); %#ok<NASGU>
bus = pfdata.getBusData(); %#ok<NASGU>
branch = pfdata.getBranchData(); %#ok<NASGU>
generator = pfdata.getGeneratorData(); %#ok<NASGU>
bus_names = pfdata.getLocationNames(); %#ok<NASGU>
bus_coordinates = pfdata.getCoordinateData(); %#ok<NASGU>

save(file, 'baseMVA', 'bus', 'branch', 'generator', 'bus_names', 'bus_coordinates');

msgbox('The file was saved successfully !', 'Info');

end

