function [ pfa_gen ] = pfa_import_generator( pfa_network, gen_data)
%PFA_IMPORT_GENERATOR Summary of this function goes here
%   Detailed explanation goes here

% create new PFA object for input data
pfa_gen = net.ee.pfanalyzer.model.Generator(pfa_network, 0);

% find generator bus
if ~isfield(gen_data, 'GEN_BUS')
    if isfield(gen_data, 'GEN_BUS_NAME')
        pfa_gen_bus = pfa_find_element(pfa_network, 'bus', 'NAME_INTERNAL', gen_data.GEN_BUS_NAME, true);
        pfa_set_param(pfa_gen, 'GEN_BUS', pfa_gen_bus.getBusNumber());
    else
        error('No GEN_BUS or GEN_BUS_NAME parameter set for generator');
    end
end

end

