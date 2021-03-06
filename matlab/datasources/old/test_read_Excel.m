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
function [  ] = test_read_Excel( input_excel )
%TEST_READ_EXCEL Summary of this function goes here
%   Detailed explanation goes here

% test_read_Excel('test_input.xlsx')

tic;

% input_excel = 'test_input.xlsx';

% name of the corresponding Excel sheet
node_table = 'nodes';

[~,dim] = xlsread_fast(input_excel,node_table, 'B:B');

% read bus data
[~, node_names] = xlsread_fast(input_excel,node_table,strcat('B2',':','B',num2str(length(dim))));
latitudes = xlsread_fast(input_excel,node_table,strcat('C2',':','C',num2str(length(dim))));
longitudes = xlsread_fast(input_excel,node_table,strcat('D2',':','D',num2str(length(dim))));
[~, countries] = xlsread_fast(input_excel,node_table,strcat('E2',':','E',num2str(length(dim))));
[~, TSO] = xlsread_fast(input_excel,node_table,strcat('F2',':','F',num2str(length(dim))));
voltages = xlsread_fast(input_excel,node_table,strcat('G2',':','G',num2str(length(dim))));
start_year = xlsread_fast(input_excel,node_table,strcat('H2',':','H',num2str(length(dim))));
end_year = xlsread_fast(input_excel,node_table,strcat('I2',':','I',num2str(length(dim))));
areas = xlsread_fast(input_excel,node_table,strcat('L2',':','L',num2str(length(dim))));

% disp(node_names);
% disp(latitudes);
% disp(longitudes);

toc;

end

