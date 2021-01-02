package network.AdminClient;

import network.ProtocolException;
import pojos.Administrator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * The admin app is a console-based application
 */
public class AdminMain {
    private static BufferedReader console = null;
    private static AdminClient adminClient;
    private static Administrator admin = null;

    public static void main(String[] args) {
        console = new BufferedReader(new InputStreamReader(System.in));
        boolean login=false;
        do {
            while (!connectToServer()) ;
            login = login();
        }while (!login);

        while(true){
            int option = displayMenu();
        }


    }

    /**
     * displays the admin menu is console
     *
     * @return the option's number or -1 if an invalid entry was specified
     */
    private static int displayMenu() {
        System.out.println("---------------- MENU ----------------\n" +
                "1. Register patient\n" +
                "2. Register doctor\n" +
                "3. Register admin\n" +
                "4. Shutdown server\n" +
                "5. Exit");

        System.out.print("Enter a number indicating the task to perform: ");
        try {
            String reading = console.readLine();
            int option = Integer.parseInt(reading);
            return option;
        } catch (IOException e) {
            System.out.println("Error during reading");
            return -1;
        } catch (NumberFormatException ex) {
            System.out.println("Invalid option");
            return -1;
        }
    }

    /**
     * Starts connection with the server by opening a socket against the server
     *
     * @return
     *      true if everything went ok
     *      false if an error occurred during the process
     */
    private static boolean connectToServer() {
        try {
            System.out.print("Introduce the server's IP: ");
            String serverIp = console.readLine();
            boolean validIP = serverIp.matches("((([01]?([0-9]){1,2})|(2[0-4][0-9])|(25[0-5]))\\.){3}(([01]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))") ||
                    serverIp.matches("localhost");
            while (!validIP) {
                System.out.print("\nIncorrect IP address. Please enter a valid address: ");
                serverIp = console.readLine().trim();
                validIP = serverIp.matches("((([01]?([0-9]){1,2})|(2[0-4][0-9])|(25[0-5]))\\.){3}(([01]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))") ||
                        serverIp.matches("localhost");
            }

            adminClient = new AdminClient(serverIp);
            boolean connected = adminClient.connect();
            return connected;

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Problems with the console");
            return false;
        }
    }

    /**
     * Gets the info from the admin needed to login in the server
     * @return
     *      true if login was successful
     *      false otherwise
     */
    private static boolean login() {
        try {
            System.out.print("DNI: ");
            String dni = console.readLine();
            System.out.println("Password: ");
            String password = console.readLine();

            Administrator adminData = new Administrator(dni,password);
            admin = adminClient.login(adminData);
            return (admin!=null);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (ProtocolException e) {
            e.printStackTrace();
            return false;
        }
    }

}
