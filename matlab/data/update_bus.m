function [ ] = update_bus( jbus, busData )
%CREATE_bus Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

%% add parameters to bus object

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

% remove old flags
jbus.clearFlags();

% add flags
add_flags_bus(jbus, busData);

end

