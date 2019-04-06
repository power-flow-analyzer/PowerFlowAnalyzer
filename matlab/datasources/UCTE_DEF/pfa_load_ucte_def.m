%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Copyright 2019 Markus Gronau
% 
% This file is part of PowerFlowAnalyzer.
% 
% Licensed under the Apache License, Version 2.0 (the "License");
% you may not use this file except in compliance with the License.
% You may obtain a copy of the License at
% 
%     http://www.apache.org/licenses/LICENSE-2.0
% 
% Unless required by applicable law or agreed to in writing, software
% distributed under the License is distributed on an "AS IS" BASIS,
% WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
% See the License for the specific language governing permissions and
% limitations under the License.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
function [ network_data ] = pfa_load_ucte_def( input_file_location )
%DACF2MATPOWER Summary of this function goes here
%   Detailed explanation goes here

uctedef_data = load_ucte_def(input_file_location);

aggregate_fixed_generation = false;

network_data = struct();
network_data.bus = struct();
network_data.branch = struct();
network_data.gen = struct();
network_data.gencost = struct();
network_data.transformer = struct();

network_data.BASE_MVA = 100;


%% bus & generator
if isfield(uctedef_data, 'nodes')
    % initialize bus data
    node_count = length(uctedef_data.nodes.NODE);
    network_data.bus.BUS_I = zeros(node_count, 1);
    network_data.bus.NAME = cell(node_count, 1);
    network_data.bus.IDENTIFIER = cell(node_count, 1);
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
    gen_count = length(find(uctedef_data.nodes.P_GEN ~= 0));
    network_data.gen.GEN_BUS = zeros(gen_count, 1);
    network_data.gen.GEN_BUS_NAME = cell(gen_count, 1);
    network_data.gen.IDENTIFIER = cell(gen_count, 1);
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
        network_data.bus.NAME{node_i} = uctedef_data.nodes.NODE{node_i};
        network_data.bus.IDENTIFIER{node_i} = uctedef_data.nodes.NODE{node_i};
        network_data.bus.BUS_TYPE(node_i) = node_to_bus_type(uctedef_data.nodes.NODE_TYPE(node_i));
        network_data.bus.PD(node_i) = uctedef_data.nodes.P_LOAD(node_i);
        network_data.bus.QD(node_i) = uctedef_data.nodes.Q_LOAD(node_i);
        network_data.bus.BASE_KV(node_i) = node_name_to_base_kv(uctedef_data.nodes.NODE{node_i});
        network_data.bus.VMAX(node_i) = 1.1;
        network_data.bus.VMIN(node_i) = 0.9;

        if uctedef_data.nodes.P_GEN(node_i) ~= 0
%             % set type of all PQ buses with a generator to PV type
%             if network_data.bus.BUS_TYPE(node_i) == 1
%                network_data.bus.BUS_TYPE(node_i) = 2; 
%             end
            PG = -uctedef_data.nodes.P_GEN(node_i);
            QG = -uctedef_data.nodes.Q_GEN(node_i);
            P_MAX = -uctedef_data.nodes.P_MAX(node_i);
            P_MIN = -uctedef_data.nodes.P_MIN(node_i);
            Q_MAX = -uctedef_data.nodes.Q_MAX(node_i);
            Q_MIN = -uctedef_data.nodes.Q_MIN(node_i);
            if isnan(P_MAX)
                P_MAX = PG;
            end
            if isnan(P_MIN)
                P_MIN = PG;
            end
            if isnan(Q_MAX)
                Q_MAX = QG;
            end
            if isnan(Q_MIN)
                Q_MIN = QG;
            end
            if aggregate_fixed_generation && ...
                    PG == P_MAX && PG == P_MIN && QG == Q_MAX && QG == Q_MIN
                network_data.bus.PD(node_i) = network_data.bus.PD(node_i) - PG;
                network_data.bus.QD(node_i) = network_data.bus.QD(node_i) - QG;
               continue; 
            end
            network_data.gen.GEN_BUS(gen_i) = node_i;
            network_data.gen.GEN_BUS_NAME{gen_i} = uctedef_data.nodes.NODE{node_i};
            network_data.gen.IDENTIFIER{gen_i} = ['GEN_' uctedef_data.nodes.NODE{node_i}];
            network_data.gen.PG(gen_i) = PG;
            network_data.gen.PMAX(gen_i) = P_MAX;
            network_data.gen.PMIN(gen_i) = P_MIN;
            network_data.gen.QG(gen_i) = QG;
            network_data.gen.QMAX(gen_i) = Q_MAX;
            network_data.gen.QMIN(gen_i) = Q_MIN;
            % set voltage set point: only used in OPF if option opf.use_vg = 1
            if isnan(uctedef_data.nodes.VOLTAGE_SET_POINT(node_i))
                network_data.gen.VG(gen_i) = 1;
            else
                network_data.gen.VG(gen_i) = ...
                    uctedef_data.nodes.VOLTAGE_SET_POINT(node_i) / network_data.bus.BASE_KV(node_i);
            end
            % set voltage set point for bus
