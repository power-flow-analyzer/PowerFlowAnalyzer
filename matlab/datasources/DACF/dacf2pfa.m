function [ network_data ] = dacf2pfa( dacf_data )
%DACF2MATPOWER Summary of this function goes here
%   Detailed explanation goes here


network_data = struct();
network_data.bus = struct();
network_data.branch = struct();
network_data.gen = struct();
network_data.gencost = struct();
network_data.transformer = struct();

network_data.BASE_MVA = 100;


%% bus & generator
if isfield(dacf_data, 'nodes')
    % initialize bus data
    node_count = length(dacf_data.nodes.NODE);
    network_data.bus.BUS_I = zeros(node_count, 1);
    network_data.bus.NAME = cell(node_count, 1);
    network_data.bus.BUS_TYPE = zeros(node_count, 1);
    network_data.bus.PD = zeros(node_count, 1);
    network_data.bus.QD = zeros(node_count, 1);
    network_data.bus.GS = zeros(node_count, 1);
    network_data.bus.BS = zeros(node_count, 1);
    network_data.bus.BUS_AREA = ones(node_count, 1);
    network_data.bus.VM = ones(node_count, 1);
    network_data.bus.VA = zeros(node_count, 1);
    network_data.bus.BASE_KV = zeros(node_count, 1);
    network_data.bus.ZONE = ones(node_count, 1);
    network_data.bus.VMAX = zeros(node_count, 1);
    network_data.bus.VMIN = zeros(node_count, 1);
    % initialize generator data
    gen_count = length(find(~isnan(dacf_data.nodes.P_MIN)>0));
    network_data.gen.GEN_BUS = zeros(gen_count, 1);
    network_data.gen.GEN_BUS_NAME = cell(gen_count, 1);
    network_data.gen.PG = zeros(gen_count, 1);
    network_data.gen.QG = zeros(gen_count, 1);
    network_data.gen.QMAX = zeros(gen_count, 1);
    network_data.gen.QMIN = zeros(gen_count, 1);
    network_data.gen.VG = zeros(gen_count, 1);
    network_data.gen.MBASE = zeros(gen_count, 1);
    network_data.gen.GEN_STATUS = zeros(gen_count, 1);
    network_data.gen.PMAX = zeros(gen_count, 1);
    network_data.gen.PMIN = zeros(gen_count, 1);
    % 
    gen_i = 1;
    for node_i = 1:node_count
        network_data.bus.BUS_I(node_i) = node_i;
        network_data.bus.NAME{node_i} = dacf_data.nodes.NODE{node_i};
        network_data.bus.BUS_TYPE(node_i) = node_to_bus_type(dacf_data.nodes.NODE_TYPE(node_i));
        network_data.bus.PD(node_i) = dacf_data.nodes.P_LOAD(node_i);
        network_data.bus.QD(node_i) = dacf_data.nodes.Q_LOAD(node_i);
        network_data.bus.BASE_KV(node_i) = node_name_to_base_kv(dacf_data.nodes.NODE{node_i});
        network_data.bus.VMAX(node_i) = 1.1;
        network_data.bus.VMIN(node_i) = 0.9;

        if ~isnan(dacf_data.nodes.P_MIN(node_i))
            network_data.gen.GEN_BUS(gen_i) = node_i;
            network_data.gen.GEN_BUS_NAME{gen_i} = dacf_data.nodes.NODE{node_i};
            network_data.gen.PG(gen_i) = dacf_data.nodes.P_GEN(node_i);
            network_data.gen.QG(gen_i) = dacf_data.nodes.Q_GEN(node_i);
            network_data.gen.QMAX(gen_i) = dacf_data.nodes.Q_MAX(node_i);
            network_data.gen.QMIN(gen_i) = dacf_data.nodes.Q_MIN(node_i);
            if isnan(dacf_data.nodes.VOLTAGE_SET_POINT(node_i))
                network_data.gen.VG(gen_i) = 1;
            else
                network_data.gen.VG(gen_i) = ...
                    dacf_data.nodes.VOLTAGE_SET_POINT(node_i) / network_data.bus.BASE_KV(node_i);
            end
            % set voltage set point for bus
            network_data.bus.VM(node_i) = network_data.gen.VG(gen_i);
            network_data.gen.MBASE(gen_i) = dacf_data.nodes.P_NOM(node_i);
            network_data.gen.GEN_STATUS(gen_i) = ...
                abs(dacf_data.nodes.P_GEN(node_i)) > 0 || abs(dacf_data.nodes.Q_GEN(node_i)) > 0;
            network_data.gen.PMAX(gen_i) = dacf_data.nodes.P_MAX(node_i);
            network_data.gen.PMIN(gen_i) = dacf_data.nodes.P_MIN(node_i);
            gen_i = gen_i + 1;
        end
    end
    % Some values might not be given for all voltage levels
    network_data.gen.MBASE(find(isnan(network_data.gen.MBASE) == 1)) = 0;
    network_data.gen.QMAX(find(isnan(network_data.gen.QMAX) == 1)) = 100;
    network_data.gen.QMIN(find(isnan(network_data.gen.QMIN) == 1)) = -100;
