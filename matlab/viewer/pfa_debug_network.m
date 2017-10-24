function [  ] = pfa_debug_network( jnetwork )
%PFA_DEBUG Summary of this function goes here
%   Detailed explanation goes here

% export PFA network to workspace
assignin('base', 'pfa_network', jnetwork);

% hide progress bar
pfa_hide_progress();

end

