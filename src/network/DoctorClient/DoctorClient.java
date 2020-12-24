package network.DoctorClient;

import network.Network;
import network.NetworkMessage;
import network.ProtocolException;
import pojos.Doctor;
import pojos.Measurement;
import pojos.Patient;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DoctorClient implements Network {

	private String serverIP;
	private Socket socket = null;
	private ObjectOutputStream objectOutputStream = null;
	private ObjectInputStream objectInputStream = null;

	/**
	 * Client constructor
	 */
	public DoctorClient(String serverIP) {
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
	 * @returns the Doctor with the Patients if the id and password are correct, null otherwise.
	 * @param doctor A doctor that should have DNI and password in it
	 * @throws  ProtocolException when there is an error.
	 */
	public Doctor login (Doctor doctor ) throws ProtocolException {

		NetworkMessage msg = new NetworkMessage( NetworkMessage.Protocol.DOCTOR_LOGIN, doctor);

		System.out.println("socket: "+ socket.getInetAddress());

		if ( objectOutputStream != null ) {
			try {
				objectOutputStream.writeObject( msg );
				objectOutputStream.flush();
				NetworkMessage answer = (NetworkMessage) objectInputStream.readObject();
				NetworkMessage.Protocol protocol = answer.getProtocol();
				if ( protocol == NetworkMessage.Protocol.LOGIN_ACCEPT){
					return answer.getDoctor();
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
	 *  Requests the measurements of a particular patient.
	 * @returns A patient with a list of measurements inside.
	 * @param  patient a patient object, that must have the patient id
	 * @throws  ProtocolException when there is an error.
	 */
	public Patient getPatient( Patient patient ) throws ProtocolException {
		NetworkMessage msg = new NetworkMessage( NetworkMessage.Protocol.GET_PATIENT_MEASURES, patient );
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
				Logger.getLogger(DoctorClient.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		if ( output != null ) {
			try {
				output.close();
			} catch (IOException ex) {
				Logger.getLogger(DoctorClient.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		if ( socket != null ) {
			try {
				socket.close();
			} catch (IOException ex) {
				Logger.getLogger(DoctorClient.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

	}
}
