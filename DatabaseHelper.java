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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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
import simpleDatabase.SpecialAccessGroups;




class DatabaseHelper {
	
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
	
	public static Connection getConnection()
	{
		return connection;
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
			createTables(); 
			createHelpTable();
			// Create the necessary tables if they don't exist
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

	
	public long generateUniqueID()
	{
		long value = UUID.randomUUID().getMostSignificantBits();
		return value;
	}
	
	
	

	private void createHelpTable() throws SQLException {
		String destroy = "DROP TABLE IF EXISTS Articles ";
		statement.execute(destroy);
		
		String articlesTable = "CREATE TABLE IF NOT EXISTS Articles ("
                + "id INT PRIMARY KEY AUTO_INCREMENT, "
                + "title VARCHAR(255), "
                + "author VARCHAR(255), "
                + "description VARCHAR(500), "
                + "body TEXT, "
                + "level VARCHAR(255), "
                + "groupIdentifier VARCHAR(100), "
                + "keywords VARCHAR(500), "
                + "accessLevel VARCHAR(255), "
                + "other VARCHAR(500), "
                + "links_misc VARCHAR(500), "
                + "uniqueID BIGINT UNIQUE"
                + ")";
		
        statement.execute(articlesTable);
        
        String studentQueries = "CREATE TABLE IF NOT EXISTS queries ("
        		+ "id INT PRIMARY KEY AUTO_INCREMENT, "
        		+ "usernmae VARCHAR(255), "
        		+ "question TEXT, "
        		+ "answered BOOLEAN DEFAULT FALSE"
        		+ ")";
        
        statement.execute(studentQueries);
        
	}
	
	
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
	public boolean register(String username, String password, String role, String email, String fullName, 
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
	public void createHelpArticle(String title, String author, String description, String body, String level, String groupIdentifier, 
			String keywords, String accessLevel, String other, String links) throws SQLException
	{
		long uniqueID = generateUniqueID();
		String insertArticle = "INSERT INTO Articles (title, author, description, body, level, groupIdentifier, keywords, accessLevel, other, links_misc, uniqueID) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?)";
		
		try (PreparedStatement pstmt = connection.prepareStatement(insertArticle))
		{
			pstmt.setString(1, title);
			pstmt.setString(2, author);
			pstmt.setString(3, description);
			pstmt.setString(4, body);
			pstmt.setString(5, level);
			pstmt.setString(6, groupIdentifier);
		    pstmt.setString(7, keywords);
		    pstmt.setString(8, accessLevel);
		    pstmt.setString(9, other);
		    pstmt.setString(10, links);
		    pstmt.setLong(11, uniqueID);
		    pstmt.executeUpdate();
		} 
	}
	
	
	
	/***
	 * @params username. The user should NOT be promted for their username and the username should be automatically added because 
	 * 					 of the flow they are already in. 
	 * @throws Exception
	 * Takes the username and feeds it into SpecialAccessGroups.getUserGroup(username) which checks if that user is involved in a 
	 * special access group and returns which one. 
	 * If a group is found and they have viewing rights for that user then all articles that are public 
	 * and all articles that the user has been granted access for will be decrypted and listed
	 * Otherwise only the articles whose accessLevel is public will be able to be listed.  
	 * */
public String listArticles() throws Exception{ 
		
		String findArticle = "SELECT * FROM Articles WHERE accessLevel = 'public' "; 
		String display = "";
		try (PreparedStatement stmt = connection.prepareStatement(findArticle);
		         ResultSet rs = stmt.executeQuery()) {

		        while (rs.next()) {
		            int id = rs.getInt("id");
		            String title = rs.getString("title");
		            String level = rs.getString("level");
		            String groupID = rs.getString("groupIdentifier");

		            display += "ID: " + id + "\n";
		            display += "Title: " + title + "\n";
		            display += "Level: " + level + "\n";
		            display += "Group Identifier: " + groupID + "\n";
		        }
		    }
		
			
		return display;	
	}
	

	
	/***
	 * @params username. The user should NOT be promted for their username and the username should be automatically added because of the flow they are already in. 
	 * @params id. Takes in username and articleId as input.
	 * Takes the username and feeds it into SpecialAccessGroups.getUserGroup(username) which checks if that user is involved in a special access group and returns which one. 
	 * If a group is found and they have viewing rights for that user and the ID matches then that article will be decrypted and displayed. 
	 * Otherwise only the articles whose accessLevel is public will be able to be displayed.  
	 * */
		
	public String displayArticle(int id) throws Exception
	{
		 	String query = "SELECT * FROM Articles WHERE id = ? AND accessLevel = 'public' ";
		    String display = "";

		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setInt(1, id); 

		        try (ResultSet rs = pstmt.executeQuery()) {
		        	
						while(rs.next()) {

		                     int newid = rs.getInt("id");
		                    String title = rs.getString("title");
		                    String author = rs.getString("author");
		                    String description = rs.getString("description");
		                    String body = rs.getString("body");
		                    String level = rs.getString("level");
		                    String groupID = rs.getString("groupIdentifier");
		                    String keywords = rs.getString("keywords");
		                    String accessLevel = rs.getString("accessLevel");
		                    String other = rs.getString("other");
		                    String links = rs.getString("links_misc");
		                    
		                    
		                    display += "ID: " + newid + "\n";
		    	            display += "Title: " + title + "\n";
		    	            display += "Author: " + title + "\n";
		    	            display += "Description: " + description + "\n";
		    	            display += "Body: " + body + "\n";
		    	            display += "Level: " + level + "\n";
		    	            display += "Group Identifier: " + groupID + "\n";
		    	            display += "Keywords: " + keywords + "\n";
		    	            display += "Access Level: " + accessLevel + "\n";
		    	            display += "Other: " + other + "\n";
		    	            display += "Links: " + links + "\n";
		                }
		        }
		    }
		    
		  
		    return display;
	}
	
	
	/***
	 * @params username. The user should NOT be promted for their username and the username should be automatically added because of the flow they are already in. 
	 * @params groupIdentifier. Takes in username and articleId as input.
	 * @throws Exception
	 * Takes the username and feeds it into SpecialAccessGroups.getUserGroup(username) which checks if that user is involved in a special access group and returns which one. 
	 * If a group is found and it is the same as the one provided and they have viewing rights for that user then that article will be decrypted and displayed. 
	 * Otherwise only the articles whose accessLevel is public will be able to be displayed.  
	 * ***/
public String displayArticleByGroup(String groupIdentifier) throws Exception {
	    
	    String query = "SELECT * FROM Articles WHERE groupIdentifier = ? and accessLevel = 'public' ";
	    
	    String display = "";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, groupIdentifier);

	        try (ResultSet rs = pstmt.executeQuery()) {
	        	
					while(rs.next()) {

	                     int newid = rs.getInt("id");
	                    String title = rs.getString("title");
	                    String author = rs.getString("author");
	                    String description = rs.getString("description");
	                    String body = rs.getString("body");
	                    String level = rs.getString("level");
	                    String groupID = rs.getString("groupIdentifier");
	                    String keywords = rs.getString("keywords");
	                    String accessLevel = rs.getString("accessLevel");
	                    String other = rs.getString("other");
	                    String links = rs.getString("links_misc");
	                    
	                    
	                    display += "ID: " + newid + "\n";
	    	            display += "Title: " + title + "\n";
	    	            display += "Author: " + author + "\n";
	    	            display += "Description: " + description + "\n";
	    	            display += "Body: " + body + "\n";
	    	            display += "Level: " + level + "\n";
	    	            display += "Group Identifier: " + groupID + "\n";
	    	            display += "Keywords: " + keywords + "\n";
	    	            display += "Access Level: " + accessLevel + "\n";
	    	            display += "Other: " + other + "\n";
	    	            display += "Links: " + links + "\n";
	                }
					
	        }
	    }
	    
	    
	    return display;
	}
	
	/*
	public String searchArticle(String username, String level, String groupIdentifier, String word) throws SQLException {
	    boolean isSpecialUser = SpecialAccessGroups.doesSpecialUserExist(username);
	    String accessClause = isSpecialUser
	        ? " AND (accessLevel = 'public' OR (accessLevel = 'restricted' AND groupIdentifier = ?))"
	        : " AND accessLevel = 'public'";
	    
	    String display = "";
	    
	    // Display active group
	    display += "Active Group: " + groupIdentifier + "\n";
	    
	    // Count query for each level
	    String countQuery = 
	    	    "SELECT level, COUNT(*) AS count " +
	    	    "FROM Articles " +
	    	    "WHERE groupIdentifier = ? " +
	    	    "AND (title LIKE ? OR description LIKE ?) " +
	    	    accessClause + 
	    	    " GROUP BY level";

	    	// Main search query
	    	String searchQuery = 
	    	    "SELECT id, title, description, level, author " +
	    	    "FROM Articles " +
	    	    "WHERE groupIdentifier = ? " +
	    	    "AND (title LIKE ? OR description LIKE ?) " +
	    	    (level.equalsIgnoreCase("all") ? "" : " AND level = ? ") +
	    	    accessClause + 
	    	    " ORDER BY id";
	    	
	    try {
	        // First display the counts for each level
	    	display += "Articles found at each level:\n";
	        String searchPattern = "%" + word + "%";
	        
	        try (PreparedStatement countStmt = connection.prepareStatement(countQuery)) {
	            int paramIndex = 1;
	            countStmt.setString(paramIndex++, groupIdentifier);
	            countStmt.setString(paramIndex++, searchPattern);
	            countStmt.setString(paramIndex++, searchPattern);
	            if (isSpecialUser) {
	                countStmt.setString(paramIndex++, groupIdentifier);
	            }
	            
	            ResultSet countRs = countStmt.executeQuery();
	            while (countRs.next()) {
	                String lvl = countRs.getString("level");
	                int count = countRs.getInt("count");
	                display += String.format("%s: %d articles%n", 
	                        lvl.substring(0, 1).toUpperCase() + lvl.substring(1), 
	                        count);
	            }
	        }
	        
	        // Display the matching articles
	        display += "Matching articles:\n";
	        
	        try (PreparedStatement searchStmt = connection.prepareStatement(searchQuery)) {
	            int paramIndex = 1;
	            searchStmt.setString(paramIndex++, groupIdentifier);
	            searchStmt.setString(paramIndex++, searchPattern);
	            searchStmt.setString(paramIndex++, searchPattern);
	            if (isSpecialUser) {
	                searchStmt.setString(paramIndex++, groupIdentifier);
	            }
	            if (!level.equalsIgnoreCase("all")) {
	                searchStmt.setString(paramIndex++, level);
	            }
	            
	            ResultSet rs = searchStmt.executeQuery();
	            int sequence = 1;
	            boolean resultsFound = false;
	            
	            while (rs.next()) {
	                resultsFound = true;
	                display += String.format("%d. %s by %s: %s%n", 
	                        sequence++, 
	                        rs.getString("title"), 
	                        rs.getString("author"), 
	                        rs.getString("description"));
	            }
	            
	        }
	    } catch (SQLException e) {
	        System.err.println("Error executing search: " + e.getMessage());
	        throw e;
	    }
	    
	    return display;
	}
	*/
	
	
	public String displayArticleByAuthor(String author) throws Exception {
	    // Query to search for articles by a specific author with public access level
	    String query = "SELECT * FROM Articles WHERE author = ? AND accessLevel = 'public' ";
	    String display = "";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, author); // Set the author parameter

	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                int newid = rs.getInt("id");
	                String title = rs.getString("title");
	                String articleAuthor = rs.getString("author");
	                String description = rs.getString("description");
	                String body = rs.getString("body");
	                String level = rs.getString("level");
	                String groupID = rs.getString("groupIdentifier");
	                String keywords = rs.getString("keywords");
	                String accessLevel = rs.getString("accessLevel");
	                String other = rs.getString("other");
	                String links = rs.getString("links_misc");

	                // Display article details
	                display += "ID: " + newid + "\n";
	                display += "Title: " + title + "\n";
	                display += "Author: " + articleAuthor + "\n";
	                display += "Description: " + description + "\n";
	                display += "Body: " + body + "\n";
	                display += "Level: " + level + "\n";
	                display += "Group Identifier: " + groupID + "\n";
	                display += "Keywords: " + keywords + "\n";
	                display += "Access Level: " + accessLevel + "\n";
	                display += "Other: " + other + "\n";
	                display += "Links: " + links + "\n";
	            }
	        }
	    }
	    return display;
	}
	
	public String searchArticlesByWord(String searchTerm) throws Exception {
	    
	    String searchPattern = "%" + searchTerm + "%";
	    String display = "";

	    
	    String query = "SELECT * FROM Articles WHERE (title LIKE ? OR description LIKE ? OR keywords LIKE ?) AND accessLevel = 'public' ";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	       
	        pstmt.setString(1, searchPattern); // Search in title
	        pstmt.setString(2, searchPattern); // Search in description
	        pstmt.setString(3, searchPattern); // Search in keywords

	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                int newid = rs.getInt("id");
	                String title = rs.getString("title");
	                String author = rs.getString("author");
	                String description = rs.getString("description");
	                String body = rs.getString("body");
	                String level = rs.getString("level");
	                String groupID = rs.getString("groupIdentifier");
	                String keywords = rs.getString("keywords");
	                String accessLevel = rs.getString("accessLevel");
	                String other = rs.getString("other");
	                String links = rs.getString("links_misc");

	                // Display article details
	                display += "ID: " + newid + "\n";
	                display += "Title: " + title + "\n";
	                display += "Author: " + author + "\n";
	                display += "Description: " + description + "\n";
	                display += "Body: " + body + "\n";
	                display += "Level: " + level + "\n";
	                display += "Group Identifier: " + groupID + "\n";
	                display += "Keywords: " + keywords + "\n";
	                display += "Access Level: " + accessLevel + "\n";
	                display += "Other: " + other + "\n";
	                display += "Links: " + links + "\n";
	     
	                }
	            }
	        }
	    return display;
	}
	
	public void addSpecificMessage(String username, String message) throws SQLException {
	    String insertMessage = "INSERT INTO queries (username, question, answered) VALUES (?, ?, FALSE)";
	    try (PreparedStatement pstmt = connection.prepareStatement(insertMessage)) {
	        pstmt.setString(1, username); 
	        pstmt.setString(2, message); 
	        pstmt.executeUpdate(); 
	    }
	}
	
	public void createGenericMessageArticles(String title, String author, String description, String body, 
			String keywords, String other, String links) throws SQLException{
		
		long uniqueID = generateUniqueID();
		String insertArticle = "INSERT INTO Articles (title, author, description, body, level, groupIdentifier, keywords, accessLevel, other, links_misc, uniqueID) "
				+ "VALUES (?, ?, ?, ?, 'N/A', 'Query', ?, 'public', ?, ?, ?)";
		
		try (PreparedStatement pstmt = connection.prepareStatement(insertArticle))
		{
			pstmt.setString(1, title);
			pstmt.setString(2, author);
			pstmt.setString(3, description);
			pstmt.setString(4, body);
		    pstmt.setString(5, keywords);
		    pstmt.setString(6, other);
		    pstmt.setString(7, links);
		    pstmt.setLong(8, uniqueID);
		    pstmt.executeUpdate();
		} 
	}

