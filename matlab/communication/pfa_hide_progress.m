function pfa_hide_progress(  )
%PFA_HIDE_PROGRESS Summary of this function goes here
%   Detailed explanation goes here

client = net.ee.pfanalyzer.io.MatpowerGUIClient;
client.cancelPowerFlow();

end

