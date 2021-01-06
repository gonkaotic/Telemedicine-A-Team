package pojos;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Patient implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1130150939909222240L;


    private Integer id;
    private String name;
    private Integer doctorId;
    private Date birthDate;
    private Sex sex;
    private List<RiskFactor> riskFactor;
    private String dni;
    private String password;
    private ArrayList<Measurement> measurements;

    public enum Sex {
        MALE ( "Male"), FEMALE ( "Female");

        private final String name;
        private Sex(String name){
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    // CKD: Chronic Kidney Disease, Chronic Obstructive Pulmonary Disease
    public enum RiskFactor {
        CANCER("Cancer"), CKD("Chronic Kidney Disease"), COPD("Chronic Obstructive Pulmonary Disease"),
        HEART_CONDITIONS ("Heart Conditions"), IMMUNOCOMPROMISED( "Inmunocompromised"), OBESITY("Obesity"),
        SMOKING("Smoker"), PREGNANCY( "Pregnant"), DIABETES2("Diabetes Type 2");

        private final String name;
        private RiskFactor( String name ){
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }


    public Patient() {
        super();
        this.name = "Joaquin";
        this.birthDate = Date.valueOf("1998-10-15");
        this.sex = Sex.MALE;
        this.riskFactor = new ArrayList<RiskFactor>(Arrays. asList(RiskFactor.CANCER,RiskFactor.COPD,RiskFactor.IMMUNOCOMPROMISED));;
        this.dni = String.valueOf((Math.random()*1000));
        this.password = "Craneos";
        this.doctorId = 1;
        measurements = new ArrayList<>();

    }

    public Patient( String dni, String password) {
        super();
        this.dni = dni;
        this.password = password;
        measurements = new ArrayList<>();

    }



    public Patient(Integer id, String name, Integer doctorId, Date birthDate, Sex sex, List<RiskFactor> riskFactor) {
        super();
        this.id = id;
        this.name = name;
        this.doctorId = doctorId;
        this.birthDate = birthDate;
        this.sex = sex;
        this.riskFactor = riskFactor;
        measurements = new ArrayList<>();

    }

    public Patient(Integer id, String name, Integer doctorId, Date birthDate, Sex sex, List<RiskFactor> riskFactor, String dni, String password) {
        this.id = id;
        this.name = name;
        this.doctorId = doctorId;
        this.birthDate = birthDate;
        this.sex = sex;
        this.riskFactor = riskFactor;
        this.dni = dni;
        this.password = password;
        measurements = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patient patient = (Patient) o;
        return id.equals(patient.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", doctorId=" + doctorId +
                ", birthDate=" + birthDate +
                ", sex=" + sex +
                ", riskFactor=" + riskFactor +
                ", dni='" + dni + '\'' +
                ", password='" + password + '\'' +
                ", measurements=" + measurements +
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

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
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

    public ArrayList<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(ArrayList<Measurement> measurements) {
        this.measurements = measurements;
    }
}
