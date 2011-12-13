function [  ] = add_flags_generator( jgenerator, genData )
%ADD_FLAGS_BUS Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

% do not show flags if generator is not running or if the network was not
% calculated yet
if abs(genData(PG)) < 1E-8 && abs(genData(QG)) < 1E-8
    return;
end

%% real power output
real_power_percentage = genData(PG) / genData(PMAX) * 100;
real_power_failure = genData(PG) > genData(PMAX) || genData(PG) < genData(PMIN);

jflag = net.ee.pfanalyzer.model.NetworkFlag('Real power Output');
jflag.setValue(genData(PG), 'PG');
jflag.addParameter('PG');
if genData(PG) > genData(PMAX)
    jflag.addParameter('PMAX');
elseif genData(PG) < genData(PMIN)
    jflag.addParameter('PMIN');
end
jflag.setWarning(real_power_percentage >= 95);
jflag.setFailure(real_power_failure);
jflag.setPercentage(real_power_percentage);
jgenerator.addFlag(jflag);

%% reactive power output
if(genData(QG) >= 0) % positive reactive power
    reactive_power_percentage = genData(QG) / genData(QMAX) * 100;
    reactive_power_failure = genData(QG) > genData(QMAX);
else % negative reactive power
    if genData(QMIN) < 0
        reactive_power_percentage = genData(QG) / genData(QMIN) * 100;
        reactive_power_failure = genData(QG) < genData(QMIN);
    else
        reactive_power_percentage = 101;
        reactive_power_failure = true;
    end
end

jflag = net.ee.pfanalyzer.model.NetworkFlag('Reactive power Output');
jflag.setValue(genData(QG), 'QG');
jflag.addParameter('QG');
if genData(QG) > genData(QMAX)
    jflag.addParameter('QMAX');
elseif genData(QG) < genData(QMIN)
    jflag.addParameter('QMIN');
end
jflag.setWarning(reactive_power_percentage >= 95);
jflag.setFailure(reactive_power_failure);
jflag.setPercentage(reactive_power_percentage);
jgenerator.addFlag(jflag);

end

