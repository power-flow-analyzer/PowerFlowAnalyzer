function [ value ] = pfparameter( scenario, parameterName )
%PFPARAMETER Returns the value of the named parameter in the given power flow scenario
%   scenario - a power flow scenario
%   parameterName - the name of the parameter:
%       'INPUT_FILE' - case file (either Excel or Matlab file)

value = char(scenario.getParameterValue(parameterName));

end