end

%% branch
if isfield(dacf_data, 'lines')
    % initialize branch data
    branch_count = length(dacf_data.lines.LINE_CONNECTIVITY_NODE_1);
    network_data.branch.F_BUS_NAME = cell(branch_count, 1);
    network_data.branch.T_BUS_NAME = cell(branch_count, 1);
    network_data.branch.ORDER_CODE = cell(branch_count, 1);
    network_data.branch.F_BUS = zeros(branch_count, 1);
    network_data.branch.T_BUS = zeros(branch_count, 1);
    network_data.branch.BR_R = zeros(branch_count, 1);
    network_data.branch.BR_X = zeros(branch_count, 1);
    network_data.branch.BR_B = zeros(branch_count, 1);
    network_data.branch.RATE_A = zeros(branch_count, 1);
    network_data.branch.BR_STATUS = zeros(branch_count, 1);
    network_data.branch.ANGMIN = zeros(branch_count, 1);
    network_data.branch.ANGMAX = zeros(branch_count, 1);

    for branch_i = 1:branch_count
        % find bus nodes
        from_bus_name  = dacf_data.lines.LINE_CONNECTIVITY_NODE_1{branch_i};
        to_bus_name    = dacf_data.lines.LINE_CONNECTIVITY_NODE_2{branch_i};
        from_bus_index = find(strcmp(network_data.bus.NAME, from_bus_name));
        to_bus_index   = find(strcmp(network_data.bus.NAME, to_bus_name));
        network_data.branch.F_BUS_NAME{branch_i} = from_bus_name;
        network_data.branch.T_BUS_NAME{branch_i} = to_bus_name;
        network_data.branch.F_BUS(branch_i) = from_bus_index;
        network_data.branch.T_BUS(branch_i) = to_bus_index;
        network_data.branch.ORDER_CODE{branch_i} = ...
            dacf_data.lines.LINE_ORDER_CODE{branch_i};
        base_voltage = network_data.bus.BASE_KV(from_bus_index) * 1000;% change to V
        base_power = network_data.BASE_MVA * 1000000;                   % change to VA
        base_impedance=(base_voltage)^2/base_power;
        branch_r = dacf_data.lines.LINE_RESISTANCE (branch_i) / base_impedance;
        branch_x = dacf_data.lines.LINE_REACTANCE  (branch_i) / base_impedance;
        branch_b = dacf_data.lines.LINE_SUSCEPTANCE(branch_i) / base_impedance;
        network_data.branch.BR_R(branch_i) = branch_r;
        network_data.branch.BR_X(branch_i) = branch_x;
        network_data.branch.BR_B(branch_i) = branch_b;
        network_data.branch.RATE_A(branch_i) = ...
            network_data.bus.BASE_KV(from_bus_index) * dacf_data.lines.LINE_RATING(branch_i) / 1000;
        network_data.branch.BR_STATUS(branch_i) = ...
            line_to_branch_status(dacf_data.lines.LINE_STATUS(branch_i));
        network_data.branch.ANGMIN(branch_i) = -360;
        network_data.branch.ANGMAX(branch_i) = 360;
    end
    % Some values might not be given for all voltage levels
    network_data.branch.BR_B(find(isnan(network_data.branch.BR_B) == 1)) = 0;
    network_data.branch.RATE_A(find(isnan(network_data.branch.RATE_A) == 1)) = 0;
end

