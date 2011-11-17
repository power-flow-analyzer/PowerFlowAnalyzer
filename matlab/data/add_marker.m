function [ jmarker ] = add_marker( jbus, modelID, name, latitude, longitude )
%ADD_LOAD Summary of this function goes here
%   Detailed explanation goes here

jnetwork = jbus.getNetwork();
bus_number = jbus.getBusNumber();

% create network element for marker
jmarker = create_marker(jnetwork);
jmarker.setModelID(modelID);
% % % jmarker.setParameter('BUS', bus_number);
jmarker.setParameter('PARENT_BUS', bus_number);
jmarker.setParameter('NAME', name);
jmarker.setParameter('LATITUDE', latitude);
jmarker.setParameter('LONGITUDE', longitude);

jnetwork.addElement(jmarker);

end

