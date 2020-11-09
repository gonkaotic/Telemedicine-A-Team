package Database;

import pojos.Measurement;
import pojos.Patient;
import pojos.Patient.Sex;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLManager {

    private static Connection c;

    public static void main(String[] args) {
        try {
            connect("jdbc:sqlite:././Database/covid watchlist.db");

            //generateDataBase();

            List<Patient> allPatients = getAllPatients();
            //List<Measurement> measurements = getAllMeasurements();


            disconnect();
        } catch (Exception e) {
            System.out.println("Error in connecting/generating/disconnecting from the database: " + e);
        }

    }

    private static void generateDataBase() throws SQLException {
        createTablePatients();
        createTableMeasures();
    }

    public static void connect(String directory) throws ClassNotFoundException, SQLException {

        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection(directory);
        c.createStatement().execute("PRAGMA foreign_keys=ON");
        System.out.println("Database connection opened.");

    }

    public static void disconnect() throws SQLException {
        if (c != null) {
            c.close();
            System.out.println("Database connection closed.");
        }

    }

    /*
     *
     * Table creation methods
     *
     */

    public static void createTablePatients() throws SQLException {
        Statement stmt = c.createStatement();
        String table = "CREATE TABLE patient(\r\n" +
                "patient_id INTEGER PRIMARY KEY AUTOINCREMENT,\r\n" +
                "name TEXT NOT NULL,\r\n" +
                "date_birth DATE NOT NULL,\r\n" +
                "sex TEXT NOT NULL, \r\n" +
                "risk_factors TEXT, \r\n" +
                "dni TEXT UNIQUE NOT NULL, \r\n" +
                "password TEXT NOT NULL);";
        stmt.executeUpdate(table);
        stmt.close();

    }

    public static void createTableMeasures() throws SQLException {
        Statement stmt1 = c.createStatement();
        String sql1 = "CREATE TABLE measures " + "(measure_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " measure_date DATETIME DEFAULT CURRENT_DATETIME," + " ecg TEXT," + " bpm INT NOT NULL,"
                + " o2_saturation FLOAT," + " temperature FLOAT NOT NULL,"
                + " patient_id REFERENCES patient (patient_id) ON UPDATE CASCADE ON DELETE CASCADE );";
        stmt1.executeUpdate(sql1);
        stmt1.close();
    }

    /*
     *
     * Inserts
     *
     */

    public static void insertPatient(Patient patient) throws SQLException {

        String sql1 = "INSERT INTO patient(name, date_birth, sex, risk_factors, dni, password)" + "VALUES(?,?,?,?,?,?);";

        PreparedStatement prep = c.prepareStatement(sql1);
        prep.setString(1, patient.getName());
        prep.setDate(2, patient.getBirthDate());
        
        if (patient.getSex().equals(Sex.FEMALE)) {
            prep.setString(3, "FEMALE");
        } else {
            prep.setString(3, "MALE");
        }

        // Faltan los risk factors

        prep.setString(5, patient.getDni());
        prep.setString(6, patient.getPassword());

        prep.executeUpdate();
        prep.close();
    }

    public static void insertMeasurement(Measurement measurement) throws SQLException {
    	String sql1 = "INSERT INTO measures(ecg, bpm, o2_saturation,temperature,patient_id)" + "VALUES(?,?,?,?,?);";

    	PreparedStatement prep = c.prepareStatement(sql1);
    	prep.setString(1, measurement.getECG().toString());
        prep.setInt(2,measurement.getBPM());
        prep.setFloat(3,measurement.getSpO2());
        prep.setFloat(4, measurement.getTemperature());
        prep.setInt(5,measurement.getPatient().getId());

        prep.executeUpdate();
        prep.close();
	}

    /*
     *
     * Reads
     *
     */

    public static Patient searchPatientByID(Integer id) throws SQLException {
        String sql = "SELECT * FROM patient WHERE patient_id = ? ;";
        PreparedStatement prep = c.prepareStatement(sql);

        prep.setInt(1, id);

        ResultSet rs1 = prep.executeQuery();
        if (!rs1.isBeforeFirst()) {
            prep.close();
            return null;
        }

        Patient patient = getPatient(rs1);

        if (id == patient.getId()) {
            prep.close();
            rs1.close();
            return patient;
        } else {

            prep.close();
            rs1.close();
            return null;
        }

    }

    public static Measurement searchMeasurementByID(Integer id) throws SQLException {

        String sql = "SELECT * FROM measures WHERE measure_id = ? ;";

        PreparedStatement prep = c.prepareStatement(sql);

        prep.setInt(1, id);

        ResultSet rs1 = prep.executeQuery();
        if (!rs1.isBeforeFirst()) {
            prep.close();
            return null;
        }

        Measurement measurement = getMeasurement(rs1);

        if (id == measurement.getId()) {
            prep.close();
            rs1.close();
            return measurement;
        } else {

            prep.close();
            rs1.close();
            return null;
        }

    }

    public static List<Patient> getAllPatients() throws SQLException {

        String sql = "SELECT * FROM patient ;";
        PreparedStatement prep = c.prepareStatement(sql);
        ResultSet rs1 = prep.executeQuery();
        List<Patient> patientList = new ArrayList<Patient>();
        while (rs1.next()) {
            patientList.add(getPatient(rs1));
        }
        rs1.close();
        prep.close();
        return patientList;
    }

    public static List<Measurement> getAllMeasurements() throws SQLException {

        String sql = "SELECT * FROM measures ;";
        PreparedStatement prep = c.prepareStatement(sql);
        ResultSet rs1 = prep.executeQuery();
        List<Measurement> measuresList = new ArrayList<Measurement>();
        while (rs1.next()) {
            measuresList.add(getMeasurement(rs1));
        }
        rs1.close();
        prep.close();
        return measuresList;

    }

    /*
     *
     * Private Get Methods
     *
     */

    private static Patient getPatient(ResultSet rs1) throws SQLException {

        Patient patient = new Patient();
        patient.setId(rs1.getInt("patient_id"));
        patient.setName(rs1.getString("name"));
        patient.setBirthDate(rs1.getDate("date_birth"));
        if (rs1.getString("sex").equals("MALE")) {
            patient.setSex(Sex.MALE);
        } else if (rs1.getString("sex").equals("FEMALE")) {
            patient.setSex(Sex.FEMALE);
        }
        patient.setDni(rs1.getString("dni"));
        patient.setPassword(rs1.getString("password"));

        //Falta risk factor

        return patient;

    }

    private static Measurement getMeasurement(ResultSet rs1) throws SQLException {

        Measurement measurement = new Measurement();
        measurement.setId(rs1.getInt("measure_id"));
        measurement.setDate(rs1.getDate("measure_date"));
        measurement.setECG((List<Float>) rs1.getArray("ecg"));
        measurement.setSpO2(rs1.getFloat("o2_saturation"));
        measurement.setBPM(rs1.getInt("bpm"));
        measurement.setTemperature(rs1.getFloat("temperature"));
        measurement.setPatient(searchPatientByID(rs1.getInt("patient_id")));
        //FALTA SYMPTOMS!!!!!!!!!!!!!!!!!!!
        return measurement;

    }
}