%% transformer
if isfield(dacf_data, 'transformers')
    % initialize transformer data
    trafo_count = length(dacf_data.transformers.NRW_TRANSFORMER_CONNECTIVITY_NODE);
    network_data.transformer.F_BUS_NAME = cell(trafo_count, 1);
    network_data.transformer.T_BUS_NAME = cell(trafo_count, 1);
    network_data.transformer.ORDER_CODE = cell(trafo_count, 1);
    network_data.transformer.F_BUS = zeros(trafo_count, 1);
    network_data.transformer.T_BUS = zeros(trafo_count, 1);
    network_data.transformer.BR_R = zeros(trafo_count, 1);
    network_data.transformer.BR_X = zeros(trafo_count, 1);
    network_data.transformer.BR_B = zeros(trafo_count, 1);
    network_data.transformer.RATE_A = zeros(trafo_count, 1);
    network_data.transformer.TAP = zeros(trafo_count, 1);
    network_data.transformer.SHIFT = zeros(trafo_count, 1);
    network_data.transformer.BR_STATUS = zeros(trafo_count, 1);
    network_data.transformer.ANGMIN = zeros(trafo_count, 1);
    network_data.transformer.ANGMAX = zeros(trafo_count, 1);

    for trafo_i = 1:trafo_count
        % find bus nodes
        from_bus_name  = dacf_data.transformers.NRW_TRANSFORMER_CONNECTIVITY_NODE{trafo_i};
        to_bus_name    = dacf_data.transformers.RW_TRANSFORMER_CONNECTIVITY_NODE{trafo_i};
        from_bus_index = find(strcmp(network_data.bus.NAME, from_bus_name));
        to_bus_index   = find(strcmp(network_data.bus.NAME, to_bus_name));
        network_data.transformer.F_BUS_NAME{trafo_i} = from_bus_name;
        network_data.transformer.T_BUS_NAME{trafo_i} = to_bus_name;
        network_data.transformer.F_BUS(trafo_i) = from_bus_index;
        network_data.transformer.T_BUS(trafo_i) = to_bus_index;
        network_data.transformer.ORDER_CODE{trafo_i} = ...
            dacf_data.transformers.TRANSFORMER_ORDER_CODE{trafo_i};
        base_voltage = network_data.bus.BASE_KV(from_bus_index) * 1000;% change to V
        base_power = network_data.BASE_MVA * 1000000;                   % change to VA
        base_impedance=(base_voltage)^2/base_power;
        branch_r = dacf_data.transformers.TRANSFORMER_RESISTANCE (trafo_i) / base_impedance;
        branch_x = dacf_data.transformers.TRANSFORMER_REACTANCE  (trafo_i) / base_impedance;
        branch_b = dacf_data.transformers.TRANSFORMER_SUSCEPTANCE(trafo_i) / base_impedance;
        network_data.transformer.BR_R(trafo_i) = branch_r;
        network_data.transformer.BR_X(trafo_i) = branch_x;
        network_data.transformer.BR_B(trafo_i) = branch_b;
        network_data.transformer.RATE_A(trafo_i) = dacf_data.transformers.TRANSFORMER_SNOM(trafo_i);
        network_data.transformer.BR_STATUS(trafo_i) = ...
            line_to_branch_status(dacf_data.transformers.TRANSFORMER_STATUS(trafo_i));
        network_data.transformer.ANGMIN(trafo_i) = -360;
        network_data.transformer.ANGMAX(trafo_i) = 360;
    end

    %% transformer regulation
    trafo_regulation_count = length(dacf_data.transformers_regulation.LTC_TRANSFORMER_IDENTIFIER);

    for trafo_reg_i = 1:trafo_regulation_count
        trafo_id = dacf_data.transformers_regulation.LTC_TRANSFORMER_IDENTIFIER{trafo_reg_i};
        trafo_ref = strsplit(trafo_id, ' ');
        from_bus = find(strcmp(network_data.transformer.F_BUS_NAME, trafo_ref{1}));
        to_bus = find(strcmp(network_data.transformer.T_BUS_NAME, trafo_ref{2}));
        code = find(strcmp(network_data.transformer.ORDER_CODE, trafo_ref{3}));
        trafo_index = intersect(intersect(from_bus, to_bus), code);
        if length(trafo_index) == 1
            % TODO
        else
            warning('Transformer cannot be found: "%s". Ignoring regulation in row %i.', trafo_id, trafo_reg_i);
        end
    end

end

end

function [ bus_type ] = node_to_bus_type(node_type)
    switch node_type
        case 0
            bus_type = 1; % PQ;
        case 2
            bus_type = 2; % PV;
        case 3
            bus_type = 3; % REF;
        otherwise
            error('Unknown node type: %s', node_type);
    end
end

function [ base_kv ] = node_name_to_base_kv(node_name)
    voltage_level_code = str2double(node_name(length(node_name)-1));
    switch voltage_level_code
        case 0
            base_kv = 750;
        case 1
            base_kv = 400;%380;
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

function [ branch_status ] = line_to_branch_status(line_status)
    switch line_status
        case 0 % Normal line or cable
            branch_status = 1;
        case 1 % Equivalent line
            branch_status = 1;            
        case 2 % Busbar coupler
            branch_status = 1;            
        case 7 % Busbar coupler
            branch_status = 0;            
        case 8 % Normal line or cable
            branch_status = 0;            
        case 9 % Equivalent line
            branch_status = 0;
        otherwise
            error('Unknown line status code: %i', line_status);
    end
end