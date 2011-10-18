function [ ] = update_working_directory( )
%UPDATE_WORKING_DIRECTORY Summary of this function goes here
%   Detailed explanation goes here

client = net.ee.pfanalyzer.io.MatpowerGUIClient;
client.setWorkingDirectory(pwd);

end

