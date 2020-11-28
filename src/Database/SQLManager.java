package Database;
import pojos.ECG;
import pojos.Measurement;
import pojos.Patient;
import pojos.Patient.Sex;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SQLManager {

    private static Connection c;

    public static void main(String[] args) {
        try {
            connect("jdbc:sqlite:././Database/covid watchlist.db");

            //Measurement measurement = new Measurement();
            //insertMeasurement(measurement);

            Measurement measurement = searchMeasurementByID(4);

            System.out.println(measurement.toString());

            disconnect();
        } catch (Exception e) {
            System.out.println("Error in connecting/generating/disconnecting from the database: " + e);
            e.printStackTrace();
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
                + " measure_date DATE NOT NULL," + " ecg BLOB," + " bpm INT NOT NULL,"
                + " o2_saturation FLOAT," + " temperature FLOAT NOT NULL," + " symptoms TEXT,"
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

    public static void insertMeasurement(Measurement measurement) throws SQLException, IOException {
    	String sql1 = "INSERT INTO measures(measure_date,ecg, bpm, o2_saturation,temperature,symptoms,patient_id)" + "VALUES(?,?,?,?,?,?,?);";

    	PreparedStatement prep = c.prepareStatement(sql1);

        Calendar calendar = Calendar.getInstance();
        java.util.Date currentDate = calendar.getTime();
        Date date = new Date(currentDate.getTime());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(measurement.getECG());
        byte[] ecgAsBytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(ecgAsBytes);

        prep.setDate(1, date);
        prep.setBinaryStream(2, bais,ecgAsBytes.length);
        prep.setInt(3,measurement.getBPM());
        prep.setFloat(4,measurement.getSpO2());
        prep.setFloat(5, measurement.getTemperature());
        prep.setString(6,symptomsToBinaryString(measurement.getSymptomChecklist()));
        prep.setInt(7,measurement.getPatient().getId());

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

        if (id.equals(patient.getId())) {
            prep.close();
            rs1.close();
            return patient;
        } else {

            prep.close();
            rs1.close();
            return null;
        }

    }

    public static Patient searchPatientByDniAndPassword(String dni, String password) throws SQLException {

        String sql="SELECT * FROM patient WHERE dni = ? AND password = ? ;";
        PreparedStatement prep = c.prepareStatement(sql);

        prep.setString(1, dni);
        prep.setString(2, password);

        ResultSet rs1 = prep.executeQuery();
        Patient patient = getPatient(rs1);

        if	(dni.equals(patient.getDni()) && password.equals(patient.getPassword())) {
            prep.close();
            rs1.close();
            return patient;
        }else {
            System.out.println("Wrong Id or password");
            prep.close();
            rs1.close();
            return null;
        }

    }

    public static Patient searchPatientByDni(String dni) throws SQLException {
		
		String sql="SELECT * FROM patient WHERE dni = ? ;";
		PreparedStatement prep = c.prepareStatement(sql);
		
		prep.setString(1, dni);
		
		
		ResultSet rs1 = prep.executeQuery();	
		Patient patient = getPatient(rs1);
				
		if	(dni.equals(patient.getDni())) {
			prep.close();
			rs1.close();
			return patient;
		}else {
			System.out.println("Wrong dni\nDni: "+dni+"\nclient: "+patient.getDni());
			prep.close();
			rs1.close();
			return null;
		}

	}

    public static List<Patient> getAllPatients() throws SQLException {

        String sql = "SELECT * FROM patient ;";
        PreparedStatement prep = c.prepareStatement(sql);
        ResultSet rs1 = prep.executeQuery();
        List<Patient> patientList = new ArrayList<>();
        while (rs1.next()) {
            patientList.add(getPatient(rs1));
        }
        rs1.close();
        prep.close();
        return patientList;
    }

    public static Measurement searchMeasurementByID(Integer id) throws SQLException, IOException, ClassNotFoundException {

        String sql = "SELECT * FROM measures WHERE measure_id = ? ;";

        PreparedStatement prep = c.prepareStatement(sql);

        prep.setInt(1, id);

        ResultSet rs1 = prep.executeQuery();
        if (!rs1.isBeforeFirst()) {
            prep.close();
            return null;
        }

        Measurement measurement = getMeasurement(rs1);

        if (id.equals(measurement.getId())) {
            prep.close();
            rs1.close();
            return measurement;
        } else {

            prep.close();
            rs1.close();
            return null;
        }

    }

    public static List<Measurement> searchMeasurementByPatientId(Integer patientId) throws SQLException, IOException, ClassNotFoundException {

        String sql = "SELECT * FROM measures WHERE patient_id = ? ;";

        PreparedStatement prep = c.prepareStatement(sql);

        prep.setInt(1, patientId);

        ResultSet rs1 = prep.executeQuery();

        if (!rs1.isBeforeFirst()) {
            prep.close();
            return null;
        }

        List<Measurement> measuresList = new ArrayList<>();

        while (rs1.next()) {
            measuresList.add(getMeasurement(rs1));
        }

            prep.close();
            rs1.close();
            return measuresList;

    }

    public static List<Measurement> getAllMeasurements() throws SQLException, IOException, ClassNotFoundException {

        String sql = "SELECT * FROM measures ;";
        PreparedStatement prep = c.prepareStatement(sql);
        ResultSet rs1 = prep.executeQuery();
        List<Measurement> measuresList = new ArrayList<>();
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

        //TODO risk factors missing

        return patient;

    }

    private static Measurement getMeasurement(ResultSet rs1) throws SQLException, IOException, ClassNotFoundException {

        Measurement measurement = new Measurement();
        measurement.setId(rs1.getInt("measure_id"));
        measurement.setDate(rs1.getDate("measure_date"));

        byte[] st = (byte[]) rs1.getObject("ecg");
        ByteArrayInputStream baip = new ByteArrayInputStream(st);
        ObjectInputStream ois = new ObjectInputStream(baip);
        ECG ecg = (ECG) ois.readObject();

        measurement.setECG(ecg);
        measurement.setSpO2(rs1.getFloat("o2_saturation"));
        measurement.setBPM(rs1.getInt("bpm"));
        measurement.setTemperature(rs1.getFloat("temperature"));
        measurement.setSymptomChecklist(binaryStringToSymptoms(rs1.getString("symptoms")));
        measurement.setPatient(searchPatientByID(rs1.getInt("patient_id")));

        return measurement;

    }

    //Symptom {FEVER, DRY_COUGH, TIREDNESS, ANOSMIA, AUGEUSIA, DIFF_BREATH, CHEST_PAIN};

    private static String symptomsToBinaryString(List<Measurement.Symptom> symptomChecklist){

        Integer i, size = symptomChecklist.size(), binaryChain = 0;
        String binaryFlag;

        for(i = 0; i < size; i++){
            if(symptomChecklist.get(i).equals(Measurement.Symptom.FEVER)){
                binaryChain = binaryChain + 1000000;
            }else if (symptomChecklist.get(i).equals(Measurement.Symptom.DRY_COUGH)) {
                binaryChain = binaryChain + 100000;
            }else if(symptomChecklist.get(i).equals(Measurement.Symptom.TIREDNESS)){
                binaryChain = binaryChain + 10000;
            }else if (symptomChecklist.get(i).equals(Measurement.Symptom.ANOSMIA)) {
                binaryChain = binaryChain + 1000;
            }else if(symptomChecklist.get(i).equals(Measurement.Symptom.AUGEUSIA)){
                binaryChain = binaryChain + 100;
            }else if (symptomChecklist.get(i).equals(Measurement.Symptom.DIFF_BREATH)) {
                binaryChain = binaryChain + 10;
            }else if(symptomChecklist.get(i).equals(Measurement.Symptom.CHEST_PAIN)){
                binaryChain = binaryChain + 1;
            }
        }

        binaryFlag = binaryChain.toString();

        return binaryFlag;
    }

    private static List<Measurement.Symptom> binaryStringToSymptoms(String binaryString){

        int binarySymptom;
        List<Measurement.Symptom> symptomsList = new ArrayList<>();

        for (int i = 0; i < binaryString.length(); i++) {

            binarySymptom = Character.getNumericValue(binaryString.charAt(i));

            if(binarySymptom == 1 && i == 0){
                symptomsList.add(Measurement.Symptom.FEVER);
            }else if (binarySymptom == 1 && i == 1) {
                symptomsList.add(Measurement.Symptom.DRY_COUGH);
            }else if(binarySymptom == 1 && i == 2){
                symptomsList.add(Measurement.Symptom.TIREDNESS);
            }else if (binarySymptom == 1 && i == 3) {
                symptomsList.add(Measurement.Symptom.ANOSMIA);
            }else if(binarySymptom == 1 && i == 4){
                symptomsList.add(Measurement.Symptom.AUGEUSIA);
            }else if (binarySymptom == 1 && i == 5) {
                symptomsList.add(Measurement.Symptom.DIFF_BREATH);
            }else if(binarySymptom == 1 && i == 6){
                symptomsList.add(Measurement.Symptom.CHEST_PAIN);
            }
        }

        return symptomsList;
    }

}



