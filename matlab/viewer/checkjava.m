
% check if viewer's main class can be found on the classpath
if exist('net.ee.pfanalyzer.PowerFlowAnalyzer', 'class') ~= 8
    error('PowerFlowAnalyzer is not configured in Matlab, you need to run "pfa_start_application.m" first!')
else
    % perform internal checks of Java runtime
    javachk('jvm');
    javachk('awt');
    javachk('swing');
end