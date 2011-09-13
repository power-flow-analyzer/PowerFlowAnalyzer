function [  ] = add_flags_generator( jgenerator, genData )
%ADD_FLAGS_BUS Summary of this function goes here
%   Detailed explanation goes here

% define Matpower constants
define_constants;

%% real power output
real_power_percentage = genData(PG) / genData(PMAX) * 100;
real_power_failure = genData(PG) > genData(PMAX) || genData(PG) < genData(PMIN);

jflag = net.ee.pfanalyzer.model.NetworkFlag('Real power Output');
jflag.addParameter('PG');
jflag.setWarning(real_power_percentage >= 90);
jflag.setFailure(real_power_failure);
jflag.setPercentage(real_power_percentage);
jgenerator.addFlag(jflag);

%% reactive power output
if(genData(QG) >= 0)
    reactive_power_percentage = genData(QG) / genData(QMAX) * 100;
    reactive_power_failure = genData(QG) > genData(QMAX);
else
    reactive_power_percentage = genData(QG) / genData(QMIN) * 100;
    reactive_power_failure = genData(QG) < genData(QMIN);
end

jflag = net.ee.pfanalyzer.model.NetworkFlag('Reactive power Output');
jflag.addParameter('QG');
jflag.setWarning(reactive_power_percentage >= 90);
jflag.setFailure(reactive_power_failure);
jflag.setPercentage(reactive_power_percentage);
jgenerator.addFlag(jflag);

end

