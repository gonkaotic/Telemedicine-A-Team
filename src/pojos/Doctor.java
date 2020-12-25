package pojos;

public class Doctor {

    private String dni;
    private String password;
    private String name;


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
}
