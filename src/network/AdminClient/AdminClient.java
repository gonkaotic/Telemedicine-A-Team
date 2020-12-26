package network.AdminClient;

import network.Client;
import pojos.Patient;

public class AdminClient extends Client {

    public AdminClient (String serverIP){
        this.serverIP=serverIP;
    }

    /**
     * Sends a new patient to the server to register it in the server's database
     *
     * @param patient the patient to be registered
     */
    public void registerPatient(Patient patient){

    }

}
