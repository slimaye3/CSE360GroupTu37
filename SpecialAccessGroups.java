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
 * @version 2.0
 * @date October 30, 2024
 */



package simpleDatabase;
import java.sql.*;
import java.util.Base64;
import org.bouncycastle.util.Arrays;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.*;
import java.util.Base64;
import java.util.UUID;
import Encryption.EncryptionHelper;
import org.bouncycastle.util.Arrays;
import Encryption.EncryptionUtils;
import Encryption.EncryptionUtils;
import simpleDatabase.DatabaseHelper;



class SpecialAccessGroups {
	
	/** ------------ Declarations  ------------ */

	/**
	 * Database configuration constants for the H2 database connection, including JDBC driver, URL, user credentials,
	 * and connection and statement objects for executing SQL queries.
	 */
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/firstDatabase";  

	
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private static Connection connection = null;
	private Statement statement = null; 
	private EncryptionHelper encryptionHelper;
	
	public SpecialAccessGroups() throws Exception {
		encryptionHelper = new EncryptionHelper();
	}
	
	
	/** ------------ Database Connection  ------------ */

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
			createSpecialUserTable();
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}
	
	
	/** ------------ Create Tables  ------------ */

	/**
	 * Creates the necessary tables in the database if they do not already exist. 
	 * This includes a user table for storing user information and an invite table 
	 * for managing invite codes and roles.
	 * 
	 * @throws SQLException if there is an error executing SQL commands.
	 */
	
	private long generateUniqueID()
	{
		long value = UUID.randomUUID().getMostSignificantBits();
		return value;
	}
	
	
	/**
	 * Creates the necessary tables in the database if they do not already exist. 
	 * This includes an article Table.
	 * 
	 * @throws SQLException if there is an error executing SQL commands.
	 */
	
	
	
	
	/** ------------ Database Basic Functions  ------------ */

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
	
	
	
	
	/** ------------ Article Help Methods  ------------ */
	
	/**
	 * Adds article to database. Only available to instructor and admin.
	 * 
	 * @param title Title of the article 
	 * @param description Description of the article
	 * @param body Body of the article
	 * @param level Writing level of the article
	 * @param groupIdentifier Identifes which group the article belongs to
	 * @param keywords The keywords of the article
	 * @param accessLevel The access level of the article
	 * @param other Other info needed
	 * @param links Any links associated with the article
	 * @throws SQLException if there is an error executing SQL commands.
	 */

	
	/***
	 * Creates the necessary tables in the database if they do not already exist. 
	 * This includes a special access user table for storing user information and an invite table 
	 * for managing invite codes and roles.
	 * 
	 * @throws SQLException if there is an error executing SQL commands.
	 * */
	private void createSpecialUserTable() throws SQLException {

		String specialUserTable = "CREATE TABLE IF NOT EXISTS specialUsers ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "username VARCHAR(255), "
				+ "groupName VARCHAR(255), "
				+ "adminRights VARCHAR(255), "
				+ "viewingRights VARCHAR, "
				+ "role VARCHAR(20))";
		
		statement.execute(specialUserTable);
		String inviteSpecialUserTable = "CREATE TABLE IF NOT EXISTS specialInvites ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "username VARCHAR(255), "
				+ "groupName VARCHAR(255), "
				+ "adminRights VARCHAR(255), "
				+ "viewingRights VARCHAR(255), "
				+ "role VARCHAR(20), "
				+ "code VARCHAR(255))";

		
		statement.execute(inviteSpecialUserTable);
	}
	
	
	/**
	 * Registers a new user in the cse360users table with the provided details. 
	 * 
	 * @param username the username of the firstInstructor.
	 * @param groupName, the groupName of the first instructor in that group
	 * first instructor of a new group has admin and viewing access
	 * @throws SQLException if there is an error executing the insert command.
	 */
	public void addFirstInstructor(String username, String groupName) throws Exception
	{
		String role = "instructor";
		String adminRights = "true";
		String vRights = "true";
		String insertFirst = "INSERT INTO specialUsers (username, groupName, adminRights, viewingRights, role)"
				+ "VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertFirst))
		{
			pstmt.setString(1, username);
			pstmt.setString(2, groupName);
			pstmt.setString(3, adminRights);
			pstmt.setString(4, vRights);
			pstmt.setString(5, role);
		    pstmt.executeUpdate();
		} 
	}
	
	/**
	 * Registers a new user in the cse360users table with the provided details. 
	 * 
	 * @param username the username of the admin
	 * @param groupName, the groupName 
	 * admin do not have access to anything until specifically given. 
	 * @throws SQLException if there is an error executing the insert command.
	 */
	public void addAdmin(String username, String groupName) throws Exception
	{
		String adminRights = "false";
		String vRights = "false";
		String role = "admin";
		
		String insertUser = "INSERT INTO specialUsers (username, groupName, adminRights, viewingRights, role)"
				+ "VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser))
		{
			pstmt.setString(1, username);
			pstmt.setString(2, groupName);
			pstmt.setString(3, adminRights);
			pstmt.setString(4, vRights);
			pstmt.setString(5, role);
		    pstmt.executeUpdate();
		} 
	}
	
	/**
	 * Registers a new user in the cse360users table with the provided details. 
	 * 
	 * @param username the username of the instructors
	 * @param groupName, the groupName 
	 * instructors added to a precreated group are automatically gievn viewing rights but not admin rights  
	 * @throws SQLException if there is an error executing the insert command.
	 */
	public void addInstructor(String username, String groupName) throws Exception
	{
		String adminRights = "false";
		String vRights = "true";
		String role = "admin";
		
		String insertUser = "INSERT INTO specialUsers (username, groupName, adminRights, viewingRights, role)"
				+ "VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser))
		{
			pstmt.setString(1, username);
			pstmt.setString(2, groupName);
			pstmt.setString(3, adminRights);
			pstmt.setString(4, vRights);
			pstmt.setString(5, role);
		    pstmt.executeUpdate();
		} 
	}
	
	
	/**
	 * Registers a new user in the cse360users table with the provided details. 
	 * 
	 * @param username the username of the students
	 * @param groupName, the groupName
	 * students do not have access to anything until specifically given. 
	 * @throws SQLException if there is an error executing the insert command.
	 */
	public void addStudent(String username, String groupName) throws Exception
	{
		String adminRights = "false";
		String vRights = "false";
		String role = "student";
		
		String insertUser = "INSERT INTO specialUsers (username, groupName, adminRights, viewingRights, role)"
				+ "VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser))
		{
			pstmt.setString(1, username);
			pstmt.setString(2, groupName);
			pstmt.setString(3, adminRights);
			pstmt.setString(4, vRights);
			pstmt.setString(5, role);
		    pstmt.executeUpdate();
		} 
	}
	
	
	
	/**
	 * @params username, the username of the person whose group you are trying to find
	 * @returns the name of the group that is connected to the user whose username is being input. 
	 * @throws SQLException if there is an error executing the insert command. 
	 * **/
	public static String getUserGroup(String username) throws SQLException{
		String groupName = null;
		
		if(doesSpecialUserExist(username) == true)
		{
			String query = "SELECT * FROM specialUsers WHERE groupName = ? ";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, username);
				try (ResultSet rs = pstmt.executeQuery()) {
					if(rs.next()) {
						return rs.getString("groupName");
					}
				}
			
			}
			 
		}
		return groupName;
	}
	
	
	/**
	 * @paramas username, username of the user
	 * @params groupName, group name of the user to whom admin rights are being provided to. 

	 * */
	public String giveAdminAccess(String username, String groupName) throws SQLException
	{
		if(getUserGroup(username) != null)
		{
			String giveAccess = "UPDATE specialUsers SET adminRights = ? WHERE username = ? AND groupName = ?"; 
					
			try (PreparedStatement pstmt = connection.prepareStatement(giveAccess)) {
		        pstmt.setString(1, "true"); 
		        pstmt.setString(2, username); 
		        pstmt.setString(3, groupName);
		        
		        int rowsUpdated = pstmt.executeUpdate();
		        
		        if (rowsUpdated > 0) {
		        	String status = "Admin Access Updated.";
		        		return status;
		        } else {
		        	String status = "Admin Access Update Failed.";
			           return status;
		        }
		    }
		}
		String status = "Could not find " + username;
		return status;
	}
	
	/**
	 * @paramas username, username of the user
	 * @params groupName, group name of the user to whom viewing rights are being provided to. 
	 * */
	public String giveViewingAccess(String username, String groupName) throws SQLException
	{
		if(getUserGroup(username) != null)
		{
			String giveAccess = "UPDATE specialUsers SET viewingRights = ? WHERE username = ? AND groupName = ?"; 
			try (PreparedStatement pstmt = connection.prepareStatement(giveAccess)) {
		        pstmt.setString(1, "true"); 
		        pstmt.setString(2, username); 
		        pstmt.setString(3, groupName); 
		        
		        int rowsUpdated = pstmt.executeUpdate();
		        
		        if (rowsUpdated > 0) {
		        	String status = "Viewing Access Updated.";
		           return status;
		        } else {
		        	String status = "Viewing Access Update Failed.";
			           return status;
		        }
		    }
		}
		String status = "Could not find " + username;
		return status;
	}

	
	/**
	 * Adds encrypted version of article to database. Only available to those that have special access rights. 
	 * 
	 * @param title Title of the article 
	 * @param description Description of the article
	 * @param body Body of the article
	 * @param level Writing level of the article
	 * @param groupIdentifier Identifes which group the article belongs to
	 * @param keywords The keywords of the article
	 * @param accessLevel The access level of the article
	 * @param other Other info needed
	 * @param links Any links associated with the article
	 * @throws SQLException if there is an error executing SQL commands.
	 */
	public void addSpecialArticle(String title, String author,String description, String body, String level, String groupIdentifier, 
			String keywords,String accessLevel, String other, String links) throws Exception
	{

		long uniqueID = generateUniqueID();
		String specialRestriction = "restricted";
		accessLevel = specialRestriction;
		String UID = String.valueOf(uniqueID);
//		byte[] iv = EncryptionUtils.getInitializationVector(UID.toCharArray());
		
		
		String encryptedTitle = Base64.getEncoder().encodeToString(
				encryptionHelper.encrypt(title.getBytes(),EncryptionUtils.getInitializationVector(UID.toCharArray()))
		);
		String encryptedAuthor = Base64.getEncoder().encodeToString(
				encryptionHelper.encrypt(author.getBytes(),EncryptionUtils.getInitializationVector(UID.toCharArray()))
		);
		String encryptedDescription = Base64.getEncoder().encodeToString(
				encryptionHelper.encrypt(description.getBytes(),EncryptionUtils.getInitializationVector(UID.toCharArray()))
		);
		String encryptedBody = Base64.getEncoder().encodeToString(
				encryptionHelper.encrypt(body.getBytes(),EncryptionUtils.getInitializationVector(UID.toCharArray()))
		);
		String encryptedLevel = Base64.getEncoder().encodeToString(
				encryptionHelper.encrypt(level.getBytes(),EncryptionUtils.getInitializationVector(UID.toCharArray()))
		);
		String encryptedGID = Base64.getEncoder().encodeToString(
				encryptionHelper.encrypt(groupIdentifier.getBytes(),EncryptionUtils.getInitializationVector(UID.toCharArray()))
		);
		String encryptedKeywords = Base64.getEncoder().encodeToString(
				encryptionHelper.encrypt(String.join(", ", keywords).getBytes(), EncryptionUtils.getInitializationVector(UID.toString().toCharArray()))
		);
		String encryptedAccessLevel = Base64.getEncoder().encodeToString(
				encryptionHelper.encrypt(accessLevel.getBytes(),EncryptionUtils.getInitializationVector(UID.toCharArray()))
		);
		String encryptedOther = Base64.getEncoder().encodeToString(
				encryptionHelper.encrypt(String.join(", ", other).getBytes(), EncryptionUtils.getInitializationVector(UID.toString().toCharArray()))
		);
		String encryptedLinks = Base64.getEncoder().encodeToString(
				encryptionHelper.encrypt(String.join(", ", links).getBytes(), EncryptionUtils.getInitializationVector(UID.toString().toCharArray()))
		);
		
//		 String encryptedTitle = Base64.getEncoder().encodeToString(encryptionHelper.encrypt(title.getBytes(), iv));
//		    String encryptedAuthor = Base64.getEncoder().encodeToString(encryptionHelper.encrypt(author.getBytes(), iv));
//		    String encryptedDescription = Base64.getEncoder().encodeToString(encryptionHelper.encrypt(description.getBytes(), iv));
//		    String encryptedBody = Base64.getEncoder().encodeToString(encryptionHelper.encrypt(body.getBytes(), iv));
//		    String encryptedLevel = Base64.getEncoder().encodeToString(encryptionHelper.encrypt(level.getBytes(), iv));
//		    String encryptedGID = Base64.getEncoder().encodeToString(encryptionHelper.encrypt(groupIdentifier.getBytes(), iv));
//		    String encryptedKeywords = Base64.getEncoder().encodeToString(encryptionHelper.encrypt(keywords.getBytes(), iv));
//		    String encryptedAccessLevel = Base64.getEncoder().encodeToString(encryptionHelper.encrypt(accessLevel.getBytes(), iv));
//		    String encryptedOther = Base64.getEncoder().encodeToString(encryptionHelper.encrypt(other.getBytes(), iv));
//		    String encryptedLinks = Base64.getEncoder().encodeToString(encryptionHelper.encrypt(links.getBytes(), iv));
		
		
		String insertArticle = "INSERT INTO Articles (title, author, description, body, level, groupIdentifier, keywords, accessLevel, other, links_misc, uniqueID) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?)";
		
		try (PreparedStatement pstmt = connection.prepareStatement(insertArticle))
		{
			pstmt.setString(1, encryptedTitle);
			pstmt.setString(2, encryptedAuthor);
			pstmt.setString(3, encryptedDescription);
			pstmt.setString(4, encryptedBody);
			pstmt.setString(5, encryptedLevel);
			pstmt.setString(6, encryptedGID);
		    pstmt.setString(7, encryptedKeywords);
		    pstmt.setString(8, encryptedAccessLevel);
		    pstmt.setString(9, encryptedOther);
		    pstmt.setString(10, encryptedLinks);
		    pstmt.setLong(11, uniqueID);
		    pstmt.executeUpdate();
		} 
	}

	
	/***
	 * Lists all those admins that have een given create, read, update, and delete access rights. Which i think are view and admin rights. 
	 * */
	public String listAdmin() throws Exception
	{
		String query = "SELECT * FROM specialUsers WHERE role = 'admin' AND adminRights = 'true' and viewingRights = 'true' ";
		String display = "";
		try (PreparedStatement stmt = connection.prepareStatement(query);
		         ResultSet rs = stmt.executeQuery()) {		
		        while (rs.next()) {
		            int id = rs.getInt("id");
		            String username = rs.getString("username");
		            String groupName = rs.getString("groupName");
		            display += "ID: " + id + "\n";
		            display += "Username: " + username + "\n";
		            display += "Group Name: " + groupName + "\n";
		        }
		    }
		return display;
	}
	
	
	/***
	 * Lists all those instructors that have admin rights 
	 * */
	public String listInstructorsAdmin() throws Exception
	{
		String query = "SELECT * FROM specialUsers WHERE role = 'instructor' AND adminRights = 'true' ";
		String display = "";
		try (PreparedStatement stmt = connection.prepareStatement(query);
		         ResultSet rs = stmt.executeQuery()) {		
		        while (rs.next()) {
		            int id = rs.getInt("id");
		            String username = rs.getString("username");
		            String groupName = rs.getString("groupName");
		            display += "ID: " + id + "\n";
		            display += "Username: " + username + "\n";
		            display += "Group Name: " + groupName + "\n";
		        }
		    }
		return display;
	}
	
	
	/***
	 * Lists all those instructors that have viewing rights 
	 * */
	public String listInstructorsViewing() throws Exception
	{
		String query = "SELECT * FROM specialUsers WHERE role = 'instructor' AND viewingRights = 'true' ";
		String display = "";
		try (PreparedStatement stmt = connection.prepareStatement(query);
		         ResultSet rs = stmt.executeQuery()) {		
		        while (rs.next()) {
		            int id = rs.getInt("id");
		            String username = rs.getString("username");
		            String groupName = rs.getString("groupName");
		            display += "ID: " + id + "\n";
		            display += "Username: " + username + "\n";
		            display += "Group Name: " + groupName + "\n";
		        }
		    }
		return display;
	}
	
	
	/***
	 * Lists all those students that have viewing rights 
	 * */
	public String listStudentViewing() throws Exception
	{
		String query = "SELECT * FROM specialUsers WHERE role = 'student' AND viewingRights = 'true' ";
		String display = "";
		try (PreparedStatement stmt = connection.prepareStatement(query);
		         ResultSet rs = stmt.executeQuery()) {		
		        while (rs.next()) {
		            int id = rs.getInt("id");
		            String username = rs.getString("username");
		            String groupName = rs.getString("groupName");
		            display += "ID: " + id + "\n";
		            display += "Username: " + username + "\n";
		            display += "Group Name: " + groupName + "\n";
		        }
		    }
		return display;
	}
	
	
	/***
	 * @params username
	 * @params groupName
	 * Takes in these values and returns a boolean value for if they that person has admin rights for that group
	 * */
	public static boolean adminRights(String username, String groupName) throws Exception
	{
		String query = "SELECT * FROM specialUsers WHERE adminRights = 'true' AND username = ? AND groupName = ? ";
		 try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        
		        pstmt.setString(1, username);
		        pstmt.setString(2, groupName);
		        ResultSet rs = pstmt.executeQuery();
		        
		        if (rs.next()) {
		            return rs.getInt(1) > 0;
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		    return false;
	}
	
	
	/***
	 * @params username
	 * @params groupName
	 * Takes in these values and returns a boolean value for if they that person has viewing rights for that group
	 * */
	
	public static boolean vRights(String username, String groupName) throws Exception
	{
		String query = "SELECT * FROM specialUsers WHERE viewingRights = 'true' AND username = ? AND groupName = ? ";
		 try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        
		        pstmt.setString(1, username);
		        pstmt.setString(2, groupName);
		        ResultSet rs = pstmt.executeQuery();
		        
		        if (rs.next()) {
		            return rs.getInt(1) > 0;
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		    return false;
	}
	
	
	//Deletes special article
	public void deleteSpecialArticle(int id) throws Exception
	{
		String removeArticle = "DELETE FROM Articles WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(removeArticle))
		{
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
		}
	        
	}
	
	/*** Deletes a specific user given an id*/
	public void deleteSpecialUser(int id) throws Exception
	{
		String removeArticle = "DELETE FROM specialUsers WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(removeArticle))
		{
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
		}
	        
	}
	
	
	/*** Invites a specialUser to going the group*/
	public void inviteSpecialUsers(String groupName, String code, String role) throws SQLException {
		String insertInvite = "INSERT INTO specialInvites (cgroupName, code, role) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertInvite)) {
			pstmt.setString(1, groupName);
			pstmt.setString(2, code);
			pstmt.setString(3, role);
			pstmt.executeUpdate();
		}
	}

	/*** Checks if the specialInvite code exists to add the user*/
	public boolean doesSpecialInviteExist(String code) throws SQLException{
		String query = "SELECT COUNT(*) FROM specialInvites WHERE code = ?";
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

	
	/*** Checks if the username inputed matches any user in the special access database*/
	public static boolean doesSpecialUserExist(String username) {
	    String query = "SELECT COUNT(*) FROM specialUsers WHERE username = ?";
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
	
	/****Returns the role of a user in the special access users*/
	public static String getSpecialRole(String code) throws SQLException{
		String role = null;
		String query = "SELECT * FROM specialUsers WHERE code = ? ";
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
	

	/***Revokes an invite to the special access group*/
	public void removeSpecialInvite(String code) throws SQLException{
		String query = "DELETE FROM specialInvities WHERE code = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			pstmt.executeUpdate();
		}
	}
	

	
	
	
	/** ------------ Close Database Connection  ------------ */
	
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
	
}
	
	
