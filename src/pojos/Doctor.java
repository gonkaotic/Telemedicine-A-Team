package pojos;

import java.util.ArrayList;
import java.util.Objects;

public class Doctor {

    private Integer id;
    private String dni;
    private String password;
    private String name;
    private ArrayList<Patient> patients;


    public Doctor () {
        this.dni = "11111111Y";
        this.password = "Craneos";
        this.name = "Juan";
    }

    public Doctor ( String dni, String password){
        this.dni = dni;
        this.password = password;
    }

    public Doctor(String dni, String password, String name) {
        this.dni = dni;
        this.password = password;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Doctor doctor = (Doctor) o;
        return dni.equals(doctor.dni);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dni);
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "dni='" + dni + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", patients=" + patients +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
