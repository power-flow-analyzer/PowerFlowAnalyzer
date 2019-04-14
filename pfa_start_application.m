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

% add PFA's Java binaries to class path of Matlab
javaaddpath([pwd '/java/PowerFlowAnalyzer.jar']);
javaaddpath([pwd '/java/miglayout-3.7-swing.jar']);
javaaddpath([pwd '/java/fatcow-hosting-icons-2000.zip']);

% add PFA's Matlab files to search path of Matlab
addpath([pwd '/matlab']);
addpath([pwd '/matlab/communication']);
addpath([pwd '/matlab/data']);
addpath([pwd '/matlab/datasources']);
addpath([pwd '/matlab/datasources/UCTE_DEF']);
addpath([pwd '/matlab/matpower']);
addpath([pwd '/matlab/parameters']);
addpath([pwd '/matlab/viewer']);
addpath([pwd '/matpower']);

% add "scripts" folder to search path of Matlab;
% points dynamically to subfolder of current directory in Matlab, thus 
% overwriting library functions above. Directory must not exist in
% user directory after all.
addpath('scripts');

% start GUI of PFA application
PowerFlowAnalyzer_MAIN
