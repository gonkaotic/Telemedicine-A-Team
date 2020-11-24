package network.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.sun.security.ntlm.Server;
import network.Network;
import network.NetworkMessage;
import pojos.Measurement;
import pojos.Patient;

public class ConnectionServer implements Network {	
	
	public static void main( String[] args ) {
		ServerSocket server = null;
		
		try {
			while ( true ) {
				server = new ServerSocket(SERVERPORT);
				acceptConnection(server.accept());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			releaseResources(server);
		}
	}


	//one client at a time version
	/*
	private static void acceptConnection(Socket s) {
		if ( socket != null ) {
            ObjectInputStream inputStream = null;
            ObjectOutputStream outputStream = null;
            try {

                inputStream = new ObjectInputStream( socket.getInputStream() );
                outputStream = new ObjectOutputStream( socket.getOutputStream() );

                NetworkMessage msg = (NetworkMessage) inputStream.readObject();

                if ( msg.getProtocol() == NetworkMessage.Protocol.GET_PATIENT ) {
                    Patient patientLogged = msg.getPatient();
                    //TODO: check if patient is in the database with the right connection.
                    NetworkMessage answer = null;
                    outputStream = new ObjectOutputStream ( socket.getOutputStream() );

                    if(patientLogged != null) {
                        //continue connection, do as necessary
                        answer = new NetworkMessage(NetworkMessage.Protocol.PUSH_PATIENT, patientLogged);
                        outputStream.writeObject( answer );
                        while ( true ) {
                            msg = (NetworkMessage) inputStream.readObject();

                            if ( msg.getProtocol() == NetworkMessage.Protocol.PUSH_MEASUREMENT ) {
                                ArrayList<Measurement> measures = msg.getMeasurements();
                                //TODO: write the measurements in the database
                            } else if ( msg.getProtocol() == NetworkMessage.Protocol.DISCONNECT ) {
                                break;
                            }
                        }
                    } else {
                        //Deny the log in, close connection.
                        answer = new NetworkMessage(NetworkMessage.Protocol.DENY_PATIENT);
                        outputStream.writeObject( answer );
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                ServerLogic.releaseResources ( s, inputStream, outputStream );
            }
        }
	}
	*/
	//Uses threads
	private static void acceptConnection(Socket s) {
		new Thread ( new ServerLogic( s )).start();
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

	private void releaseResources (Socket socket, InputStream in, OutputStream out){
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
