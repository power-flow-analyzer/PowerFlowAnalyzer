function load_network( file )
%LOADPFDATA Summary of this function goes here
%   Detailed explanation goes here

% load the file contents
load(file, 'baseMVA', 'bus', 'branch', 'generator', 'bus_names', 'bus_coordinates');

% define Matpower constants
define_constants;
% if ispfviewerrunning
    
% collect data for visualisation
jnetwork = create_network();
jnetwork.setParameter('BASE_MVA', baseMVA);

% create busses
for i=1:length(bus(:,1)) %#ok<*NODEF>
    % erstellen neuen Bus
    jbus = create_bus(jnetwork, bus(i,:), i - 1);
    real_bus = bus(i,BUS_I);
    % Realen Bus bestimmen
    if bus(i,BUS_I) > 10000 && bus(i,BUS_I) < 20000
        real_bus = floor((real_bus-10000)/10); % konventionell
    elseif bus(i,BUS_I) > 20000 && bus(i,BUS_I) < 30000
        real_bus = floor((real_bus-20000)/10); % Wind
    elseif bus(i,BUS_I) > 30000 && bus(i,BUS_I) < 40000
        real_bus = floor((real_bus-30000)/10); % PV
    end
    % Setze Bus-Namen
	if length(bus_names) >= real_bus
        jbus.setParameter('NAME', bus_names(real_bus));
	end
    % Setze Bus-Koordinaten
    if length(bus_coordinates) >= real_bus
        jbus.setParameter('LONGITUDE', bus_coordinates(real_bus, 1));
        jbus.setParameter('LATTITUDE', bus_coordinates(real_bus, 2));
    end
    % füge neuen Bus dem Netzwerk hinzu
    jnetwork.addElement(jbus);
end
% create branches
for i=1:length(branch(:,1))
    jnetwork.addElement(create_branch(jnetwork, branch(i,:), i - 1));
end
% create generators
for i=1:length(generator(:,1))
    jnetwork.addElement(create_generator(jnetwork, generator(i,:), i - 1));
end

% transfer data to visualisation
transferpfdata(jnetwork);

end

