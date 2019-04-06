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
function [ converted_text ] = convert_field_name( original_text )
%CONVERT_FIELD_NAME Summary of this function goes here
%   Detailed explanation goes here

%% REPLACED BY matlab.lang.makeValidName

%namelengthmax

% ASCII codes
% [48:57] numbers
% [65:90] uppercase letters
% [97:122] lowercase letters
% 95 underscore

% trim variable name to maximum allowable length
converted_text = original_text(1:min(length(original_text), namelengthmax));

% if length(original_text) <= namelengthmax
%     converted_text = original_text;
% else
%     converted_text = original_text(1:namelengthmax);
% end

field_name = 'R (resistan.) [?]';

% convert text to ascii codes
ascii_text = double(field_name);

if ismember(ascii_text(1), [48:57 65:90 97:122])
    
end

for char_i = 1: length(ascii_text)
    if ~ismember(ascii_text(char_i), [48:57 65:90 97:122])
        field_name(char_i) = '_';
    end
end


end

