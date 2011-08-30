function [ jbus ] = create_bus( jnetwork, busData, index )
%CREATE_bus Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

%% create bus object
jbus = net.ee.pfanalyzer.model.Bus(jnetwork, index);

% add parameters to bus object
jbus.setParameter('BUS_I', busData(BUS_I));
jbus.setParameter('BUS_TYPE', busData(BUS_TYPE));
jbus.setParameter('PD', busData(PD));
jbus.setParameter('QD', busData(QD));
jbus.setParameter('GS', busData(GS));
jbus.setParameter('BS', busData(BS));
jbus.setParameter('BUS_AREA', busData(BUS_AREA));
jbus.setParameter('VM', busData(VM));
jbus.setParameter('VA', busData(VA));
jbus.setParameter('BASE_KV', busData(BASE_KV));
jbus.setParameter('ZONE', busData(ZONE));
jbus.setParameter('VMAX', busData(VMAX));
jbus.setParameter('VMIN', busData(VMIN));
% the following parameters are only included in OPF output
if length(busData) > LAM_P 
    jbus.setParameter('LAM_P', busData(LAM_P));
    jbus.setParameter('LAM_Q', busData(LAM_Q));
    jbus.setParameter('MU_VMAX', busData(MU_VMAX));
    jbus.setParameter('MU_VMIN', busData(MU_VMIN));
end

%% add flags

% voltage magnitude
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

