function [ running ] = ispfviewerrunning(  )
%ISPFVIEWERRUNNING Summary of this function goes here
%   Detailed explanation goes here

checkjava();
client = net.ee.pfanalyzer.io.MatpowerGUIClient;
running = client.isServerRunning();

end

