function [ value ] = pfa_param( network_element,  parameter_name)
%pfa_param Returns the DOUBLE value of this element's parameter
%   Detailed explanation goes here

value = network_element.getDoubleParameter(parameter_name, NaN);

end

