function [ series ] = pfa_datasource2timeseries( data )
%PFA_DATASOURCE2TIMESERIES Summary of this function goes here
%   Detailed explanation goes here

series = struct();
series.time = zeros(size(data.time, 1), 1);

for time_i = 1:size(data.time, 1)
   series.time(time_i) = time2num(data.time(time_i));
end

series.fields = data.fields(1, :);

series.values = nan(size(data.time, 1), size(data.fields, 2));
for field_i = 1:size(data.fields, 2)
    internal_field_name = data.fields{2, field_i};
    series.values(:, field_i) = data.(internal_field_name);
end

end

