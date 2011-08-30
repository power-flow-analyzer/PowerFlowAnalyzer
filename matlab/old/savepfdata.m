function savepfdata( file, pfdata )
%SAVEPFDATA Summary of this function goes here
%   Detailed explanation goes here

baseMVA = pfdata.getVoltageLevel(); %#ok<NASGU>
bus = pfdata.getBusData(); %#ok<NASGU>
branch = pfdata.getBranchData(); %#ok<NASGU>
generator = pfdata.getGeneratorData(); %#ok<NASGU>
bus_names = pfdata.getLocationNames(); %#ok<NASGU>
bus_coordinates = pfdata.getCoordinateData(); %#ok<NASGU>

save(file, 'baseMVA', 'bus', 'branch', 'generator', 'bus_names', 'bus_coordinates');

msgbox('The file was saved successfully !', 'Info');

end

