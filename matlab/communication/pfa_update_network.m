function pfa_update_network( pfa_network )
%PFA_UPDATE_NETWORK Summary of this function goes here
%   Detailed explanation goes here

% update network in PFA
client = net.ee.pfanalyzer.io.MatpowerGUIClient;
client.updateNetwork(pfa_network);

end

