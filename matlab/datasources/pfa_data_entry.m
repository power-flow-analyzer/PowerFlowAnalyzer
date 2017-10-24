function [ output_data ] = pfa_data_entry( input_data, index )
%PFA_DATA_ENTRY Summary of this function goes here
%   Detailed explanation goes here

if isstruct(input_data)
    parameters = fieldnames(input_data);
    output_data.index = index;

    for param_i = 1:length(parameters)
        param = parameters{param_i};
        value = input_data.(param)(index);
        if iscell(value)
            value = value{1};
        end
        output_data.(param) = value;
    end
else
    error('Must only be called on structured input data');
end

end

