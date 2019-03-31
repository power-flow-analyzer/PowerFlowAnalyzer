function [ dacf_data ] = load_ucte_def( input_file_location )
%READ_DACF Summary of this function goes here
%   Detailed explanation goes here

% create data structure for the output
dacf_data.nodes = struct();
dacf_data.lines = struct();
dacf_data.transformers = struct();
dacf_data.transformers_regulation = struct();
dacf_data.exchange_powers = struct();
dacf_data.nodes.COUNTRY = cell(0, 1);

% running variables
country = '';
mode = 'comments';
line_index = 0;

% read input file line per line
fid = fopen(input_file_location,'r');
while ~feof(fid)
    
    line_index = line_index + 1;
    
    %% Substations and energy injections
    try
        line = fgets(fid);
        
        % check if line is a comment/directive
        if any(strfind(line, '##'))
            if any(strfind(line, '##C'))
                mode = 'comments';
            else
                switch line(1:3)
                    case '##Z'
                        country = line(4:end-1);
                    case '##N'
                        mode = 'nodes';
                    case '##L'
                        mode = 'lines';
                    case '##T'
                        mode = 'transformers';
                    case '##R'
                        mode = 'transformer_regulation';
                    case '##E'
                        mode = 'exchange_power';
                    otherwise
                        mode = 'unknown';
                        error('Mode not implemented yet: %s', line);
                end
            end
%             % proceed with next line
%             continue;
        % line is not a comment but contains data
        else
            switch mode
                case 'nodes'
                    node_data = read_node(line);
                    dacf_data.nodes.COUNTRY{end+1, 1} = country;
                    dacf_data.nodes = copy_fields(dacf_data.nodes, node_data);
                case 'lines'
                    line_data = read_line(line);
                    dacf_data.lines = copy_fields(dacf_data.lines, line_data);
                case 'transformers'
                    transformer_data = read_transformer(line);
                    dacf_data.transformers = copy_fields(...
                        dacf_data.transformers, transformer_data);
                case 'transformer_regulation'
                    transformer_regulation_data = read_transformer_regulation(line);
                    dacf_data.transformers_regulation = copy_fields(...
                        dacf_data.transformers_regulation, transformer_regulation_data);
                case 'exchange_power'
                    echange_power_data = read_exchange_power(line);
                    dacf_data.exchange_powers = copy_fields(...
                        dacf_data.exchange_powers, echange_power_data);
                otherwise
                    error('Mode not implemented yet: %s', mode);
            end
        end
    catch NE
        fprintf('Error while reading %s data in line %i: %s\n', mode, line_index, line);
%         disp(NE.message);
%         disp(NE.stack);
        rethrow(NE);
    end

end
% close file handle - not that important
fclose(fid);

end

% copy all fields from new entry to end of data structure
function [ data_struct ] = copy_fields(data_struct, new_entry)
    all_fields = fieldnames(new_entry);
    for field_name_i = 1:length(all_fields)
        source_field_name = all_fields{field_name_i};
        target_field_name = upper(source_field_name);
        field_data = new_entry.(source_field_name);
        if isnumeric(field_data) % also accepts NaNs
            if ~isfield(data_struct, target_field_name)
                data_struct.(target_field_name) = nan(0, 1);
            end
            data_struct.(target_field_name)(end+1, 1) = field_data;
        else
            if ~isfield(data_struct, target_field_name)
                data_struct.(target_field_name) = cell(0, 1);
            end
            data_struct.(target_field_name){end+1, 1} = field_data;
        end
    end
end