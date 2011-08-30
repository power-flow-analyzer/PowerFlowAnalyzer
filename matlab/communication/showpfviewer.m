function showpfviewer

client = net.ee.pfanalyzer.io.MatpowerGUIClient;
if client.isServerRunning()
    client.showViewer();
end

end