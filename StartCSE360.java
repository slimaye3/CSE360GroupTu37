/**
 * The StartCSE360 class serves as the main entry point for the CSE 360 help system application.
 * It initializes the application, sets up the user interface, and handles user interactions 
 * related to registration, login, and navigation within the application.
 * 
 * Responsibilities:
 * - Handle user input for registration and login.
 * - Manage the navigation between different screens of the application.
 * - Integrate with the DatabaseHelper class to manage user data, including registration, login validation, 
 *   and user role management.
 * 
 * This class connects to the DatabaseHelper for managing user data and ensures 
 * a seamless user experience in the help system.
 * 
 * @version 2.0
 * @date October 30, 2024
 */

package simpleDatabase;

import java.sql.*;
import java.sql.Date;
import java.util.Scanner;
import java.time.*;
import java.util.InputMismatchException;
import java.time.format.DateTimeParseException;

public class StartCSE360 {
	
	/** ------------ Declarations ------------ */
	
	private static final DatabaseHelper databaseHelper = new DatabaseHelper(); //// Instance of DatabaseHelper for managing user data and database operations.
	private static final Scanner scanner = new Scanner(System.in);
	
	private static Date today = Date.valueOf(LocalDate.now());

	
	
	/** ------------ Main ------------ */

	/**
 	* This method  establishes a connection to the database, ensuring that the 
	* necessary resources are available for user management operations. 
 	* @throws SQLException If there is an error establishing a connection to the database.
 	*/
	public static void main( String[] args ) throws SQLException
	{
		System.out.println(today);
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
	
	
	/** ------------ Setup Database Admin ------------ */
	
	/**
	 * This method prompts the user for an administrator username and password,
	 * ensuring that the entered passwords match. Once the administrator details
	 * are confirmed, it registers the administrator in the database using the 
	 * DatabaseHelper class.
	 *
	 * @throws SQLException If an error occurs during database operations.
	 */
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
	
	
	/** ------------ Main Menu ------------ */
	
	/**
	 * This method presents the user with options to log in as an administrator, 
	 * instructor, student, or to enter an invitation code. Based on the user's 
	 * choice, it directs the flow to the appropriate login method.
	 */
	private static void mainMenu() {
		System.out.println(" ---------------------------- ");
	    System.out.println("Welcome to the System");
	    System.out.println("1. Admin Login");
	    System.out.println("2. Instructor Flow");
	    System.out.println("3. Student Flow");
	    System.out.println("4. Invitation Code");
	    System.out.println("5. One-Time Password Reset");
	    System.out.println("6. Quit");
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
	        case "5":
	            try {
	                resetFlow();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	            break;
	        case "6":
	            databaseHelper.closeConnection();
	            
	            System.out.println("Exiting the program...");
	            
	            System.exit(0);
	            break;
	        default:
	            System.out.println("Invalid choice. Please try again.");
	            mainMenu(); 
	            break;
	    }
	} 
	
	
	/** ------------ Article Flow ------------ */
	
	/**
	 * Article Management System, acts as main menu for managing articles as an admin
	 * or instructor. 
	 * 
	 * @throws SQLException
	 */
	private static void articleManage() throws SQLException {
		boolean loggedOut = false;
		
		while (!loggedOut) {
		System.out.println(" ---------------------------- ");
		System.out.println("Article Management System");
	    System.out.println("1. Create an Article");
	    System.out.println("2. Display an Article");
	    System.out.println("3. Delete an Article");
	    System.out.println("4. Backup an Article");
	    System.out.println("5. Restore an Article");
	    System.out.println("6. Search an Article by Keywords");
	    System.out.println("7. Go Back to Main Menu");
	    System.out.print("Choose an option: ");
	    String choice = scanner.nextLine();
		
	    switch (choice) {
	    	case "1":
                articleCreateManage();
	    	case "2":
	    		articleDisplayManage();
	    	case "3":
	    		articleDeleteManage();
	    	case "4":
	    		articleBackUpManage();
	    	case "5":
	    		articleRestoreManage();
	    	case "6":
	    		System.out.print("Enter the keyword to search for: ");
                String keyword = scanner.nextLine();
                try {
                    databaseHelper.searchKeyword(keyword);
                } catch (Exception e) {
                    System.out.println("Error during keyword search: " + e.getMessage());
                }
                System.out.println(" ----------------------- ");
                break;
	    	case "7":
	    		loggedOut = true;
                System.out.println("Going Back to Main Menu...");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
	    }
	    	
	    }
		mainMenu();
	}
	
	
	/**
	 * Creation for for articles, including input of title, description, body, 
	 * level, access level, group, keywords, other info, and links!
	 * 
	 * @throws SQLException
	 */
	private static void articleCreateManage() throws SQLException{
		System.out.print("Enter Article Title: ");
        String title = scanner.nextLine();
        System.out.print("Enter Article Description: ");
        String description = scanner.nextLine();
        System.out.print("Enter Article Body: ");
        String body = scanner.nextLine();

        String level;
        while (true) {
            System.out.print("Enter Article Level (b: beginner, i: intermediate, a: advanced, e: expert): ");
            String levelInput = scanner.nextLine().trim().toLowerCase();
            switch (levelInput) {
                case "b":
                    level = "beginner";
                    break;
                case "i":
                    level = "intermediate";
                    break;
                case "a":
                    level = "advanced";
                    break;
                case "e":
                    level = "expert";
                    break;
                default:
                    System.out.println("Invalid input. Please enter 'b', 'i', 'a', or 'e'.");
                    continue;
            }
            break;
        }

        System.out.print("Enter Group Identifier: ");
        String groupIdentifier = scanner.nextLine();

        System.out.print("Enter Article Keywords: ");
        String keywords = scanner.nextLine();

        String accessLevel;
        while (true) {
            System.out.print("Enter Access Level (p: public, r: restricted): ");
            String accessLevelInput = scanner.nextLine().trim().toLowerCase();
            switch (accessLevelInput) {
                case "p":
                    accessLevel = "public";
                    break;
                case "r":
                    accessLevel = "restricted";
                    break;
                default:
                    System.out.println("Invalid input. Please enter 'p' or 'r'.");
                    continue;
            }
            break;
        }

        System.out.print("Enter Other Details: ");
        String other = scanner.nextLine();

        System.out.print("Enter Links: ");
        String links = scanner.nextLine();

        databaseHelper.createHelpArticle(title, description, body, level, groupIdentifier, keywords, accessLevel, other, links);
        System.out.println("Article Created Successfully!");
        System.out.println(" ---------------------------- ");
        
        articleManage();
	}
	
	
	/**
	 * Display article manager, can view article by ID, group, or list them out in a shortened 
	 * fashion. 
	 * 
	 * @throws SQLException
	 */
	private static void articleDisplayManage() throws SQLException {
		boolean loggedOut = false;
		
		while (!loggedOut) {
		System.out.println(" ---------------------------- ");
	    System.out.println("1. View an Article by ID");
	    System.out.println("2. Display Articles by Group");
	    System.out.println("3. List All Articles");
	    System.out.println("4. Go Back");
	    System.out.print("Choose an option: ");
	    String choice = scanner.nextLine();
	    
	    switch (choice) {
    	case "1":
    		if (!databaseHelper.hasArticles()) {
                System.out.println("There are no articles at the moment.");
                System.out.println(" ----------------------- ");
                break;
            }

            System.out.print("Enter the ID of the article to display: ");
            try {
                int articleID = scanner.nextInt();
                scanner.nextLine(); // Clear newline character
                
                try {
                    if (!databaseHelper.displayArticle(articleID)) {
                        System.out.println("Invalid ID: This article does not exist.");
                    }
                } catch (SQLException e) {
                    System.out.println("An error occurred while retrieving the article: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("An error occurred while retrieving the article: " + e.getMessage());
            }
                
            }catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid numeric ID.");
                    scanner.nextLine();
            }

            System.out.println(" ----------------------- ");
            break;
    	case "2":
    		System.out.print("Enter Group Identifier: ");
            String groupIdentify = scanner.nextLine();
            try {
                databaseHelper.displayArticleByGroup(groupIdentify); 
            } catch (SQLException e) {
                System.out.println("An error occurred while trying to display group articles. Please check if the Identifier exists.");
            } catch (Exception e) {
                System.out.println("Error displaying articles by group: " + e.getMessage());
            }
            System.out.println(" ----------------------- ");
            break;
    	case "3":
    		try { databaseHelper.listArticles();
        	System.out.println(" ----------------------- ");
            break;
        	} catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number for the Article ID.");
                System.out.println(" ----------------------- ");
            } catch (SQLException e) {
                System.out.println("An error occurred while trying to delete the article. Please check if the ID exists.");
                System.out.println(" ----------------------- ");
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
                System.out.println(" ----------------------- ");
            }
    	case "4":
    		loggedOut = true;
            System.out.println("Going Back...");
            break;
    	default:
            System.out.println("Invalid choice. Please try again.");
            break;
	    }
		}
		articleManage();
	    
	}
	
	
	/**
	 * Article deletion manager. Can delete an article by ID, or you may delete all of them 
	 * at the same time (purge). 
	 * 
	 * @throws SQLException
	 */
	private static void articleDeleteManage() throws SQLException {
		boolean loggedOut = false;
		
		while (!loggedOut) {
		System.out.println(" ---------------------------- ");
	    System.out.println("1. Delete an Article by ID");
	    System.out.println("2. Delete All Articles");
	    System.out.println("3. Go Back");
	    System.out.print("Choose an option: ");
	    String choice = scanner.nextLine();
	    
	    switch (choice) {
    	case "1":
    		System.out.print("Enter Article ID to delete: ");
            int idToDelete;
            try {
                idToDelete = Integer.parseInt(scanner.nextLine());

                if (!databaseHelper.hasArticles()) {
                    System.out.println("No articles at the moment.");
                    System.out.println(" ---------------------------- ");
                    break;
                }

                boolean isDeleted = databaseHelper.deleteArticle(idToDelete);
                if (isDeleted) {
                    System.out.println("Article Deleted.");
                } else {
                    System.out.println("Invalid ID: This article does not exist.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number for the Article ID.");
            } catch (SQLException e) {
                System.out.println("An error occurred while trying to delete the article. Please check if the ID exists.");
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
            System.out.println(" ----------------------- ");
            break;
    	case "2":
    		String confirmation;
            do {
                System.out.print("Are you sure you want to delete all articles? (y/n): ");
                confirmation = scanner.nextLine().trim().toLowerCase();
                if (!confirmation.equals("y") && !confirmation.equals("n")) {
                    System.out.println("Invalid input. Please enter 'y' for yes or 'n' for no.");
                }
            } while (!confirmation.equals("y") && !confirmation.equals("n"));

            if (confirmation.equals("y")) {
                try {
                    databaseHelper.deleteAll();
                    System.out.println("All articles are deleted.");
                } catch (Exception e) {
                    System.out.println("Error deleting articles: " + e.getMessage());
                }
            } else {
                System.out.println("Deletion canceled."); 
            }
            System.out.println(" ----------------------- ");
            break;
    	case "3":
    		loggedOut = true;
            System.out.println("Going Back...");
            break;
    	default:
            System.out.println("Invalid choice. Please try again.");
            break;
	    }
		}
		articleManage();
	    
	    
	}
	
	
	/**
	 * Back up article manager, you can back up the entire database or by group identifier, 
	 * and it will ask for the backup file name to be used. 
	 * 
	 * @throws SQLException
	 */
	private static void articleBackUpManage() throws SQLException {
		boolean loggedOut = false;
		
		while (!loggedOut) {
		System.out.println(" ---------------------------- ");
	    System.out.println("1. BackUp All Articles");
	    System.out.println("2. BackUp Articles by Group");
	    System.out.println("3. Go Back");
	    System.out.print("Choose an option: ");
	    String choice = scanner.nextLine();
	    
	    switch (choice) {
    	case "1":
    		System.out.print("Enter the filename for backup (e.g., backup.csv): ");
            String backupFile = scanner.nextLine();
            try {
                databaseHelper.backupHelpSystemToFile(backupFile);
                System.out.println("Backup of the entire help system completed successfully.");
            } catch (Exception e) {
                System.out.println("Error during backup: " + e.getMessage());
            }
            System.out.println(" ----------------------- ");
            break;
    	case "2":
    		System.out.print("Enter the group identifier for backup: ");
            String groupIdentifierBackup = scanner.nextLine();
            System.out.print("Enter the filename for backup (e.g., groupBackup.csv): ");
            String groupBackupFile = scanner.nextLine();
            try {
                databaseHelper.backUpGroupToFile(groupBackupFile, groupIdentifierBackup);
                System.out.println("Backup of group '" + groupIdentifierBackup + "' completed successfully.");
            } catch (Exception e) {
                System.out.println("Error during backup: " + e.getMessage());
            }
            System.out.println(" ----------------------- ");
            break;
    	case "3":
    		loggedOut = true;
            System.out.println("Going Back...");
            break;
    	default:
            System.out.println("Invalid choice. Please try again.");
            break;
	    }
		}
		articleManage();
	}
	
	private static void articleRestoreManage() throws SQLException {
		boolean loggedOut = false;
		
		while (!loggedOut) {
		System.out.println(" ---------------------------- ");
	    System.out.println("1. Restore with Deleting");
	    System.out.println("2. Restore without Deleting");
	    System.out.println("3. Go Back");
	    System.out.print("Choose an option: ");
	    String choice = scanner.nextLine();
	    
	    switch (choice) {
    	case "1":
    		System.out.print("Enter the filename to restore from: ");
            String restoreFile = scanner.nextLine();
            try {
                databaseHelper.restoreSystem(restoreFile);
                System.out.println("System restored successfully from " + restoreFile);
            } catch (Exception e) {
                System.out.println("Error during restoration: " + e.getMessage());
            }
            System.out.println(" ----------------------- ");
            break;
    	case "2":
    		System.out.print("Enter the filename to restore existing from: ");
            String restoreExistingFile = scanner.nextLine();
            try {
                databaseHelper.restoreSystemExisting(restoreExistingFile);
                System.out.println("Existing system restored successfully from " + restoreExistingFile);
            } catch (Exception e) {
                System.out.println("Error during existing system restoration: " + e.getMessage());
            }
            System.out.println(" ----------------------- ");
            break;
    	case "3":
    		loggedOut = true;
            System.out.println("Going Back...");
            break;
    	default:
            System.out.println("Invalid choice. Please try again.");
            break;
	    }
		}
		articleManage();
	}

	
	/**
	 * This method allows students to either register or log in to the system.
	 * If the user chooses to register, it collects necessary details (username, 
	 * email, password, and personal information) and checks if the user already 
	 * exists in the database. If the user exists, it informs the user; otherwise, 
	 * it registers the new student.
  	 *
	 * If the user chooses to log in, it verifies the provided credentials against 
	 * the database. Upon successful login, the student can log out at any time.
	 *
	 * @throws SQLException If an error occurs during database operations.
	 */
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
		    	databaseHelper.register(username, password, "student", email , fullName, prefName, false, expire, skillLevel);
		    	studentHome();
		    } else {
		        System.out.println("User already exists.");
		        mainMenu();
		    }
			break;
		case "2":
			System.out.print("Enter Student Username: ");
			username = scanner.nextLine();
			System.out.print("Enter Student Password: ");
			password = scanner.nextLine();
			if (databaseHelper.login(username, password, "student")) {
				System.out.println("User login successful.");
			    studentHome();
			} else {
				System.out.println("Invalid user credentials. Try again!!");
				mainMenu();
			}
			break;
		}
	}
	/**
	 * This method provides instructors with options to register or log in. If 
	 * the user opts to register, it collects necessary information (username, 
	 * email, password, and personal details) and checks for the existence of 
	 * the user in the database. If the instructor does not exist, it registers 
	 * them; otherwise, it notifies the user of their existing account. 
	 * 
	 * For login, it verifies the provided credentials and allows the instructor 
	 * to log out anytime.
	 * 
	 * @throws SQLException If there is an error during the database operations.
	 */
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
		        instructorHome();
		    } else {
		        System.out.println("Instructor already exists.");
		        mainMenu();
		    }
			break;
		case "2":
			System.out.print("Enter Instructor Username: ");
			username = scanner.nextLine();
			System.out.print("Enter Instructor Password: ");
			password = scanner.nextLine();
			if (databaseHelper.login(username, password, "instructor")) {
				System.out.println("Instructor login successful.");
				instructorHome();

			} else {
				System.out.println("Invalid user credentials. Try again!!");
				mainMenu();
			}
			break;
		}
	}

	/**
	 * Prompts the user to select a role for inviting a new user (either an instructor or a student). 
	 * The method displays a message instructing the user to enter 'I' for Instructor or 'S' for Student. 
	 * It continues to prompt the user until a valid choice is made. The input is case-sensitive.
	 *
	 * @return The role selected by the user, either "Student" or "Instructor".
	 */
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
	/**
	 * Handles the invitation flow for new users by verifying the provided invite code.
	 * If the invite code exists in the database, it retrieves the role associated with that code 
	 * and welcomes the user accordingly. Depending on the role, it directs the user to either 
	 * the student or instructor flow, allowing them to proceed with registration or login. 
	 * If the invite code is invalid, the method notifies the user and returns to the main menu.
	 * 
	 * @throws SQLException If an error occurs during database operations, such as checking 
	 *                      the invite code or retrieving user roles.
	 */
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
			
		}else {
			System.out.println("Invite Code Invalid.");
			System.out.println(" ----------------------- ");
			mainMenu();
		}
	}
	
	/**
	 * Manages the admin login process. The method prompts the user for the admin username 
	 * and password. It checks the entered credentials against the database. If the login is 
	 * successful, the user is directed to the admin home page where they can manage users 
	 * and other administrative tasks. If the credentials are invalid, the method notifies the user 
	 * and allows them to try again.
	 * 
	 * @throws SQLException If an error occurs during the login process or any database operations.
	 */
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
	
	/**
	 * Manages the reset password process where the user 
	 * has been given a one-time password, checks the date 
	 * against the expiration date and allows for creation
	 * of a new password for the user. 
	 * 
	 * @throws SQLException If an error occurs during the login process or any database operations.
	 */
	private static void resetFlow() throws SQLException {
		System.out.println("reset flow");
		String passwordFirst = null;
		String password = null;
    	boolean matched = false;
		System.out.println("Enter your User ID:");
		String username = scanner.nextLine();
		System.out.println("Enter your one-time reset password:");
		String tempPassword = scanner.nextLine();
		
		
		
		if(databaseHelper.isPasswordValid(username, tempPassword)) {
			
			if(databaseHelper.getDate(username).before(today)) {
				System.out.println("One-Time Password has expired! Please contact admin.");
				System.out.println(" -------------------------------------------------- ");
				mainMenu();
			}else {
				String role = databaseHelper.getRole(username);
				
				switch (role) {
	            case "Student":
	            	databaseHelper.oneTimePasswordUsed(username);
	            	while(!matched) {
	    				System.out.print("Enter New Student Password: ");
	    				passwordFirst = scanner.nextLine(); 
	    				System.out.print("Enter New Student Password Again: ");
	    				password = scanner.nextLine(); 
	    				if(password.compareTo(passwordFirst) != 0) {
	    					System.out.print("ERROR : Passwords must match.\n");
	    				}else {
	    					matched = true;
	    				}
	    			}
	            	databaseHelper.updatePassword(username, password);
	            	System.out.println("Password Reset!");
	            	System.out.println(" -------------- ");
	                studentHome();
	                break;
	            case "Instructor":
	            	databaseHelper.oneTimePasswordUsed(username);
	            	while(!matched) {
	    				System.out.print("Enter New Instructor Password: ");
	    				passwordFirst = scanner.nextLine(); 
	    				System.out.print("Enter New Instructor Password Again: ");
	    				password = scanner.nextLine(); 
	    				if(password.compareTo(passwordFirst) != 0) {
	    					System.out.print("ERROR : Passwords must match.\n");
	    				}else {
	    					matched = true;
	    				}
	    			}
	            	databaseHelper.updatePassword(username, password);
	            	System.out.println("Password Reset!");
	            	System.out.println(" -------------- ");
	                instructorHome();
	                break;
	            default: 
	            	System.out.println("Password not set");
	            	mainMenu();
				}
			}
		}else {
			System.out.println("One-time Password Invalid.");
			System.out.println(" ----------------------- ");
			mainMenu();
		}
	}

	/**
	 * Displays the admin home page, offering various management options including 
	 * viewing all users, inviting new users, resetting accounts, deleting accounts, 
	 * and changing user roles. The method keeps running until the admin chooses to log out. 
	 * It prompts the user for input and executes the selected option, providing feedback 
	 * based on their choices. If an invalid option is chosen, the method prompts the admin 
	 * to select again.
	 * 
	 * @throws SQLException If an error occurs during any of the database operations performed
	 *                      within the selected options.
	 */
	private static void adminHome() throws SQLException {
		boolean loggedOut = false;
	    System.out.println("Welcome to the Admin Home Page, ");
	    System.out.println(" ---------------------------- ");
	    while (!loggedOut) {
	        System.out.println("1. View All Users");
	        System.out.println("2. Invite User");
	        System.out.println("3. Reset an Account");
	        System.out.println("4. Delete an Account");
	        System.out.println("5. Change a User's Role");
	        System.out.println("6. Manage Articles");
	        System.out.println("7. Logout");
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
	            	System.out.print("Input username to be reset: ");
	                String resetName = scanner.nextLine();
	                if(databaseHelper.doesUserExist(resetName)) {
	                	System.out.print("Enter One-Time Password: ");
	                	String onetimepass = scanner.nextLine();
	                	System.out.print("Enter expiration date (YYYY-MM-DD): ");
	                	String expiration = scanner.nextLine();
	                	try {
	                		LocalDate expireLocal = LocalDate.parse(expiration);
	                		Date expireDate = Date.valueOf(expireLocal);
	                		databaseHelper.resetUserPassword(resetName, onetimepass, expireDate);
	                		System.out.println("One time password set!");
	                		System.out.println(" -------------------- ");
	                    }catch(DateTimeParseException e) {
	                        System.out.println(e);
	                        System.out.println("Invalid date format! Please follow format: YYYY-MM-DD");
	                        System.out.println(" --------------------------------------------------- ");
	                    } 
	                	
	                }else {
	                	System.out.println("User does not exist!");
	                	System.out.println(" ------------------ ");
	                }
	                break;
	            case "4":
	            	System.out.print("Enter Username for Account Deletion: ");
	                String deleteUser = scanner.nextLine();
	                if(databaseHelper.doesUserExist(deleteUser)) {
	                	databaseHelper.removeUser(deleteUser);
	                	System.out.println("User Removed.");
	                	System.out.println(" ------------------ ");
	                }else {
	                	System.out.println("User does not exist!");
	                	System.out.println(" ------------------ ");
	                }
	                break;
	            case "5":
	            	System.out.print("Enter user to be changed: ");
	                String userToChange = scanner.nextLine();
	                System.out.print("Enter S to change to Student, Enter I to change to Instructor: ");
	                String newRole = scanner.nextLine();
	                if(databaseHelper.doesUserExist(userToChange)) {
		                switch(newRole) {
		                	case "S":
		                		databaseHelper.changeRole(userToChange, "student");
		                		System.out.println(userToChange + "'s role was changed to student.");
		                		System.out.println(" -------------------------------------------- ");
		                	break;
		                	
		                	case "I": 
		                		databaseHelper.changeRole(userToChange, "instructor");
		                		System.out.println(userToChange + "'s role was changed to instructor.");
		                		System.out.println(" -------------------------------------------- ");
		                	break;
		                	
		                	default: 
		                		System.out.println("Invalid Input.");
		                		System.out.println(" ------------ ");
		                	break;
	                	}
	                }else {
	                	System.out.println("User does not exist.");
                		System.out.println(" ------------------ ");

	                }
	                break;
	            case "6":
	            	articleManage();
	            case "7":
	                loggedOut = true;
	                System.out.println("Logging out...");
	                break;
	        }
	    }
	    mainMenu();
	} 
	
	/**
	 * Displays the student home page, currenly offering to view courses, 
	 * grades, or to logout. Logout is currently the only functional 
	 * course of action. 
	 * 
	 * @throws SQLException If an error occurs during any of the database operations performed
	 *                      within the selected options.
	 */
	private static void studentHome() {
	    boolean loggedOut = false;
	    System.out.println("Welcome to the Student Home Page, ");

	    while (!loggedOut) {
	        System.out.println("Please choose an option:");
	        System.out.println("1. View Courses");
	        System.out.println("2. View Grades");
	        System.out.println("3. Search Article by Keyword");
	        System.out.println("4. Logout");

	        String choice = scanner.nextLine();

	        switch (choice) {
	            case "1":
	                System.out.println("Displaying courses...");
	                break;
	            case "2":
	                System.out.println("Displaying grades...");
	                break;
	            case "3":
	                System.out.print("Enter keyword to search: ");
	                String keyword = scanner.nextLine();
	                try {
	                    databaseHelper.studentSearchKeyword(keyword);
	                } catch (Exception e) {
	                    System.out.println("An error occurred while searching for articles: " + e.getMessage());
	                }
	                System.out.println(" ----------------------- ");
	                break;
	            case "4":
	                loggedOut = true;
	                System.out.println("Logging out...");
	                break;
	            default:
	                System.out.println("Invalid choice. Please try again.");
	                break;
	        }
	    }
	  mainMenu();
	}

	
	/**
	 * Displays the instructor home page, currenly offering to view students, 
	 * manage courses, or to logout. Logout is currently the only functional 
	 * course of action. 
	 * 
	 * @throws SQLException If an error occurs during any of the database operations performed
	 *                      within the selected options.
	 */
	private static void instructorHome() throws SQLException{
	    boolean loggedOut = false;
	    System.out.println("Welcome to the Instructor Home Page, ");

	    while (!loggedOut) {
	        System.out.println("Please choose an option:");
	        System.out.println("1. View Students");
	        System.out.println("2. Manage Courses");
	        System.out.println("3. Manage Articles");
	        System.out.println("4. Logout");

	        String choice = scanner.nextLine();

	        switch (choice) {
	            case "1":
	                System.out.println("Displaying students...");
	                break;
	            case "2":
	                System.out.println("Managing courses...");
	                break;
	            case "3":
	            	articleManage();
	            case "4":
	                loggedOut = true; 
	                System.out.println("Logging out...");
	                break;
	            default:
	                System.out.println("Invalid choice. Please try again.");
	                break;
	        }
	    }
	   mainMenu();
	}



}
