package network.AdminClient;

import network.Client;
import network.NetworkMessage;
import network.ProtocolException;
import pojos.Administrator;
import pojos.Patient;
import sun.nio.ch.Net;

import java.io.IOException;


public class AdminClient extends Client {

    public AdminClient (String serverIP){
        this.serverIP=serverIP;
    }

    /**
     * Sends the administrator's information to the server to login. Should be the initial step when launching the admin app
     *
     * @param admin the administrator with DNI and password
     * @return the administrator if login successful, null otherwise
     * @throws network.ProtocolException if there is an error in the server or with the object types
     */
    public Administrator login (Administrator admin) throws ProtocolException {
        NetworkMessage msg = new NetworkMessage(NetworkMessage.Protocol.ADMIN_LOGIN, admin);
        if (objectOutputStream != null) {
            try {
                objectOutputStream.writeObject(msg);
                objectOutputStream.flush();
                NetworkMessage answer = (NetworkMessage) objectInputStream.readObject();
                NetworkMessage.Protocol protocolMessage = answer.getProtocol();

                if(protocolMessage == NetworkMessage.Protocol.LOGIN_ACCEPT){
                    return answer.getAdmin();
                }
                if(protocolMessage == NetworkMessage.Protocol.LOGIN_DENY){
                    return null;
                }
                if (protocolMessage == NetworkMessage.Protocol.ERROR){
                    throw new ProtocolException("Error on the server side", ProtocolException.ErrorType.SERVERSIDE_ERROR);
                }

            } catch (IOException e) {
                //e.printStackTrace(); --> Esto en realidad saltará cuando no se pueda ller o escribir (aka conexion cerrada)
            } catch (ClassNotFoundException e) {
                throw new ProtocolException("The network didn't answer with the correct object", ProtocolException.ErrorType.CONNECTION_ERROR);
            }
        }
        return null;
    }

            /**
     * Sends a new patient to the server to register it in the server's database
     *
     * @param patient the patient to be registered
     */
    public void registerPatient(Patient patient){

    }

}
