package network.client;

import network.ProtocolException;
import pojos.Measurement;
import pojos.Patient;

import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;

public class NetworkTester {

    public static void main(String[] args) {

        Client client = new Client( "localhost");
        if ( client.connect() ) {
            System.out.println("Connection Successful");
            try {

                Patient patient = client.sendToServer(new Patient());
                System.out.println("Patient requested");
                if (patient != null) {
                    ArrayList<Measurement> measurements = new ArrayList<Measurement>();
                    measurements.add(new Measurement());
                    client.sendToServer(measurements);
                    System.out.println("measurements sent");
                    client.disconnect();
                } else {
                    System.out.println("Patient not found. The connection is closed");
                }
            } catch (ProtocolException e) {
                System.out.println("There was an error in the process");
                e.printStackTrace();
            }
        } else {
            System.out.println("Connection unsuccessful");
        }

    }
}
