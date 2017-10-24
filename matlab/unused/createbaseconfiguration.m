function [ configuration ] = createbaseconfiguration( use_pfviewer )
%CREATEBASECONFIGURATION Summary of this function goes here
%   Detailed explanation goes here

% signals when the script execution should be stopped
configuration.stop_script = false;

% copy setting to configuration
configuration.use_pfviewer = use_pfviewer;

end

