package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import pojos.Patient;

public class SQLManager {

	private static Connection c;
	
	public static void main (String[] args) {
		try {
			connect("jdbc:sqlite:././Database/covid watchlist.db");
			generateDataBase();
			disconnect();
		} catch ( Exception e ) {
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
		if(c != null) {
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
		Statement stmt=c.createStatement();
		String table = "CREATE TABLE patient(\r\n"+
						"patient_id INTEGER PRIMARY KEY AUTOINCREMENT,\r\n"+
						"name TEXT NOT NULL,\r\n"+
						"date_birth DATE NOT NULL,\r\n"+
						"risk_factors TEXT)";
		stmt.executeUpdate(table);
		stmt.close();
		
	}
	
	public static void createTableMeasures() throws SQLException {
		Statement stmt1 = c.createStatement();
		String sql1 = "CREATE TABLE measures " + "(measure_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ " measure_date DATETIME DEFAULT CURRENT_DATETIME," + " ecg TEXT," + " bpm INT NOT NULL," 
				+ " o2_saturation FLOAT,"+ " temperature FLOAT NOT NULL," 
				+ " patient_id REFERENCES patients (patient_id) ON UPDATE CASCADE ON DELETE CASCADE );";
		stmt1.executeUpdate(sql1);
		stmt1.close();
	}
	
	
/*
* 
* Reads
* 
*/
	
	public static Patient searchPatientByID(Integer id) throws SQLException {
		String sql="SELECT * FROM patient WHERE patient_id = ? ;";
		PreparedStatement prep = c.prepareStatement(sql);
		
		prep.setInt(1, id);
		
		ResultSet rs1 = prep.executeQuery();
		if(!rs1.isBeforeFirst()) {
			prep.close();
			return null;
		}
		
		Patient patient = getPatient(rs1);
		
		if	(id == patient.getId()) {
			prep.close();
			rs1.close();
			return patient;
		}else {
				
			prep.close();
			rs1.close();
			return null;
		}	
		
	}
	
    public static List<Patient> getAllPatients() throws SQLException {
    	
    	String sql="SELECT * FROM patient ;";
    	PreparedStatement prep=c.prepareStatement(sql);
    	ResultSet rs1=prep.executeQuery();
    	List <Patient> patientList=new ArrayList <Patient> ();
    	while(rs1.next()) {
    		patientList.add(getPatient(rs1));
    	}
    	rs1.close();
    	prep.close();
    	return patientList;
    }
	
	
/*
 * Private Get Methods
 * 
 */
	
private static Patient getPatient (ResultSet rs1) throws SQLException {
    	
    	Patient patient = new Patient();
		patient.setId(rs1.getInt("id"));
		patient.setName(rs1.getString("name"));
		patient.setBirthDate(rs1.getDate("birthday"));
		//patient.setSex(rs1.getSex("sex"));
		patient.setRiskFactor(rs1.getBoolean("riskFactor"));    		   		
		return patient;
    	
    }
	
	
	
}



