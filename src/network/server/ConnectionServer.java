package network.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import Database.SQLManager;
import com.sun.security.ntlm.Server;
import network.Network;
import network.NetworkMessage;
import pojos.Measurement;
import pojos.Patient;

public class ConnectionServer implements Network {	
	
	public static void main( String[] args ) {
		ServerSocket server = null;
		try {
			SQLManager.connect("jdbc:sqlite:././Database/covid watchlist.db");

			server = new ServerSocket(SERVERPORT);
			System.out.println("Server started.");
			while ( true ) {
				System.out.println("Waiting for new client.");
				acceptConnection(server.accept());
			}
			
		} catch (IOException e) {
			System.out.println("Server error");
			e.printStackTrace();
		} catch ( SQLException | ClassNotFoundException e ){
			System.out.println("Database error");
			e.printStackTrace();
		} finally {
			try {
				SQLManager.disconnect();
			} catch ( SQLException e){
				e.printStackTrace();
			}
			System.out.println("Closing the server");
			releaseResources(server);
		}
	}


	//one client at a time version
	private static void acceptConnection(Socket s) {
		if ( s != null ) {
			System.out.println("New client: " + s.getInetAddress());
			ObjectInputStream inputStream = null;
			ObjectOutputStream outputStream = null;
			try {

				inputStream = new ObjectInputStream( s.getInputStream() );
				outputStream = new ObjectOutputStream( s.getOutputStream() );

				NetworkMessage msg = (NetworkMessage) inputStream.readObject();
				System.out.println(msg.toString());

				if ( msg.getProtocol() == NetworkMessage.Protocol.GET_PATIENT ) {
					Patient patientLogged = msg.getPatient();
					System.out.println("Patient received: "+ patientLogged.toString());
					try {
						patientLogged = SQLManager.searchPatientByDniAndPassword(patientLogged.getDni(), patientLogged.getPassword());
					} catch ( SQLException e){
						System.out.println( "Patient not found.");
						patientLogged = null;
					}
					NetworkMessage answer = null;

					if(patientLogged != null) {
						//continue connection, do as necessary
						answer = new NetworkMessage(NetworkMessage.Protocol.PUSH_PATIENT, patientLogged);
						outputStream.writeObject( answer );
						while ( true ) {
							msg = (NetworkMessage) inputStream.readObject();

							if ( msg.getProtocol() == NetworkMessage.Protocol.PUSH_MEASUREMENT ) {
								System.out.println("Inserting measurements.");
								ArrayList<Measurement> measures = msg.getMeasurements();
								try {
									if ( measures != null ) {
										SQLManager.insertMeasurements( measures );
									} else {
										System.out.println( "Trying to insert empty measures, this shouldn't happen");
									}
								} catch ( SQLException e){
									System.out.println("Error inserting the measurements in the database. ");
								}
							} else if ( msg.getProtocol() == NetworkMessage.Protocol.DISCONNECT ) {
								break;
							}
						}
					} else {
						//Deny the log in, close connection.
						System.out.println("Wrong DNI or password");
						answer = new NetworkMessage(NetworkMessage.Protocol.DENY_PATIENT);
						outputStream.writeObject( answer );
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				releaseResources ( s, inputStream, outputStream );
			}
		}
	}
	//Uses threads
	/*
	private static void acceptConnection(Socket s) {
		new Thread ( new ServerLogic( s )).start();
	}
	*/
	private static void releaseResources(ServerSocket server) {
		if( server != null) {
			try {
				server.close();
			} catch ( IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void releaseResources (Socket socket, InputStream in, OutputStream out){
		if( in != null) {
			try {
				in.close();
			} catch ( IOException e) {
				e.printStackTrace();
				System.out.println("All is good");
			}
		}

		try {
			socket.close();
		} catch ( IOException e) {
			e.printStackTrace();
			System.out.println("All is good");
		}
	}

}
