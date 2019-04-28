%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Copyright 2019 Markus Gronau
% 
% This file is part of PowerFlowAnalyzer.
% 
% Licensed under the Apache License, Version 2.0 (the "License");
% you may not use this file except in compliance with the License.
% You may obtain a copy of the License at
% 
%     http://www.apache.org/licenses/LICENSE-2.0
% 
% Unless required by applicable law or agreed to in writing, software
% distributed under the License is distributed on an "AS IS" BASIS,
% WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
% See the License for the specific language governing permissions and
% limitations under the License.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
function [ config ] = pfa_params2struct( pfa_element )
%PFA_CREATE_CONFIG Summary of this function goes here
%   Detailed explanation goes here

config = struct();

jnetwork_params = pfa_element.getParameterList();
for param_i = 0:jnetwork_params.size() - 1
    jparam = jnetwork_params.get(param_i);
    param_name = char(jparam.getID()); % was UPPER before
%    if strfind(upper(param_name), 'CONFIG.') == 1
%        % parameter name starts with prefix "CONFIG."
%        % remove prefix to prevent "config.CONFIG..." in scripts
%        param_name = param_name(8:end);
%    else
%        % ignore network parameters not starting with prefix "CONFIG."
%        continue;
%    end
%     param_name = matlab.lang.makeValidName(param_name);
    param_value = char(jparam.getValue());
    param_path = strsplit(param_name, '.');
    param_path = matlab.lang.makeValidName(param_path);
    switch numel(param_path)
        case 1
            config.(param_path{1}) = param_value;
        case 2
            config.(param_path{1}).(param_path{2}) = param_value;
        case 3
            config.(param_path{1}).(param_path{2}).(param_path{3}) = param_value;
        case 4
            config.(param_path{1}).(param_path{2}).(param_path{3}).(param_path{4}) = param_value;
        case 5
            config.(param_path{1}).(param_path{2}).(param_path{3}).(param_path{4}).(param_path{5}) = param_value;
        otherwise
            error('Parameter name contains to many levels: "%s"', param_name);
    end
%     jparam.getType()
    
%     if numel(path) == 1
%         config.(path{1}) = param_value;
%     elseif numel(path) == 2
%         config.(path{1}).(path{2}).(path{3}) = param_value;
%     elseif numel(path) == 3
%         config.(path{1}).(path{2}).(path{3}) = param_value;
%     elseif numel(path) == 4
%         config.(path{1}).(path{2}).(path{3}).(path{4}) = param_value;
%     elseif numel(path) == 5
%         config.(path{1}).(path{2}).(path{3}).(path{4}).(path{5}) = param_value;
%     end
%     if jparam.getType() == 
%     config.(param_name) = param_value;
%     net.ee.pfanalyzer.model.data.NetworkParameterType.INTEGER.equals('integer')
end

end
