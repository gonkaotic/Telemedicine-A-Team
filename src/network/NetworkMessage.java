package network;

import java.io.Serializable;
import java.util.ArrayList;

import pojos.Measurement;
import pojos.Patient;

public class NetworkMessage implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2994950066983701095L;

	public enum Protocol {
		/*Protocols from client to server*/
		GET_PATIENT, PUSH_MEASUREMENT, 
		/*Protocols from server to client*/
		PUSH_PATIENT, DENY_PATIENT, ERROR, ACK,
		/*Protocols that can be used by both*/
		DISCONNECT
	}
	
	private Patient patient;
	//This is a list even though it might end up only being used for 1 measurement, in case in future uses, doctors for example, require more than one measure form more than one patient 
	private ArrayList<Measurement> measurements;
	private Protocol protocol;
	
	public NetworkMessage() {
		//for disconnection
		super();
		this.protocol = Protocol.DISCONNECT;
	}
	
	public NetworkMessage(Protocol protocol) {
		//Shouldn't be necessary at first, but just in case someone (like Celiaco Falso) is not comfortable making NetworkMessage() for disconnection and prefer doing NetworkMessage(Protocol.DISCONNECT)
		this.setProtocol(protocol);
	}
	
	public NetworkMessage(Protocol protocol, Patient patient, ArrayList<Measurement> measurements) {
		//most general, not sure it will ever be used
		this.setProtocol(protocol);
		this.setPatient(patient);
		this.setMeasurements(measurements); 
	}
	
	public NetworkMessage(Protocol protocol, Patient patient) {
		//for protocols that only require patient
		this.setProtocol(protocol);
		this.setPatient(patient);
	}
	
	public NetworkMessage(Protocol protocol, ArrayList<Measurement> measurements) {
		//for 
		this.setProtocol(protocol);
		this.setMeasurements(measurements); 
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public ArrayList<Measurement> getMeasurements() {
		return measurements;
	}

	public void setMeasurements(ArrayList<Measurement> measurements) {
		this.measurements = measurements;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	@Override
	public String toString() {
		return "NetworkMessage{\n" +
				" protocol=" + protocol.toString() +
				"\n}";
	}
}
