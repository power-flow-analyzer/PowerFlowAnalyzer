function [ jnetwork ] = matpower2network( caze, jparentNetwork )
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
if exist('jparentNetwork', 'var') == 0
    jnetwork = create_network();
else
    jnetwork = create_network(jparentNetwork);
end
jnetwork.setParameter('BASE_MVA', mpc.baseMVA);
if exist('mpc.success', 'var')
    jnetwork.setParameter('SUCCESS', mpc.success);
end

% create busses
for i=1:length(mpc.bus(:,1))
    % erstelle neuen Bus
    jbus = create_bus(jnetwork, mpc.bus(i,:));
    % füge neuen Bus dem Netzwerk hinzu
    jnetwork.addElement(jbus);
end
% create branches
for i=1:length(mpc.branch(:,1))
    jnetwork.addElement(create_branch(jnetwork, mpc.branch(i,:)));
end
% create generators
for i=1:length(mpc.gen(:,1))
    if exist('mpc.success', 'var')
        jgenerator = create_generator(jnetwork, mpc.gen(i,:), mpc.gencost(i,:));
    else
        jgenerator = create_generator(jnetwork, mpc.gen(i,:));
    end
    jnetwork.addElement(jgenerator);
end

end

