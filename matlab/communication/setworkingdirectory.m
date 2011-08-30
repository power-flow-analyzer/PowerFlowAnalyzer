function setworkingdirectory( path )
%SETWORKINGDIRECTORY Summary of this function goes here
%   Detailed explanation goes here

cd(path);

client = net.ee.pfanalyzer.io.MatpowerGUIClient;
client.setWorkingDirectory(pwd);

end

