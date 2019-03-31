function [ transformer_data ] = read_exchange_power( exchange_power_string )
%READ_BUS_DATA Summary of this function goes here
%   Detailed explanation goes here

    transformer_data = struct();
    transformer_data.country1 = strtrim(exchange_power_string(1:2));
    transformer_data.country2 = str2double(exchange_power_string(4:5));
    transformer_data.scheduled_exchange = str2double(exchange_power_string(7:13));
    transformer_data.comments = str2double(exchange_power_string(15:26));

end