%             network_data.bus.VM(node_i) = network_data.gen.VG(gen_i);
            network_data.gen.MBASE(gen_i) = 100;%uctedef_data.nodes.P_NOM(node_i);
            network_data.gen.GEN_STATUS(gen_i) = abs(PG) > 0 || abs(QG) > 0;
            gen_i = gen_i + 1;
        end
    end
    if aggregate_fixed_generation
        network_data.gen.GEN_BUS = network_data.gen.GEN_BUS(1:gen_i - 1); %#ok<UNRCH>
        network_data.gen.GEN_BUS_NAME = network_data.gen.GEN_BUS_NAME(1:gen_i - 1);
        network_data.gen.PG = network_data.gen.PG(1:gen_i - 1);
        network_data.gen.QG = network_data.gen.QG(1:gen_i - 1);
        network_data.gen.QMAX = network_data.gen.QMAX(1:gen_i - 1);
        network_data.gen.QMIN = network_data.gen.QMIN(1:gen_i - 1);
        network_data.gen.VG = network_data.gen.VG(1:gen_i - 1);
        network_data.gen.MBASE = network_data.gen.MBASE(1:gen_i - 1);
        network_data.gen.GEN_STATUS = network_data.gen.GEN_STATUS(1:gen_i - 1);
        network_data.gen.PMAX = network_data.gen.PMAX(1:gen_i - 1);
        network_data.gen.PMIN = network_data.gen.PMIN(1:gen_i - 1);
    end
    
    network_data.bus.NAME_INTERNAL = uctedef_data.nodes.NODE; % TODO:remove if not used anymore
    network_data.bus.SUBSTATION_NAME = uctedef_data.nodes.SUBSTATION_NAME;
end

%% branch
if isfield(uctedef_data, 'lines')
    % initialize branch data
    branch_count = length(uctedef_data.lines.LINE_CONNECTIVITY_NODE_1);
    network_data.branch.NAME = cell(branch_count, 1);
    network_data.branch.IDENTIFIER = cell(branch_count, 1);
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
        from_bus_name  = uctedef_data.lines.LINE_CONNECTIVITY_NODE_1{branch_i};
        to_bus_name    = uctedef_data.lines.LINE_CONNECTIVITY_NODE_2{branch_i};
        from_bus_index = find(strcmp(network_data.bus.NAME, from_bus_name));
        to_bus_index   = find(strcmp(network_data.bus.NAME, to_bus_name));
        network_data.branch.F_BUS_NAME{branch_i} = from_bus_name;
        network_data.branch.T_BUS_NAME{branch_i} = to_bus_name;
        network_data.branch.F_BUS(branch_i) = from_bus_index;
        network_data.branch.T_BUS(branch_i) = to_bus_index;
        if isnumeric(uctedef_data.lines.LINE_ORDER_CODE)
            network_data.branch.ORDER_CODE{branch_i} = ...
                uctedef_data.lines.LINE_ORDER_CODE(branch_i);
        else
            network_data.branch.ORDER_CODE{branch_i} = ...
                uctedef_data.lines.LINE_ORDER_CODE{branch_i};
        end
        network_data.branch.IDENTIFIER{branch_i} = get_identifier(...
            uctedef_data.lines.LINE_CONNECTIVITY_NODE_1{branch_i}, ...
            uctedef_data.lines.LINE_CONNECTIVITY_NODE_2{branch_i}, ...
            uctedef_data.lines.LINE_ORDER_CODE(branch_i));
        network_data.branch.NAME{branch_i} = uctedef_data.lines.LINE_IDENTIFIER{branch_i};
        base_voltage = network_data.bus.BASE_KV(from_bus_index) * 1000;% change to V
        base_power = network_data.BASE_MVA * 1000000;                  % change to VA
        base_impedance = (base_voltage) ^ 2 / base_power;
        branch_r = uctedef_data.lines.LINE_RESISTANCE (branch_i) / base_impedance;
        branch_x = uctedef_data.lines.LINE_REACTANCE  (branch_i) / base_impedance;
        % avoid a zero reactance (leads to infinity in admittance matrix)
        if branch_x == 0
            branch_x = 0.000001;
        end
        % susceptance is given in uS
        branch_b = uctedef_data.lines.LINE_SUSCEPTANCE(branch_i) * base_impedance / 1000000;
        if isnan(branch_b)
            branch_b = 0;
        end
        network_data.branch.BR_R(branch_i) = branch_r;
        network_data.branch.BR_X(branch_i) = branch_x;
        network_data.branch.BR_B(branch_i) = branch_b;
        % line rating in MVA: Snom = sqrt(3) * U * I
        LINE_RATING = sqrt(3) * network_data.bus.BASE_KV(from_bus_index) * ...
            uctedef_data.lines.LINE_RATING(branch_i) / 1000;
        if isnan(LINE_RATING)
            LINE_RATING = 0; % set infinite rating
        end
        network_data.branch.RATE_A(branch_i) = LINE_RATING;
        network_data.branch.BR_STATUS(branch_i) = ...
            line_to_branch_status(uctedef_data.lines.LINE_STATUS(branch_i));
        network_data.branch.ANGMIN(branch_i) = -360;
        network_data.branch.ANGMAX(branch_i) = 360;
    end
    
    network_data.branch.NAME_INTERNAL = uctedef_data.lines.LINE_IDENTIFIER;
