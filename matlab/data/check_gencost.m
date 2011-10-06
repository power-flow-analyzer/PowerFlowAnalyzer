function [ ] = check_gencost( gencostData )
%CHECK_GENCOST Summary of this function goes here
%   Detailed explanation goes here

if numel(gencostData) == 0
    throw(MException('PowerFlowAnalyzer:NoGeneratorCostData', ...
   'Optimal power flow not possible without generator cost data!'));
end

end

