function [ line_data ] = read_line( line_string )
%READ_BUS_DATA Summary of this function goes here
%   Detailed explanation goes here

    line_data = struct();
    line_data.line_connectivity_node_1 = strtrim(line_string(1:8));
    line_data.line_connectivity_node_2 = strtrim(line_string(10:17));
    line_data.line_order_code = line_string(19);
    line_data.line_status = str2double(line_string(21));
    line_data.line_resistance = str2double(line_string(23:28));
    line_data.line_reactance = str2double(line_string(30:35));
    line_data.line_susceptance = str2double(line_string(37:44));
    line_data.line_rating = str2double(line_string(46:51));
    line_data.line_identifier = strtrim(line_string(53:64));

end

