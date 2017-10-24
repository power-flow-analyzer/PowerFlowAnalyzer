function [ numericData, textData, rawData ] = xlsread_fast( file, sheet, range )
%READ_EXCEL Summary of this function goes here
%   Detailed explanation goes here

% Source File
source_file = fullfile(file);
[source_path, source_file_name, source_file_ext] = fileparts(source_file);
sourceFileInfo = dir(source_file);
if isempty(sourceFileInfo)
    error('Input file cannot be found: %s', source_file);
end

% File Cache
cache_file = fullfile(source_path, strcat(source_file_name, source_file_ext, '.mat'));
cachedFileInfo = dir(cache_file);
sheetField = strrep(sheet, ' ', '_');
rangeField = strcat('Range_', strrep(range, ':', '_'));

cache_outdated = true;
cache_incomplete = true;
if ~isempty(cachedFileInfo)
    data = load(cache_file);
    data = data.data;
    if data.date == sourceFileInfo.datenum
        cache_outdated = false;
        if isfield(data, sheetField) && isfield(data.(sheetField), rangeField)
            cache_incomplete = false;
            numericData = data.(sheetField).(rangeField).num;
            textData = data.(sheetField).(rangeField).txt;
            if nargout > 2
                rawData = data.(sheetField).(rangeField).raw;
            end
%             fprintf('Reading cached data: %s.%s.%s\n', source_file, sheet, rangeField);
        end
    else
        % delete variable, would be reused otherwise
        clear data
    end
end

if cache_outdated || cache_incomplete
    [numericData, textData, rawData] = xlsread(file, sheet, range);
    data.date = sourceFileInfo.datenum;
    data.(sheetField).(rangeField).num = numericData;
    data.(sheetField).(rangeField).txt = textData;
    if nargout > 2
        data.(sheetField).(rangeField).raw = rawData;
    end
    if cache_outdated
        % overwrite existing file
        save(cache_file, 'data');
    elseif cache_incomplete
        % append cache entry to existing file
        save(cache_file, 'data', '-append');
    end
%     fprintf('Reading Excel: %s.%s.%s\n', source_file, sheet, range);
%     fprintf('Caching data: %s.%s.%s\n', cache_file, sheetField, rangeField);
end

end
