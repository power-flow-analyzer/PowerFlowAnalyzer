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
function [ data ] = xlsread2( input_args )
%XLSREAD2 Summary of this function goes here
%   Detailed explanation goes here

% http://soliton.ae.gatech.edu/classes/ae6382/samples/matlab/excel-2/actx_excel.html

tic

exl = actxserver('excel.application');
exlWkbk = exl.Workbooks;
exlFile = exlWkbk.Open([pwd '/test_input.xlsx']);
exlSheet1 = exlFile.Sheets.Item('nodes_new');

% allValues = exlSheet1.UsedRange.Value;

data.('nodes_new') = exlSheet1.UsedRange.Value;

save('excel_cache.mat', 'data');

% rows = exlSheet1.Rows.Count;
% disp(rows);


% robj = exlSheet1.Columns.End(4);
% numrows = robj.row;
% dat_range = ['A1:A' num2str(numrows)];
% % dat_range = 'A:A';
% fprintf('%i rows in range %s\n', numrows, dat_range);
% rngObj = exlSheet1.Range(dat_range);
% exlData = rngObj.Value;
% disp(exlData);

exlWkbk.Close
exl.Quit

toc

end

function startServ1
   exl = actxserver('excel.application');
   % Load data from an excel file
   % Get Workbook interface and open file
   exlWkbk = exl.Workbooks; 
   exlFile = exlWkbk.Open([docroot '/techdoc/matlab_external/examples/input_resp_data.xls']);
   % Get interface for Sheet1 and read data into range object
   exlSheet1 = exlFile.Sheets.Item('Sheet1');
   robj = exlSheet1.Columns.End(4);
   numrows = robj.row;
   dat_range = ['A1:G' num2str(numrows)]; 
   rngObj = exlSheet1.Range(dat_range);
   % Read data from excel range object into MATLAB cell array
   exlData = rngObj.Value; 
   exl.registerevent({'WorkbookBeforeClose',@close_event1});
end % startServ1

% Handle situation where user closes Excel data file
function close_event1(varargin)
% MATLAB does not currently support 
% pass by reference arguments for events
% so you cannot set Cancel argument to True
% Instead, just quit server and restart
   if exist('exl','var')
      exl.Quit;
      set(dispButton,'Value',0,...
         'String','Show Excel Data File')
   end
   startServ1
end % close_event1

%% Terminate Excel process
function deleteFig(src,evt)
   if exist('exl','var')
      exl.unregisterevent({'WorkbookBeforeClose',@close_event1});
      exlWkbk.Close
      exl.Quit
   end
   if exist('exl2','var')
      wb.Saved = true;
      exl2.unregisterevent({'WorkbookBeforeClose',@close_event2});
      exlWkbk2.Close
      exl2.Quit
   end
end % deleteFig       
