function [ network ] = create_network( baseMVA, bus, branch, generator, bus_names, bus_coordinates )
%CREATEnetwork Creates a new container for power flow data used by the
%power flow viewer

checkjava();
network = net.ee.pfanalyzer.model.Network;

% if (exist('baseMVA','var') > 0)
%     network.setVoltageLevel(baseMVA);
%     if (exist('bus','var') > 0)
%         network.setBusData(bus);
%         if (exist('branch','var') > 0)
%             network.setBranchData(branch);
%             if (exist('generator','var') > 0)
%                 network.setGeneratorData(generator);
%                 if (exist('bus_names','var') > 0)
%                     network.setLocationNames(bus_names);
%                     if (exist('bus_coordinates','var') > 0)
%                         network.setCoordinateData(bus_coordinates);
%                     end
%                 end
%             end
%         end
%     end
% end

end

