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
	 */
	public Patient sendToServer ( Patient patient ) throws IOException, ClassNotFoundException {

		NetworkMessage msg = new NetworkMessage( NetworkMessage.Protocol.GET_PATIENT, patient);

		System.out.println("socket: "+ socket.getInetAddress());

		//objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
		//objectInputStream = new ObjectInputStream(socket.getInputStream());

		if ( objectOutputStream != null ) {
			objectOutputStream.writeObject( msg );
			objectOutputStream.flush();
			NetworkMessage answer = (NetworkMessage) objectInputStream.readObject();

			if ( answer.getProtocol() == NetworkMessage.Protocol.PUSH_PATIENT ){
				return answer.getPatient();
			}
			/*
			else if ( answer.getProtocol() == NetworkMessage.Protocol.DENY_PATIENT ){
				return null;
			}
			*/
		}
		return null;
	}
	/**
	 *  Sends the measurements to save in the database.
	 *  If the client hasn't logged in with the previous message there will be an error
	 * @param  measurements list of measurements to insert in the database
	 */
	public void sendToServer ( ArrayList<Measurement> measurements ) throws IOException {
		NetworkMessage msg = new NetworkMessage( NetworkMessage.Protocol.PUSH_MEASUREMENT, measurements );
		objectOutputStream.writeObject( msg );
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
			e.printStackTrace();
		} finally {
			releaseResources(objectInputStream, objectOutputStream, socket);
		}

	}

	public  boolean sendToServer(OutputStream outputStream, InputStream inputStream) throws ClassNotFoundException {

		while (true) {
			try {
				// Here: out and in streams

				objectOutputStream = new ObjectOutputStream(outputStream);
				objectInputStream = new ObjectInputStream(inputStream);

				// communication with server
				//objectOutputStream.writeObject(msg);
				objectOutputStream.flush();// just in case

				NetworkMessage msg_read = (NetworkMessage) objectInputStream.readObject();

				if (msg_read.getProtocol() == NetworkMessage.Protocol.PUSH_PATIENT) {
					Patient patientLogged = msg_read.getPatient();
					System.out.println("Patient received: "+ patientLogged.toString());

				}else if (msg_read.getProtocol() == NetworkMessage.Protocol.PUSH_MEASUREMENT) {
					ArrayList<Measurement> measures = msg_read.getMeasurements();
					System.out.println("Measurement received"+ measures);
					
				}else if (msg_read.getProtocol() == NetworkMessage.Protocol.DISCONNECT) {
					System.out.println("Finish");
					releaseResources(inputStream, outputStream, socket);
					System.exit(0);
				}



			} catch (IOException ex) {
				System.out.println("It is not possible to connect with the Server");
				Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
			} finally {
				releaseResources( objectInputStream, objectOutputStream, socket );
			}

			return true;
		}
	}

	//passing the current methods is kind of an overkill, as they are global variables, but lets work like that
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
