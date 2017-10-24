function [ parameter ] = createparameter( scenario, parameter_ID, label, parameterType )
%CREATEPARAMETER Summary of this function goes here
%   Detailed explanation goes here

% check if parameter 'parameterType' was set
% and use default value if necessary
if (exist('parameterType','var') == false)
   parameterType = 'list'; % default value
end

% create parameter
parameter = net.ee.pfanalyzer.model.scenario.ScenarioParameter(parameter_ID, label, parameterType);
scenario.addParameter(parameter);

end

