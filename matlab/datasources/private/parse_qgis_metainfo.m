function [ meta_info ] = parse_qgis_metainfo( line )
%PARSE_QGIS_METAINFO Parses and returns the metainformation contained in
%the given line of text

% meta_info.empty = true;
meta_info = char(0);

coord_list_end = strfind(line, ')))",');
if isempty(coord_list_end)
    return;
end
polygon_appendix = line(coord_list_end + 5 : end); % position after )))",

metainfo_array = textscan(polygon_appendix, '%s', 'delimiter', ',');

if ~isempty(metainfo_array)
    meta_info = metainfo_array{1};
end


% shapeid = polygon_appendix(1 : strfind(polygon_appendix, ',') - 1);
% char_counter = 5 + 1;
% while strcmp(shapeid, ',')
%     polygon_appendix = line(coord_list_end + char_counter : end);
%     shapeid = polygon_appendix(1 : strfind(polygon_appendix, ',') - 1);
% %     meta_info.shape_id = shapeid;
% %     meta_info.empty = false;
% end

end

