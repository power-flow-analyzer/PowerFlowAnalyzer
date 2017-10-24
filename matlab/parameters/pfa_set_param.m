function [  ] = pfa_set_param( jelement, param_name, param_value )
%pfa_set_param Stores the given value as a parameter in this network element
%   jelement a network element

jelement.setParameter(param_name, param_value);

end

