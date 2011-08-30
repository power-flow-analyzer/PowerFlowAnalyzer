function [ network ] = create_network( )
%CREATEnetwork Creates a new container for power flow data used by the
%power flow viewer

checkjava();
network = net.ee.pfanalyzer.model.Network;

end

