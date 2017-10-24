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

