function [ output_data ] = pfa_datasource_entries( input_data )
%PFA_DATASOURCE_ENTRIES Summary of this function goes here
%   Detailed explanation goes here

parameters = fieldnames(input_data);
if isempty(parameters)
    return;
end

% length of first column will be taken as element count
element_count = length(input_data.(parameters{1}));
for element_i = 1:element_count
    for param_i = 1:length(parameters)
        param = parameters{param_i};        
        output_data(element_i).(param) = input_data.(param)(element_i);
    end
end

end

