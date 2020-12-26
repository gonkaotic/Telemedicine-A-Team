package pojos;

public class Administrator {


    private String dni;
    private String password;

    public Administrator ( ) {
        this.dni = "22222222Y";
        this.password = "Craneos";
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
