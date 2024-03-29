package pojos;

import java.io.Serializable;
import java.util.Objects;

public class Administrator implements Serializable {


    private String dni;
    private String password;

    public Administrator ( ) {
        this.dni = "22222222Y";
        this.password = "Craneos";
    }

    public Administrator (String dni, String password){
        this.dni=dni;
        this.password=password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Administrator that = (Administrator) o;
        return dni.equals(that.dni);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dni);
    }

    @Override
    public String toString() {
        return "Administrator{" +
                "dni='" + dni + '\'' +
                ", password='" + password + '\'' +
                '}';
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
}
