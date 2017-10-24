function [ value ] = pfa_param_boolean( ...
    network_element, parameter_name, default_value )
%PFA_PARAM_BOOL Summary of this function goes here
%   Detailed explanation goes here

jBoolean = network_element.getBooleanParameter(parameter_name);
if not(isempty(jBoolean))
    value = jBoolean.booleanValue();
else
    value = default_value;
end

end

