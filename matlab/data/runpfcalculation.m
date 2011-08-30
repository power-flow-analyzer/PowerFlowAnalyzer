function [  ] = runpfcalculation( pfdata )
%RUNPFCALCULATION Summary of this function goes here
%   Detailed explanation goes here

% options for Matpower
mpopt = mpoption('VERBOSE', 0, 'OUT_ALL', 0);
% structure containing case data for Matpower
mpc.version = '2';
mpc.baseMVA = pfdata.getVoltageLevel();
mpc.bus     = pfdata.getBusData();
mpc.gen     = pfdata.getGeneratorData();
mpc.branch  = pfdata.getBranchData();

% calculate power flow
[baseMVA, bus, generator, branch,success,et]=runpf(mpc, mpopt);

% create flags
flags = createpfflags(bus, generator, branch);

% collect data for visualisation
bus_names = pfdata.getLocationNames();
coordinates = pfdata.getCoordinateData();
pfdata = create_network(baseMVA, bus, branch, generator, ...
    bus_names, coordinates, flags);

% transfer data to visualisation
transferpfdata(pfdata);

end

