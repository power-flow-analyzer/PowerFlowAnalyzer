% clear java; %#ok<CLSCR> % hide annoying warning

javaaddpath([pwd '/java/PowerFlowAnalyzer_2.2.2.jar']);
javaaddpath([pwd '/java/miglayout-3.7-swing.jar']);
javaaddpath([pwd '/java/fatcow-hosting-icons-2000.zip']);

addpath([pwd '/matlab']);
addpath([pwd '/matlab/communication']);
addpath([pwd '/matlab/data']);
addpath([pwd '/matlab/datasources']);
addpath([pwd '/matlab/datasources/UCTE_DEF']);
addpath([pwd '/matlab/matpower']);
addpath([pwd '/matlab/parameters']);
addpath([pwd '/matlab/viewer']);

PowerFlowAnalyzer_MAIN