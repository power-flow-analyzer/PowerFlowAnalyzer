function [ base_kv ] = ucte_def_voltage_level_to_base_kv(voltage_level_code)
    switch voltage_level_code
        case 0
            base_kv = 750;
        case 1
            base_kv = 380;
        case 2
            base_kv = 220;
        case 3
            base_kv = 150;
        case 4
            base_kv = 120;
        case 5
            base_kv = 110;
        case 6
            base_kv = 70;
        case 7
            base_kv = 27;
        case 8
            base_kv = 330;
        case 9
            base_kv = 500;
        otherwise
            error('Unknown voltage level code: %i', voltage_level_code);
    end
end
