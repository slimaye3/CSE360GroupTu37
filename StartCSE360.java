package simpleDatabase;

import java.sql.*;
import java.sql.SQLException;
import java.util.Scanner;

public class StartCSE360 {

	
	private static final DatabaseHelper databaseHelper = new DatabaseHelper();
	private static final Scanner scanner = new Scanner(System.in);

	public static void main( String[] args )
	{

		try { 
			databaseHelper.connectToDatabase();  // Connect to the database

			// Check if the database is empty (no users registered)
			if (databaseHelper.isDatabaseEmpty()) {
				System.out.println( "In-Memory Database  is empty" );
				//set up administrator access
				setupAdministrator();
			}
			else {
				normalStartup();
			}
		} catch (SQLException e) {
			System.err.println("Database error: " + e.getMessage());
			e.printStackTrace();
		}
		finally {
			System.out.println("Good Bye!!");
			databaseHelper.closeConnection();
		}
	}

	private static void setupAdministrator() throws SQLException {
		boolean matched = false;
		String passwordfirst = null;
		String password = null;
		String email = null;
		String fullName = null;
		String prefName = null;
		Date expire = null;
		String skillLevel = "Intermediate";
		System.out.println("Setting up the Administrator access.");
		System.out.print("Enter Admin Username: ");
		String username = scanner.nextLine();
		while(!matched) {
			System.out.print("Enter Admin Password: ");
			passwordfirst = scanner.nextLine();
			System.out.print("Enter Admin Password Again: ");
			password = scanner.nextLine();
			if(password.compareTo(passwordfirst) != 0) {
				System.out.print("ERROR : Passwords must match.\n");	
			}else {
				matched = true;
			}
		}
		databaseHelper.register(username, password, "admin", email, fullName, prefName, false, expire, skillLevel);
		System.out.println("Administrator setup completed.");
		normalStartup();

	}

	private static void normalStartup() throws SQLException {
		System.out.println( "If you are an administrator, then type A\nIf you are an instructor then type I\n"
				+ "If you are a Student, then type S\nEnter your choice:  " );
		String role = scanner.nextLine();

		switch (role) {
		case "S":
			studentFlow();
			break;
		case "A":
			adminFlow();
			break;
		case "I":
			instructorFlow();
			break;
		default:
			System.out.println("Invalid choice. Please select 'A', 'I', 'S'");
			databaseHelper.closeConnection();
		}
	}
	
	private static void studentFlow() throws SQLException {
		String email = null;
		String passwordfirst = null;
		String password = null;
		boolean matched = false;
		String fullName = null;
		String prefName = null;
		Date expire = null;
		String skillLevel = "Intermediate";
		System.out.println("student flow");
		System.out.print("What would you like to do 1.Register 2.Login  ");
		String choice = scanner.nextLine();
		switch(choice) {
		case "1": 
			System.out.print("Enter Student Username: ");
			String username = scanner.nextLine();
			System.out.print("Enter Student Email: ");
			email = scanner.nextLine();
			while(!matched) {
				System.out.print("Enter Student Password: ");
				passwordfirst = scanner.nextLine(); 
				System.out.print("Enter Student Password Again: ");
				password = scanner.nextLine(); 
				if(password.compareTo(passwordfirst) != 0) {
					System.out.print("ERROR : Passwords must match.\n");
				}else {
					matched = true;
				}
			}
			System.out.print("Enter Student First Name: ");
			String first = scanner.nextLine();
			System.out.print("Enter Student Middle Name: ");
			String middle = scanner.nextLine();
			System.out.print("Enter Student Last Name: ");
			String last = scanner.nextLine();
			System.out.print("Enter Student Preferred Name (Enter to Skip): ");
			prefName = scanner.nextLine();
			if(prefName.compareTo("") == 0) {
				prefName = "N/A";
			}
			fullName = first + " " + middle + " " + last;
			
			// Check if user already exists in the database
		    if (!databaseHelper.doesUserExist(username)) {
		    	boolean logout = false;
		    	databaseHelper.register(username, password, "student", email , fullName, prefName, false, expire, skillLevel);
		        System.out.println("User setup completed.");
		        System.out.println(" ------------------- ");
		        System.out.println("Logout at anytime by typing 'logout'.");
		        while(!logout) {
		        	 String input = scanner.nextLine();
				        if(input.compareTo("logout") == 0) {
				        	logout = true;
				        }
		        }
		    } else {
		        System.out.println("User already exists.");
		    }
			break;
		case "2":
			System.out.print("Enter Student Username: ");
			username = scanner.nextLine();
			System.out.print("Enter Student Password: ");
			password = scanner.nextLine();
			if (databaseHelper.login(username, password, "student")) {
				System.out.println("User login successful.");
//				databaseHelper.displayUsers();
				 System.out.println(" ------------------- ");
			        System.out.println("Logout at anytime by typing 'logout'.");
			        boolean logout = false;
			        while(!logout) {
			        	 String input = scanner.nextLine();
					        if(input.compareTo("logout") == 0) {
					        	logout = true;
					        }
			        }
			} else {
				System.out.println("Invalid user credentials. Try again!!");
			}
			break;
		}
	}
	
