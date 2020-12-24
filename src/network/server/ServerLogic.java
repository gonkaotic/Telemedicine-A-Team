package network.server;

import Database.DatabaseLock;
import Database.SQLManager;
import network.NetworkMessage;
import pojos.Administrator;
import pojos.Doctor;
import pojos.Measurement;
import pojos.Patient;
import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

public class ServerLogic implements Runnable{

    private Socket socket = null;
    private DatabaseLock lock = null;

    private ObjectInputStream inputStream = null;
    private ObjectOutputStream outputStream = null;

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
            try {

                inputStream = new ObjectInputStream( socket.getInputStream() );
                outputStream = new ObjectOutputStream( socket.getOutputStream() );

                NetworkMessage msg = (NetworkMessage) inputStream.readObject();
                System.out.println(msg.toString());

                if ( msg.getProtocol() == NetworkMessage.Protocol.PATIENT_LOGIN) {
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
                        answer = new NetworkMessage(NetworkMessage.Protocol.LOGIN_ACCEPT, patientLogged);
                        outputStream.writeObject( answer );

                        patientLogic( patientLogged );

                    } else {
                        //Deny the log in, close connection.
                        System.out.println("Wrong DNI or password");
                        answer = new NetworkMessage(NetworkMessage.Protocol.LOGIN_DENY);
                        outputStream.writeObject( answer );
                    }
                } else if ( msg.getProtocol() == NetworkMessage.Protocol.DOCTOR_LOGIN ) {
                    Doctor doctorLogged = msg.getDoctor();
                    System.out.println("Doctor received: "+ doctorLogged.toString());
                    try {
                        lock.acquireRead();
                        doctorLogged = SQLManager.searchDoctorByDniAndPassword(doctorLogged.getDni(), doctorLogged.getPassword());
                    } catch ( SQLException e){
                        System.out.println( "Doctor not found.");
                        doctorLogged = null;
                    } catch ( InterruptedException e) {
                        System.out.println("There was an error with the database lock");
                        doctorLogged = null;
                    } finally {
                        lock.releaseRead();
                    }
                    NetworkMessage answer;

                    if(doctorLogged != null) {
                        //continue connection, do as necessary
                        answer = new NetworkMessage(NetworkMessage.Protocol.LOGIN_ACCEPT, doctorLogged);
                        outputStream.writeObject( answer );

                        doctorLogic( doctorLogged );

                    } else {
                        //Deny the log in, close connection.
                        System.out.println("Wrong DNI or password");
                        answer = new NetworkMessage(NetworkMessage.Protocol.LOGIN_DENY);
                        outputStream.writeObject( answer );
                    }
                } else if ( msg.getProtocol() == NetworkMessage.Protocol.ADMIN_LOGIN ) {
                    Administrator adminLogged = msg.getAdmin();
                    System.out.println("Doctor received: "+ adminLogged.toString());
                    try {
                        lock.acquireRead();
                        adminLogged = SQLManager.searchAdminByDniAndPassword(adminLogged.getDni(), adminLogged.getPassword());
                    } catch ( SQLException e){
                        System.out.println( "Doctor not found.");
                        adminLogged = null;
                    } catch ( InterruptedException e) {
                        System.out.println("There was an error with the database lock");
                        adminLogged = null;
                    } finally {
                        lock.releaseRead();
                    }
                    NetworkMessage answer;

                    if(adminLogged != null) {
                        //continue connection, do as necessary
                        answer = new NetworkMessage(NetworkMessage.Protocol.LOGIN_ACCEPT, adminLogged);
                        outputStream.writeObject( answer );

                        adminLogic( adminLogged );

                    } else {
                        //Deny the log in, close connection.
                        System.out.println("Wrong DNI or password");
                        answer = new NetworkMessage(NetworkMessage.Protocol.LOGIN_DENY);
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

    private boolean securityCheck (ArrayList<Measurement> measures, Patient patient){

        for (Measurement m : measures ) {
            //TODO: check measures' patient id
            if ( false ){
                return false;
            }
        }
        return true;
    }

    private void patientLogic( Patient patientLogged ){
        if ( patientLogged != null ) {
            NetworkMessage msg = null;
            NetworkMessage answer = null;
            while (true) {
                try {
                    msg = (NetworkMessage) inputStream.readObject();
                    NetworkMessage.Protocol protocol = msg.getProtocol();

                    if (protocol == NetworkMessage.Protocol.PUSH_MEASUREMENT) {
                        System.out.println("Inserting measurements.");
                        ArrayList<Measurement> measures = msg.getMeasurements();
                        try {
                            if (measures != null && securityCheck(measures, patientLogged)) {
                                lock.acquireWrite();
                                SQLManager.insertMeasurements(measures);
                                answer = new NetworkMessage(NetworkMessage.Protocol.ACK);
                            } else {
                                System.out.println("Trying to insert empty measures or with an incorrect id");
                                answer = new NetworkMessage(NetworkMessage.Protocol.ERROR);
                            }
                        } catch (SQLException e) {
                            System.out.println("Error inserting the measurements in the database. ");
                            answer = new NetworkMessage(NetworkMessage.Protocol.ERROR);
                        } catch (InterruptedException e) {
                            System.out.println("There was an error with the database lock");
                            answer = new NetworkMessage(NetworkMessage.Protocol.ERROR);
                        } finally {
                            lock.releaseWrite();
                            outputStream.writeObject(answer);
                            outputStream.flush();
                        }
                    } else if (msg.getProtocol() == NetworkMessage.Protocol.DISCONNECT) {
                        break;
                    }
                } catch (IOException e) {
                    System.out.println("There was a connection error");
                } catch (ClassNotFoundException e) {
                    System.out.println("Client using incorrect object");
                }
            }

            releaseResources(socket, inputStream, outputStream);
        }
    }

    private void doctorLogic( Doctor doctorLogged){
        if ( doctorLogged != null ) {
            NetworkMessage msg = null;
            NetworkMessage answer = null;
            while (true) {
                try {
                    msg = (NetworkMessage) inputStream.readObject();
                    NetworkMessage.Protocol protocol = msg.getProtocol();

                    if ( protocol == NetworkMessage.Protocol.GET_PATIENT_MEASURES ) {
                        Patient patient = msg.getPatient();
                        try {
                            lock.acquireRead();
                            answer = new NetworkMessage(NetworkMessage.Protocol.PUSH_PATIENT_MEASURES, SQLManager.getMeasuresByPatientId( patient.getId() ));
                        } catch ( SQLException | InterruptedException e ) {
                            answer = new NetworkMessage( NetworkMessage.Protocol.ERROR);
                        } finally {
                            lock.releaseRead();
                            outputStream.writeObject(answer);
                            outputStream.flush();
                        }

                    } else if ( msg.getProtocol() == NetworkMessage.Protocol.DISCONNECT ) {
                        break;
                    }
                } catch (IOException e) {
                    System.out.println("There was a connection error");
                } catch (ClassNotFoundException e) {
                    System.out.println("Client using incorrect object");
                }
            }

            releaseResources( socket, inputStream, outputStream);
        }
    }

    private void adminLogic( Administrator admin){
        if ( admin != null ){

        }
    }
}
