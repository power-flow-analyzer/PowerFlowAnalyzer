%% Standalone Start Script for MatpowerGUI
% Technische Universität Berlin 
% Fachgebiet Energieversorgungsnetze und Integration Erneuerbarer Energien (SENSE)
% Chair of Sustainable Electric Networks and Sources of Energy

% Markus Gronau

clear java;
javaaddpath('c:/uni/Diplomarbeit/workspace_matlab/PowerFlowAnalyzer/bin');
startpfviewer

if exist('define_constants', 'file') ~= 2
    disp('Matpower not found')
    addpath('C:\Uni\Diplomarbeit\matlab\VPP\module\TSO\GTS_v4\matpower4.0');
    if exist('define_constants', 'file') == 2
        disp('Matpower was found after setting path')
    end
end
