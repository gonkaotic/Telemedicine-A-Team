package network.client;

import pojos.Measurement;
import pojos.Patient;

import java.io.IOException;
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
                } else {
                    System.out.println("Patient not found");
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            } finally {
                client.disconnect();
            }
        } else {
            System.out.println("Connection unsuccessful");
        }

    }
}
