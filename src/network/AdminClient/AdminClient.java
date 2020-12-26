package network.AdminClient;

import network.Client;
import network.NetworkMessage;
import network.ProtocolException;
import pojos.Administrator;
import pojos.Doctor;
import pojos.Patient;


import java.io.IOException;


public class AdminClient extends Client {

    public AdminClient(String serverIP) {
        this.serverIP = serverIP;
    }

    /**
     * Sends the administrator's information to the server to login. Should be the initial step when launching the admin app
     *
     * @param admin the administrator with DNI and password
     * @return the administrator if login successful, null otherwise
     * @throws network.ProtocolException if there is an error in the server or with the object types
     */
    public Administrator login(Administrator admin) throws ProtocolException {
        NetworkMessage msg = new NetworkMessage(NetworkMessage.Protocol.ADMIN_LOGIN, admin);
        NetworkMessage answer = this.sendMessageToServer(msg);
        NetworkMessage.Protocol protocolMessage = answer.getProtocol();

        if (protocolMessage == NetworkMessage.Protocol.LOGIN_ACCEPT) {
            return answer.getAdmin();
        }
        if (protocolMessage == NetworkMessage.Protocol.LOGIN_DENY) {
            return null;
        }
        if (protocolMessage == NetworkMessage.Protocol.ERROR) {
            throw new ProtocolException("Error on the server side", ProtocolException.ErrorType.SERVERSIDE_ERROR);
        }

        return null;
    }

    /**
     * Sends a new patient to the server to register it in the server's database
     *
     * @param patient the patient to be registered
     * @throws ProtocolException if there is any kind of error
     */
    public void registerPatient(Patient patient) throws ProtocolException {
        NetworkMessage msg = new NetworkMessage(NetworkMessage.Protocol.REGISTER_PATIENT, patient);
        NetworkMessage answer = this.sendMessageToServer(msg);
        if (answer!= null) {
            NetworkMessage.Protocol protocolMessage = answer.getProtocol();
            if (protocolMessage == NetworkMessage.Protocol.ERROR){
                throw new ProtocolException("Unable to register the patient in the server", ProtocolException.ErrorType.SERVERSIDE_ERROR);
            }
        }
        else{
            throw new ProtocolException("OutputStream is closed", ProtocolException.ErrorType.CONNECTION_ERROR);
        }
    }

    /**
     * Sends a doctor to the server to register it in the database
     *
     * @param doctor the doctor to be registered
     * @throws ProtocolException when an error occurs
     */
    public void registerDoctor (Doctor doctor) throws ProtocolException{
        NetworkMessage msg = new NetworkMessage(NetworkMessage.Protocol.REGISTER_DOCTOR, doctor);
        NetworkMessage answer = this.sendMessageToServer(msg);
        if (answer!= null) {
            NetworkMessage.Protocol protocolMessage = answer.getProtocol();
            if (protocolMessage == NetworkMessage.Protocol.ERROR){
                throw new ProtocolException("Unable to register the doctor in the server", ProtocolException.ErrorType.SERVERSIDE_ERROR);
            }
        }
        else{
            throw new ProtocolException("OutputStream is closed", ProtocolException.ErrorType.CONNECTION_ERROR);
        }
    }

    /**
     * Sends a message to the server
     *
     * @param msg the message to be sent
     * @return the answer from the server
     * @throws ProtocolException
     */
    private NetworkMessage sendMessageToServer(NetworkMessage msg) throws ProtocolException {
        if (objectOutputStream != null) {
            try {
                objectOutputStream.writeObject(msg);
                objectOutputStream.flush();
                return msg = (NetworkMessage) objectInputStream.readObject();
            } catch (IOException e) {
                //e.printStackTrace(); --> Esto en realidad saltará cuando no se pueda leer o escribir (aka conexion cerrada)
            } catch (ClassNotFoundException e) {
                throw new ProtocolException("The network didn't answer with the correct object", ProtocolException.ErrorType.CONNECTION_ERROR);
            }

        }
        return null;
    }
}
