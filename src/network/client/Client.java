package network.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import network.Network;
import network.NetworkMessage;
import network.ProtocolException;
import pojos.Measurement;
import pojos.Patient;

public class Client implements Network {

	private String serverIP;
	private Socket socket = null;
	private ObjectOutputStream objectOutputStream = null;
	private ObjectInputStream objectInputStream = null;

	/**
	 * Client constructor
	 */
	public Client(String serverIP) {
		this.serverIP = serverIP;
	}
	
	

	/**
	 * Connection to the server
	 * 
	 * @return true if the connection was established false if an error occurred
	 */
	public boolean connect() {
		try {
			this.socket = new Socket(serverIP, SERVERPORT);

			objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
			objectInputStream = new ObjectInputStream(socket.getInputStream());

			return true;
		} catch (UnknownHostException e) {
			System.out.println("Unknown server");
			e.printStackTrace();
			releaseResources(objectInputStream, objectOutputStream, socket );
			return false;
		} catch (IOException e) {
			System.out.println("It was not possible to connect to the server.");
			e.printStackTrace();
			releaseResources(objectInputStream, objectOutputStream, socket );
			return false;
		}
	}

	/**
	 *  This would be the first message a client should sent to the server to "log in"
	 * @returns the patient with the measurements if the id and password are correct, null otherwise.
	 * @param patient A patient that should have DNI and password in it
	 * @throws  ProtocolException when there is an error.
	 */
	public Patient sendToServer ( Patient patient ) throws ProtocolException {

		NetworkMessage msg = new NetworkMessage( NetworkMessage.Protocol.PATIENT_LOGIN, patient);

		System.out.println("socket: "+ socket.getInetAddress());

		if ( objectOutputStream != null ) {
			try {
				objectOutputStream.writeObject( msg );
				objectOutputStream.flush();
				NetworkMessage answer = (NetworkMessage) objectInputStream.readObject();
				NetworkMessage.Protocol protocol = answer.getProtocol();
				if ( protocol == NetworkMessage.Protocol.LOGIN_ACCEPT){
					return answer.getPatient();
				} else if ( protocol == NetworkMessage.Protocol.LOGIN_DENY){
					return null;
				} else if (protocol == NetworkMessage.Protocol.ERROR) {
					throw new ProtocolException("There was an error on the server side.", ProtocolException.ErrorType.SERVERSIDE_ERROR);
				}
			} catch (ClassNotFoundException | IOException e) {
				throw new ProtocolException("The network didn't answer with the correct object", ProtocolException.ErrorType.CONNECTION_ERROR);
			}

		}
		return null;
	}

	/**
	 *  Sends the measurements to save in the database.
	 *  If the client hasn't logged in with the previous message there will be an error
	 * @param  measurements list of measurements to insert in the database
	 * @throws  ProtocolException when there is an error.
	 */
	public void sendToServer ( ArrayList<Measurement> measurements ) throws ProtocolException {
		NetworkMessage msg = new NetworkMessage( NetworkMessage.Protocol.PUSH_MEASUREMENT, measurements );
		try {
			objectOutputStream.writeObject( msg );
			NetworkMessage answer = ( NetworkMessage ) objectInputStream.readObject();
			NetworkMessage.Protocol protocol = answer.getProtocol();
			if ( protocol == NetworkMessage.Protocol.ACK ){
				//all good
				return;
			} else if ( protocol == NetworkMessage.Protocol.ERROR ){
				throw new ProtocolException("The network didn't answer with the correct object", ProtocolException.ErrorType.SERVERSIDE_ERROR);
			}
		} catch ( ClassNotFoundException | IOException e){
			throw new ProtocolException("The network didn't answer with the correct object", ProtocolException.ErrorType.CONNECTION_ERROR);
		}
	}


	/**
	 * Disconnect from server and release resources
	 */
	public void disconnect() {
		// Release input and output stream
		try {
			NetworkMessage msg = new NetworkMessage(NetworkMessage.Protocol.DISCONNECT);
			objectOutputStream.writeObject( msg );
		} catch ( IOException e){
			System.out.println("Disconnection error");
			e.printStackTrace();
		} finally {
			releaseResources(objectInputStream, objectOutputStream, socket);
		}

	}

	//passing the parameters to the methods is kind of an overkill, as they are global variables, but lets work like that
	private static void releaseResources(InputStream input, OutputStream output,
			Socket socket) {

		if( input != null) {
			try {
				input.close();
			} catch (IOException ex) {
				Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		if ( output != null ) {
			try {
				output.close();
			} catch (IOException ex) {
				Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		if ( socket != null ) {
			try {
				socket.close();
			} catch (IOException ex) {
				Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

	}
}
