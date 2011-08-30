function transfer_new_network( pfdata )
%TRANSFERPFDATA Summary of this function goes here
%   Detailed explanation goes here

client = net.ee.pfanalyzer.io.MatpowerGUIClient;
client.createNetwork(pfdata);

end

