package network.DoctorClient;

import network.Client;
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

public class DoctorClient extends Client {

	/**
	 * Client constructor
	 */
	public DoctorClient(String serverIP) {
		this.serverIP = serverIP;
	}


	/**
	 *  This would be the first message a client should sent to the server to "log in"
	 * @return the Doctor with the Patients if the id and password are correct, null otherwise.
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
			} catch (ClassNotFoundException e) {
				throw new ProtocolException("The network didn't answer with the correct object", ProtocolException.ErrorType.CONNECTION_ERROR);
			} catch ( IOException e ){
				throw new ProtocolException("Server closed the connection", ProtocolException.ErrorType.CLOSED_CONNECTION_ERROR);
			}

		}
		return null;
	}

	/**
	 *  Requests the measurements of a particular patient.
	 * @return A patient with a list of measurements inside.
	 * @param  patient a patient object, that must have the patient id
	 * @throws  ProtocolException when there is an error.
	 */
	public Patient getPatient( Patient patient ) throws ProtocolException {
		NetworkMessage msg = new NetworkMessage( NetworkMessage.Protocol.GET_PATIENT_MEASURES, patient );
		try {
			objectOutputStream.writeObject( msg );
			NetworkMessage answer = ( NetworkMessage ) objectInputStream.readObject();
			NetworkMessage.Protocol protocol = answer.getProtocol();
			if ( protocol == NetworkMessage.Protocol.PUSH_PATIENT_MEASURES ){
				patient.setMeasurements( answer.getMeasurements() );
				return patient;
			} else if ( protocol == NetworkMessage.Protocol.ERROR ){
				throw new ProtocolException("There was an error in the server", ProtocolException.ErrorType.SERVERSIDE_ERROR);
			}
		} catch ( ClassNotFoundException e){
			throw new ProtocolException("The network didn't answer with the correct object", ProtocolException.ErrorType.CONNECTION_ERROR);
		} catch (IOException e) {
			throw new ProtocolException("Server closed the connection", ProtocolException.ErrorType.CLOSED_CONNECTION_ERROR);
		}

		return null;
	}


}
