package simpleDatabase;

import java.sql.*;
import java.sql.SQLException;
import java.util.Scanner;

public class StartCSE360 {

	
	private static final DatabaseHelper databaseHelper = new DatabaseHelper();
	private static final Scanner scanner = new Scanner(System.in);

	public static void main( String[] args ) throws SQLException
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
				mainMenu();
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
		mainMenu();

	}

	private static void mainMenu() {
		System.out.println(" ---------------------------- ");
	    System.out.println("Welcome to the System");
	    System.out.println("1. Admin Login");
	    System.out.println("2. Instructor Login");
	    System.out.println("3. Student Login");
	    System.out.println("4. Invitation Code");
	    System.out.print("Choose an option: ");
	    String choice = scanner.nextLine();
	    
	    switch (choice) {
	        case "1":
	            try {
	                adminFlow();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	            break;
	        case "2":
	            try {
	                instructorFlow();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	            break;
	        case "3":
	            try {
	                studentFlow();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	            break;
	        case "4":
	            try {
	                inviteFlow();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	            break;
	        default:
	            System.out.println("Invalid choice. Please try again.");
	            mainMenu(); 
	            break;
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

	private static String pickRole() {
		System.out.println( "To invite an instructor then type I\n"
				+ "To invite a Student, then type S\nEnter your choice:  " );
		String role = scanner.nextLine();
		
		while(true) {
			switch (role) {
			case "S":
				return "Student";
			case "I":
				return "Instructor";
			default:
				System.out.println("Invalid choice. Please select 'I', 'S'");
			}
		}
	}
	
	private static void inviteFlow() throws SQLException {
		System.out.println("invite flow");
		System.out.print("Enter Given Invite Code: ");
		String code = scanner.nextLine();
		if(databaseHelper.doesInviteExist(code)) {
			String role = databaseHelper.getRole(code);
			System.out.println("Welcome " + role + "!");
			System.out.println(" ----------------------- ");
			switch (role) {
            case "Student":
            	databaseHelper.removeInvite(code);
                studentFlow();
                break;
            case "Instructor":
            	databaseHelper.removeInvite(code);
            	instructorFlow();
                break;
            default: 
            	System.out.println("Invite Role Invalid");
            	mainMenu();
			}
			
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
			System.out.println();
			adminHome();

		} else {
			System.out.println("Invalid admin credentials. Try again!!");
		}
	}
	
	private static void adminHome() throws SQLException {
	    boolean loggedOut = false;
	    System.out.println("Welcome to the Admin Home Page, ");
	    System.out.println(" ---------------------------- ");
	    while (!loggedOut) {
	        System.out.println("1. View All Users");
	        System.out.println("2. Invite User");
	        System.out.println("3. Reset an Account");
	        System.out.println("4. Delete an Account");
	        System.out.println("5. Change a Users Role");
	        System.out.println("6. Logout");
	        System.out.print("Please choose an option: ");

	        String choice = scanner.nextLine();

	        switch (choice) {
	            case "1":
	                System.out.println("Displaying all users...");
	                databaseHelper.displayUsersByAdmin();
	                System.out.println(" ----------------------- ");
	                break;
	            case "2":
	                String inviteRole = pickRole();
	                System.out.print("Input the Code for Invited User: ");
	                String inviteCode = scanner.nextLine();
	                databaseHelper.inviteUser(inviteCode, inviteRole);
	                System.out.println("User Invited with Code: " + inviteCode);
	                System.out.println(" ----------------------- ");
	                break;
	            case "3":
	                System.out.println("Input username to be reset: ");
	                String resetName = scanner.nextLine();
	                if(databaseHelper.doesUserExist(resetName)) {
	                	
	                }else {
	                	System.out.println("User does not exist!");
	                	System.out.println(" ------------------ ");
	                }
	                break;
	            case "4":
	                System.out.println("Managing instructors...");
	                break;
	            case "5":
	                System.out.println("Managing instructors...");
	                break;
	            case "6":
	                loggedOut = true; // Set loggedOut to true to exit loop
	                System.out.println("Logging out...");
	                break;
	            default:
	                System.out.println("Invalid choice. Please try again.");
	                break;
	        }
	    }
	} 


}
