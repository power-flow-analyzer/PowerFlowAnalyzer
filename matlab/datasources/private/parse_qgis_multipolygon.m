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
function [ coordinates ] = parse_qgis_multipolygon( line )

    coordinates = zeros(0, 2);
    coord_list = '';
    
    if strfind(line, '"MULTIPOLYGON (((')
        coord_list_start = strfind(line, '(((');
        if isempty(coord_list_start)
            return;
        end
        coord_list_end = strfind(line, ')))"');
        if isempty(coord_list_end)
            return;
        end
        coord_list = line(coord_list_start + 3 : coord_list_end - 1);
    elseif strfind(line, '"POLYGON ((')
        coord_list_start = strfind(line, '((');
        if isempty(coord_list_start)
            return;
        end
        coord_list_end = strfind(line, '))",');
        if isempty(coord_list_end)
            return;
        end
        coord_list = line(coord_list_start + 2 : coord_list_end - 1);
    end
    coord_pairs = textscan(coord_list, '%s', 'delimiter', ',');
    offset = 0;
    for i=1 : length(coord_pairs{1})
        pair = coord_pairs{1}(i);
        pair = pair{1};
        if ~isempty(strfind(pair, '('))
            coordinates(i + offset, 1) = NaN;
            coordinates(i + offset, 2) = NaN;
            offset = offset + 1;
        end
        pair = strrep(pair, ')', '');
        pair = strrep(pair, '(', '');
        coords = textscan(pair, '%s', 'delimiter', ' ');
        latitude = str2double(coords{1}(1)); % Breitengrad
        longitude = str2double(coords{1}(2)); % Lšngengrad
        coordinates(i + offset, 1) = latitude;
        coordinates(i + offset, 2) = longitude;
        if isnan(latitude) || isnan(longitude)
           disp(pair);
        end
    end
    
end
