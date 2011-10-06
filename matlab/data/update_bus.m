function [ ] = update_bus( jbus, busData )
%CREATE_bus Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

%% add parameters to bus object

if length(busData) >= BUS_TYPE 
    jbus.setParameter('BUS_TYPE', busData(BUS_TYPE));
end
if length(busData) >= PD 
    jbus.setParameter('PD', busData(PD));
end
if length(busData) >= QD 
    jbus.setParameter('QD', busData(QD));
end
if length(busData) >= GS 
    jbus.setParameter('GS', busData(GS));
end
if length(busData) >= BS 
    jbus.setParameter('BS', busData(BS));
end
if length(busData) >= BUS_AREA 
    jbus.setParameter('BUS_AREA', busData(BUS_AREA));
end
if length(busData) >= VM 
    jbus.setParameter('VM', busData(VM));
end
if length(busData) >= VA 
    jbus.setParameter('VA', busData(VA));
end
if length(busData) >= BASE_KV 
    jbus.setParameter('BASE_KV', busData(BASE_KV));
end
if length(busData) >= ZONE 
    jbus.setParameter('ZONE', busData(ZONE));
end
if length(busData) >= VMAX 
    jbus.setParameter('VMAX', busData(VMAX));
end
if length(busData) >= VMIN 
    jbus.setParameter('VMIN', busData(VMIN));
end
% the following parameters are only included in OPF output
if length(busData) >= LAM_P 
    jbus.setParameter('LAM_P', busData(LAM_P));
end
if length(busData) >= LAM_Q 
    jbus.setParameter('LAM_Q', busData(LAM_Q));
end
if length(busData) >= MU_VMAX 
    jbus.setParameter('MU_VMAX', busData(MU_VMAX));
end
if length(busData) >= MU_VMIN 
    jbus.setParameter('MU_VMIN', busData(MU_VMIN));
end

%% add flags

% remove old flags
jbus.clearFlags();

% add flags
add_flags_bus(jbus, busData);

end

