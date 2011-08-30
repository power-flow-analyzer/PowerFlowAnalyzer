function [ continue_script ] = showscenariowindow( scenario )
%SHOWSCENARIOWINDOW Summary of this function goes here
%   Detailed explanation goes here

dialog = net.ee.pfanalyzer.ui.dialog.ScenarioSelectionDialog(scenario);
if dialog.isOkPressed() == false
    disp('Script stopped by user')
    continue_script = false;
else
    continue_script = true;
end

end

