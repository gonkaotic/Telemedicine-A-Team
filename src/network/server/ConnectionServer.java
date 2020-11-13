package network.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import network.Network;
import network.NetworkMessage;
import pojos.Measurement;
import pojos.Patient;

public class ConnectionServer implements Network {	
	
	public static void main( String[] args ) {
		ServerSocket server = null;
		
		try {
			
			server = new ServerSocket(SERVERPORT);
			acceptConnection ( server.accept() );
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			releaseResources(server);
		}
	}
	
	private static void acceptConnection(Socket s) {
		if ( s != null ) {
			ObjectInputStream inputStream = null;
			try {
				
				inputStream = new ObjectInputStream( s.getInputStream() );
				
				while (true) {
					NetworkMessage msg = (NetworkMessage) inputStream.readObject();
					
					if ( msg.getProtocol() == NetworkMessage.Protocol.GET_PATIENT ) {
						Patient patient2Log = msg.getPatient();
						//TODO: check if patient is in the database with the right connection.
						NetworkMessage answer = null;
						if(patient2Log != null) {
							answer = new NetworkMessage(NetworkMessage.Protocol.PUSH_PATIENT, patient2Log);
						} else {
							answer = new NetworkMessage(NetworkMessage.Protocol.DENY_PATIENT);
						}
						ObjectOutputStream out = null;
						try {
							out = new ObjectOutputStream ( s.getOutputStream() );
							out.writeObject( answer );
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							try {
								out.close();
							} catch ( IOException e) {
								e.printStackTrace();
								System.out.println("All is good");
							}
						}
						
						
					} else if ( msg.getProtocol() == NetworkMessage.Protocol.PUSH_MEASUREMENT ) {
						ArrayList<Measurement> measures = msg.getMeasurements();
						//TODO: write the measurements in the database
						
					} else if ( msg.getProtocol() == NetworkMessage.Protocol.DISCONNECT ) {
						break;
					}
				} 
				
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				if(inputStream != null) {
					try {
						inputStream.close();
					} catch ( IOException e) {
						e.printStackTrace();
						System.out.println("All is good");
					}
				}
				
				try {
					s.close();
				} catch ( IOException e) {
					e.printStackTrace();
					System.out.println("All is good");
				}
			}
	        
		}
	}
	
	private static void releaseResources(ServerSocket server) {
		if( server != null) {
			try {
				server.close();
			} catch ( IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
