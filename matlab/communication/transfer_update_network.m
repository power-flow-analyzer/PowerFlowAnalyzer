function transfer_update_network( pfdata )
%TRANSFERPFDATA Summary of this function goes here
%   Detailed explanation goes here

warning('Deprecated: use function "pfa_update_network(pfa_network)" instead.');

client = net.ee.pfanalyzer.io.MatpowerGUIClient;
client.updateNetwork(pfdata);

end

