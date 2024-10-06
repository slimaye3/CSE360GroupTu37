package simpleDatabase;
import java.sql.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/firstDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement pstmt

	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "email VARCHAR(255) UNIQUE, "
				+ "role VARCHAR(20), "
				+ "username VARCHAR(255), "
				+ "password VARCHAR(255),"
				+ "oneTimePassword BOOLEAN, "
				+ "passwordExpired TIMESTAMP, "
				+ "fullName VARCHAR(255), "
				+ "prefName VARCHAR(255), "  //preferred Name
				+ "skillLevel VARCHAR(18)), "; //Advanced, intermediate, etc.
		
		statement.execute(userTable);
	}


	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	public void register(String email, String password, String role, String fullName, 
			String prefName, 
            boolean oneTimePassword, Timestamp passwordExpired, String skillLevel) throws SQLException {
		String insertUser = "INSERT INTO cse360users (email, password, role, fullName, prefName, "
				+ "oneTimePassword, passwordExpired, skillLevel) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, email);
			pstmt.setString(2, password);
			pstmt.setString(3, role);
			pstmt.setString(4, fullName);
		    	pstmt.setString(5, prefName);
		    	pstmt.setBoolean(6, oneTimePassword);
		    	pstmt.setTimestamp(7, passwordExpired);
		    	pstmt.setString(8, skillLevel);
			pstmt.executeUpdate();
		}
	}

	public boolean login(String email, String password, String role) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE email = ? AND password = ? AND role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, email);
			pstmt.setString(2, password);
			pstmt.setString(3, role);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	
	public boolean doesUserExist(String email) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE email = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, email);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}

	public void displayUsersByAdmin() throws SQLException{
		String sql = "SELECT * FROM cse360users"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 

		while(rs.next()) { 
			// Retrieve by column name 
			int id  = rs.getInt("id"); 
			String  email = rs.getString("email"); 
			String password = rs.getString("password"); 
			String role = rs.getString("role");  
			String fullName = rs.getString("fullName");
			String prefName = rs.getString("prefName");
			boolean oneTime = rs.getBoolean("oneTimePassword");
			Timestamp expireDate = rs.getTimestamp("passwordExpired");
			String skill = rs.getString("skillLevel");

			// Display values 
			System.out.print("ID: " + id); 
			System.out.print(", Email: " + email); 
			System.out.print(", Pass: " + password); 
			System.out.print(", Role: " + role); 
			System.out.print(", Full Name: " + fullName); 
			System.out.print(", Pref Name: " + prefName); 
			System.out.print(", One-Time Pass: " + oneTime); 
			System.out.print(", Expire Date: " + expireDate); 
			System.out.println(", Skill Level: " + skill); 
			
		} 
	}
	
	public void displayUsersByUser() throws SQLException{
		String sql = "SELECT * FROM cse360users"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 

		while(rs.next()) { 
			// Retrieve by column name 
			int id  = rs.getInt("id"); 
			String  email = rs.getString("email"); 
			String password = rs.getString("password"); 
			String role = rs.getString("role");  
			String fullName = rs.getString("fullName");
			String prefName = rs.getString("prefName");
			boolean oneTime = rs.getBoolean("oneTimePassword");
			Timestamp expireDate = rs.getTimestamp("passwordExpired");
			String skill = rs.getString("skillLevel");

			// Display values 
			System.out.print("ID: " + id); 
			System.out.print(", Email: " + email); 
			System.out.print(", Pass: " + password); 
			System.out.print(", Role: " + role); 
			System.out.print(", Full Name: " + fullName); 
			System.out.print(", Pref Name: " + prefName); 
			System.out.print(", One-Time Pass: " + oneTime); 
			System.out.print(", Expire Date: " + expireDate); 
			System.out.println(", Skill Level: " + skill); 
		} 
	}


	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}

}