	private static void instructorFlow() throws SQLException {
		String email = null;
		String passwordfirst = null;
		String password = null;
		boolean matched = false;
		String fullName = null;
		String prefName = null;
		Date expire = null;
		String skillLevel = "Intermediate";
		System.out.println("instructor flow");
		System.out.print("What would you like to do 1.Register 2.Login  ");
		String choice = scanner.nextLine();
		switch(choice) {
		case "1": 
			System.out.print("Enter Instructor Username: ");
			String username = scanner.nextLine();
			System.out.print("Enter Instructor Email: ");
			email = scanner.nextLine();
			while(!matched) {
				System.out.print("Enter Instructor Password: ");
				passwordfirst = scanner.nextLine(); 
				System.out.print("Enter Instructor Password Again: ");
				password = scanner.nextLine(); 
				if(password.compareTo(passwordfirst) != 0) {
					System.out.print("ERROR : Passwords must match.\n");
				}else {
					matched = true;
				}
			}
			System.out.print("Enter Instructor First Name: ");
			String first = scanner.nextLine();
			System.out.print("Enter Instructor Middle Name: ");
			String middle = scanner.nextLine();
			System.out.print("Enter Instructor Last Name: ");
			String last = scanner.nextLine();
			System.out.print("Enter Instructor Preferred Name (Enter to Skip): ");
			prefName = scanner.nextLine();
			if(prefName.compareTo("") == 0) {
				prefName = "N/A";
			}
			fullName = first + " " + middle + " " + last;
			
			// Check if user already exists in the database
		    if (!databaseHelper.doesUserExist(username)) {
		    	databaseHelper.register(username, password,"instructor", email, fullName, prefName, false, expire, skillLevel);
		        System.out.println("Instructor setup completed.");
		        boolean logout = false;
		        while(!logout) {
		        	 String input = scanner.nextLine();
				        if(input.compareTo("logout") == 0) {
				        	logout = true;
				        }
		        }
		    } else {
		        System.out.println("Instructor already exists.");
		    }
			break;
		case "2":
			System.out.print("Enter Instructor Username: ");
			username = scanner.nextLine();
			System.out.print("Enter Instructor Password: ");
			password = scanner.nextLine();
			if (databaseHelper.login(username, password, "instructor")) {
				System.out.println("Instructor login successful.");
//				databaseHelper.displayUsers();
				boolean logout = false;
		        while(!logout) {
		        	 String input = scanner.nextLine();
				        if(input.compareTo("logout") == 0) {
				        	logout = true;
				        }
		        }

			} else {
				System.out.println("Invalid user credentials. Try again!!");
			}
			break;
		}
	}

	private static void adminFlow() throws SQLException {
		System.out.println("admin flow");
		System.out.print("Enter Admin Username: ");
		String username = scanner.nextLine();
		System.out.print("Enter Admin Password: ");
		String password = scanner.nextLine();
		if (databaseHelper.login(username, password, "admin")) {
			System.out.println("Admin login successful.");
			databaseHelper.displayUsersByAdmin();

		} else {
			System.out.println("Invalid admin credentials. Try again!!");
		}
	}


}