end

%% transformer
if isfield(uctedef_data, 'transformers')
    % initialize transformer data
    trafo_count = length(uctedef_data.transformers.NRW_TRANSFORMER_CONNECTIVITY_NODE);
    network_data.transformer.NAME = cell(trafo_count, 1);
    network_data.transformer.IDENTIFIER = cell(trafo_count, 1);
    network_data.transformer.F_BUS_NAME = cell(trafo_count, 1);
    network_data.transformer.T_BUS_NAME = cell(trafo_count, 1);
    network_data.transformer.ORDER_CODE = cell(trafo_count, 1);
    network_data.transformer.F_BUS = zeros(trafo_count, 1);
    network_data.transformer.T_BUS = zeros(trafo_count, 1);
    network_data.transformer.BR_R = zeros(trafo_count, 1);
    network_data.transformer.BR_X = zeros(trafo_count, 1);
    network_data.transformer.BR_B = zeros(trafo_count, 1);
    network_data.transformer.RATE_A = zeros(trafo_count, 1);
    network_data.transformer.TAP = ones(trafo_count, 1);
    network_data.transformer.SHIFT = zeros(trafo_count, 1);
    network_data.transformer.BR_STATUS = zeros(trafo_count, 1);
    network_data.transformer.ANGMIN = zeros(trafo_count, 1);
    network_data.transformer.ANGMAX = zeros(trafo_count, 1);
