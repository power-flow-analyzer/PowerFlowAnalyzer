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
function [ ] = update_network( caze, jnetwork )
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
    mpc = caze;
end

% define Matpower constants
define_constants;
    
% collect data for visualisation
if exist('mpc.success', 'var')
    jnetwork.setParameter('SUCCESS', mpc.success);
end

% update busses
for i=1:length(mpc.bus(:,1))
    % get bus object from network via bus number
    jbus = jnetwork.getBus(mpc.bus(i,BUS_I));
    % update it
    update_bus(jbus, mpc.bus(i,:));
end

% update branches
for i=1:length(mpc.branch(:,1))
    jbranch = jnetwork.getBranches.get(i - 1);
    % update it
    update_branch(jbranch, mpc.branch(i,:));
end

% update generators
for i=1:length(mpc.gen(:,1))
    jgenerator = jnetwork.getGenerators.get(i - 1);
    % update it
    update_generator(jgenerator, mpc.gen(i,:), mpc.gencost(i,:));
end

end

