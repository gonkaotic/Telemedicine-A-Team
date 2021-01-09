package network;

import java.io.Serializable;
import java.security.Key;
import java.util.ArrayList;
import java.util.LinkedList;

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
		DOCTOR_LOGIN, GET_PATIENT_MEASURES, PUSH_MEASUREMENT_COMMENT,
		/*Protocols from Server to Doctor*/
		PUSH_PATIENT_MEASURES,
		/*Protocols from Admin to Server*/
		ADMIN_LOGIN, GET_DOCTORS, REGISTER_PATIENT, REGISTER_DOCTOR, REGISTER_ADMIN, SERVER_SHUTDOWN, SERVER_CANCEL_SHUTDOWN,
		/*Protocols from Server to Admin*/
		PUSH_DOCTORS, SERVER_SHUTDOWN_CONFIRM,
		/*Protocols from Server to ANY client*/
		LOGIN_ACCEPT, LOGIN_DENY, ERROR, ACK,
		/*Protocols that can be used by all*/
		DISCONNECT, PUSH_KEY
	}
	
	private Patient patient;
	private Key key;
	private Doctor doctor;
	private Administrator admin;
	//This is a list even though it might end up only being used for 1 measurement, in case in future uses, doctors for example, require more than one measure form more than one patient 
	private ArrayList<Measurement> measurements;
	private Protocol protocol;
	private LinkedList<Doctor> registeredDoctors;
	
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

	public NetworkMessage(Protocol protocol, LinkedList<Doctor> doctors){
		this.setProtocol(protocol);
		this.setRegisteredDoctors(doctors);
	}

	public NetworkMessage(  Protocol protocol, Key key){
		setProtocol(protocol);
		setKey(key);
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

	public void setRegisteredDoctors(LinkedList<Doctor> doctors){ this.registeredDoctors=doctors;}

	public LinkedList<Doctor> getRegisteredDoctors(){return this.registeredDoctors;}

	@Override
	public String toString() {
		return "NetworkMessage{\n" +
				" protocol=" + protocol.toString() +
				"\n}";
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}
}
