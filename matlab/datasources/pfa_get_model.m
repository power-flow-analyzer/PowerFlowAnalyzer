function [ model_id ] = pfa_get_model( jnetwork, data_type, data )
%GET_MODEL Summary of this function goes here
%   Detailed explanation goes here

model_id = {};
data_type = upper(data_type);

switch data_type
    case 'BUS'
        model_id = 'bus.unknown';
    case 'BRANCH'
        model_id = 'branch.unknown';
    case 'GENERATOR'
        model_id = 'generator.unknown';
    case 'TRANSFORMER'
        model_id = 'branch.transformer.unknown';
end

end