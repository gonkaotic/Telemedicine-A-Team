package pojos;

import java.util.ArrayList;

public class Doctor {

    private String dni;
    private String password;
    private String name;

    private ArrayList<Patient> patients;


    public Doctor () {

    }

    public Doctor ( String dni, String password){
        this.dni = dni;
        this.password = password;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Patient> getPatients() {
        return patients;
    }

    public void setPatients(ArrayList<Patient> patients) {
        this.patients = patients;
    }
}
