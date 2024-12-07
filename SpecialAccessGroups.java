
/**
 * The SpecialAccessGroups class provides methods for interacting with the database
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
	private static EncryptionHelper encryptionHelper;
	
	public SpecialAccessGroups() throws Exception {
		encryptionHelper = new EncryptionHelper();
	}
	
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
			createSpecialUserTable();
			createSpecialArticleTable();
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
				+ "adminRights BOOLEAN, "
				+ "viewingRights BOOLEAN, "
				+ "role VARCHAR(20))";
		
		statement.execute(specialUserTable);
		String inviteSpecialUserTable = "CREATE TABLE IF NOT EXISTS specialInvites ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "username VARCHAR(255), "
				+ "groupName VARCHAR(255), "
				+ "adminRights BOOLEAN, "
				+ "viewingRights BOOLEAN, "
				+ "role VARCHAR(20), "
				+ "code VARCHAR(255))";

		
		statement.execute(inviteSpecialUserTable);
	}
	
	
	private void createSpecialArticleTable() throws SQLException {
		String destroy = "DROP TABLE IF EXISTS specialArticle ";
		statement.execute(destroy);
		
		String articlesTable = "CREATE TABLE IF NOT EXISTS specialArticle ("
                + "id INT PRIMARY KEY AUTO_INCREMENT, "
                + "title VARCHAR(255), "
                + "author VARCHAR(255), "
                + "description VARCHAR(500), "
                + "body TEXT, "
                + "groupIdentifier VARCHAR(100), "
                + "keywords VARCHAR(500), "
                + "other VARCHAR(500), "
                + "links_misc VARCHAR(500), "
                + "uniqueID BIGINT UNIQUE"
                + ")";
		
        statement.execute(articlesTable);
        
	}
	
	
	
	public String listSpecialArticle(String groupIdentifier) throws Exception
	{
		 	String query = "SELECT * FROM specialArticle WHERE groupIdentifier = ? ";
		    String display = "";

		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setString(1,groupIdentifier); 

		        try (ResultSet rs = pstmt.executeQuery()) {
		        	
		        	while (rs.next()) {
			            int id = rs.getInt("id");
			            String title = rs.getString("title");
			         
			            String groupID = rs.getString("groupIdentifier");

			            display += "ID: " + id + "\n";
			            display += "Title: " + title + "\n";
			            
			            display += "Group Identifier: " + groupID + "\n";
			        }
		        }
		    }
		    
		    return display;
	}
	
	
	
public String displayArticleByID(String groupIdentifier, int id) throws Exception {
	    
	    String query = "SELECT * FROM  WHERE groupIdentifier = ? AND id = ?";
	    
	    String display = "";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, groupIdentifier);
	        pstmt.setInt(1,  id);

	        try (ResultSet rs = pstmt.executeQuery()) {
	        	
					while(rs.next()) {

	                     int newid = rs.getInt("id");
	                    String title = rs.getString("title");
	                    String author = rs.getString("author");
	                    String description = rs.getString("description");
	                    String body = rs.getString("body");
	                    String keywords = rs.getString("keywords");
	                    String other = rs.getString("other");
	                    String links = rs.getString("links_misc");
	                    
	                    
	                    display += "ID: " + newid + "\n";
	    	            display += "Title: " + title + "\n";
	    	            display += "Author: " + author + "\n";
	    	            display += "Description: " + description + "\n";
	    	            display += "Body: " + body + "\n";
	    	            display += "Keywords: " + keywords + "\n";
	    	            display += "Other: " + other + "\n";
	    	            display += "Links: " + links + "\n";
	                }
					
	        }
	    }
	    
	    
	    return display;
	}
	
public String displayArticleByAuthor(String author, String groupIdentifier) throws Exception {
    // Query to search for articles by a specific author and group identifier with public access level
    String query = "SELECT * FROM specialArticle WHERE author = ? AND groupIdentifier = ?";
    String display = "";

    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
        pstmt.setString(1, author); // Set the author parameter
        pstmt.setString(2, groupIdentifier); // Set the groupIdentifier parameter

        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int newid = rs.getInt("id");
                String title = rs.getString("title");
                String articleAuthor = rs.getString("author");
                String description = rs.getString("description");
                String body = rs.getString("body");
                
                String groupID = rs.getString("groupIdentifier");
                String keywords = rs.getString("keywords");
                
                String other = rs.getString("other");
                String links = rs.getString("links_misc");

                // Display article details
                display += "ID: " + newid + "\n";
                display += "Title: " + title + "\n";
                display += "Author: " + articleAuthor + "\n";
                display += "Description: " + description + "\n";
                display += "Body: " + body + "\n";
                
                display += "Group Identifier: " + groupID + "\n";
                display += "Keywords: " + keywords + "\n";
               
                display += "Other: " + other + "\n";
                display += "Links: " + links + "\n";
            }
        }
    }
    return display;
}

public String searchArticlesByWord(String searchTerm, String groupIdentifier) throws Exception {
    
    String searchPattern = "%" + searchTerm + "%";
    String display = "";

    
    String query = "SELECT * FROM specialArticle WHERE (title LIKE ? OR description LIKE ? OR keywords LIKE ?) ";
    
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
       
        pstmt.setString(1, searchPattern); // Search in title
        pstmt.setString(2, searchPattern); // Search in description
        pstmt.setString(3, searchPattern); // Search in keywords
        
        if (groupIdentifier != null) {
            pstmt.setString(4, groupIdentifier); 
        }

        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int newid = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String description = rs.getString("description");
                String body = rs.getString("body");
                
                String groupID = rs.getString("groupIdentifier");
                String keywords = rs.getString("keywords");
                String other = rs.getString("other");
                String links = rs.getString("links_misc");

                // Display article details
                display += "ID: " + newid + "\n";
                display += "Title: " + title + "\n";
                display += "Author: " + author + "\n";
                display += "Description: " + description + "\n";
                display += "Body: " + body + "\n";
                
                display += "Group Identifier: " + groupID + "\n";
                display += "Keywords: " + keywords + "\n";
                display += "Other: " + other + "\n";
                display += "Links: " + links + "\n";
     
                }
            }
        }
    return display;
}




	/** ------------ Edit Article Methods (2)  ------------ */
	

	/**
	 * Get body of article for edit.
	 * 
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public static String getArticleBodyByID(int id) throws Exception
	{
	    String display = "";
	
	    String query = "SELECT body FROM specialArticle WHERE id = ?";
	
	     try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	            pstmt.setInt(1, id);
	
	            try (ResultSet rs = pstmt.executeQuery()) {
	                if (!rs.next()) {
	                    return display;
	                }
	
	                String body = rs.getString("body");
	               
	                
	                display += "Body: " + body + "\n";
	                return display;
	            }
	        }
	
	
	}
	
	/**
	 * Insert text into the body of an article.
	 * 
	 * @param id
	 * @param body
	 * @throws SQLException
	 */
	public static void insertArticleBody(int id, String body) throws SQLException
	{ 
	    String query = "UPDATE specialArticle SET body = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, body);
	        pstmt.setInt(2, id);
	
	        try (ResultSet rs = pstmt.executeQuery()) {
	        }
	    }
	}


	public void restorationAdd(String title, String author, String description, String body, String groupIdentifier, 
			String keywords, String other, String links_misc, long UID) throws SQLException
	{
			String insertArticle = "INSERT INTO specialArticle (title, author, description, body, groupIdentifier, keywords, other, links_misc, uniqueID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		try (PreparedStatement pstmt = connection.prepareStatement(insertArticle))
		{
			pstmt.setString(1, title);
			pstmt.setString(2, author);
			pstmt.setString(3, description);
			pstmt.setString(4, body);
			pstmt.setString(5, groupIdentifier);
		    pstmt.setString(6, keywords);
		    pstmt.setString(7, other);
		    pstmt.setString(8, links_misc);
		    pstmt.setLong(9, UID);
		    pstmt.executeUpdate();
		} 
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
		boolean adminRights = true;
		boolean vRights = true;
		String insertFirst = "INSERT INTO specialUsers (username, groupName, adminRights, viewingRights, role)"
				+ "VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertFirst))
		{
			pstmt.setString(1, username);
			pstmt.setString(2, groupName);
			pstmt.setBoolean(3, adminRights);
			pstmt.setBoolean(4, vRights);
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
	public static void addAdmin(String username, String groupName) throws Exception
	{
		boolean adminRights = true;
		boolean vRights = true;
		String role = "admin";
		
		String insertUser = "INSERT INTO specialUsers (username, groupName, adminRights, viewingRights, role)"
				+ "VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser))
		{
			pstmt.setString(1, username);
			pstmt.setString(2, groupName);
			pstmt.setBoolean(3, adminRights);
			pstmt.setBoolean(4, vRights);
			pstmt.setString(5, role);
		    pstmt.executeUpdate();
		} 
	}
	
	
	public void backupSpecialSystemToFile(String file) throws Exception
	{
		String backup = "SELECT * FROM specialArticle";
	    
	    try(Statement stmt = connection.createStatement())
	    {
	    	ResultSet rs = stmt.executeQuery(backup);
	    	BufferedWriter writer = new BufferedWriter(new FileWriter(file));
	    	
	    	writer.write("ID, Title, Author, Description, Body, Group Identifier, Keywords, Other, Links, Unique ID");
	        writer.newLine();
	    	
	    	while (rs.next()) {
	    		int id = rs.getInt("id");
	            String title = rs.getString("title");
	            String author = rs.getString("author");
	            String description = rs.getString("description");
	            String body = rs.getString("body");
	            String groupID = rs.getString("groupIdentifier");
	            String keywords = rs.getString("keywords");
	            String other = rs.getString("other");
	            String links = rs.getString("links_misc");
	            String uniqueID = rs.getString("uniqueID");
	            
	            writer.write(id + "," + title + "," + author + "," + description + "," + body + "," +
	                         groupID + "," + keywords + "," + other + "," + links + "," + uniqueID);
	            writer.newLine();
	        }
	    	
	    	writer.close();
	    	rs.close(); 
	    }

	}
	
	
	public void backUpSpecialGroupToFile(String file, String groupIdentifier) throws Exception {
	    String backup = "SELECT * FROM specialArticle WHERE groupIdentifier = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(backup)) {
	        pstmt.setString(1, groupIdentifier); 

	        ResultSet rs = pstmt.executeQuery();
	        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

	        writer.write("ID, Title, Author, Description, Body, Group Identifier, Keywords, Other, Links, Unique ID");
	        writer.newLine();

	        while (rs.next()) {
	    		int id = rs.getInt("id");
	            String title = rs.getString("title");
	            String author = rs.getString("author");
	            String description = rs.getString("description");
	            String body = rs.getString("body");
	            String groupID = rs.getString("groupIdentifier");
	            String keywords = rs.getString("keywords");
	            String other = rs.getString("other");
	            String links = rs.getString("links_misc");
	            String uniqueID = rs.getString("uniqueID");
	            
	            writer.write(id + "," + title + "," + author + "," + description + "," + body + "," +
	                         groupID + "," + keywords + "," + other + "," + links + "," + uniqueID);
	            writer.newLine();
	        }
	    	
	    	writer.close();
	    	rs.close(); 
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
	public static void addInstructor(String username, String groupName) throws Exception
	{
		boolean adminRights = false;
		boolean vRights = true;
		String role = "instructor";
		
		String insertUser = "INSERT INTO specialUsers (username, groupName, adminRights, viewingRights, role)"
				+ "VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser))
		{
			pstmt.setString(1, username);
			pstmt.setString(2, groupName);
			pstmt.setBoolean(3, adminRights);
			pstmt.setBoolean(4, vRights);
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
		boolean adminRights = false;
		boolean vRights = true;
		String role = "student";
		
		String insertUser = "INSERT INTO specialUsers (username, groupName, adminRights, viewingRights, role)"
				+ "VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser))
		{
			pstmt.setString(1, username);
			pstmt.setString(2, groupName);
			pstmt.setBoolean(3, adminRights);
			pstmt.setBoolean(4, vRights);
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
		        pstmt.setBoolean(1, true); 
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
		        pstmt.setBoolean(1, true); 
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
	public void addSpecialArticle(String title, String author,String description, String body, String groupIdentifier, 
			String keywords, String other, String links) throws Exception
	{

		long uniqueID = generateUniqueID();
		String UID = String.valueOf(uniqueID);
			
		
		String insertArticle = "INSERT INTO specialArticle (title, author, description, body, groupIdentifier, keywords, other, links_misc, uniqueID) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		try (PreparedStatement pstmt = connection.prepareStatement(insertArticle))
		{
			pstmt.setString(1, title);
			pstmt.setString(2, author);
			pstmt.setString(3, description);
			pstmt.setString(4, body);
			pstmt.setString(5, groupIdentifier);
		    pstmt.setString(6, keywords);
		    pstmt.setString(7, other);
		    pstmt.setString(8, links);
		    pstmt.setLong(9, uniqueID);
		    pstmt.executeUpdate();
		} 
	}

	
	/***
	 * Lists all those admins that have een given create, read, update, and delete access rights. Which i think are view and admin rights. 
	 * */
	public String listAdmin() throws Exception
	{
		String query = "SELECT * FROM specialUsers WHERE role = 'admin' AND adminRights = true and viewingRights = true ";
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
		String query = "SELECT * FROM specialUsers WHERE role = 'instructor' AND adminRights = true ";
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
	
	
	public String listAllSpecialAccessUsers() throws Exception
	{
		String query = "SELECT * FROM specialUsers id = ? ";
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
		String query = "SELECT * FROM specialUsers WHERE role = 'instructor' AND viewingRights = true ";
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
		String query = "SELECT * FROM specialUsers WHERE role = 'student' AND viewingRights = true ";
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
		String query = "SELECT * FROM specialUsers WHERE adminRights = true AND username = ? AND groupName = ? ";
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
		String query = "SELECT * FROM specialUsers WHERE viewingRights = true AND username = ? AND groupName = ? ";
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
		String removeArticle = "DELETE FROM specialArticle WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(removeArticle))
		{
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
		}
	        
	}
	
	/*** Deletes a specific user given an id*/
	public void deleteSpecialUser(String username) throws Exception
	{
		String removeArticle = "DELETE FROM specialUsers WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(removeArticle))
		{
			pstmt.setString(1, username);
			pstmt.executeUpdate();
		}
	        
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
	
	
