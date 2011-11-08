function [ network ] = create_network( jnetwork )
%CREATEnetwork Creates a new container for power flow data used by the
%power flow viewer

checkjava();
if exist('jnetwork', 'var') == 0
    network = net.ee.pfanalyzer.model.Network;
else
    network = net.ee.pfanalyzer.model.Network(jnetwork.getCaseID());
end

end

