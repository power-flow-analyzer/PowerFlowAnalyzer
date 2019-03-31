function [ mpc ] = mp_check_mpc( mpc, correctErrors )
%MP_CHECK_MPC Summary of this function goes here
%   Detailed explanation goes here

%% Check branch data

if correctErrors
    % correct missing TAP data
    mpc.branch(find(isnan(mpc.branch(:, 9))), 9) = 1;
end


%% Check for NaN values in MatPower matrices

if any(any(isnan(mpc.bus)))
   error('Bus matrix contains NaN!'); 
end

if any(any(isnan(mpc.branch)))
   error('Branch matrix contains NaN!'); 
end

if any(any(isnan(mpc.gen)))
   error('Generator matrix contains NaN!'); 
end

if isfield(mpc, 'gencost') && any(any(isnan(mpc.gencost)))
   error('Generator cost matrix contains NaN!'); 
end

end