%     network_data.transformer.TAP_COUNT = zeros(trafo_count, 1);
%     network_data.transformer.TAP_POSITION = zeros(trafo_count, 1);

    for trafo_i = 1:trafo_count
        % find bus nodes
        from_bus_name  = uctedef_data.transformers.NRW_TRANSFORMER_CONNECTIVITY_NODE{trafo_i};
        to_bus_name    = uctedef_data.transformers.RW_TRANSFORMER_CONNECTIVITY_NODE{trafo_i};
        from_bus_index = find(strcmp(network_data.bus.NAME, from_bus_name));
        to_bus_index   = find(strcmp(network_data.bus.NAME, to_bus_name));
        network_data.transformer.F_BUS_NAME{trafo_i} = from_bus_name;
        network_data.transformer.T_BUS_NAME{trafo_i} = to_bus_name;
        network_data.transformer.F_BUS(trafo_i) = from_bus_index;
        network_data.transformer.T_BUS(trafo_i) = to_bus_index;
        if isnumeric(uctedef_data.transformers.TRANSFORMER_ORDER_CODE)
            network_data.transformer.ORDER_CODE{trafo_i} = ...
                uctedef_data.transformers.TRANSFORMER_ORDER_CODE(trafo_i);
        else
            network_data.transformer.ORDER_CODE{trafo_i} = ...
                uctedef_data.transformers.TRANSFORMER_ORDER_CODE{trafo_i};
        end
        network_data.transformer.IDENTIFIER{trafo_i} = get_identifier(...
            uctedef_data.transformers.NRW_TRANSFORMER_CONNECTIVITY_NODE{trafo_i}, ...
            uctedef_data.transformers.RW_TRANSFORMER_CONNECTIVITY_NODE{trafo_i}, ...
            uctedef_data.transformers.TRANSFORMER_ORDER_CODE(trafo_i));
        network_data.transformer.NAME{trafo_i} = ...
            uctedef_data.transformers.TRANSFORMER_IDENTIFIER{trafo_i};
        base_voltage = network_data.bus.BASE_KV(from_bus_index) * 1000;% change to V
        base_power = network_data.BASE_MVA * 1000000;                  % change to VA
        base_impedance = (base_voltage) ^ 2 / base_power;
        branch_r = uctedef_data.transformers.TRANSFORMER_RESISTANCE (trafo_i) / base_impedance;
        branch_x = uctedef_data.transformers.TRANSFORMER_REACTANCE  (trafo_i) / base_impedance;
        % susceptance is given in uS
        branch_b = uctedef_data.transformers.TRANSFORMER_SUSCEPTANCE(trafo_i) * base_impedance / 1000000;
        if isnan(branch_b)
            branch_b = 0;
        end
        network_data.transformer.BR_R(trafo_i) = branch_r;
        network_data.transformer.BR_X(trafo_i) = branch_x;
        network_data.transformer.BR_B(trafo_i) = branch_b;
        network_data.transformer.RATE_A(trafo_i) = uctedef_data.transformers.TRANSFORMER_SNOM(trafo_i);
        % nominal voltage of busbars
        Un1 = network_data.bus.BASE_KV(from_bus_index);
        Un2 = network_data.bus.BASE_KV(to_bus_index);
        % rated voltage of transformer
        Ur1 = uctedef_data.transformers.RATED_VOLTAGE_1(trafo_i);
        Ur2 = uctedef_data.transformers.RATED_VOLTAGE_2(trafo_i);
        % tap ratio based on rated and nominal voltage
        tap_ratio = (Ur1 / Ur2) / (Un1 / Un2);
        network_data.transformer.TAP(trafo_i) = tap_ratio;
        network_data.transformer.BR_STATUS(trafo_i) = ...
            line_to_branch_status(uctedef_data.transformers.TRANSFORMER_STATUS(trafo_i));
        network_data.transformer.ANGMIN(trafo_i) = -360;
        network_data.transformer.ANGMAX(trafo_i) = 360;
    end
    
    network_data.transformer.NAME_INTERNAL = uctedef_data.transformers.TRANSFORMER_IDENTIFIER;

    %% transformer regulation
    if isfield(uctedef_data, 'transformers_regulation')
        trafo_regulation_count = length(uctedef_data.transformers_regulation.LTC_TRANSFORMER_IDENTIFIER);

        for trafo_reg_i = 1:trafo_regulation_count
            trafo_id = uctedef_data.transformers_regulation.LTC_TRANSFORMER_IDENTIFIER{trafo_reg_i};
            trafo_ref = strsplit(trafo_id, ' ');
            from_bus_name = trafo_ref{1};
            to_bus_name = trafo_ref{2};
            order_code = trafo_ref{3};
            from_bus = find(strcmp(network_data.transformer.F_BUS_NAME, from_bus_name));
            to_bus = find(strcmp(network_data.transformer.T_BUS_NAME, to_bus_name));
            code = find(strcmp(network_data.transformer.ORDER_CODE, order_code));
            trafo_index = intersect(intersect(from_bus, to_bus), code);
            if length(trafo_index) == 1
                % TODO
%                 network_data.transformer.TAP_POSITION(trafo_index) = ...
%                     uctedef_data.transformers_regulation.PHASE_REGULATION_CURRENT_TAP_POSITION(trafo_reg_i);
                from_bus_index = find(strcmp(network_data.bus.NAME, from_bus_name));
                to_bus_index = find(strcmp(network_data.bus.NAME, to_bus_name));
                % nominal voltage of busbars
                Un1 = network_data.bus.BASE_KV(from_bus_index);
                Un2 = network_data.bus.BASE_KV(to_bus_index);
                % rated voltage of transformer
                Ur1 = uctedef_data.transformers.RATED_VOLTAGE_1(trafo_index);
                Ur2 = uctedef_data.transformers.RATED_VOLTAGE_2(trafo_index);
                
                % voltage change per tap [%]
                du = uctedef_data.transformers_regulation.PHASE_REGULATION_VOLTAGE_CHANGE_PER_TAP(trafo_reg_i) / 100;
                % current tap position
                n = uctedef_data.transformers_regulation.PHASE_REGULATION_CURRENT_TAP_POSITION(trafo_reg_i);
                % total voltage change [%]
                DU = 1 + n * du;
                % regulated winding is on secondary side
                Ur2_DU = Ur2 * DU;
                % tap ratio based on rated and nominal voltage
                tap_ratio = (Ur1 / Ur2_DU) / (Un1 / Un2);
                network_data.transformer.TAP(trafo_index) = tap_ratio;
            else
                warning('Transformer cannot be found: "%s". Ignoring regulation in row %i.', trafo_id, trafo_reg_i);
            end
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
    base_kv = ucte_def_voltage_level_to_base_kv(voltage_level_code);
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

function [ identifier ] = get_identifier(part_1, part_2, part_3)
    if isnumeric(part_1)
        part_1 = num2str(part_1);
    end
    if isnumeric(part_2)
        part_2 = num2str(part_2);
    end
    if iscell(part_3)
        part_3 = part_3{1};
    end
    if isnumeric(part_3)
        part_3 = num2str(part_3);
    end
    identifier = strjoin({part_1, part_2, part_3});
end
