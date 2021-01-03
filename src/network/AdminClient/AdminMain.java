package network.AdminClient;

import network.ProtocolException;
import pojos.Administrator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;


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
        while (!connectToServer()) ;
        do {
            login = login();
        }while (!login);

        while(true){
            int option = displayMenu();
            switch (option){
                case 1:
                    registerPatient();
                    break;
                case 2: break;
                case 3: break;
                case 4: break;
                case 5:
                    System.out.println("Exiting ...");
                    adminClient.disconnect();
                    closeConsole();
                    System.exit(0);

                default:
                    System.out.println("Invalid option");
            }
        }


    }

    /**
     * displays the admin menu in console
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

    private static void registerPatient() {
        try {
            System.out.print("\n ------ PATIENT INFO ------- " +
                    "\nYou can type x at any time to cancel the resgistry\n");

            System.out.print("\nDNI: ");
            String dni = console.readLine();
            if(exitOption(dni)) return;
            while (!verifyDNI(dni)) {
                System.out.print("\nInvalid DNI (should be 8 digits and a letter)");
                System.out.print("\nDNI: ");
                dni = console.readLine();
                if(exitOption(dni)) return;
            }

            System.out.print("\nPassword: ");
            String password = console.readLine();
            if(exitOption(password)) return;


            System.out.print("\nSex (m/f): ");
            String sex = console.readLine();
            while(!verifySex(sex)){
                System.out.println("Invalid sex. Only m/f options are allowed");
                System.out.print("Sex (m/f): ");
                sex = console.readLine();
                if(exitOption(sex)) return;
            }

            System.out.print("\nDate of birth (YYYY-MM-DD):");
            String date = console.readLine();
            while(!verifyDate(date)){

            }

            System.out.println("Select one of the following doctors (enter his DNI):");
            //TODO: Print all doctors and get one

            System.out.println("From the risk factors below select y/n");
            System.out.print("Cancer (y/n): ");
            System.out.print("\nChronic Kidney Disease (y/n): ");
            System.out.print("\nChronic Obstructive Pulmonary Disease (y/n): ");
            System.out.print("\nHeart Conditions (y/n): ");
            System.out.print("\nInmunocompromised (y/n): ");
            System.out.print("\nObesity (y/n): ");
            System.out.print("\nSmoker (y/n): ");
            System.out.print("\nPregnancy (y/n): ");
            System.out.print("\nDiabetes Type 2 (y/n): ");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Verifies that DNI is a combination of 8 numbers and a letter: does not evaluate that the letter is correct
     * @param dni
     * @return
     *      true if verification succeeded
     *      false otherwise
     */
    private static boolean verifyDNI(String dni){
        return dni.matches("[0-9]{8}[a-zA-Z]");
    }

    private static boolean verifySex(String sex){
        return sex.equalsIgnoreCase("f")||sex.equalsIgnoreCase("m");
    }


    private static boolean exitOption(String reading){
        return (reading.equalsIgnoreCase("x"));
    }

    private static boolean verifyDate(String date){
        try{
            Date.valueOf(date);
            return true;
        }catch(IllegalArgumentException ex){
            return false;
        }
    }

    private static void closeConsole(){
        if(console != null){
            try{
                console.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