public String displayGenericMessageArticles(String inputTitle) throws Exception {
	    
	    String query = "SELECT * FROM Articles WHERE title = ? AND groupIdentifier = 'Query'";
	    
	    String display = "";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, inputTitle);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (!rs.next()) {
	                return display;
	            }

	            int newid = rs.getInt("id");
	            String title = rs.getString("title");
	            String author = rs.getString("author");
	            String description = rs.getString("description");
	            String body = rs.getString("body");
	            String level = rs.getString("level");
	            String groupIdentifier = rs.getString("groupIdentifier");
	            String keywords = rs.getString("keywords");
	            String accessLevel = rs.getString("accessLevel");
	            String other = rs.getString("other");
	            String links = rs.getString("links_misc");

	            display += "ID: " + newid + "\n";
	            display += "Title: " + title + "\n";
	            display += "Author: " + author + "\n";
	            display += "Description: " + description + "\n";
	            display += "Body: " + body + "\n";
	            display += "Level: " + level + "\n";
	            display += "Group Identifier: " + groupIdentifier + "\n";
	            display += "Keywords: " + keywords + "\n";
	            display += "Access Level: " + accessLevel + "\n";
	            display += "Other: " + other + "\n";
	            display += "Links: " + links;
	            return display;
	        }
	    }
	}


	
