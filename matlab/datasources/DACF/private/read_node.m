function [ node_data ] = read_node( node_string )
%READ_BUS_DATA Summary of this function goes here
%   Detailed explanation goes here

    node_data = struct();
    node_data.node = strtrim(node_string(1:8));
    node_data.substation_name = strtrim(node_string(10:21));
    node_data.status = str2double(node_string(23));
    node_data.node_type = str2double(node_string(25));
    node_data.voltage_set_point = str2double(node_string(27:32));
    node_data.p_load = str2double(node_string(34:40));
    node_data.q_load = str2double(node_string(42:48));
    node_data.p_gen  = str2double(node_string(50:56));
    node_data.q_gen  = str2double(node_string(58:64));
    node_data.p_min  = str2double(node_string(66:72));
    node_data.p_max  = str2double(node_string(74:80));
    node_data.q_min  = str2double(node_string(82:88));
    node_data.q_max  = str2double(node_string(90:96));
    node_data.static  = str2double(node_string(98:102));
    node_data.p_nom  = str2double(node_string(104:110));
    node_data.sk  = str2double(node_string(112:118));
    node_data.x_r_ratio  = str2double(node_string(120:126));
    node_data.pp_type  = str2double(node_string(128));

end

