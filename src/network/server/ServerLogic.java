package network.server;

import Database.DatabaseLock;
import Database.SQLManager;
import network.NetworkMessage;
import pojos.Measurement;
import pojos.Patient;
import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

public class ServerLogic implements Runnable{

    private Socket socket = null;
    private DatabaseLock lock = null;

    public ServerLogic () {
        super();
    }

    public ServerLogic (Socket socket, DatabaseLock lock) {
        this.socket = socket;
        this.lock = lock;
    }


    @Override
    public void run() {
        if ( socket != null ) {
            System.out.println("New client: " + socket.getInetAddress());
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
                    try {
                        lock.acquireRead();
                        patientLogged = SQLManager.searchPatientByDniAndPassword(patientLogged.getDni(), patientLogged.getPassword());
                    } catch ( SQLException e){
                        System.out.println( "Patient not found.");
                        patientLogged = null;
                    } catch ( InterruptedException e) {
                        System.out.println("There was an error with the database lock");
                        patientLogged = null;
                    } finally {
                        lock.releaseRead();
                    }
                    NetworkMessage answer;

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
                                        lock.acquireWrite();
                                        SQLManager.insertMeasurements(measures);
                                    } else {
                                        System.out.println( "Trying to insert empty measures, this shouldn't happen");
                                    }
                                } catch ( SQLException e){
                                    System.out.println("Error inserting the measurements in the database. ");
                                } catch ( InterruptedException e ) {
                                    System.out.println("There was an error with the database lock");
                                }finally {
                                    lock.releaseWrite();
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
                releaseResources ( socket, inputStream, outputStream );
            }
        }

        System.out.println("Finishing one thread");
    }

    private static void releaseResources (Socket socket, InputStream in, OutputStream out){
        if( in != null) {
            try {
                in.close();
            } catch ( IOException e) {
                e.printStackTrace();
                System.out.println("All is good. Don't worry, everything will be alright, there was just an error closing the Input stream");
            }
        }


        if ( socket != null ) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("All is good. Don't worry, everything will be alright, there was just an error closing the Socket");
            }
        }

        if ( out != null ){
            try {
                out.close();
            } catch ( IOException e) {
                e.printStackTrace();
                System.out.println("All is good. Don't worry, everything will be alright, there was just an error closing the Output Stream");
            }
        }
    }
}
