function [ value ] = pfa_param_int( network_element,  parameter_name)
%pfa_param_int Returns the integer (INT) value of this element's parameter
%   Detailed explanation goes here

value = network_element.getIntParameter(parameter_name, NaN);

end

