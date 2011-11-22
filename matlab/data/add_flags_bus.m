function [  ] = add_flags_bus( jbus, busData )
%ADD_FLAGS_BUS Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

%% voltage magnitude
voltage_rel_to_one = busData(VM) - 1;
voltage_failure = false;
if voltage_rel_to_one >= 0
    voltage_percentage = abs(voltage_rel_to_one) / (busData(VMAX) - 1) * 100;
else
    voltage_percentage = abs(voltage_rel_to_one) / (1 - busData(VMIN)) * 100;
end
if busData(VM) < busData(VMIN) || busData(VM) > busData(VMAX)
    voltage_failure = true;
end
jflag = net.ee.pfanalyzer.model.NetworkFlag('Voltage magnitude');
jflag.addParameter('VM');
jflag.setWarning(voltage_percentage >= 95);
jflag.setFailure(voltage_failure);
jflag.setPercentage(voltage_percentage);
jbus.addFlag(jflag);


% % % %% voltage angle
% % % voltage_angle_min = -30;
% % % voltage_angle_max = 30;
% % % voltage_angle_failure = false;
% % % if busData(VA) >= 0
% % %     voltage_angle_percentage = busData(VA) / voltage_angle_max * 100;
% % % else
% % %     voltage_angle_percentage = busData(VA) / voltage_angle_min * 100;
% % % end
% % % if busData(VA) < voltage_angle_min || busData(VA) > voltage_angle_max
% % %     voltage_angle_failure = true;
% % % end
% % % jflag = net.ee.pfanalyzer.model.NetworkFlag('Voltage angle');
% % % jflag.addParameter('VA');
% % % jflag.setWarning(voltage_angle_percentage >= 95);
% % % jflag.setFailure(voltage_angle_failure);
% % % jflag.setPercentage(voltage_angle_percentage);
% % % jbus.addFlag(jflag);
end

