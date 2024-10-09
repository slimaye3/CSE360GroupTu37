/**
 * The DatabaseHelper class provides methods for interacting with the database
 * used by the StartCSE360.java system application. It is responsible for managing 
 * user data, including registration, login validation, and role management.
 * 
 * Responsibilities:
 * - Establish and manage database connections.
 * - Provide methods for user registration, checking if a user exists, 
 *   and logging in users based on their credentials.
 * - Facilitate user role management, including inviting users and 
 *   handling account resets and deletions.
 * - Ensure data integrity and security during database operations.
 * 
 * This class serves as an intermediary between the application and the database,
 * ensuring efficient data management and retrieval for a smooth user experience 
 * in the help system.
 * 
 * @version 1.0
 * @date October 8, 2024
 */

package simpleDatabase;
import java.sql.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


class DatabaseHelper {

	/**
	 * Database configuration constants for the H2 database connection, including JDBC driver, URL, user credentials,
	 * and connection and statement objects for executing SQL queries.
	 */
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/firstDatabase";  

	
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 

	/**
	 * Establishes a connection to the database by loading the JDBC driver
	 * and creating a statement object. This method also invokes the 
	 * createTables() method to ensure necessary tables are present in 
	 * the database.
	 * 
	 * @throws SQLException if there is an error connecting to the database.
	 */
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

	/**
	 * Creates the necessary tables in the database if they do not already exist. 
	 * This includes a user table for storing user information and an invite table 
	 * for managing invite codes and roles.
	 * 
	 * @throws SQLException if there is an error executing SQL commands.
	 */
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

	/**
	 * Inserts a new invite record into the invites table with the specified
	 * invite code and role.
	 * 
	 * @param code the invite code to be stored.
	 * @param role the role associated with the invite code.
	 * @throws SQLException if there is an error executing the insert command.
	 */
	public void inviteUser(String code, String role) throws SQLException {
		String insertInvite = "INSERT INTO invites (code, role) VALUES (?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertInvite)) {
			pstmt.setString(1, code);
			pstmt.setString(2, role);
			pstmt.executeUpdate();
		}
	}

	/**
	 * Checks if an invite code exists in the invites table. 
	 * 
	 * @param code the invite code to be checked.
	 * @return true if the invite code exists, false otherwise.
	 * @throws SQLException if there is an error executing the query.
	 */
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

	/**
	 * Retrieves the role associated with a given invite code from the invites table.
	 * 
	 * @param code the invite code for which the role is to be retrieved.
	 * @return the role associated with the invite code, or null if not found.
	 * @throws SQLException if there is an error executing the query.
	 */
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

	/**
	 * Removes an invite record from the invites table based on the provided invite code.
	 * 
	 * @param code the invite code of the record to be removed.
	 * @throws SQLException if there is an error executing the delete command.
	 */
	public void removeInvite(String code) throws SQLException{
		String query = "DELETE FROM invites WHERE code = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			pstmt.executeUpdate();
		}
	}
	
	/**
	 * Removes a user record from the cse360users table based on the provided username.
	 * 
	 * @param username the username of the record to be removed.
	 * @throws SQLException if there is an error executing the delete command.
	 */
	public void removeUser(String username) throws SQLException{
		String query = "DELETE FROM cse360users WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			pstmt.executeUpdate();
		}
	}

	/**
	 * Checks if the cse360users table in the database is empty. 
	 * 
	 * @return true if the table is empty, false otherwise.
	 * @throws SQLException if there is an error executing the count query.
	 */
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	/**
	 * Registers a new user in the cse360users table with the provided details. 
	 * 
	 * @param username the username of the new user.
	 * @param password the password for the new user.
	 * @param role the role of the new user (e.g., Student, Instructor).
	 * @param email the email address of the new user.
	 * @param fullName the full name of the new user.
	 * @param prefName the preferred name of the new user.
	 * @param oneTimePassword indicates if a one-time password should be used.
	 * @param passwordExpired the date when the password expires.
	 * @param skillLevel the skill level of the new user (e.g., Advanced, Intermediate).
	 * @throws SQLException if there is an error executing the insert command.
	 */
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

	/**
	 * Authenticates a user by checking their username, password, and role in the cse360users table.
	 * 
	 * @param username the username of the user attempting to log in.
	 * @param password the password of the user attempting to log in.
	 * @param role the role of the user attempting to log in.
	 * @return true if the user is successfully authenticated, false otherwise.
	 * @throws SQLException if there is an error executing the query.
	 */
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

	/**
	 * Checks if a user exists in the cse360users table based on the provided username. 
	 * 
	 * @param username the username to be checked.
	 * @return true if the user exists, false otherwise.
	 */
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

	/**
	 * Displays all users in the cse360users table from the perspective of an admin.
	 * Admins can view all user details, including email, full name, preferred name, 
	 * one-time password status, password expiration date, and skill level.
	 * 
	 * @throws SQLException if there is an error executing the SQL query.
	 */
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

	/**
	 * Displays all users in the cse360users table from the perspective of a regular user.
	 * Regular users can view their own details, but sensitive information such as 
	 * passwords and emails are not displayed for security reasons.
	 * 
	 * @throws SQLException if there is an error executing the SQL query.
	 */
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
	
	/**
	 * Closes the database connection and statement if they are open.
	 * This method is important for resource management to prevent memory leaks.
	 */
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

	
///////// Reset Functions 
	
	
public void resetUserPassword(String username, String oneTimePassword, Date expiration) throws SQLException {
	String sql = "UPDATE cse360users SET oneTimePassword = ?, passwordExpiration = ?, oneTimePasswordUsed = FALSE WHERE username = ?";
	
	try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
		pstmt.setString(1, oneTimePassword);
        pstmt.setDate(2, expiration);
        pstmt.setString(3, username);
        pstmt.executeUpdate();
	}
	
}

public boolean isPasswordValid(String username, String oneTimePassword) throws SQLException {
    boolean isValid = false;
    String sql = "SELECT oneTimePassword FROM cse360users WHERE username = ? AND oneTimePasswordUsed = FALSE AND oneTimePassword = ?";
    
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
    	pstmt.setString(1, username); 
        pstmt.setString(2, oneTimePassword);
        
        
        ResultSet rs = pstmt.executeQuery();
        
            if (rs.next()) {
                isValid = true;
                return isValid;
            }    
    }
    return isValid;
}

public Date getDate(String username) throws SQLException{
	Date expire = null;
	String query = "SELECT * FROM cse360users WHERE username = ? ";
	try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		pstmt.setString(1, username);
		try (ResultSet rs = pstmt.executeQuery()) {
			while(rs.next()) {
				return rs.getDate("passwordExpired");
			}
		}
	
	}
	return expire; 
}

public void updatePassword(String username, String givenPassword) throws SQLException {
    String sql = "UPDATE cse360users SET password = ? WHERE username = ?";
    
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setString(1, givenPassword);  
        pstmt.setString(2, username);  
        pstmt.executeUpdate();

    }
}

public void oneTimePasswordUsed(String username) throws SQLException {
    String sql = "UPDATE cse360users SET oneTimePasswordUsed = TRUE WHERE username = ?";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setString(1, username); 
        pstmt.executeUpdate();
    }
}

public String getRoleID(String username) throws SQLException{
	String role = null;
	String query = "SELECT * FROM cse360users WHERE username = ? ";
	try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		pstmt.setString(1, username);
		try (ResultSet rs = pstmt.executeQuery()) {
			while(rs.next()) {
				return rs.getString("role");
			}
		}
	
	}
	return role; 
}

}
