package network.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import network.Network;

/**
 * This class 
 * 
 * @author LolaA
 *
 */
public class Client implements  Network{
	
	private String serverIP;
	private Socket socket;
	
	/**
	 * Client constructor
	 */
	public Client(String serverIP) {
		this.serverIP=serverIP;
	}
	
	
	/**
	 * Connection to the server
	 * 
	 * @return true if the connection was established
	 *  	   false if an error occurred
	 */
	public boolean connect() {
		try {
			this.socket= new Socket(serverIP,SERVERPORT);
			//Here: out and in streams
			return true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * Disconnect from server and release resources
	 */
	public void disconnect() {
		//Release input and output stream
		// I would define a command to be sent to the server
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
