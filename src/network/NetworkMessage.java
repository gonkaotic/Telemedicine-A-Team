package network;

import java.io.Serializable;
import java.util.ArrayList;

import pojos.Administrator;
import pojos.Doctor;
import pojos.Measurement;
import pojos.Patient;

public class NetworkMessage implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2994950066983701095L;

	public enum Protocol {
		/*Protocols from Patient to Server*/
		PATIENT_LOGIN, PUSH_MEASUREMENT,
		/*Protocols from Doctor to Server*/
		DOCTOR_LOGIN, GET_PATIENT_MEASURES,
		/*Protocols from Server to Doctor*/
		PUSH_PATIENT_MEASURES,
		/*Protocols from Admin to Server*/
		ADMIN_LOGIN, REGISTER_PATIENT, REGISTER_DOCTOR, SERVER_SHUTDOWN, SERVER_CANCEL_SHUTDOWN,
		/*Protocols from Server to Admin*/
		SERVER_SHUTDOWN_CONFIRM,
		/*Protocols from Server to ANY client*/
		LOGIN_ACCEPT, LOGIN_DENY, ERROR, ACK,
		/*Protocols that can be used by all*/
		DISCONNECT
	}
	
	private Patient patient;

	private Doctor doctor;
	private Administrator admin;
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

	public NetworkMessage(Protocol protocol, Doctor doctor) {
		this.setProtocol(protocol);
		this.setDoctor(doctor);
	}

	public NetworkMessage(Protocol protocol, Administrator admin) {
		this.setProtocol(protocol);
		this.setAdmin(admin);
	}
	
	public NetworkMessage(Protocol protocol, ArrayList<Measurement> measurements) {
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


	public Doctor getDoctor() {
		return doctor;
	}

	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}

	public Administrator getAdmin() {
		return admin;
	}

	public void setAdmin(Administrator admin) {
		this.admin = admin;
	}

	@Override
	public String toString() {
		return "NetworkMessage{\n" +
				" protocol=" + protocol.toString() +
				"\n}";
	}
}
