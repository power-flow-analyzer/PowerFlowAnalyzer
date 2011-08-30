
% check if viewer's main class can be found on the classpath
if exist('net.ee.pfanalyzer.PowerFlowAnalyzer', 'class') ~= 8
    disp('The PowerFlowAnalyzer cannot be found, probably it is not installed.')
    disp('If this is not the case it needs to be in Matlab`s classpath:')
    disp('    Type "edit classpath.txt" in Matlab`s console and append the following lines:')
    disp('        <path-to-MatpowerGUI>/java/pfviewer.jar')
    disp('        <path-to-MatpowerGUI>/java/fatcow-hosting-icons-2000.zip')
    disp('    You will need to restart Matlab afterwards.')
else
    % perform internal checks of Java runtime
    javachk('jvm');
    javachk('awt');
    javachk('swing');
end