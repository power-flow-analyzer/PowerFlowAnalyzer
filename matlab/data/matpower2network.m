function [ jnetwork ] = matpower2network( caze )
%LOADPFDATA Summary of this function goes here
%   Detailed explanation goes here

% load the file contents
if ischar(caze) % (exist('file','var') > 0)
    oldDir = pwd; % save current working directory
    [pathstr, name, ~] = fileparts(caze);
    cd(pathstr); % change working directory to file's parent folder
    mpc = loadcase(name);
    cd(oldDir); % change back to old working directory
elseif isstruct(caze)
    mpc = caze; % for testing purposes
end

% define Matpower constants
define_constants;
    
% collect data for visualisation
jnetwork = create_network();
jnetwork.setParameter('BASE_MVA', mpc.baseMVA);

% create busses
for i=1:length(mpc.bus(:,1))
    % erstelle neuen Bus
    jbus = create_bus(jnetwork, mpc.bus(i,:), i - 1);
    % füge neuen Bus dem Netzwerk hinzu
    jnetwork.addElement(jbus);
end
% create branches
for i=1:length(mpc.branch(:,1))
    jnetwork.addElement(create_branch(jnetwork, mpc.branch(i,:), i - 1));
end
% create generators
for i=1:length(mpc.gen(:,1))
    jnetwork.addElement(create_generator(...
            jnetwork, mpc.gen(i,:), mpc.gencost(i,:), i - 1));
end
% 
% % transfer data to visualisation
% transfer_new_network(jnetwork);

end

