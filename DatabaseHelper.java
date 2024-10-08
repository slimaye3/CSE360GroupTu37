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
			createTables(); 
			// Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}
	
	private void createTables() throws SQLException {
		String destroy = "DROP TABLE IF EXISTS cse360users ";
		statement.execute(destroy);
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "username VARCHAR(255), "
				+ "password VARCHAR(255),"
				+ "role VARCHAR(20), "
				+ "email VARCHAR(255) UNIQUE, "
				+ "fullName VARCHAR(255), "
				+ "prefName VARCHAR(255), "  //preferred Name
				+ "oneTimePassword BOOLEAN, "
				+ "passwordExpired DATE, "
				+ "skillLevel VARCHAR(255)) "; //Advanced, intermediate, etc.
		
		statement.execute(userTable);
		String inviteTable = "CREATE TABLE IF NOT EXISTS invites ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "code VARCHAR(255), "
				+ "role VARCHAR(20))";

		
		statement.execute(inviteTable);
	}
	
	public void inviteUser(String code, String role) throws SQLException {
		String insertInvite = "INSERT INTO invites (code, role) VALUES (?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertInvite)) {
			pstmt.setString(1, code);
			pstmt.setString(2, role);
			pstmt.executeUpdate();
		}
	}

	public boolean doesInviteExist(String code) throws SQLException{
		String query = "SELECT COUNT(*) FROM invites WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, code);
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
	
	public String getRole(String code) throws SQLException{
		String role = null;
		String query = "SELECT * FROM invites WHERE code = ? ";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			try (ResultSet rs = pstmt.executeQuery()) {
				while(rs.next()) {
					return rs.getString("role");
				}
			}
		
		}
		return role; 
	}
	
	public void removeInvite(String code) throws SQLException{
		String query = "DELETE FROM invites WHERE code = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			pstmt.executeUpdate();
		}
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

	public void register(String username, String password, String role, String email, String fullName, 
			String prefName, 
            boolean oneTimePassword, Date passwordExpired, String skillLevel) throws SQLException {
		String insertUser = "INSERT INTO cse360users (username, password, role, email, fullName, prefName, "
				+ "oneTimePassword, passwordExpired, skillLevel) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			pstmt.setString(3, role);
			pstmt.setString(4, email);
			pstmt.setString(5, fullName);
		    pstmt.setString(6, prefName);
		    pstmt.setBoolean(7, oneTimePassword);
		    pstmt.setDate(8, passwordExpired);
		    pstmt.setString(9, skillLevel);
			pstmt.executeUpdate();
		}
	}

	public boolean login(String username, String password, String role) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE username = ? AND password = ? AND role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			pstmt.setString(3, role);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	
	public boolean doesUserExist(String username) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, username);
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
			String  username = rs.getString("username"); 
			String password = rs.getString("password"); 
			String  email = rs.getString("email"); 
			String role = rs.getString("role");  
			String fullName = rs.getString("fullName");
			String prefName = rs.getString("prefName");
			boolean oneTime = rs.getBoolean("oneTimePassword");
			Date expireDate = rs.getDate("passwordExpired");
			String skill = rs.getString("skillLevel");

			// Display values 
			System.out.print("ID: " + id); 
			System.out.print(", Username: " + username); 
			System.out.print(", Pass: " + password); 
			System.out.print(", Role: " + role); 
			if(role.compareTo("admin") != 0) {
				System.out.print(", Email: " + email); 
				System.out.print(", Full Name: " + fullName); 
				System.out.print(", Pref Name: " + prefName); 
				System.out.print(", One-Time Pass: " + oneTime); 
				System.out.print(", Expire Date: " + expireDate); 
				System.out.println(", Skill Level: " + skill); 
			}else {
				System.out.println();
			}
		} 
	}
	
	public void displayUsersByUser() throws SQLException{
		String sql = "SELECT * FROM cse360users"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 
	
		while(rs.next()) { 
			// Retrieve by column name 
			int id  = rs.getInt("id"); 
			String  username = rs.getString("username"); 
			String password = rs.getString("password"); 
			String  email = rs.getString("email"); 
			String role = rs.getString("role");  
			String fullName = rs.getString("fullName");
			String prefName = rs.getString("prefName");
			boolean oneTime = rs.getBoolean("oneTimePassword");
			Date expireDate = rs.getDate("passwordExpired");
			String skill = rs.getString("skillLevel");

			// Display values 
			System.out.print("ID: " + id); 
			System.out.print(", Username: " + username); 
			System.out.print(", Pass: " + password); 
			System.out.print(", Role: " + role); 
			if(role.compareTo("admin") != 0) {
				System.out.print(", Email: " + email); 
				System.out.print(", Full Name: " + fullName); 
				System.out.print(", Pref Name: " + prefName); 
				System.out.print(", One-Time Pass: " + oneTime); 
				System.out.print(", Expire Date: " + expireDate); 
				System.out.println(", Skill Level: " + skill); 
			}else {
				System.out.println();
			}
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
