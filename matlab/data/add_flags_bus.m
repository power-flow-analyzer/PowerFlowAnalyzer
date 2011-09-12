function [  ] = add_flags_bus( jbus, busData )
%ADD_FLAGS_BUS Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

%% voltage magnitude
voltage_rel_to_one = busData(VM) - 1;
voltage_failure = false;
if voltage_rel_to_one >= 0
    voltage_percentage = abs(voltage_rel_to_one) / (busData(VMAX) - 1) * 100;
else
    voltage_percentage = abs(voltage_rel_to_one) / (1 - busData(VMIN)) * 100;
end
if busData(VM) < busData(VMIN) || busData(VM) > busData(VMAX)
    voltage_failure = true;
end
jflag = net.ee.pfanalyzer.model.NetworkFlag('Voltage magnitude (p.u.)');
jflag.addParameter('VM');
jflag.setWarning(false);
jflag.setFailure(voltage_failure);
jflag.setPercentage(voltage_percentage);
jbus.addFlag(jflag);

end

