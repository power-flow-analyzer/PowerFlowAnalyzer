
client = net.ee.pfanalyzer.io.MatpowerGUIClient;
if client.isServerRunning()
    client.closeViewer();
end
clear client;