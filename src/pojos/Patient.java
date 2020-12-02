package pojos;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

public class Patient implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1130150939909222240L;


    private Integer id;
    private String name;
    private Date birthDate;
    private Sex sex;
    private List<RiskFactor> riskFactor;
    private String dni;
    private String password;

    public enum Sex {MALE, FEMALE}

    // CKD: Chronic Kidney Disease, Chronic Obstructive Pulmonary Disease
    public enum RiskFactor {CANCER, CKD, COPD, HEART_CONDITIONS, IMMUNOCOMPROMISED, OBESITY, SMOKING, PREGNANCY, DIABETES2}


    public Patient() {
        super();
        this.name = "Joaquin";
        this.birthDate = Date.valueOf("1998-10-15");
        this.sex = Sex.MALE;
        this.riskFactor = null;
        this.dni = String.valueOf((Math.random()*1000));
        this.password = "Craneos";
    }

    public Patient(Integer id, String name, Date birthDate, Sex sex, List<RiskFactor> riskFactor) {
        super();
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.sex = sex;
        this.riskFactor = riskFactor;

    }

    public Patient(Integer id, String name, Date birthDate, Sex sex, List<RiskFactor> riskFactor, String dni, String password) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.sex = sex;
        this.riskFactor = riskFactor;
        this.dni = dni;
        this.password = password;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Patient other = (Patient) obj;
        if (id == null) {
            return other.id == null;
        } else return id.equals(other.id);
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthDate=" + birthDate +
                ", sex=" + sex +
                ", riskFactor=" + riskFactor +
                ", dni='" + dni + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public List<RiskFactor> getRiskFactor() {
        return riskFactor;
    }

    public void setRiskFactor(List<RiskFactor> riskFactor) {
        this.riskFactor = riskFactor;
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
