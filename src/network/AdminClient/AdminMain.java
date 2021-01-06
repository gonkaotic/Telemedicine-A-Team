package network.AdminClient;

import network.ProtocolException;
import pojos.Administrator;
import pojos.Doctor;
import pojos.Patient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.util.Calendar;
import java.util.LinkedList;


/**
 * The admin app is a console-based application
 */
public class AdminMain {
    private static BufferedReader console = null;
    private static AdminClient adminClient;
    private static Administrator admin = null;
    private static boolean connected = false;

    public static void main(String[] args) {
        console = new BufferedReader(new InputStreamReader(System.in));
        boolean login=false;
        while (!connected) {
            connectToServer();
        }
        do {
            login = login();
        }while (!login);

        while(true){
            int option = displayMenu();
            switch (option){
                case 1:
                    registerPatient();
                    break;
                case 2:
                    registerDoctor();
                    break;
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
        System.out.println("\n---------------- MENU ----------------\n" +
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
    private static void connectToServer() {
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
            connected = adminClient.connect();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Problems with the console");
            connected=false;
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
            if(connected) {
                System.out.print("DNI: ");
                String dni = console.readLine();
                System.out.print("Password: ");
                String password = console.readLine();

                Administrator adminData = new Administrator(dni, password);
                admin = adminClient.login(adminData);
                if (admin == null) connected = false;
            }
            else{
                connected= adminClient.connect();
                admin=null;
            }
            return (admin!=null);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (ProtocolException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets all data required to register a patient. And calls the AdminClient's registerPatient method
     */
    private static void registerPatient() {
        try {
            LinkedList<Doctor> doctors =adminClient.getRegisteredDoctors();
            if(doctors != null && !doctors.isEmpty()) {

                System.out.print("\n ------ PATIENT INFO ------- " +
                        "\nYou can type x at any time to cancel the resgistry\n");

                System.out.print("\nName:");
                String name = console.readLine();
                if (exitOption(name)) return;

                System.out.print("DNI: ");
                String dni = console.readLine();
                if (exitOption(dni)) return;
                while (!verifyDNI(dni)) {
                    System.out.print("Invalid DNI (should be 8 digits and a letter)");
                    System.out.print("\nDNI: ");
                    dni = console.readLine();
                    if (exitOption(dni)) return;
                }

                System.out.print("Password: ");
                String password = console.readLine();
                if (exitOption(password)) return;


                System.out.print("Sex (m/f): ");
                String sex = console.readLine();
                if (exitOption(sex)) return;
                while (!verifySex(sex)) {
                    System.out.println("Invalid sex. Only m/f options are allowed");
                    System.out.print("Sex (m/f): ");
                    sex = console.readLine();
                    if (exitOption(sex)) return;
                }
                Patient.Sex sex1 = getSex(sex);

                System.out.print("Date of birth (YYYY-MM-DD):");
                String date = console.readLine();
                if (exitOption(date)) return;
                while (!verifyDate(date)) {
                    System.out.println("Invalid date. Please check format and that it is before today's date");
                    System.out.print("Date of birth (YYYY-MM-DD):");
                    date = console.readLine();
                    if (exitOption(date)) return;
                }
                Date birthdate = Date.valueOf(date);

                System.out.println("Select one of the following doctors (enter his ID):");
                for(Doctor doc:doctors){
                    System.out.println("ID: "+doc.getId()+"\tName: "+doc.getName());
                }
                System.out.print("Enter the doctor's id: ");
                String id = console.readLine();
                while(!verifyId(id,doctors)){
                    System.out.println("Invalid id");
                    System.out.print("Enter the doctor's id: ");
                    id = console.readLine();
                }
                int doctorId = Integer.parseInt(id);

                System.out.println("From the risk factors below select y/n");
                LinkedList<Patient.RiskFactor> riskFactors = new LinkedList<>();
                System.out.print("Cancer (y/n): ");
                String confirmation = console.readLine();
                if (exitOption(confirmation)) return;
                while (!checkYesNo(confirmation)) {
                    System.out.println("Invalid input. Only y/n allowed");
                    System.out.print("Cancer (y/n): ");
                    confirmation = console.readLine();
                    if (exitOption(confirmation)) return;
                }
                if (checkConfirmation(confirmation)) riskFactors.add(Patient.RiskFactor.CANCER);

                System.out.print("Chronic Kidney Disease (y/n): ");
                confirmation = console.readLine();
                if (exitOption(confirmation)) return;
                while (!checkYesNo(confirmation)) {
                    System.out.println("Invalid input. Only y/n allowed");
                    System.out.print("Chronic Kidney Disease (y/n): ");
                    confirmation = console.readLine();
                    if (exitOption(confirmation)) return;
                }
                if (checkConfirmation(confirmation)) riskFactors.add(Patient.RiskFactor.CKD);

                System.out.print("Chronic Obstructive Pulmonary Disease (y/n): ");
                confirmation = console.readLine();
                if (exitOption(confirmation)) return;
                while (!checkYesNo(confirmation)) {
                    System.out.println("Invalid input. Only y/n allowed");
                    System.out.print("Chronic Obstructive Pulmonary Disease (y/n): ");
                    confirmation = console.readLine();
                    if (exitOption(confirmation)) return;
                }
                if (checkConfirmation(confirmation)) riskFactors.add(Patient.RiskFactor.COPD);

                System.out.print("Heart Conditions (y/n): ");
                confirmation = console.readLine();
                if (exitOption(confirmation)) return;
                while (!checkYesNo(confirmation)) {
                    System.out.println("Invalid input. Only y/n allowed");
                    System.out.print("Heart Conditions (y/n): ");
                    confirmation = console.readLine();
                    if (exitOption(confirmation)) return;
                }
                if (checkConfirmation(confirmation)) riskFactors.add(Patient.RiskFactor.HEART_CONDITIONS);

                System.out.print("Inmunocompromised (y/n): ");
                confirmation = console.readLine();
                if (exitOption(confirmation)) return;
                while (!checkYesNo(confirmation)) {
                    System.out.println("Invalid input. Only y/n allowed");
                    System.out.print("Inmunocompromised (y/n): ");
                    confirmation = console.readLine();
                    if (exitOption(confirmation)) return;
                }
                if (checkConfirmation(confirmation)) riskFactors.add(Patient.RiskFactor.IMMUNOCOMPROMISED);

                System.out.print("Obesity (y/n): ");
                confirmation = console.readLine();
                if (exitOption(confirmation)) return;
                while (!checkYesNo(confirmation)) {
                    System.out.println("Invalid input. Only y/n allowed");
                    System.out.print("Obesity (y/n): ");
                    confirmation = console.readLine();
                    if (exitOption(confirmation)) return;
                }
                if (checkConfirmation(confirmation)) riskFactors.add(Patient.RiskFactor.OBESITY);

                System.out.print("Smoker (y/n): ");
                confirmation = console.readLine();
                if (exitOption(confirmation)) return;
                while (!checkYesNo(confirmation)) {
                    System.out.println("Invalid input. Only y/n allowed");
                    System.out.print("Smoker (y/n): ");
                    confirmation = console.readLine();
                    if (exitOption(confirmation)) return;
                }
                if (checkConfirmation(confirmation)) riskFactors.add(Patient.RiskFactor.SMOKING);

                System.out.print("Pregnancy (y/n): ");
                confirmation = console.readLine();
                if (exitOption(confirmation)) return;
                while (!checkYesNo(confirmation)) {
                    System.out.println("Invalid input. Only y/n allowed");
                    System.out.print("Pregnancy (y/n): ");
                    confirmation = console.readLine();
                    if (exitOption(confirmation)) return;
                }
                if (checkConfirmation(confirmation)) riskFactors.add(Patient.RiskFactor.PREGNANCY);

                System.out.print("Diabetes Type 2 (y/n): ");
                confirmation = console.readLine();
                if (exitOption(confirmation)) return;
                while (!checkYesNo(confirmation)) {
                    System.out.println("Invalid input. Only y/n allowed");
                    System.out.print("Diabetes Type 2 (y/n): ");
                    confirmation = console.readLine();
                    if (exitOption(confirmation)) return;
                }
                if (checkConfirmation(confirmation)) riskFactors.add(Patient.RiskFactor.DIABETES2);

                System.out.println("\nPatient data completed");
                Patient patient = new Patient(1,name,doctorId,birthdate, sex1,riskFactors,dni,password);
                System.out.println(patient);
               // adminClient.registerPatient(patient);
            }
            else{
                System.out.println("No doctors available. Please register a doctor before registering a patient");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
    }


    /**
     * Gets all data required to register a doctor. And calls the AdminClient's registerDoctor method
     */
    private static void registerDoctor() {
        try {
            System.out.print("\n ------ DOCTOR INFO ------- " +
                    "\nYou can type x at any time to cancel the resgistry\n");

            System.out.print("\nName:");
            String name = console.readLine();
            if (exitOption(name)) return;

            System.out.print("DNI: ");
            String dni = console.readLine();
            if (exitOption(dni)) return;
            while (!verifyDNI(dni)) {
                System.out.print("Invalid DNI (should be 8 digits and a letter)");
                System.out.print("\nDNI: ");
                dni = console.readLine();
                if (exitOption(dni)) return;
            }

            System.out.print("Password: ");
            String password = console.readLine();
            if (exitOption(password)) return;

            Doctor doctor = new Doctor(dni,password,name);

            System.out.println("Doctor data completed");
            adminClient.registerDoctor(doctor);

        } catch (ProtocolException e) {
            System.out.println(e.getErrorMessage());

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

    private static Patient.Sex getSex(String sex){
        if(sex.equalsIgnoreCase("f")) return Patient.Sex.FEMALE;
        return Patient.Sex.MALE;
    }

    private static boolean exitOption(String reading){
        return (reading.equalsIgnoreCase("x"));
    }

    private static boolean verifyDate(String date){
        try{
            Date.valueOf(date);
            if (Date.valueOf(date).compareTo(new Date(Calendar.getInstance().getTime().getTime())) >= 0) return false; // introduced date is bigger than actual date
            return true;
        }catch(IllegalArgumentException ex){
            return false;
        }
    }

    private static boolean verifyId(String id, LinkedList<Doctor> doctors){
        try{
            int doctorId = Integer.parseInt(id);
            for (int i = 0; i<doctors.size();i++){
                if (doctorId == doctors.get(i).getId()) return true;
            }
            return false;
        }catch(NumberFormatException ex){
            return false;
        }
    }

    private static boolean checkYesNo(String reading){
        return reading.equalsIgnoreCase("y")||reading.equalsIgnoreCase("n");
    }

    private static boolean checkConfirmation(String confirmation){
        return confirmation.equalsIgnoreCase("y");
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