public String displayUnansweredStudentQueries() throws SQLException {
    String query = "SELECT username, question FROM queries WHERE answered = FALSE";
    Statement stmt = connection.createStatement();
    ResultSet rs = stmt.executeQuery(query); 

    String display = "";

    while(rs.next()) { 
            String username = rs.getString("username");
            String question = rs.getString("question");

            display += "Username: " + username + "\n";
            display += "Question: " + question + "\n";
    }

    if (display.isEmpty()) {
        display = "All questions have been answered.";
    }

    return display;
}

	/**
	 * Helper method or restoration that inserts articles into the database
	 * given all the data.
	 * 
	 * @param title
	 * @param description
	 * @param body
	 * @param level
	 * @param groupIdentifier
	 * @param keywords
	 * @param accessLevel
	 * @param other
	 * @param links_misc
	 * @param UID
	 * @throws SQLException
	 */
	public void restorationAdd(String title, String author, String description, String body, String level, String groupIdentifier, 
			String keywords, String accessLevel, String other, String links_misc, long UID) throws SQLException
	{
			String insertArticle = "INSERT INTO Articles (title, author, description, body, level, groupIdentifier, keywords, accessLevel, other, links_misc, uniqueID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		try (PreparedStatement pstmt = connection.prepareStatement(insertArticle))
		{
			pstmt.setString(1, title);
			pstmt.setString(2, author);
			pstmt.setString(3, description);
			pstmt.setString(4, body);
			pstmt.setString(5, level);
			pstmt.setString(6, groupIdentifier);
		    pstmt.setString(7, keywords);
		    pstmt.setString(8, accessLevel);
		    pstmt.setString(9, other);
		    pstmt.setString(10, links_misc);
		    pstmt.setLong(11, UID);
		    pstmt.executeUpdate();
		} 
	}
	
	
	/**
	 * Backs up the entire database into a given backup file. 
	 * 
	 * @param file
	 * @throws Exception
	 */
	public void backupHelpSystemToFile(String file) throws Exception
	{
		String backup = "SELECT * FROM Articles";
	    
	    try(Statement stmt = connection.createStatement())
	    {
	    	ResultSet rs = stmt.executeQuery(backup);
	    	BufferedWriter writer = new BufferedWriter(new FileWriter(file));
	    	
	    	writer.write("Title, Author, Description, Body, Level, Group Identifier, Keywords, Access Level, Other, Links, Unique ID");
	        writer.newLine();
	    	
	    	while (rs.next()) {
	            String title = rs.getString("title");
	            String author = rs.getString("author");
	            String description = rs.getString("description");
	            String body = rs.getString("body");
	            String level = rs.getString("level");
	            String groupIdentifier = rs.getString("groupIdentifier");
	            String keywords = rs.getString("keywords");
	            String accessLevel = rs.getString("accessLevel");
	            String other = rs.getString("other");
	            String links = rs.getString("links_misc");
	            String uniqueID = rs.getString("uniqueID");
	            
	            writer.write(title + "&&" + author + "&&" + description + "&&" + body + "&&" + level + "&&" +
	                         groupIdentifier + "&&" + keywords + "&&" + accessLevel +
	                         "&&" + other + "&&" + links + "&&" + uniqueID);
	            writer.newLine();
	        }
	    	
	    	writer.close();
	    	rs.close(); 
	    }

	}
	
	/**
	 * Backs up only articles that belong to the group identifier
	 * into a given file name. 
	 * 
	 * @param file
	 * @param groupIdentifier
	 * @throws Exception
	 */
	public void backUpGroupToFile(String file, String groupIdentifier) throws Exception {
	    String backup = "SELECT * FROM Articles WHERE groupIdentifier = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(backup)) {
	        pstmt.setString(1, groupIdentifier); 

	        ResultSet rs = pstmt.executeQuery();
	        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

	        writer.write("Title, Author, Description, Body, Level, Group Identifier, Keywords, Access Level, Other, Links");
	        writer.newLine();

	        while (rs.next()) {
	            String title = rs.getString("title");
	            String author = rs.getString("author");
	            String description = rs.getString("description");
	            String body = rs.getString("body");
	            String level = rs.getString("level");
	            String groupID = rs.getString("groupIdentifier");
	            String keywords = rs.getString("keywords");
	            String accessLevel = rs.getString("accessLevel");
	            String other = rs.getString("other");
	            String links = rs.getString("links_misc");
	            String uniqueID = rs.getString("uniqueID");

	            writer.write(title + "&&" + author + "&&" + description + "&&" + body + "&&" + level + "&&" +
                        groupID + "&&" + keywords + "&&" + accessLevel +
                        "&&" + other + "&&" + links + "&&" + uniqueID);
	            writer.newLine();
	        }

	        writer.close();
	        rs.close(); 
	    }
	}
	
	/**
	 * Deletes an article given its unique ID.
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public boolean deleteArticle(int id) throws SQLException {
	    String query = "DELETE FROM Articles WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, id);
	        int rowsAffected = pstmt.executeUpdate();
	        return rowsAffected > 0;
		}
	        
	}
	
	/**
	 * Deletes all articles from the database. Only 
	 * for instructor and admin. 
	 * 
	 * @throws Exception
	 */
	public void deleteAll() throws Exception
	{
		String deleteAll = "DELETE FROM Articles";
		try (PreparedStatement pstmt = connection.prepareStatement(deleteAll))
		{
			pstmt.executeUpdate();
		}
	}
	
	
	/**
	 * Restores a backup file after deleting all files from database. 
	 * 
	 * @param file
	 * @throws Exception
	 */
	public void restoreSystem(String file) throws Exception {
	    if (hasArticles()) {
	        deleteAll();
	    }

	    String row;
	    String delimiter = "&&";

	    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
	        reader.readLine(); 

	        while ((row = reader.readLine()) != null) {
	            String[] data = row.split(delimiter);

	            if (data.length >= 9) {
	                if (isValidLevel(data[3]) && isValidAccessLevel(data[6])) {
	                    createHelpArticle( data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7], data[8], data[9]);
	                } else {
	                    System.out.println("Invalid data in row (level/accessLevel constraints): " + row);
	                }
	            } else {
	                System.out.println("Could not read row. Incorrect number of columns.");
	            }
	        }
	    }
	}

	/**
	 * Checks to see if an article is of a valid level type. 
	 * 
	 * @param level
	 * @return
	 */
	private boolean isValidLevel(String level) {
	    return level.compareTo("beginner") == 0 || level.compareTo("intermediate") == 0 || level.compareTo("advanced") == 0 || level.compareTo("expert") == 0;
	}

	
	/**
	 * Checks to see if an article is of a valid access level.
	 * 
	 * @param accessLevel
	 * @return
	 */
	private boolean isValidAccessLevel(String accessLevel) {
	    return accessLevel.compareTo("public") == 0 || accessLevel.compareTo("restricted") == 0;
	}
	
	
	/**
	 * Checks to see if an article exists in the database
	 * given an article title.
	 * 
	 * @param title
	 * @return
	 * @throws SQLException
	 */
	public boolean articleExists(String title) throws SQLException {
		String query = "SELECT COUNT(*) FROM articles WHERE title = ?";
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, title);
			try (ResultSet result = statement.executeQuery()) {
				if (result.next()) {
					return result.getInt(1) > 0; 
				}
			}
		}
		return false;
	}
	
	
	/**
	 * Checks to see if articles exist.
	 * 
	 * @return
	 * @throws SQLException
	 */
	public boolean hasArticles() throws SQLException {
	    String query = "SELECT COUNT(*) FROM Articles";
	    try (PreparedStatement pstmt = connection.prepareStatement(query);
	         ResultSet rs = pstmt.executeQuery()) {
	        if (rs.next()) {
	            return rs.getInt(1) > 0;
	        }
	    }
	    return false;
	}

	/**
	 * Restores system if the database is not empty and makes sure no duplicates are made. 
	 * Only instructor and admin.
	 * 
	 * @param file
	 * @throws Exception
	 */
	public void restoreSystemExisting(String file) throws Exception {
	    String row;
	    String delimiter = "&&";

	    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
	        reader.readLine(); // Skip header

	        while ((row = reader.readLine()) != null) {
	            String[] data = row.split(delimiter);

	            if (data.length >= 10) {
	                if (isValidLevel(data[3]) && isValidAccessLevel(data[6])) {
	                    if (!articleExists(data[9])) { 
	                        long UID = Long.parseLong(data[9]);
	                        restorationAdd(data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7], data[8], data[9], UID);
	                    } else {
	                        System.out.println("Duplicate uniqueID detected: " + data[9]);
	                    }
	                } else {
	                    System.out.println("Invalid data in row (level/accessLevel constraints): " + row);
	                }
	            } else {
	                System.out.println("Could not read row. Incorrect number of columns: " + row);
	            }
	        }
	    }
	}

	


	/** ------------ User Login and Database Functions  ------------ */
	
	
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
	public String displayUsersByAdmin() throws SQLException{
		String sql = "SELECT * FROM cse360users"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 
		String output = "";

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
			output += "ID: " + id;
			output += ", Username: " + username; 
			output += ", Pass: " + password; 
			output += ", Role: " + role; 
			if(role.compareTo("admin") != 0) {
				output += ", Email: " + email; 
				output += ", Full Name: " + fullName; 
				output += ", Pref Name: " + prefName; 
				output += ", One-Time Pass: " + oneTime; 
				output += ", Expire Date: " + expireDate; 
				output += ", Skill Level: " + skill + "\n"; 
			}else {
				output += "\n";
			}
		} 
		return output;
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
		
	
	/** ------------ Remove User Function  ------------ */
	
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
	
	
	/** ------------ Invite User Functions  ------------ */

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
	

	

	/** ------------ Switch Role Functions ------------ */
		
	/**
	 * Changes the role of a given user to a specified new role, currently in use
	 * by the admin for their Switch Role access.
	 * 
	 * @throws SQLException if there is an error executing the SQL query.
	 */
	public void changeRole(String username, String newRole) throws SQLException {
		String sql = "UPDATE cse360users SET role = ? WHERE username = ?";
		    
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, newRole);  
	        pstmt.setString(2, username);  
	        pstmt.executeUpdate();
	
	    }
	}
	
		
	/** ------------ Reset Functions  ------------ */
	
	/**
	 * Resets a users password to a one time password given by the admin 
	 * in order to reset the users account, expiration date is also given and 
	 * boolean oneTimePassword is set to true to indicate that the password 
	 * currently tied to the user is a one-time password.
	 * 
	 * @throws SQLException if there is an error executing the SQL query.
	 */
	public void resetUserPassword(String username, String password, Date expiration) throws SQLException {
		String sql = "UPDATE cse360users SET password = ?, passwordExpired = ?, oneTimePassword = true WHERE username = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, password);
	        pstmt.setDate(2, expiration);
	        pstmt.setString(3, username);
	        pstmt.executeUpdate();
		}
		
	}
	
	/**
	 * Checks to see if a username's password is valid without requiring a role
	 * to be checked. 
	 * 
	 * @throws SQLException if there is an error executing the SQL query.
	 */
	public boolean isPasswordValid(String username, String password) throws SQLException {
	    boolean isValid = false;
	    String sql = "SELECT FROM cse360users WHERE username = ? AND password = ? AND oneTimePassword = true";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	    	pstmt.setString(1, username); 
	        pstmt.setString(2, password);
	        
	        
	        ResultSet rs = pstmt.executeQuery();
	        
	            if (rs.next()) {
	                isValid = true;
	                return isValid;
	            }    
	    }
	    return isValid;
	}
	
	
	/**
	 * Retrieves the expiration date for the users one time password
	 * to be checked against the current date and verify that the 
	 * password is not expired. 
	 * 
	 * @throws SQLException if there is an error executing the SQL query.
	 */
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
	
	/**
	 * Changes a users password to a different password given the username 
	 * and the new password.
	 * 
	 * @throws SQLException if there is an error executing the SQL query.
	 */
	public void updatePassword(String username, String givenPassword) throws SQLException {
	    String sql = "UPDATE cse360users SET password = ? WHERE username = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, givenPassword);  
	        pstmt.setString(2, username);  
	        pstmt.executeUpdate();
	
	    }
	}
		
	/**
	 * Sets the boolean oneTimePassword to false to indicate that the 
	 * user's current password is no longer a one time password.
	 * 
	 * @throws SQLException if there is an error executing the SQL query.
	 */
	public void oneTimePasswordUsed(String username) throws SQLException {
	    String sql = "UPDATE cse360users SET oneTimePassword = false WHERE username = ?";
	
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, username); 
	        pstmt.executeUpdate();
	    }
	}

	/**
	 * Retrieves a users role given their username. 
	 * 
	 * @throws SQLException if there is an error executing the SQL query.
	 */
	public String getRoleFrom(String username) throws SQLException{
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
