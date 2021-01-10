package network.server;

import Database.DatabaseLock;
import Database.SQLManager;
import network.Network;
import network.NetworkMessage;
import pojos.Administrator;
import pojos.Doctor;
import pojos.Measurement;
import pojos.Patient;

import javax.crypto.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerLogic implements Runnable, Network {

    private Socket socket = null;
    private DatabaseLock lock = null;

    private AtomicInteger threads;

    private KeyPair keyPair;
    private Key clientKey;
    private Cipher cipherOut;
    private Cipher cipherIn;

    private ObjectInputStream inputStream = null;
    private ObjectOutputStream outputStream = null;
    private DataOutputStream dataOutputStream = null;
    private DataInputStream dataInputStream = null;

    public ServerLogic (Socket socket, DatabaseLock lock, AtomicInteger threads, KeyPair keyPair) {
        this.socket = socket;
        this.lock = lock;
        this.threads = threads;
        this.threads.incrementAndGet();
        this.keyPair = keyPair;

    }


    @Override
    public void run() {
        if ( socket != null ) {
            System.out.println("New client: " + socket.getInetAddress());
            try {

                inputStream = new ObjectInputStream(socket.getInputStream());
                outputStream = new ObjectOutputStream(socket.getOutputStream());

                NetworkMessage msg = (NetworkMessage) inputStream.readObject();
                System.out.println(msg.toString());
                if (msg.getProtocol() == NetworkMessage.Protocol.PUSH_KEY) {

                    //now that we have the public key of the server we encrypt our communications.
                    //AES stands for Advance Encryption Standard
                    try {
                        clientKey = msg.getKey();
                        cipherOut = Cipher.getInstance(encryptionAlgorithm);
                        cipherOut.init(Cipher.ENCRYPT_MODE, clientKey);

                        cipherIn = Cipher.getInstance(encryptionAlgorithm);
                        cipherIn.init( Cipher.DECRYPT_MODE, keyPair.getPrivate() );

                        outputStream.writeObject( new NetworkMessage(NetworkMessage.Protocol.PUSH_KEY, keyPair.getPublic()));

                        /*
                        CipherOutputStream cos = new CipherOutputStream(socket.getOutputStream(), cipherOut);
                        CipherInputStream cis = new CipherInputStream(socket.getInputStream(), cipherIn);


                         */
                        System.out.println("this works?");
                       // outputStream = new ObjectOutputStream(cos);
                        //SealedObject message = null;
                        // try {
                      /*       message = new SealedObject(new NetworkMessage(NetworkMessage.Protocol.ACK),cipherOut);
                        } catch (IllegalBlockSizeException e) {
                            e.printStackTrace();
                        }
                        outputStream.writeObject(message);
                        //outputStream.writeObject(new NetworkMessage());
                        System.out.println("this too?");
                        inputStream = new ObjectInputStream(cis);


                       */
                        dataOutputStream = new DataOutputStream(socket.getOutputStream());
                        dataInputStream = new DataInputStream(socket.getInputStream());
                        sendMessageToClient(new NetworkMessage(NetworkMessage.Protocol.ACK));


                        System.out.println("Cyphering ready");
                       // msg = (NetworkMessage) inputStream.readObject();
                        System.out.println(msg.toString());
                        if (msg.getProtocol() == NetworkMessage.Protocol.PATIENT_LOGIN) {
                            Patient patientLogged = msg.getPatient();
                            System.out.println("Patient received: " + patientLogged.toString());
                            try {
                                lock.acquireRead();
                                patientLogged = SQLManager.getPatientByDniAndPassword(patientLogged.getDni(), patientLogged.getPassword());
                            } catch (SQLException e) {
                                System.out.println("Patient not found.");
                                patientLogged = null;
                            } catch (InterruptedException e) {
                                System.out.println("There was an error with the database lock");
                                patientLogged = null;
                            } finally {
                                lock.releaseRead();
                            }
                            NetworkMessage answer;

                            if (patientLogged != null) {
                                //continue connection, do as necessary
                                answer = new NetworkMessage(NetworkMessage.Protocol.LOGIN_ACCEPT, patientLogged);
                                outputStream.writeObject(answer);

                                patientLogic(patientLogged);

                            } else {
                                //Deny the log in, close connection.
                                System.out.println("Wrong DNI or password");
                                answer = new NetworkMessage(NetworkMessage.Protocol.LOGIN_DENY);
                                outputStream.writeObject(answer);
                            }
                        } else if (msg.getProtocol() == NetworkMessage.Protocol.DOCTOR_LOGIN) {
                            Doctor doctorLogged = msg.getDoctor();
                            System.out.println("Doctor received: " + doctorLogged.toString());
                            try {
                                lock.acquireRead();
                                doctorLogged = SQLManager.getDoctorByDniAndPassword(doctorLogged.getDni(), doctorLogged.getPassword());
                            } catch (SQLException e) {
                                System.out.println("Doctor not found.");
                                doctorLogged = null;
                            } catch (InterruptedException e) {
                                System.out.println("There was an error with the database lock");
                                doctorLogged = null;
                            } finally {
                                lock.releaseRead();
                            }
                            NetworkMessage answer;

                            if (doctorLogged != null) {
                                //continue connection, do as necessary
                                answer = new NetworkMessage(NetworkMessage.Protocol.LOGIN_ACCEPT, doctorLogged);
                                outputStream.writeObject(answer);

                                doctorLogic(doctorLogged);

                            } else {
                                //Deny the log in, close connection.
                                System.out.println("Wrong DNI or password");
                                answer = new NetworkMessage(NetworkMessage.Protocol.LOGIN_DENY);
                                outputStream.writeObject(answer);
                            }
                        } else if (msg.getProtocol() == NetworkMessage.Protocol.ADMIN_LOGIN) {
                            Administrator adminLogged = msg.getAdmin();
                            System.out.println("Admin received: " + adminLogged.toString());
                            try {
                                lock.acquireRead();
                                adminLogged = SQLManager.getAdminByDniAndPassword(adminLogged.getDni(), adminLogged.getPassword());
                            } catch (SQLException e) {
                                System.out.println("Admin not found.");
                                adminLogged = null;
                            } catch (InterruptedException e) {
                                System.out.println("There was an error with the database lock");
                                adminLogged = null;
                            } finally {
                                lock.releaseRead();
                            }
                            NetworkMessage answer;

                            if (adminLogged != null) {
                                //continue connection, do as necessary
                                answer = new NetworkMessage(NetworkMessage.Protocol.LOGIN_ACCEPT, adminLogged);
                                outputStream.writeObject(answer);

                                adminLogic(adminLogged);

                            } else {
                                //Deny the log in, close connection.
                                System.out.println("Wrong DNI or password");
                                answer = new NetworkMessage(NetworkMessage.Protocol.LOGIN_DENY);
                                outputStream.writeObject(answer);
                            }
                        }

                    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e){
                        e.printStackTrace();
                        System.out.println("there was an error setting up encryption");
                        outputStream.writeObject( new NetworkMessage(NetworkMessage.Protocol.ERROR));
                    }
                }
            }catch(SocketException ex){
                System.out.println("Client closed socket");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                threads.decrementAndGet();
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
            if ( m.getPatientId() != patient.getId() ){
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
                    break;
                } catch (ClassNotFoundException e) {
                    System.out.println("Client using incorrect object");
                }
            }
            threads.decrementAndGet();
            releaseResources(socket, inputStream, outputStream);
        }
    }

    private void doctorLogic( Doctor doctorLogged){
        if ( doctorLogged != null ) {
            NetworkMessage msg = null;
            NetworkMessage answer = null;
            Patient patient = null;
            while (true) {
                try {
                    msg = (NetworkMessage) inputStream.readObject();
                    NetworkMessage.Protocol protocol = msg.getProtocol();

                    if ( protocol == NetworkMessage.Protocol.GET_PATIENT_MEASURES ) {
                        patient = msg.getPatient();
                        if ( patient.getDoctorId().compareTo(doctorLogged.getId()) == 0 ) {
                            try {
                                lock.acquireRead();
                                answer = new NetworkMessage(NetworkMessage.Protocol.PUSH_PATIENT_MEASURES,
                                        (ArrayList<Measurement>) SQLManager.getMeasurementsByPatientId(patient.getId()));
                            } catch (SQLException | InterruptedException e) {
                                System.out.println("Error reading the measurements. ");
                                answer = new NetworkMessage(NetworkMessage.Protocol.ERROR);
                            } finally {
                                lock.releaseRead();
                            }
                        } else {
                            //doctor asking for information about a patient that isn't theirs.
                            System.out.println("Error in information given for what is requested");
                            answer = new NetworkMessage(NetworkMessage.Protocol.ERROR);
                        }
                        outputStream.writeObject(answer);
                        outputStream.flush();

                    } else if ( msg.getProtocol() == NetworkMessage.Protocol.PUSH_MEASUREMENT_COMMENT ) {
                        if ( patient != null ) {
                            //for this message there will only be 1 measurement
                            Measurement measurement = msg.getMeasurements().get(0);

                            if ( measurement.getPatientId().compareTo(patient.getId()) == 0 &&
                                 patient.getDoctorId().compareTo(doctorLogged.getId()) == 0 &&
                                 measurement.getComment() != null ) {
                                //we made sure that everything is in place about what is being sent to the database
                                try {
                                    lock.acquireWrite();
                                    SQLManager.updateMeasurementComment(measurement.getId(), measurement.getComment());
                                    answer = new NetworkMessage(NetworkMessage.Protocol.ACK);
                                } catch (InterruptedException | SQLException e) {
                                    System.out.println("there was an error writing the measurement");
                                    answer = new NetworkMessage(NetworkMessage.Protocol.ERROR);
                                }

                            } else {
                                System.out.println("Error in information given for what is requested");
                                answer = new NetworkMessage(NetworkMessage.Protocol.ERROR);
                            }

                            outputStream.writeObject(answer);
                            outputStream.flush();
                        }
                    } else if ( msg.getProtocol() == NetworkMessage.Protocol.DISCONNECT ) {
                        break;
                    }
                } catch (IOException e) {
                    System.out.println("There was a connection error");
                    break;
                } catch (ClassNotFoundException e) {
                    System.out.println("Client using incorrect object");
                }
            }
            threads.decrementAndGet();
            releaseResources( socket, inputStream, outputStream);
        }
    }

    private void adminLogic( Administrator admin){
        if ( admin != null ){
            NetworkMessage msg = null;
            NetworkMessage answer = null;
            while(true){
                try {
                    msg = (NetworkMessage) inputStream.readObject();
                    NetworkMessage.Protocol protocol = msg.getProtocol();
                    if(protocol == NetworkMessage.Protocol.GET_DOCTORS){
                        LinkedList<Doctor> doctors = new LinkedList<>();
                        try {
                            lock.acquireRead();
                            doctors = (LinkedList<Doctor>) SQLManager.getAllDoctors();
                            answer = new NetworkMessage(NetworkMessage.Protocol.PUSH_DOCTORS, doctors);
                        } catch (InterruptedException e) {
                            System.out.println("Error when acquiring the database lock");
                            answer = new NetworkMessage(NetworkMessage.Protocol.ERROR);
                        } catch (SQLException throwables) {
                            System.out.println("Error when reading from the database");
                            answer = new NetworkMessage(NetworkMessage.Protocol.ERROR);
                        } finally{
                            lock.releaseRead();
                            outputStream.writeObject(answer);
                            outputStream.flush();
                        }
                    }
                    if(protocol == NetworkMessage.Protocol.REGISTER_DOCTOR){
                        Doctor doctor = msg.getDoctor();
                        if(doctor!=null){
                           try{
                               lock.acquireWrite();
                               SQLManager.insertDoctor(doctor);
                               answer = new NetworkMessage(NetworkMessage.Protocol.ACK);
                           } catch (InterruptedException e) {
                               System.out.println("Problems when acquiring database lock");
                               answer = new NetworkMessage(NetworkMessage.Protocol.ERROR);
                           } catch (SQLException throwables) {
                               System.out.println("Error when inserting in the database");
                               answer = new NetworkMessage(NetworkMessage.Protocol.ERROR);
                           }
                           finally{
                               lock.releaseWrite();
                               outputStream.writeObject(answer);
                               outputStream.flush();
                           }
                        }else{
                            System.out.println("No doctor provided");
                            answer = new NetworkMessage(NetworkMessage.Protocol.ERROR);
                            outputStream.writeObject(answer);
                            outputStream.flush();
                        }
                    }

                    if(protocol == NetworkMessage.Protocol.REGISTER_PATIENT){
                        Patient patient = msg.getPatient();
                        if(patient!=null){
                            try{
                                lock.acquireWrite();
                                SQLManager.insertPatient(patient);
                                answer = new NetworkMessage(NetworkMessage.Protocol.ACK);
                            } catch (InterruptedException e) {
                                System.out.println("Problems when acquiring database lock");
                                answer = new NetworkMessage(NetworkMessage.Protocol.ERROR);
                            } catch (SQLException throwables) {
                                System.out.println("Error when inserting in the database");
                                answer = new NetworkMessage(NetworkMessage.Protocol.ERROR);
                            }
                            finally{
                                lock.releaseWrite();
                                outputStream.writeObject(answer);
                                outputStream.flush();
                            }
                        }else{
                            System.out.println("No patient provided");
                            answer = new NetworkMessage(NetworkMessage.Protocol.ERROR);
                            outputStream.writeObject(answer);
                            outputStream.flush();
                        }
                    }

                    if(protocol == NetworkMessage.Protocol.REGISTER_ADMIN){
                        Administrator administrator = msg.getAdmin();
                        if(administrator!=null){
                            try{
                                lock.acquireWrite();
                                SQLManager.insertAdmin(administrator);
                                answer = new NetworkMessage(NetworkMessage.Protocol.ACK);
                            } catch (InterruptedException e) {
                                System.out.println("Problems when acquiring database lock");
                                answer = new NetworkMessage(NetworkMessage.Protocol.ERROR);
                            } catch (SQLException throwables) {
                                System.out.println("Error when inserting in the database");
                                answer = new NetworkMessage(NetworkMessage.Protocol.ERROR);
                            }
                            finally{
                                lock.releaseWrite();
                                outputStream.writeObject(answer);
                                outputStream.flush();
                            }
                        }else{
                            System.out.println("No admin provided");
                            answer = new NetworkMessage(NetworkMessage.Protocol.ERROR);
                            outputStream.writeObject(answer);
                            outputStream.flush();
                        }
                    }

                    if (protocol == NetworkMessage.Protocol.SERVER_SHUTDOWN) {
                        Administrator administrator = msg.getAdmin();
                        if(administrator != null && administrator.equals(admin)){
                            if(threads.get() > 1){
                                answer = new NetworkMessage(NetworkMessage.Protocol.SERVER_SHUTDOWN_CONFIRM);
                                outputStream.writeObject(answer);
                                outputStream.flush();
                                msg = (NetworkMessage) inputStream.readObject();
                                if(msg.getProtocol() == NetworkMessage.Protocol.SERVER_SHUTDOWN_CONFIRM){
                                    System.out.println("Shutting down the server");
                                    answer = new NetworkMessage(NetworkMessage.Protocol.ACK);
                                    outputStream.writeObject(answer);
                                    outputStream.flush();
                                    releaseResources(socket, inputStream, outputStream);
                                    System.exit(0);
                                }
                                if (msg.getProtocol() == NetworkMessage.Protocol.SERVER_CANCEL_SHUTDOWN){
                                    System.out.println("Admin canceled the shutdown");
                                    answer = new NetworkMessage(NetworkMessage.Protocol.ACK);
                                    outputStream.writeObject(answer);
                                    outputStream.flush();
                                }
                            }
                            else{
                                System.out.println("Shutting down the server");
                                answer = new NetworkMessage(NetworkMessage.Protocol.ACK);
                                outputStream.writeObject(answer);
                                outputStream.flush();
                                releaseResources(socket, inputStream, outputStream);
                                System.exit(0);
                            }
                        }
                        else{
                            System.out.println("Invalid admin. It is not the admin that logged in");
                            System.out.println("RegisteredAdmin: "+ admin);
                            System.out.println("ReceivedAdmin: "+administrator);
                            answer = new NetworkMessage(NetworkMessage.Protocol.ERROR);
                            outputStream.writeObject(answer);
                            outputStream.flush();
                        }

                    }




                } catch (IOException e) {
                    System.out.println("There was a connection error");
                    break;
                } catch (ClassNotFoundException e) {
                    System.out.println("Client using incorrect object");
                }
            }
            threads.decrementAndGet();
            releaseResources( socket, inputStream, outputStream);
        }
    }

    private byte[] serialize(NetworkMessage message){
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(message);
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private NetworkMessage deserialize(byte[] byteArray){
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
            ObjectInputStream in = new ObjectInputStream(bis);
            return (NetworkMessage) in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sends an encrypted NetworkMessage to the client. First it sends the size in bytes of the message to be sent
     * @param message
     */
    private void sendMessageToClient(NetworkMessage message) throws IOException {
        try {
            if (dataOutputStream != null) {
                byte[] data = serialize(message);
                int size = (int) ((data.length/MAX_ENCRYPTION_SIZE)+1)*ENCRYPTED_SIZE;
                System.out.println("Data length: "+data.length);
                System.out.println("Size: "+size);
                byte[] bytesToSend = new byte[size];
                dataOutputStream.writeInt(size);
                for(int i=0,round=0;i<data.length; i=i+MAX_ENCRYPTION_SIZE,round=round+ENCRYPTED_SIZE){
                    System.out.println("i: "+i);
                    System.out.println("Round: "+round);
                    System.out.println();
                    if (i + MAX_ENCRYPTION_SIZE < data.length) {
                        System.arraycopy(cipherOut.doFinal(Arrays.copyOfRange(data, i, i+MAX_ENCRYPTION_SIZE)),0,bytesToSend,round,ENCRYPTED_SIZE);
                    }
                    else{
                        System.arraycopy(cipherOut.doFinal(Arrays.copyOfRange(data, i, data.length)),0,bytesToSend,round,ENCRYPTED_SIZE);
                    }
                }
                dataOutputStream.write(bytesToSend);
            }
        } catch (BadPaddingException e) {
            e.printStackTrace();
            System.out.println("Error with the encryption");
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            System.out.println("Error with the encryption");
        }
    }

    /**
     * Receives a message from server
     * @return the message received from the client
     *          null if there was any problem with the encryption process or if dataInputStream is null
     * @throws IOException
     */
    private NetworkMessage getMessageFromClient() throws IOException {
        try {
            if(dataInputStream!=null) {
                int size = dataInputStream.readInt();
                byte[] byteArray = new byte[size];
                dataInputStream.read(byteArray);
                int numberOfBlocks = size/ENCRYPTED_SIZE;
                byte[] decryptedArray = new byte[numberOfBlocks*MAX_ENCRYPTION_SIZE];

                for (int i = 0,coveredSize=0; i < numberOfBlocks-1; i++) {
                    byte[] decrypted = cipherIn.doFinal(Arrays.copyOfRange(byteArray, i * ENCRYPTED_SIZE, i * ENCRYPTED_SIZE+ENCRYPTED_SIZE));

                    System.arraycopy(decrypted,0,decryptedArray,coveredSize,decrypted.length);
                    coveredSize = coveredSize + decrypted.length;
                }
                NetworkMessage message = deserialize(decryptedArray);
                return message;
            }
            return null;
        } catch (BadPaddingException e) {
            e.printStackTrace();
            System.out.println("Problems in the encryption");
            return null;
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            System.out.println("Problems in the encryption");
            return null;
        }
    }
}
