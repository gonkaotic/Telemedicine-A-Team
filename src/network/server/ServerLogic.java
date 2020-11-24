package network.server;

import com.sun.security.ntlm.Server;
import network.NetworkMessage;
import pojos.Measurement;
import pojos.Patient;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ServerLogic implements Runnable{

    private Socket socket;

    public ServerLogic () {
        super();
    }

    public ServerLogic ( Socket socket ) {
        this.socket = socket;
    }


    @Override
    public void run() {
        if ( socket != null ) {
            ObjectInputStream inputStream = null;
            ObjectOutputStream outputStream = null;
            try {

                inputStream = new ObjectInputStream( socket.getInputStream() );
                outputStream = new ObjectOutputStream( socket.getOutputStream() );

                NetworkMessage msg = (NetworkMessage) inputStream.readObject();
                System.out.println(msg.toString());

                if ( msg.getProtocol() == NetworkMessage.Protocol.GET_PATIENT ) {
                    Patient patientLogged = msg.getPatient();
                    System.out.println("Patient received: "+ patientLogged.toString());
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
                releaseResources ( inputStream, outputStream );
            }
        }
    }

    private void releaseResources (InputStream in, OutputStream out){
        if(in != null) {
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
