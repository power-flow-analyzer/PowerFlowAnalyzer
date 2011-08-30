package net.ee.pfanalyzer.io;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import javax.swing.JOptionPane;

import net.ee.pfanalyzer.PowerFlowAnalyzer;
import net.ee.pfanalyzer.model.CaseSerializer;
import net.ee.pfanalyzer.model.data.NetworkData;


public class MatpowerGUIServer implements Runnable, IConnectionConstants {
	
	private static final long serialVersionUID = 1L;
	
	private PowerFlowAnalyzer application;
	private ServerSocket server;
	private boolean runServer = true;

	public MatpowerGUIServer(PowerFlowAnalyzer application) {
		this.application = application;
		try {
			server = new ServerSocket(1412);
			server.setSoTimeout(3000);// delay for stopping server
			new Thread(this).start();
		} catch(BindException e) {
			JOptionPane.showMessageDialog(null, "The interface between Matlab and Power Flow Analyzer " +
					"could not be established.\nPossibly another instance of Power Flow Analyzer is still running?\n" +
					"It is recommanded to close this instance and to reuse the existing one.", 
					"Error", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		System.out.println("Power Flow Analyzer started");
		while(runServer) {
			try {
				Socket socket = server.accept();
				new ConnectionHandler(socket);
			} catch(SocketTimeoutException e) {
				// do nothing, Server was stopped
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			server.close();
		} catch (IOException e) {
			System.err.println("Could not close server connection: " + e);
		}
		System.out.println("Power Flow Analyzer terminated");
	}
	
	public void stopServer() {
		runServer = false;
	}
	
	class ConnectionHandler implements Runnable {

		Socket socket;
		
		ConnectionHandler(Socket socket) {
			this.socket = socket;
			new Thread(this).start();
		}
		
		@Override
		public void run() {
			try {
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				String action = (String) in.readObject();
				if(action != null) {
					if(action.equals(SERVER_STATUS_CONNECTION)) {
						sendMessage(SERVER_MESSAGE_OK);
					} else if(action.equals(NETWORK_DATA_CONNECTION)) {
						ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
						out.writeObject(SERVER_MESSAGE_OK);
						NetworkImport netImport = receiveNetworkData(in, out);
						NetworkData network = netImport.network;
						if(network != null) {
							if(IMPORT_TYPE_NEW_CASE_DATA_VALUE.equals(netImport.importType))
								application.createNewCase(network);
							else if(IMPORT_TYPE_REPLACE_CASE_DATA_VALUE.equals(netImport.importType))
								application.updateNetwork(network);
						}
						out.close();
					} else if(action.equals(SET_WORKING_DIR_CONNECTION)) {
						ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
						out.writeObject(SERVER_MESSAGE_OK);
						String workingDir = receiveWorkingDirectory(in, out);
						if(workingDir != null)
							application.setWorkingDirectory(workingDir, true);
						out.close();
					} else if(action.equals(SHOW_VIEWER_CONNECTION)) {
						application.showViewer();
						sendMessage(SERVER_MESSAGE_OK);
					} else if(action.equals(POWER_FLOW_CANCELED_CONNECTION)) {
						application.cancelPowerFlow();
						sendMessage(SERVER_MESSAGE_OK);
					} else if(action.equals(CLOSE_VIEWER_CONNECTION)) {
						application.closeViewer();
						sendMessage(SERVER_MESSAGE_OK);
					} else {
						sendMessage("unknown connection: " + action);
					}
				}
				in.close();
			} catch (SocketException e) {
				System.err.println("Connection to Matlab closed unexpectedly: " + e);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		private void sendMessage(String message) throws IOException {
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(message);
			out.close();
		}
		
		private NetworkImport receiveNetworkData(ObjectInputStream in, ObjectOutputStream out) throws IOException {
			try {
				DataMap dataMap = new DataMap(NETWORK_DATA_CONNECTION);
				receiveData(in, out, dataMap);
				String data = (String) dataMap.get(NETWORK_DATA_FIELD);
				String importType = (String) dataMap.get(IMPORT_TYPE_DATA_FIELD);
				NetworkData network = CaseSerializer.readNetwork(data);
				out.writeObject(SERVER_MESSAGE_OK);
				NetworkImport netImport = new NetworkImport();
				netImport.network = network;
				netImport.importType = importType;
				return netImport;
			} catch (IllegalDataException e) {
				out.writeObject(e.getMessage() + "(server)");
			} catch (ClassCastException e) {
				out.writeObject("Invalid data: " + e.getMessage() + "(server)");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		class NetworkImport {
			NetworkData network;
			String importType;
		}
		
		private String receiveWorkingDirectory(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
			try {
				DataMap dataMap = new DataMap(SET_WORKING_DIR_CONNECTION);
				receiveData(in, out, dataMap);
				return (String) dataMap.get(WORKING_DIR_DATA_FIELD);
			} catch (IllegalDataException e) {
				out.writeObject(e.getMessage() + "(server)");
			} catch (ClassCastException e) {
				out.writeObject("Invalid data: " + e.getMessage() + "(server)");
			}
			return null;
		}
		
		private void receiveData(ObjectInputStream in, ObjectOutputStream out, DataMap dataMap) throws IOException, ClassNotFoundException {
			try {
				while(true) {
					String dataName = (String) in.readObject();
					Object data = in.readObject();
					dataMap.put(dataName, data);
					out.writeObject(SERVER_MESSAGE_OK);
				}
			} catch(EOFException e) {
				// do nothing, end of stream was reached
			}
			dataMap.checkValues();
		}
	}
}
