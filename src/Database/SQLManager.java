package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLManager {

	private static Connection c;
	
	public static void main (String[] args) {
		try {
			connect("jdbc:sqlite:././Database/covid watchlist.db");
			//generateDataBase();
			disconnect();
		} catch ( Exception e ) {
			System.out.println("Error in connecting/generating/disconnecting from the database:" + e.getMessage());
		}
		
	}
	
	
	private static void generateDataBase() {
		System.out.println("Not implemented yet");
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
	
	
	public static void createTablePatients() {
		
	}
	
	public static void createTableMeasures() {
		
	}
	
}
