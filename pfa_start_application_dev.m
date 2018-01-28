% clear java; %#ok<CLSCR> % hide annoying warning

javaaddpath([pwd '/bin']);
javaaddpath([pwd '/java/miglayout-3.7-swing.jar']);
javaaddpath([pwd '/java/fatcow-hosting-icons-2000.zip']);

addpath([pwd '/matlab']);
addpath([pwd '/matlab/communication']);
addpath([pwd '/matlab/data']);
addpath([pwd '/matlab/datasources']);
addpath([pwd '/matlab/datasources/DACF']);
addpath([pwd '/matlab/matpower']);
addpath([pwd '/matlab/parameters']);
addpath([pwd '/matlab/viewer']);

% % points dynamically to subfolder of current user directory, thus 
% % overwriting library functions above. Directory must not exist in
% % user directory after all.
% addpath('scripts');

PowerFlowAnalyzer_MAIN