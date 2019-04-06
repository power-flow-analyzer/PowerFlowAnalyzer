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
function [ path ] = pfa_param_dir( network_element,  parameter_name )
%pfa_param_text Returns the directory of this element's parameter
%   If the value represented by this parameter is a file, its directory
%   will be returned. If the value denotes a directory, it will be returned.
%   Throws an error if the file/directory does not exist.

path = pfa_param_text(network_element, parameter_name);

% check if it exists
if isempty(path)
    error('Path must not be empty');
end
% check if path is a directory
if exist(path, 'dir')
    % do nothing, must be called before "exist(.., 'file)"
elseif exist(path, 'file') % ... if it is a file
    % set the file's directory as the simulation path
    [path, ~, ~] = fileparts(path);
else
    error('Path does not exist: %s', path);

end

