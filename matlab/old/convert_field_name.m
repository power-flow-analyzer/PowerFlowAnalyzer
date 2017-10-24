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

