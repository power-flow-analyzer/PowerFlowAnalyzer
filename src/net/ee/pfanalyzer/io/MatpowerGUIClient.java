/*******************************************************************************
 * Copyright 2019 Markus Gronau
 * 
 * This file is part of PowerFlowAnalyzer.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.ee.pfanalyzer.io;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

import net.ee.pfanalyzer.model.Network;


public class MatpowerGUIClient implements IConnectionConstants {

	public MatpowerGUIClient() {
	}
	
	public boolean isServerRunning() {
		try {
			InetAddress localhost = InetAddress.getLocalHost();
			Socket socket = new Socket(localhost.getHostName(), 1412);
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(SERVER_STATUS_CONNECTION);
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			boolean isRunning = isServerMessageOK(in);
			out.close();
			in.close();
			return isRunning;
		} catch(ConnectException e) {
			// do nothing, server is not running
		} catch(IOException e) {
			System.err.println("Could not connect to MatpowerGUI");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println("Could not connect to MatpowerGUI");
			e.printStackTrace();
		}
		return false;
	}
	
	public void setWorkingDirectory(String workingDirectory) {
		// create data map
		DataMap dataMap = new DataMap(SET_WORKING_DIR_CONNECTION);
		dataMap.put(WORKING_DIR_DATA_FIELD, workingDirectory);
//		dataMap.put(SIGNAL_TO_USER_DATA_FIELD, signalToUser);
		// check data
		dataMap.checkValues();
		// send data map to server
		sendData(dataMap);
	}
	
	public void setProgress(double caseID, String text, int progress) {
		// send request to server
		sendRequest(POWER_FLOW_CANCELED_CONNECTION);
	}
	
	public void cancelPowerFlow() {
		// send request to server
		sendRequest(POWER_FLOW_CANCELED_CONNECTION);
	}
	
	public void showViewer() {
		// send request to server
		sendRequest(SHOW_VIEWER_CONNECTION);
	}
	
	public void closeViewer() {
		// send request to server
		sendRequest(CLOSE_VIEWER_CONNECTION);
	}
	
	private void sendRequest(String connectionType) {
		sendData(connectionType, null);
	}
	
	private void sendData(DataMap dataMap) {
		sendData(dataMap.getConnectionType(), dataMap);
	}
	
	public void createNetwork(Network network) {
		sendNetwork(network, IMPORT_TYPE_NEW_CASE_DATA_VALUE);
	}
	
	public void updateNetwork(Network network) {
		sendNetwork(network, IMPORT_TYPE_REPLACE_CASE_DATA_VALUE);
	}
	
	private void sendNetwork(Network network, String importType) {
		// create data map
		DataMap dataMap = new DataMap(NETWORK_DATA_CONNECTION);
		dataMap.put(IMPORT_TYPE_DATA_FIELD, importType);
		dataMap.put(NETWORK_DATA_FIELD, network.toXML());
		// check data
		dataMap.checkValues();
		// send data map to server
		sendData(dataMap);
	}
	
	private void sendData(String connectionType, DataMap dataMap) {
		try {
			InetAddress localhost = InetAddress.getLocalHost();
			Socket socket = new Socket(localhost.getHostName(), 1412);
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(connectionType);
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			String message = getServerMessage(in);
			if(message == null || message.equals(SERVER_MESSAGE_OK)) {
				if(dataMap != null) {
					for (String dataField : dataMap.keySet())
						transferData(out, in, dataField, dataMap.get(dataField));
				}
			} else
				System.out.println("MatpowerGUI reported an error: " + message);
			out.close();
			in.close();
		} catch(ConnectException e) {
			System.out.println("MatpowerGUI is not running");
		} catch(IOException e) {
			System.err.println("Could not connect to MatpowerGUI");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println("Could not connect to MatpowerGUI");
			e.printStackTrace();
		}
	}
	
	private String getServerMessage(ObjectInputStream in) throws IOException, ClassNotFoundException {
		return (String) in.readObject();
	}
	
	private boolean isServerMessageOK(ObjectInputStream in) throws IOException, ClassNotFoundException {
		String message = getServerMessage(in);
		return message != null && message.equals(SERVER_MESSAGE_OK);
	}
	
	private void transferData(ObjectOutputStream out, ObjectInputStream in, String dataName, Object data) 
			throws IOException, ClassNotFoundException {
		if(data == null)// do not send any "null" values
			return;
		if(dataName != null)
			out.writeObject(dataName);
		out.writeObject(data);
		String message = getServerMessage(in);
		if(message == null || message.equals(SERVER_MESSAGE_OK) == false)
			System.err.println("Transfer to MatpowerGUI NOT successful: " + message);
	}
}
