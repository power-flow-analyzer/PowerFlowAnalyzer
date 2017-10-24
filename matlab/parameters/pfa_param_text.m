function [ value ] = pfa_param_text( network_element,  parameter_name)
%pfa_param_text Returns the string (TEXT) value of this element's parameter
%   Detailed explanation goes here

value = char(network_element.getTextParameter(parameter_name, ''));

end

