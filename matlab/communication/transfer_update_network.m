function transfer_update_network( pfdata )
%TRANSFERPFDATA Summary of this function goes here
%   Detailed explanation goes here

client = net.ee.pfanalyzer.io.MatpowerGUIClient;
client.updateNetwork(pfdata);

end

