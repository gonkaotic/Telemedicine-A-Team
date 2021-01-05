package Database;

import pojos.*;
import pojos.Patient.Sex;
import security.*;

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

            Doctor doctor = getDoctorByDniAndPassword("11111111Y","Craneos");
            System.out.println(doctor.toString());

            disconnect();
        } catch (Exception e) {
            System.out.println("Error in connecting/generating/disconnecting from the database: " + e);
            e.printStackTrace();
        }

    }

    private static void generateDataBase() throws SQLException, IOException {
        createTablePatients();
        createTableMeasures();
        createTableDoctor();
        createTableAdmin();
    }

    public static void connect(String directory) throws ClassNotFoundException, SQLException, IOException {

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
                "password TEXT NOT NULL,\r\n" +
                "doctor_id INTEGER REFERENCES doctor (doctor_id) ON UPDATE CASCADE ON DELETE CASCADE );";
        stmt.executeUpdate(table);
        stmt.close();

    }

    public static void createTableMeasures() throws SQLException {
        Statement stmt1 = c.createStatement();
        String sql1 = "CREATE TABLE measures " + "(measure_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " measure_date DATE NOT NULL," + " ecg BLOB," + " bpm INT NOT NULL,"
                + " o2_saturation FLOAT," + " temperature FLOAT NOT NULL," + " symptoms TEXT," + "comment TEXT,"
                + " patient_id INTEGER REFERENCES patient (patient_id) ON UPDATE CASCADE ON DELETE CASCADE );";
        stmt1.executeUpdate(sql1);
        stmt1.close();
    }

    public static void createTableDoctor() throws SQLException {
        Statement stmt1 = c.createStatement();
        String sql1 = "CREATE TABLE doctor(\r\n" +
                "doctor_id INTEGER PRIMARY KEY AUTOINCREMENT,\r\n" +
                "name TEXT NOT NULL,\r\n" +
                "dni TEXT UNIQUE NOT NULL, \r\n" +
                "password TEXT NOT NULL);";
        stmt1.executeUpdate(sql1);
        stmt1.close();
    }

    public static void createTableAdmin() throws SQLException {
        Statement stmt1 = c.createStatement();
        String sql1 = "CREATE TABLE admin(\r\n" +
                "admin_id INTEGER PRIMARY KEY AUTOINCREMENT,\r\n" +
                "dni TEXT UNIQUE NOT NULL, \r\n" +
                "password TEXT NOT NULL);";
        stmt1.executeUpdate(sql1);
        stmt1.close();
    }

    /*
     *
     * Inserts
     *
     */

    public static void insertPatient(Patient patient) throws SQLException {

        String sql1 = "INSERT INTO patient(name, date_birth, sex, risk_factors, dni, password,doctor_id)" +
                "VALUES(?,?,?,?,?,?,?);";

        PasswordAuthentication passwordAuthentication = new PasswordAuthentication();
        PreparedStatement prep = c.prepareStatement(sql1);
        prep.setString(1, patient.getName());
        prep.setDate(2, patient.getBirthDate());
        
        if (patient.getSex().equals(Sex.FEMALE)) {
            prep.setString(3, "FEMALE");
        } else {
            prep.setString(3, "MALE");
        }
        prep.setString(4, rFactorToBinaryString(patient.getRiskFactor()));
        prep.setString(5, patient.getDni());
        prep.setString(6, passwordAuthentication.hash(patient.getPassword()));
        prep.setInt(7,patient.getDoctorId());

        prep.executeUpdate();
        prep.close();
    }

    public static void insertMeasurements ( ArrayList< Measurement> measurements) throws IOException, SQLException {
        for (Measurement measurement : measurements) {
            insertMeasurement(measurement);
        }
    }

    public static void insertMeasurement(Measurement measurement) throws SQLException, IOException {
    	String sql1 = "INSERT INTO measures(measure_date,ecg, bpm, o2_saturation,temperature,symptoms, patient_id)"
                + "VALUES(?,?,?,?,?,?,?);";

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
        prep.setInt(7,measurement.getPatientId());

        prep.executeUpdate();
        prep.close();
	}

    public static void insertDoctor(Doctor doctor) throws SQLException {

        String sql1 = "INSERT INTO doctor(name, dni, password)" + "VALUES(?,?,?);";

        PasswordAuthentication passwordAuthentication = new PasswordAuthentication();
        PreparedStatement prep = c.prepareStatement(sql1);
        prep.setString(1, doctor.getName());
        prep.setString(2, doctor.getDni());
        prep.setString(3, passwordAuthentication.hash(doctor.getPassword()));

        prep.executeUpdate();
        prep.close();
    }

    public static void insertAdmin(Administrator admin) throws SQLException {

        String sql1 = "INSERT INTO admin(dni, password)" + "VALUES(?,?);";

        PasswordAuthentication passwordAuthentication = new PasswordAuthentication();
        PreparedStatement prep = c.prepareStatement(sql1);
        prep.setString(1, admin.getDni());
        prep.setString(2, passwordAuthentication.hash(admin.getPassword()));

        prep.executeUpdate();
        prep.close();
    }

    /*
     *
     * Reads
     *
     */

    public static Patient getPatientByID(Integer id) throws SQLException, IOException, ClassNotFoundException {
        String sql = "SELECT * FROM patient WHERE patient_id = ? ;";
        PreparedStatement prep = c.prepareStatement(sql);

        prep.setInt(1, id);

        ResultSet rs1 = prep.executeQuery();
        if (!rs1.isBeforeFirst()) {
            prep.close();
            return null;
        }

        Patient patient = getPatient(rs1);
        patient.setMeasurements((ArrayList<Measurement>) getMeasurementsByPatientId(patient.getId()));

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

    public static Patient getPatientByDniAndPassword(String dni, String password) throws SQLException, IOException, ClassNotFoundException {

        String sql="SELECT * FROM patient WHERE dni = ?";
        PreparedStatement prep = c.prepareStatement(sql);

        prep.setString(1, dni);

        ResultSet rs1 = prep.executeQuery();
        Patient patient = getPatient(rs1);
        patient.setMeasurements((ArrayList<Measurement>) getMeasurementsByPatientId(patient.getId()));

        PasswordAuthentication passwordAuthentication = new PasswordAuthentication();

        if	(dni.equals(patient.getDni()) && passwordAuthentication.authenticate(password,patient.getPassword())) {
            prep.close();
            rs1.close();
            patient.setPassword(null);
            return patient;
        }else {
            System.out.println("Wrong Id or password");
            prep.close();
            rs1.close();
            return null;
        }

    }

    public static Patient getPatientByDni(String dni) throws SQLException, IOException, ClassNotFoundException {
		
		String sql="SELECT * FROM patient WHERE dni = ? ;";
		PreparedStatement prep = c.prepareStatement(sql);
		
		prep.setString(1, dni);
		
		
		ResultSet rs1 = prep.executeQuery();	
		Patient patient = getPatient(rs1);
		patient.setMeasurements((ArrayList<Measurement>) getMeasurementsByPatientId(patient.getId()));
				
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

    public static List<Patient> getPatientsByDoctorId(Integer doctorId) throws SQLException, IOException, ClassNotFoundException {

        Patient tempPatient;
        String sql = "SELECT * FROM patient WHERE doctor_id = ? ;";

        PreparedStatement prep = c.prepareStatement(sql);

        prep.setInt(1, doctorId);

        ResultSet rs1 = prep.executeQuery();

        if (!rs1.isBeforeFirst()) {
            prep.close();
            return null;
        }

        List<Patient> patientList = new ArrayList<>();

        while (rs1.next()) {
            tempPatient = getPatient(rs1);
            tempPatient.setMeasurements((ArrayList<Measurement>) getMeasurementsByPatientId(tempPatient.getId()));
            tempPatient.setDni(null);
            tempPatient.setPassword(null);
            patientList.add(tempPatient);
        }

        prep.close();
        rs1.close();
        return patientList;

    }

    public static List<Patient> getAllPatients() throws SQLException, IOException, ClassNotFoundException {

        Patient tempPatient;
        String sql = "SELECT * FROM patient ;";
        PreparedStatement prep = c.prepareStatement(sql);
        ResultSet rs1 = prep.executeQuery();
        List<Patient> patientList = new ArrayList<>();
        while (rs1.next()) {
            tempPatient = getPatient(rs1);
            tempPatient.setMeasurements((ArrayList<Measurement>) getMeasurementsByPatientId(tempPatient.getId()));
            patientList.add(tempPatient);
        }
        rs1.close();
        prep.close();
        return patientList;
    }

    public static Measurement getMeasurementByID(Integer id) throws SQLException, IOException, ClassNotFoundException {

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

    public static List<Measurement> getMeasurementsByPatientId(Integer patientId) throws SQLException, IOException, ClassNotFoundException {

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

    public static List<Measurement> getMeasurementsByPatientDni(String dni) throws SQLException, IOException, ClassNotFoundException {

        Patient patient = getPatientByDni(dni);
        Integer patientId = patient.getId();

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

    public static Doctor getDoctorByDniAndPassword(String dni, String password) throws SQLException, IOException, ClassNotFoundException {

        String sql="SELECT * FROM doctor WHERE dni = ? ;";
        PreparedStatement prep = c.prepareStatement(sql);

        prep.setString(1, dni);

        ResultSet rs1 = prep.executeQuery();
        Doctor doctor = getDoctor(rs1);
        doctor.setPatients((ArrayList<Patient>) getPatientsByDoctorId(doctor.getId()));

        PasswordAuthentication passwordAuthentication = new PasswordAuthentication();

        if	(dni.equals(doctor.getDni()) && passwordAuthentication.authenticate(password,doctor.getPassword())) {
            doctor.setPassword(null);
            prep.close();
            rs1.close();
            return doctor;
        }else {
            System.out.println("Wrong Id or password");
            prep.close();
            rs1.close();
            return null;
        }

    }

    public static List<Doctor> getAllDoctors() throws SQLException {

        Doctor tempDoctor;
        String sql = "SELECT * FROM doctor ;";
        PreparedStatement prep = c.prepareStatement(sql);
        ResultSet rs1 = prep.executeQuery();
        List<Doctor> doctorList = new ArrayList<>();
        while (rs1.next()) {
            tempDoctor = getDoctor(rs1);
            tempDoctor.setPassword(null);
            tempDoctor.setDni(null);

            doctorList.add(tempDoctor);
        }
        rs1.close();
        prep.close();
        return doctorList;
    }

    public static Administrator getAdminByDniAndPassword(String dni, String password) throws SQLException {

        String sql="SELECT * FROM admin WHERE dni = ? ;";
        PreparedStatement prep = c.prepareStatement(sql);

        prep.setString(1, dni);

        ResultSet rs1 = prep.executeQuery();
        Administrator admin = getAdmin(rs1);

        PasswordAuthentication passwordAuthentication = new PasswordAuthentication();

        if	(dni.equals(admin.getDni()) && passwordAuthentication.authenticate(password,admin.getPassword())) {
            prep.close();
            rs1.close();
            return admin;
        }else {
            System.out.println("Wrong Id or password");
            prep.close();
            rs1.close();
            return null;
        }

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
        patient.setRiskFactor(binaryStringToRFactor(rs1.getString("risk_factors")));
        patient.setDni(rs1.getString("dni"));
        patient.setPassword(rs1.getString("password"));
        patient.setDoctorId(rs1.getInt("doctor_id"));



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
        measurement.setPatientId(rs1.getInt("patient_id"));

        return measurement;

    }

    private static Doctor getDoctor(ResultSet rs1) throws SQLException {

        Doctor doctor = new Doctor();

        doctor.setId(rs1.getInt("doctor_id"));
        doctor.setName(rs1.getString("name"));
        doctor.setDni(rs1.getString("dni"));
        doctor.setPassword(rs1.getString("password"));

        return doctor;

    }

    private static Administrator getAdmin(ResultSet rs1) throws SQLException {

        Administrator admin = new Administrator();

        admin.setDni(rs1.getString("dni"));
        admin.setPassword(rs1.getString("password"));

        return admin;

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

    //RiskFactor {CANCER, CKD, COPD, HEART_CONDITIONS, IMMUNOCOMPROMISED, OBESITY, SMOKING, PREGNANCY, DIABETES2}

    private static String rFactorToBinaryString(List<Patient.RiskFactor> riskFactorList){

        Integer i, size = riskFactorList.size(), binaryChain = 0;
        String binaryFlag;

        for(i = 0; i < size; i++){
            if(riskFactorList.get(i).equals(Patient.RiskFactor.CANCER)){
                binaryChain = binaryChain + 100000000;
            }else if (riskFactorList.get(i).equals(Patient.RiskFactor.CKD)) {
                binaryChain = binaryChain + 10000000;
            }else if(riskFactorList.get(i).equals(Patient.RiskFactor.COPD)){
                binaryChain = binaryChain + 1000000;
            }else if (riskFactorList.get(i).equals(Patient.RiskFactor.HEART_CONDITIONS)) {
                binaryChain = binaryChain + 100000;
            }else if(riskFactorList.get(i).equals(Patient.RiskFactor.IMMUNOCOMPROMISED)){
                binaryChain = binaryChain + 10000;
            }else if (riskFactorList.get(i).equals(Patient.RiskFactor.OBESITY)) {
                binaryChain = binaryChain + 1000;
            }else if(riskFactorList.get(i).equals(Patient.RiskFactor.SMOKING)){
                binaryChain = binaryChain + 100;
            }else if (riskFactorList.get(i).equals(Patient.RiskFactor.PREGNANCY)) {
                binaryChain = binaryChain + 10;
            }else if(riskFactorList.get(i).equals(Patient.RiskFactor.DIABETES2)){
                binaryChain = binaryChain + 1;
            }
        }

        binaryFlag = binaryChain.toString();

        return binaryFlag;
    }

    private static List<Patient.RiskFactor> binaryStringToRFactor(String binaryString){
    	
    	if (binaryString!=null) {
        int binaryRiskFactor;
        List<Patient.RiskFactor> riskFactorList = new ArrayList<>();

        for (int i = 0; i < binaryString.length(); i++) {

            binaryRiskFactor = Character.getNumericValue(binaryString.charAt(i));

            if(binaryRiskFactor == 1 && i == 0){
                riskFactorList.add(Patient.RiskFactor.CANCER);
            }else if (binaryRiskFactor == 1 && i == 1) {
                riskFactorList.add(Patient.RiskFactor.CKD);
            }else if(binaryRiskFactor == 1 && i == 2){
                riskFactorList.add(Patient.RiskFactor.COPD);
            }else if (binaryRiskFactor == 1 && i == 3) {
                riskFactorList.add(Patient.RiskFactor.HEART_CONDITIONS);
            }else if(binaryRiskFactor == 1 && i == 4){
                riskFactorList.add(Patient.RiskFactor.IMMUNOCOMPROMISED);
            }else if (binaryRiskFactor == 1 && i == 5) {
                riskFactorList.add(Patient.RiskFactor.OBESITY);
            }else if(binaryRiskFactor == 1 && i == 6){
                riskFactorList.add(Patient.RiskFactor.SMOKING);
            }else if (binaryRiskFactor == 1 && i == 7) {
                riskFactorList.add(Patient.RiskFactor.PREGNANCY);
            }else if(binaryRiskFactor == 1 && i == 8){
                riskFactorList.add(Patient.RiskFactor.DIABETES2);
            }
        }

        return riskFactorList;
    	}
    
    return null;
    }
}