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
function [ logging_config ] = logging( new_config )
%LOGGING Summary of this function goes here
%   Detailed explanation goes here

persistent internal_logging_config;

if exist('new_config', 'var')
    internal_logging_config = new_config;
elseif isempty(internal_logging_config)
    
    internal_logging_config.io.read = false;
    internal_logging_config.io.write = false;
    
    internal_logging_config.io.cache = false;
    
    internal_logging_config.reduceBuses = false;
    internal_logging_config.matpower.pf.verbose = false;
    internal_logging_config.matpower.pf.systemSummary = false;
    internal_logging_config.matpower.opf.verbose = false;
    internal_logging_config.matpower.opf.systemSummary = false;
    internal_logging_config.reduceBuses = false;
    internal_logging_config.summaryPF = false;
    internal_logging_config.summaryOPF = false;

    internal_logging_config.opf.info = false;
    internal_logging_config.opf.success = true;

    internal_logging_config.model.instCap.info = false;
    internal_logging_config.model.instCap.warning = true;
    internal_logging_config.model.injRES.info = false;
    internal_logging_config.model.curtailment.info = false;
    internal_logging_config.model.curtailment.warning = true;
end

logging_config = internal_logging_config;

end

