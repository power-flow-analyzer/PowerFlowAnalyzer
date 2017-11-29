function [ series ] = pfa_datasource2timeseries( data, date_format_long, date_format_short )
%PFA_DATASOURCE2TIMESERIES Summary of this function goes here
%   Detailed explanation goes here

series = struct();
series.time = zeros(size(data.time, 1), 1);

for time_i = 1:size(data.time, 1)
   try
       series.time(time_i) = datenum(data.time(time_i), date_format_long);
   catch EXC
       switch EXC.identifier
           case 'MATLAB:datenum:ConvertDateString'
               series.time(time_i) = datenum(data.time(time_i), date_format_short);
           otherwise
               rethrow(EXC);
       end
   end
end

series.fields = data.fields(1, :);

series.values = nan(size(data.time, 1), size(data.fields, 2));
for field_i = 1:size(data.fields, 2)
    internal_field_name = data.fields{2, field_i};
    series.values(:, field_i) = data.(internal_field_name);
end

end

