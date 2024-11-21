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
 * @version 3.0
 * @date November 16, 2024
 */

package simpleDatabase;


import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Pair;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.sql.Date;

/**
 * The StartCSE360 class represents the main entry point for the CSE 360 application.
 * It handles user logins and provides a graphical user interface for Admin, Instructor, and Student functionalities.
 */
public class StartCSE360 extends Application {
    private DatabaseHelper databaseHelper;
    private SpecialAccessGroups accessGroups;

    /**
     * Constructor to initialize the DatabaseHelper.
     * @throws Exception 
     */
    public StartCSE360() throws Exception {
        this.databaseHelper = new DatabaseHelper();
        this.accessGroups = new SpecialAccessGroups();// Assume DatabaseHelper is correctly initialized
    }
    
	/** ------------ Start Method for GUI  ------------ */

    /**
     * The start method is the main entry point for JavaFX applications.
     *
     * @param primaryStage the primary stage for this application
     * @throws SQLException 
     */
    @Override
    public void start(Stage primaryStage) throws SQLException {
    	databaseHelper.connectToDatabase();
    	accessGroups.connectToDatabase();
    	
        primaryStage.setTitle("CSE 360 - Welcome Page");

        /** Layout */
        VBox layout = new VBox(10);
        layout.setPadding(new javafx.geometry.Insets(20));

        /** Main Menu Buttons */
        Button adminButton = new Button("Admin Login");
        Button instructorButton = new Button("Instructor Login");
        Button studentButton = new Button("Student Login");
        Button adminRegisterButton = new Button("Admin Register");
        Button studentRegisterButton = new Button("Student Register");
        Button instructorRegisterButton = new Button("Instructor Register");

        /** Set button actions */
        adminButton.setOnAction(e -> {
			try {
				adminLogin();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});
        instructorButton.setOnAction(e -> instructorLogin());
        studentButton.setOnAction(e -> studentLogin());
        adminRegisterButton.setOnAction(e -> adminRegister());
        studentRegisterButton.setOnAction(e -> studentRegister());
        instructorRegisterButton.setOnAction(e -> instructorRegister());

        // Add buttons to the layout
        layout.getChildren().addAll(adminButton, instructorButton, studentButton, adminRegisterButton, studentRegisterButton, instructorRegisterButton);

        // Create the scene
        Scene scene = new Scene(layout, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
        if(databaseHelper.isDatabaseEmpty()) {
        	adminRegister();
        }
    }
    
	/** ------------ Admin Methods  ------------ */

    /**
     * Displays the admin login page.
     * @throws SQLException 
     */
    private void adminLogin() throws SQLException {
        Stage loginStage = new Stage();
        loginStage.setTitle("Admin Login Page");

        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new javafx.geometry.Insets(20));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        Button backButton = new Button("Back to Main Menu");

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            /** Validate credentials (implement login logic) */
            try {
				if (databaseHelper.login(username, password, "admin")) {
				    adminHome();
				    loginStage.close();
				} else {
				    showAlert("Error", "Invalid credentials!");
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
        });

        backButton.setOnAction(e -> {
            loginStage.close();
            /** Redirect to main menu or another action */
        });

        loginLayout.getChildren().addAll(usernameField, passwordField, loginButton, backButton);

        Scene loginScene = new Scene(loginLayout, 300, 200);
        loginStage.setScene(loginScene);
        loginStage.show();
        
        if(databaseHelper.isDatabaseEmpty()) {
        	adminRegister();
        }
    }

    /**
     * Handles admin registration functionality.
     * @throws SQLException 
     */
    private void adminRegister() {
        Stage registerStage = new Stage();
        registerStage.setTitle("Admin Registration Page");

        VBox registerLayout = new VBox(10);
        registerLayout.setPadding(new javafx.geometry.Insets(20));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        
        PasswordField passwordField2 = new PasswordField();
        passwordField2.setPromptText("Password Again");

        Button registerButton = new Button("Register");
        Button backButton = new Button("Back to Main Menu");

        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String password2 = passwordField2.getText();
            String email = null;
    		String fullName = null;
    		String prefName = null;
    		Date expire = null;
    		String skillLevel = "Intermediate";
    		if(password.compareTo(password2) == 0) {
    			/** Validate and register admin (implement registration logic) */
                try {
					if (databaseHelper.register(username, password, "admin", email, fullName, prefName, false, expire, skillLevel)) {
					    showAlert("Success", "Admin registered successfully!");
					    registerStage.close();
					} else {
					    showAlert("Error", "Registration failed!");
					}
				} catch (SQLException e1) {
					
					e1.printStackTrace();
				}
    		}else {
    			showAlert("Error", "Passwords must match");
    		}
            
        });

        backButton.setOnAction(e -> {
            registerStage.close();
            /** Redirect to main menu or another action */
        });

        registerLayout.getChildren().addAll(usernameField, passwordField, passwordField2, registerButton, backButton);

        Scene registerScene = new Scene(registerLayout, 300, 200);
        registerStage.setScene(registerScene);
        registerStage.show();
    }
    
    
	/** ------------ Login and Register  ------------ */

    /**
     * Handles the student login functionality.
     *
     */
    private void studentLogin() {
        Stage studentStage = new Stage();
        studentStage.setTitle("Student Login Page");

        VBox studentLayout = new VBox(10);
        studentLayout.setPadding(new javafx.geometry.Insets(20));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        Button backButton = new Button("Back to Main Menu");

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            /** Validate credentials (implement login logic) */
            try {
				if (databaseHelper.login(username, password, "student")) {
				    studentHome();
				    studentStage.close();
				} else {
				    showAlert("Error", "Invalid credentials!");
				}
			} catch (SQLException e1) {
				
				e1.printStackTrace();
			}
        });

        backButton.setOnAction(e -> {
            studentStage.close();
            /** Redirect to main menu or another action */
        });

        studentLayout.getChildren().addAll(usernameField, passwordField, loginButton, backButton);

        Scene studentScene = new Scene(studentLayout, 300, 200);
        studentStage.setScene(studentScene);
        studentStage.show();
    }

    /**
     * Handles student registration functionality.
     *
     */
    private void studentRegister() {
        Stage studentStage = new Stage();
        studentStage.setTitle("Student Registration Page");

        VBox studentLayout = new VBox(10);
        studentLayout.setPadding(new javafx.geometry.Insets(20));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        
        PasswordField passwordField2 = new PasswordField();
        passwordField2.setPromptText("Password Again");
        
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        
        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Full Name");
        
        TextField prefNameField = new TextField();
        prefNameField.setPromptText("Preferred Name (Leave empty if N/A)");

        Button registerButton = new Button("Register");
        Button backButton = new Button("Back to Main Menu");

        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String password2 = passwordField2.getText();
            String email = emailField.getText();
            String fullName = fullNameField.getText();
            String prefName = prefNameField.getText();
            Date expire = null;
            String skillLevel = "Intermediate";
            if(prefName.compareTo("") == 0) {
            	prefName = "N/A";
            }
            if(password.compareTo(password2) == 0) {
            	/** Validate credentials (implement login logic) */
                try {
					if (databaseHelper.register(username, password, "student", email, 
							fullName, prefName, false, expire, skillLevel)) {
					    studentHome();
					    studentStage.close();
					} else {
					    showAlert("Error", "Invalid credentials!");
					}
				} catch (SQLException e1) {

					e1.printStackTrace();
				}
            }else {
            	showAlert("Error", "Passwords must match");
            }
        });

        backButton.setOnAction(e -> {
            studentStage.close();
            /** Redirect to main menu or another action */
        });

        studentLayout.getChildren().addAll(usernameField, passwordField, passwordField2, 
        		emailField,fullNameField, prefNameField, registerButton, backButton);

        Scene studentScene = new Scene(studentLayout, 300, 300);
        studentStage.setScene(studentScene);
        studentStage.show();
    }

    /**
     * Handles instructor login functionality.
     */
    private void instructorRegister() {
        Stage instructorStage = new Stage();
        instructorStage.setTitle("Instructor Registration Page");

        VBox instructorLayout = new VBox(10);
        instructorLayout.setPadding(new javafx.geometry.Insets(20));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        
        PasswordField passwordField2 = new PasswordField();
        passwordField2.setPromptText("Password Again");
        
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        
        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Full Name");
        
        TextField prefNameField = new TextField();
        prefNameField.setPromptText("Preferred Name (Leave empty if N/A)");

        Button registerButton = new Button("Register");
        Button backButton = new Button("Back to Main Menu");

        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String password2 = passwordField2.getText();
            String email = emailField.getText();
            String fullName = fullNameField.getText();
            String prefName = prefNameField.getText();
            Date expire = null;
            String skillLevel = "Intermediate";
            if(prefName.compareTo("") == 0) {
            	prefName = "N/A";
            }
            if(password.compareTo(password2) == 0) {
            	/** Validate credentials (implement register logic) */
                try {
					if (databaseHelper.register(username, password, "instructor", email, 
							fullName, prefName, false, expire, skillLevel)) {
					    instructorHome();
					    instructorStage.close();
					} else {
					    showAlert("Error", "Invalid credentials!");
					}
				} catch (SQLException e1) {
				
					e1.printStackTrace();
				}
            }else {
            	showAlert("Error", "Passwords must match");
            }
        });

        backButton.setOnAction(e -> {
            instructorStage.close();
            /** Redirect to main menu or another action */
        });

        instructorLayout.getChildren().addAll(usernameField, passwordField, passwordField2, 
        		emailField,fullNameField, prefNameField, registerButton, backButton);

        Scene instructorScene = new Scene(instructorLayout, 300, 300);
        instructorStage.setScene(instructorScene);
        instructorStage.show();
    }

    /**
     * Handles instructor registration functionality.
     */
    private void instructorLogin() {
        Stage instructorStage = new Stage();
        instructorStage.setTitle("Instructor Login Page");

        VBox instructorLayout = new VBox(10);
        instructorLayout.setPadding(new javafx.geometry.Insets(20));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        Button backButton = new Button("Back to Main Menu");

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            /** Validate credentials (implement login logic) */
            try {
				if (databaseHelper.login(username, password, "instructor")) {
				    instructorHome();
				    instructorStage.close();
				} else {
				    showAlert("Error", "Invalid credentials!");
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
        });

        backButton.setOnAction(e -> {
            instructorStage.close();
            /** Redirect to main menu or another action */
        });

        instructorLayout.getChildren().addAll(usernameField, passwordField, loginButton, backButton);

        Scene instructorScene = new Scene(instructorLayout, 300, 200);
        instructorStage.setScene(instructorScene);
        instructorStage.show();
    }
    
    
	/** ------------ Home Pages  ------------ */
    
    
    /**
     * Displays the admin home page with various functionalities.
     * @throws SQLException 
     */
    private void adminHome() throws SQLException {
        Stage adminStage = new Stage();
        adminStage.setTitle("Admin Home Page");

        /** Layout for Admin Home */
        VBox adminLayout = new VBox(10);
        adminLayout.setPadding(new javafx.geometry.Insets(20));

        /** Buttons for admin functionalities */
        Button viewUsersButton = new Button("View All Users");
        Button inviteUserButton = new Button("Invite User");
        Button resetPasswordButton = new Button("Reset an Account");
        Button deleteUserButton = new Button("Delete an Account");
        Button changeUserRoleButton = new Button("Change a User's Role");
        Button manageArticlesButton = new Button("Manage Articles");
        Button createSpecialGroupButton = new Button("Create New Special Group");
        Button logoutButton = new Button("Logout");

        
      
        
        
        /** Set button actions */
        viewUsersButton.setOnAction(e -> {
            System.out.println("Displaying all users...");
            try {
				databaseHelper.displayUsersByAdmin();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
        });

        inviteUserButton.setOnAction(e -> {
            /** Create a new stage for role selection */
            Stage inviteStage = new Stage();
            inviteStage.setTitle("Invite User");

            /** Radio buttons for role selection */
            RadioButton studentRadio = new RadioButton("Student");
            RadioButton instructorRadio = new RadioButton("Instructor");
            ToggleGroup roleGroup = new ToggleGroup();
            studentRadio.setToggleGroup(roleGroup);
            instructorRadio.setToggleGroup(roleGroup);

            /** Button to confirm the role selection */
            Button confirmButton = new Button("Confirm");

            /** Layout for the invite dialog */
            VBox inviteLayout = new VBox(10, new Label("Select a Role:"), studentRadio, instructorRadio, confirmButton);
            inviteLayout.setPadding(new javafx.geometry.Insets(20));
            inviteLayout.setAlignment(Pos.CENTER);

            /** Set confirm button action */
            confirmButton.setOnAction(ev -> {
                RadioButton selectedRole = (RadioButton) roleGroup.getSelectedToggle();
                if (selectedRole != null) {
                    String role = selectedRole.getText().toLowerCase();
                    TextInputDialog codeDialog = new TextInputDialog();
                    codeDialog.setTitle("User Code");
                    codeDialog.setHeaderText("Enter the Code for the Invited User:");
                    codeDialog.showAndWait().ifPresent(inviteCode -> {
                        try {
							databaseHelper.inviteUser(inviteCode, role);
						} catch (SQLException e1) {

							e1.printStackTrace();
						}
                        System.out.println("User invited with role: " + role + " and code: " + inviteCode);
                    });
                    inviteStage.close();
                } else {
                    System.out.println("Please select a role.");
                }
            });

            /** Set up the stage and show */
            Scene inviteScene = new Scene(inviteLayout, 300, 200);
            inviteStage.setScene(inviteScene);
            inviteStage.show();
        });

        resetPasswordButton.setOnAction(e -> {
            TextInputDialog userDialog = new TextInputDialog();
            userDialog.setTitle("Reset Account");
            userDialog.setHeaderText("Input username to be reset:");
            userDialog.showAndWait().ifPresent(resetName -> {
                if (databaseHelper.doesUserExist(resetName)) {
                    TextInputDialog otpDialog = new TextInputDialog();
                    otpDialog.setTitle("Set One-Time Password");
                    otpDialog.setHeaderText("Enter One-Time Password:");
                    otpDialog.showAndWait().ifPresent(onetimepass -> {
                        TextInputDialog dateDialog = new TextInputDialog();
                        dateDialog.setTitle("Set Expiration Date");
                        dateDialog.setHeaderText("Enter expiration date (YYYY-MM-DD):");
                        dateDialog.showAndWait().ifPresent(expiration -> {
                            try {
                                LocalDate expireLocal = LocalDate.parse(expiration);
                                Date expireDate = Date.valueOf(expireLocal);
                                try {
									databaseHelper.resetUserPassword(resetName, onetimepass, expireDate);
								} catch (SQLException e1) {

									e1.printStackTrace();
								}
                                System.out.println("One-time password set!");
                            } catch (DateTimeParseException ex) {
                                System.out.println("Invalid date format! Please follow format: YYYY-MM-DD");
                            }
                        });
                    });
                } else {
                    System.out.println("User does not exist!");
                }
            });
        });

        deleteUserButton.setOnAction(e -> {
            TextInputDialog deleteDialog = new TextInputDialog();
            deleteDialog.setTitle("Delete Account");
            deleteDialog.setHeaderText("Enter Username for Account Deletion:");
            deleteDialog.showAndWait().ifPresent(deleteUser -> {
                if (databaseHelper.doesUserExist(deleteUser)) {
                    try {
						databaseHelper.removeUser(deleteUser);
					} catch (SQLException e1) {

						e1.printStackTrace();
					}
                    System.out.println("User Removed.");
                } else {
                    System.out.println("User does not exist!");
                }
            });
        });

        changeUserRoleButton.setOnAction(e -> {
            TextInputDialog userDialog = new TextInputDialog();
            userDialog.setTitle("Change Role");
            userDialog.setHeaderText("Enter user to be changed:");
            userDialog.showAndWait().ifPresent(userToChange -> {
                if (databaseHelper.doesUserExist(userToChange)) {
                    TextInputDialog roleDialog = new TextInputDialog();
                    roleDialog.setTitle("Change Role");
                    roleDialog.setHeaderText("Enter S to change to Student, Enter I to change to Instructor:");
                    roleDialog.showAndWait().ifPresent(newRole -> {
                        switch (newRole.toUpperCase()) {
                            case "S":
							try {
								databaseHelper.changeRole(userToChange, "student");
							} catch (SQLException e1) {

								e1.printStackTrace();
							}
                                System.out.println(userToChange + "'s role was changed to student.");
                                break;
                            case "I":
							try {
								databaseHelper.changeRole(userToChange, "instructor");
							} catch (SQLException e1) {

								e1.printStackTrace();
							}
                                System.out.println(userToChange + "'s role was changed to instructor.");
                                break;
                            default:
                                System.out.println("Invalid Input.");
                                break;
                        }
                    });
                } else {
                    System.out.println("User does not exist.");
                }
            });
        });

        manageArticlesButton.setOnAction(e -> {
            try {
				articleManage();
			} catch (SQLException e1) {

				e1.printStackTrace();
			}
        });
        
        
       
       
        createSpecialGroupButton.setOnAction(e -> {
        	Stage articleCreateStage = new Stage();
        	articleCreateStage.setTitle("Create Group");

        	TextField titleField = new TextField();
        	titleField.setPromptText("Enter Group Name");

        	TextField mainInstructorField = new TextField();
        	mainInstructorField.setPromptText("Enter Lead Instructor Name");

        	TextField mainInstructorUsernameField = new TextField();
        	mainInstructorUsernameField.setPromptText("Enter Instructor Username");
        	

        	
        	Button createGroupButton = new Button("Create Group");

        	createGroupButton.setOnAction(event -> {

        	    String groupIdentifier = titleField.getText();
        	    String leadInstructorName = mainInstructorField.getText();
        	    String leadInstructorUsername = mainInstructorUsernameField.getText();
        	    

        	    
        	    if (groupIdentifier.isEmpty() || leadInstructorName.isEmpty() || leadInstructorUsername.isEmpty()) {
        	        System.out.println("Please fill in all required fields.");
        	        return;
        	    }

        	    try {

        	        accessGroups.addFirstInstructor(leadInstructorUsername, groupIdentifier);
        	        System.out.println("Group created successfully!");
        	        System.out.println(accessGroups.listInstructorsAdmin());

        	        articleCreateStage.close();
        	    } catch (Exception ex) {
        	        System.out.println("Error creating group: " + ex.getMessage());
        	        ex.printStackTrace();
        	    }
        	});

        	VBox formLayout = new VBox(10);
        	formLayout.setPadding(new javafx.geometry.Insets(20));

        	formLayout.getChildren().addAll(titleField, mainInstructorField, mainInstructorUsernameField,createGroupButton );

        	Scene articleCreateScene = new Scene(formLayout, 400, 500);
        	articleCreateStage.setScene(articleCreateScene);
        	articleCreateStage.show(); 
        }); 
        
    

        logoutButton.setOnAction(e -> {
            adminStage.close();
            System.out.println("Logging out...");
        });

        /** Add buttons to the admin layout */
        adminLayout.getChildren().addAll(
            viewUsersButton, inviteUserButton, resetPasswordButton, 
            deleteUserButton, changeUserRoleButton, manageArticlesButton, createSpecialGroupButton, 
            logoutButton
        );

        /** Create the scene */
        Scene adminScene = new Scene(adminLayout, 400, 400);
        adminStage.setScene(adminScene);
        adminStage.show();
    }    
    
    
    /**
     * Displays the student home page with specific functionalities.
     */
    private void studentHome() {
        Stage studentHomeStage = new Stage();
        studentHomeStage.setTitle("Student Home Page");

        VBox studentLayout = new VBox(10);
        studentLayout.setPadding(new javafx.geometry.Insets(20));

        /** Add student functionalities here */
        Button viewGradesButton = new Button("View Grades");
        Button enrollButton = new Button("Enroll in Course");
        Button getHelpButton = new Button("Get Help");
        Button logoutButton = new Button("Logout");
        
        
        viewGradesButton.setOnAction(e -> showAlert("Info", "Displaying grades..."));
        enrollButton.setOnAction(e -> showAlert("Info", "Enrolling in course..."));
        getHelpButton.setOnAction(e -> {
        	try {
				helpManager();
			} catch (SQLException e1) {
				
				e1.printStackTrace();
			}
        });
        logoutButton.setOnAction(e -> {
            studentHomeStage.close();
            /** Redirect to main menu or login page if needed */
        });

        studentLayout.getChildren().addAll(viewGradesButton, enrollButton, getHelpButton,  logoutButton);

        Scene studentHomeScene = new Scene(studentLayout, 300, 300);
        studentHomeStage.setScene(studentHomeScene);
        studentHomeStage.show();
    }

    /**
     * Displays the instructor home page with specific functionalities.
     */
    private void instructorHome(){
        Stage instructorHomeStage = new Stage();
        instructorHomeStage.setTitle("Instructor Home Page");

        VBox instructorLayout = new VBox(10);
        instructorLayout.setPadding(new javafx.geometry.Insets(20));

        /** Add instructor functionalities here */
        Button viewClassesButton = new Button("View Classes");
        Button manageAssignmentsButton = new Button("Manage Assignments");
        Button manageArticlesButton = new Button("Manage Articles");
        Button createGenericHelpArticleButton = new Button("Create General Help Article");
        Button viewSpecificHelpButton = new Button("View Specific Help Requests");
        Button createSpecialArticleButton = new Button("Create New Special Article");
        Button deleteSpecialArticleButton = new Button("Delete Special Article");
        Button giveAdminRightsButton = new Button("Give Admin Rights");
        Button addPeopleButton = new Button("Add People");
        Button listPeopleButton = new Button("List People");
        Button logoutButton = new Button("Logout");

        
        
        addPeopleButton.setOnAction(e -> showAddPeopleDialog(instructorHomeStage));
        listPeopleButton.setOnAction(e -> showListPeopleDialog(instructorHomeStage));
        

        viewClassesButton.setOnAction(e -> showAlert("Info", "Displaying classes..."));
        manageAssignmentsButton.setOnAction(e -> showAlert("Info", "Managing assignments..."));
        manageArticlesButton.setOnAction(e -> {
            try {
				articleManage();
			} catch (SQLException e1) {

				e1.printStackTrace();
			}
        });
        
        createGenericHelpArticleButton.setOnAction(e ->{
        	createGenericHelpArticle();
        });
        
        viewSpecificHelpButton.setOnAction(e ->{
        	Alert queryAlert = new Alert(Alert.AlertType.INFORMATION);
        	queryAlert.setTitle("Queries");
        	queryAlert.setHeaderText("Specific Help");
        	try {
				queryAlert.setContentText(databaseHelper.displayUnansweredStudentQueries());
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
        	queryAlert.showAndWait();
        	
        });
        
        deleteSpecialArticleButton.setOnAction(event -> {

            TextInputDialog inputDialog = new TextInputDialog();
            inputDialog.setTitle("Delete Special Article");
            inputDialog.setHeaderText("Enter the ID of the article you want to delete:");
            inputDialog.setContentText("Article ID:");

            Optional<String> result = inputDialog.showAndWait();
            result.ifPresent(articleIdStr -> {
                try {
                    int articleId = Integer.parseInt(articleIdStr);
                    accessGroups.deleteSpecialArticle(articleId); // Call deleteSpecialArticle method
                    System.out.println("Article with ID " + articleId + " deleted successfully.");
                } catch (NumberFormatException ex) {
                    System.out.println("Invalid ID format. Please enter a valid number.");
                } catch (Exception ex) {
                    System.out.println("Error deleting article: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });
        });
       
        createSpecialArticleButton.setOnAction(e -> {
        	Stage articleCreateStage = new Stage();
        	articleCreateStage.setTitle("Create Article");

        	TextField titleField = new TextField();
        	titleField.setPromptText("Enter Article Title");

        	TextField authorField = new TextField();
        	authorField.setPromptText("Enter Author Name");

        	TextArea descriptionArea = new TextArea();
        	descriptionArea.setPromptText("Enter Article Description");
        	descriptionArea.setWrapText(true);

        	TextArea bodyArea = new TextArea();
        	bodyArea.setPromptText("Enter Article Body");
        	bodyArea.setWrapText(true);

        	ChoiceBox<String> levelChoiceBox = new ChoiceBox<>();
        	levelChoiceBox.getItems().addAll("Beginner", "Intermediate", "Advanced", "Expert");
        	levelChoiceBox.setValue("Beginner");

        	TextField groupField = new TextField();
        	groupField.setPromptText("Enter Group Identifier");

        	TextField keywordsField = new TextField();
        	keywordsField.setPromptText("Enter Article Keywords");


        	TextArea otherDetailsArea = new TextArea();
        	otherDetailsArea.setPromptText("Enter Other Details");
        	otherDetailsArea.setWrapText(true);

        	TextField linksField = new TextField();
        	linksField.setPromptText("Enter Links");

        	Button saveArticleButton = new Button("Save Article");

        	saveArticleButton.setOnAction(event -> {

        	    String title = titleField.getText();
        	    String author = authorField.getText();
        	    String description = descriptionArea.getText();
        	    String body = bodyArea.getText();
        	    String level = levelChoiceBox.getValue();
        	    String groupIdentifier = groupField.getText();
        	    String keywords = keywordsField.getText();
        	    String other = otherDetailsArea.getText();
        	    String links = linksField.getText();

        	    
        	    if (title.isEmpty() || author.isEmpty() || description.isEmpty() || body.isEmpty() || groupIdentifier.isEmpty()) {
        	        System.out.println("Please fill in all required fields.");
        	        return;
        	    }

        	    try {

        	        accessGroups.addSpecialArticle(title, author ,description, body, level, groupIdentifier, keywords, other, links);
        	        System.out.println("Article added successfully!");

        	        articleCreateStage.close();
        	    } catch (Exception ex) {
        	        System.out.println("Error adding article: " + ex.getMessage());
        	        ex.printStackTrace();
        	    }
        	});

        	VBox formLayout = new VBox(10);
        	formLayout.setPadding(new javafx.geometry.Insets(20));

        	formLayout.getChildren().addAll(titleField, authorField, descriptionArea, bodyArea, levelChoiceBox, 
        	    groupField, keywordsField, otherDetailsArea, linksField, saveArticleButton);

        	Scene articleCreateScene = new Scene(formLayout, 400, 600);
        	articleCreateStage.setScene(articleCreateScene);
        	articleCreateStage.show(); 
        }); 
        
        
        giveAdminRightsButton.setOnAction(e -> {
            
            Stage adminRightsStage = new Stage();
            adminRightsStage.setTitle("Give Admin Rights");

            
            TextField usernameField = new TextField();
            usernameField.setPromptText("Enter Username");

            TextField groupNameField = new TextField();
            groupNameField.setPromptText("Enter Group Name");

            
            Button submitButton = new Button("Submit");

            submitButton.setOnAction(event -> {
                String username = usernameField.getText();
                String groupName = groupNameField.getText();

                
                if (username.isEmpty() || groupName.isEmpty()) {
                    System.out.println("Please enter both username and group name.");
                    return;
                }

                try {
					if (!SpecialAccessGroups.adminRights(username, groupName)) {
					    // Grant admin rights
					    String result = accessGroups.giveAdminAccess(username, groupName);
					    System.out.println(result);

					    // Confirmation stage to show success message
					    Label confirmationLabel = new Label("Admin rights have been granted successfully!");
					    VBox confirmationVBox = new VBox(10, confirmationLabel);
					    confirmationVBox.setPadding(new javafx.geometry.Insets(20));

					    Scene confirmationScene = new Scene(confirmationVBox, 300, 100);
					    Stage confirmationStage = new Stage();
					    confirmationStage.setScene(confirmationScene);
					    confirmationStage.setTitle("Confirmation");
					    confirmationStage.show();

					    // Close the adminRightsStage after successful operation
					    if (adminRightsStage != null) {
					        adminRightsStage.close(); // Close only if it's not null
					    }
					} else {
					    System.out.println("User already has admin rights.");
					    if (adminRightsStage != null) {
					        adminRightsStage.close(); // Close if already has admin rights
					    }
					}
				} catch (Exception e1) {
					
					e1.printStackTrace();
				} 
            });
            // Layout the components in the new stage
            VBox vbox = new VBox(10);
            vbox.getChildren().addAll(usernameField, groupNameField, submitButton);
            vbox.setPadding(new javafx.geometry.Insets(20));

            // Create and set the scene for the new stage
            Scene adminRightsScene = new Scene(vbox, 300, 200);
            adminRightsStage.setScene(adminRightsScene);
            adminRightsStage.show(); // Show the new window
        });
        
        logoutButton.setOnAction(e -> {
            instructorHomeStage.close();
            /** Redirect to main menu or login page if needed */
        });

        instructorLayout.getChildren().addAll(viewClassesButton, manageAssignmentsButton, 
        		manageArticlesButton, viewSpecificHelpButton, createGenericHelpArticleButton, createSpecialArticleButton, deleteSpecialArticleButton, 
        		giveAdminRightsButton, addPeopleButton, listPeopleButton,  logoutButton);

        Scene instructorHomeScene = new Scene(instructorLayout, 300, 350);
        instructorHomeStage.setScene(instructorHomeScene);
        instructorHomeStage.show();
    }
    
    
    private void createGenericHelpArticle() {
    	Stage helpCreateStage = new Stage();
    	helpCreateStage.setTitle("Create Help Article");

        /** Form Fields */
        TextField titleField = new TextField();
        titleField.setPromptText("Enter Article Title");
        
        TextField authorField = new TextField();
        authorField.setPromptText("Enter Author Name");

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Enter Article Description");
        descriptionArea.setWrapText(true);

        TextArea bodyArea = new TextArea();
        bodyArea.setPromptText("Enter Article Body");
        bodyArea.setWrapText(true);

        TextField keywordsField = new TextField();
        keywordsField.setPromptText("Enter Article Keywords");

        TextArea otherDetailsArea = new TextArea();
        otherDetailsArea.setPromptText("Enter Other Details");
        otherDetailsArea.setWrapText(true);

        TextField linksField = new TextField();
        linksField.setPromptText("Enter Links");

        Button submitButton = new Button("Submit");
        Button cancelButton = new Button("Cancel");
        
        /** Form Layout */
        GridPane formLayout = new GridPane();
        formLayout.setPadding(new javafx.geometry.Insets(20));
        formLayout.setHgap(10);
        formLayout.setVgap(10);

        formLayout.add(new Label("Title:"), 0, 0);
        formLayout.add(titleField, 1, 0);
        formLayout.add(new Label("Author:"), 0, 1);
        formLayout.add(authorField, 1, 1);
        formLayout.add(new Label("Description:"), 0, 2);
        formLayout.add(descriptionArea, 1, 2);
        formLayout.add(new Label("Body:"), 0, 3);
        formLayout.add(bodyArea, 1, 3);
        formLayout.add(new Label("Keywords:"), 0, 4);
        formLayout.add(keywordsField, 1, 4);
        formLayout.add(new Label("Other:"), 0, 5);
        formLayout.add(otherDetailsArea, 1, 5);
        formLayout.add(new Label("Links:"), 0, 6);
        formLayout.add(linksField, 1, 6);

        HBox buttonLayout = new HBox(10, submitButton, cancelButton);
        buttonLayout.setAlignment(Pos.CENTER);

        VBox mainLayout = new VBox(10, formLayout, buttonLayout);
        mainLayout.setPadding(new javafx.geometry.Insets(20));

        /** Submit Button Action */
        submitButton.setOnAction(e -> {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String description = descriptionArea.getText().trim();
            String body = bodyArea.getText().trim();
            String keywords = keywordsField.getText().trim();
            String otherDetails = otherDetailsArea.getText().trim();
            String links = linksField.getText().trim();

            if (title.isEmpty() || description.isEmpty() || body.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation Error");
                alert.setHeaderText("Missing Required Fields");
                alert.setContentText("Please ensure Title, Description, and Body are filled.");
                alert.showAndWait();
                return;
            }

            try {
                databaseHelper.createGenericMessageArticles(title, author, description, body, keywords, otherDetails, links);
                System.out.println("Article Created Successfully!");
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText("Article Created");
                successAlert.setContentText("The article has been created successfully.");
                successAlert.showAndWait();
                helpCreateStage.close();
                articleManage(); // Return to the article management interface
            } catch (SQLException ex) {
                System.out.println("Error creating article: " + ex.getMessage());
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Database Error");
                errorAlert.setHeaderText("Failed to Create Article");
                errorAlert.setContentText("Error: " + ex.getMessage());
                errorAlert.showAndWait();
            }
        });

        /** Cancel Button Action */
        cancelButton.setOnAction(e -> helpCreateStage.close());

        /** Set the Scene and Show */
        Scene helpCreateScene = new Scene(mainLayout, 600, 600);
        helpCreateStage.setScene(helpCreateScene);
        helpCreateStage.show();
    }
    
    
    /** ------------ Article Managers  ------------ */
    
    /**
	 * Article Management System, acts as main menu for managing articles as an admin
	 * or instructor. 
	 * 
	 * @throws SQLException
	 */
    private void articleManage() throws SQLException{
        Stage articleStage = new Stage();
        articleStage.setTitle("Article Management System");

        /** Layout for Article Management */
        VBox articleLayout = new VBox(10);
        articleLayout.setPadding(new javafx.geometry.Insets(20));

        /** Buttons for article management functionalities */
        Button createArticleButton = new Button("Create an Article");
        Button displayArticleButton = new Button("Display an Article");
        Button deleteArticleButton = new Button("Delete an Article");
        Button backupArticleButton = new Button("Backup an Article");
        Button restoreArticleButton = new Button("Restore an Article");
        Button searchArticleButton = new Button("Search an Article by Keywords");
        Button backToMenuButton = new Button("Go Back to Main Menu");

        /** Set button actions */
        createArticleButton.setOnAction(e -> {
			try {
				articleCreateManage();
			} catch (SQLException e1) {

				e1.printStackTrace();
			}
		});
        displayArticleButton.setOnAction(e -> {
			try {
				articleDisplayManage();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});
        deleteArticleButton.setOnAction(e -> {
			try {
				articleDeleteManage();
			} catch (SQLException e1) {

				e1.printStackTrace();
			}
		});
        backupArticleButton.setOnAction(e -> {
			try {
				articleBackUpManage();
			} catch (SQLException e1) {

				e1.printStackTrace();
			}
		});
        restoreArticleButton.setOnAction(e -> {
			try {
				articleRestoreManage();
			} catch (SQLException e1) {

				e1.printStackTrace();
			}
		});
        searchArticleButton.setOnAction(e -> {
            // Prompt for the username
            TextInputDialog usernameDialog = new TextInputDialog();
            usernameDialog.setTitle("Search Article");
            usernameDialog.setHeaderText("Enter your Username:");
            usernameDialog.setContentText("Username:");

            usernameDialog.showAndWait().ifPresent(username -> {
                // Prompt for the user level
                TextInputDialog levelDialog = new TextInputDialog();
                levelDialog.setTitle("Search Article");
                levelDialog.setHeaderText("Enter your Level:");
                levelDialog.setContentText("Level (e.g., admin, user, instructor):");

                levelDialog.showAndWait().ifPresent(level -> {
                    // Prompt for the group identifier
                    TextInputDialog groupDialog = new TextInputDialog();
                    groupDialog.setTitle("Search Article");
                    groupDialog.setHeaderText("Enter the Group Identifier:");
                    groupDialog.setContentText("Group Identifier:");

                    groupDialog.showAndWait().ifPresent(groupIdentifier -> {
                        // Prompt for the search keyword
                        TextInputDialog keywordDialog = new TextInputDialog();
                        keywordDialog.setTitle("Search Article");
                        keywordDialog.setHeaderText("Enter keyword to search:");
                        keywordDialog.setContentText("Keyword:");

                        keywordDialog.showAndWait().ifPresent(keyword -> {
                            try {
                                // Perform the search with all parameters
                                databaseHelper.searchArticle(username, level, groupIdentifier, keyword);
                                System.out.println("Search complete for keyword: " + keyword + " with username: " + username + ", level: " + level + ", group identifier: " + groupIdentifier);
                            } catch (Exception ex) {
                                System.out.println("Error during keyword search: " + ex.getMessage());
                            }
                        });
                    });
                });
            });
        });

        backToMenuButton.setOnAction(e -> {
            articleStage.close();
            System.out.println("Going Back to Main Menu...");
            
        });

        /** Add buttons to the article layout */
        articleLayout.getChildren().addAll(
            createArticleButton, displayArticleButton, deleteArticleButton, 
            backupArticleButton, restoreArticleButton, searchArticleButton, 
            backToMenuButton
        );

        /** Create the scene */
        Scene articleScene = new Scene(articleLayout, 400, 400);
        articleStage.setScene(articleScene);
        articleStage.show();
    }
    
    /**
	 * Creation for for articles, including input of title, description, body, 
	 * level, access level, group, keywords, other info, and links!
	 * 
	 * @throws SQLException
	 */
    private void articleCreateManage() throws SQLException{
        Stage articleCreateStage = new Stage();
        articleCreateStage.setTitle("Create Article");

        /** Form Fields */
        TextField titleField = new TextField();
        titleField.setPromptText("Enter Article Title");
        
        TextField authorField = new TextField();
        authorField.setPromptText("Enter Author Name");

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Enter Article Description");
        descriptionArea.setWrapText(true);

        TextArea bodyArea = new TextArea();
        bodyArea.setPromptText("Enter Article Body");
        bodyArea.setWrapText(true);

        /** Dropdown for Article Level */
        ChoiceBox<String> levelChoiceBox = new ChoiceBox<>();
        levelChoiceBox.getItems().addAll("Beginner", "Intermediate", "Advanced", "Expert");
        levelChoiceBox.setValue("Beginner");

        TextField groupField = new TextField();
        groupField.setPromptText("Enter Group Identifier");

        TextField keywordsField = new TextField();
        keywordsField.setPromptText("Enter Article Keywords");

        /** Dropdown for Access Level */
        ChoiceBox<String> accessLevelChoiceBox = new ChoiceBox<>();
        accessLevelChoiceBox.getItems().addAll("Public", "Restricted");
        accessLevelChoiceBox.setValue("Public");

        TextArea otherDetailsArea = new TextArea();
        otherDetailsArea.setPromptText("Enter Other Details");
        otherDetailsArea.setWrapText(true);

        TextField linksField = new TextField();
        linksField.setPromptText("Enter Links");

        Button submitButton = new Button("Submit");
        Button cancelButton = new Button("Cancel");

        /** Form Layout */
        GridPane formLayout = new GridPane();
        formLayout.setPadding(new javafx.geometry.Insets(20));
        formLayout.setHgap(10);
        formLayout.setVgap(10);

        formLayout.add(new Label("Title:"), 0, 0);
        formLayout.add(titleField, 1, 0);
        formLayout.add(new Label("Author:"), 0, 1);
        formLayout.add(authorField, 1, 1);
        formLayout.add(new Label("Description:"), 0, 2);
        formLayout.add(descriptionArea, 1, 2);
        formLayout.add(new Label("Body:"), 0, 3);
        formLayout.add(bodyArea, 1, 3);
        formLayout.add(new Label("Level:"), 0, 4);
        formLayout.add(levelChoiceBox, 1, 4);
        formLayout.add(new Label("Group Identifier:"), 0, 5);
        formLayout.add(groupField, 1, 5);
        formLayout.add(new Label("Keywords:"), 0, 6);
        formLayout.add(keywordsField, 1, 6);
        formLayout.add(new Label("Access Level:"), 0, 7);
        formLayout.add(accessLevelChoiceBox, 1, 7);
        formLayout.add(new Label("Other Details:"), 0, 8);
        formLayout.add(otherDetailsArea, 1, 8);
        formLayout.add(new Label("Links:"), 0, 9);
        formLayout.add(linksField, 1, 9);

        HBox buttonLayout = new HBox(10, submitButton, cancelButton);
        buttonLayout.setAlignment(Pos.CENTER);

        VBox mainLayout = new VBox(10, formLayout, buttonLayout);
        mainLayout.setPadding(new javafx.geometry.Insets(20));

        /** Submit Button Action */
        submitButton.setOnAction(e -> {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String description = descriptionArea.getText().trim();
            String body = bodyArea.getText().trim();
            String level = levelChoiceBox.getValue().toLowerCase();
            String groupIdentifier = groupField.getText().trim();
            String keywords = keywordsField.getText().trim();
            String accessLevel = accessLevelChoiceBox.getValue().toLowerCase();
            String otherDetails = otherDetailsArea.getText().trim();
            String links = linksField.getText().trim();

            if (title.isEmpty() || description.isEmpty() || body.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation Error");
                alert.setHeaderText("Missing Required Fields");
                alert.setContentText("Please ensure Title, Description, and Body are filled.");
                alert.showAndWait();
                return;
            }

            try {
                databaseHelper.createHelpArticle(title, author, description, body, level, groupIdentifier, keywords, accessLevel, otherDetails, links);
                System.out.println("Article Created Successfully!");
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText("Article Created");
                successAlert.setContentText("The article has been created successfully.");
                successAlert.showAndWait();
                articleCreateStage.close();
                articleManage(); // Return to the article management interface
            } catch (SQLException ex) {
                System.out.println("Error creating article: " + ex.getMessage());
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Database Error");
                errorAlert.setHeaderText("Failed to Create Article");
                errorAlert.setContentText("Error: " + ex.getMessage());
                errorAlert.showAndWait();
            }
        });

        /** Cancel Button Action */
        cancelButton.setOnAction(e -> articleCreateStage.close());

        /** Set the Scene and Show */
        Scene articleCreateScene = new Scene(mainLayout, 600, 600);
        articleCreateStage.setScene(articleCreateScene);
        articleCreateStage.show();
    }

    
    /**
	 * Display article manager, can view article by ID, group, or list them out in a shortened 
	 * fashion. 
	 * 
	 * @throws SQLException
	 */
    private void articleDisplayManage() throws SQLException{
        Stage displayStage = new Stage();
        displayStage.setTitle("Article Display Manager");

        /** Menu Buttons */
        Button viewByIdButton = new Button("View Article by ID");
        Button displayByGroupButton = new Button("Display Articles by Group");
        Button listAllButton = new Button("List All Articles");
        Button backButton = new Button("Go Back");

        /** Layout */
        VBox buttonLayout = new VBox(10, viewByIdButton, displayByGroupButton, listAllButton, backButton);
        buttonLayout.setPadding(new javafx.geometry.Insets(20));
        buttonLayout.setAlignment(Pos.CENTER);

        /** Scene Setup */
        Scene displayScene = new Scene(buttonLayout, 400, 300);
        displayStage.setScene(displayScene);
        displayStage.show();

        /** Button Actions */

        /** 1. View Article by ID */
        viewByIdButton.setOnAction(e -> {
            try {
				if (!databaseHelper.hasArticles()) {
				    Alert noArticlesAlert = new Alert(Alert.AlertType.INFORMATION);
				    noArticlesAlert.setTitle("No Articles");
				    noArticlesAlert.setHeaderText("No Articles Available");
				    noArticlesAlert.setContentText("There are currently no articles in the system.");
				    noArticlesAlert.showAndWait();
				    return;
				}
			} catch (SQLException e1) {

				e1.printStackTrace();
			}

            /** Input Dialog for Article ID */
            TextInputDialog idDialog = new TextInputDialog();
            idDialog.setTitle("View Article by ID");
            idDialog.setHeaderText("Enter the ID of the Article");
            idDialog.setContentText("Article ID:");

            idDialog.showAndWait().ifPresent(input -> {
                try {
                    int articleID = Integer.parseInt(input);
                    
                    // Prompt for the Username
                    TextInputDialog usernameDialog = new TextInputDialog();
                    usernameDialog.setTitle("Enter Username");
                    usernameDialog.setHeaderText("Enter the Username for the Article");
                    usernameDialog.setContentText("Username:");

                    usernameDialog.showAndWait().ifPresent(username -> {
                        try {
                            // Check if the article exists with the provided ID and username
                            if (databaseHelper.displayArticle(articleID, username).compareTo("") == 0) {
                                Alert invalidIdAlert = new Alert(Alert.AlertType.WARNING);
                                invalidIdAlert.setTitle("Invalid ID");
                                invalidIdAlert.setHeaderText("Article Not Found");
                                invalidIdAlert.setContentText("No article exists with the provided ID and username.");
                                invalidIdAlert.showAndWait();
                            } else {
                                try {
                                    Alert articlesFound = new Alert(Alert.AlertType.INFORMATION);
                                    articlesFound.setTitle("Articles");
                                    articlesFound.setHeaderText("Articles by ID and Username");
                                    articlesFound.setContentText(databaseHelper.displayArticle(articleID, username)); 
                                    articlesFound.showAndWait();
                                } catch (SQLException ex) {
                                    showErrorDialog("Error Displaying Articles", "An error occurred while displaying the article.", ex.getMessage());
                                } catch (Exception ex) {
                                    showErrorDialog("Unexpected Error", "An unexpected error occurred.", ex.getMessage());
                                }
                            }
                        } catch (SQLException ex) {
                            showErrorDialog("Error Retrieving Article", "An error occurred while retrieving the article.", ex.getMessage());
                        } catch (Exception ex) {
                            showErrorDialog("Unexpected Error", "An unexpected error occurred.", ex.getMessage());
                        }
                    });
                } catch (NumberFormatException ex) {
                    Alert invalidInputAlert = new Alert(Alert.AlertType.ERROR);
                    invalidInputAlert.setTitle("Invalid Input");
                    invalidInputAlert.setHeaderText("Non-numeric ID Entered");
                    invalidInputAlert.setContentText("Please enter a valid numeric ID.");
                    invalidInputAlert.showAndWait();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            });
        });

        /** 2. Display Articles by Group */
        displayByGroupButton.setOnAction(e -> {
            // Prompt for the Group Identifier
            TextInputDialog groupDialog = new TextInputDialog();
            groupDialog.setTitle("Display Articles by Group");
            groupDialog.setHeaderText("Enter the Group Identifier");
            groupDialog.setContentText("Group Identifier:");

            groupDialog.showAndWait().ifPresent(group -> {
                // Prompt for the Username
                TextInputDialog usernameDialog = new TextInputDialog();
                usernameDialog.setTitle("Enter Username");
                usernameDialog.setHeaderText("Enter the Username for Articles");
                usernameDialog.setContentText("Username:");

                usernameDialog.showAndWait().ifPresent(username -> {
                    try {
                        Alert articlesFound = new Alert(Alert.AlertType.INFORMATION);
                        articlesFound.setTitle("Articles");
                        articlesFound.setHeaderText("Articles by Group and User");
                        articlesFound.setContentText(databaseHelper.displayArticleByGroup(group, username)); 
                        articlesFound.showAndWait();
                    } catch (SQLException ex) {
                        showErrorDialog("Error Displaying Articles", "An error occurred while displaying articles by group and user.", ex.getMessage());
                    } catch (Exception ex) {
                        showErrorDialog("Unexpected Error", "An unexpected error occurred.", ex.getMessage());
                    }
                });
            });
        });

        /** 3. List All Articles */
        listAllButton.setOnAction(e -> {
            // Prompt for the username
            TextInputDialog usernameDialog = new TextInputDialog();
            usernameDialog.setTitle("Enter Username");
            usernameDialog.setHeaderText("Enter the Username for Articles");
            usernameDialog.setContentText("Username:");

            usernameDialog.showAndWait().ifPresent(username -> {
                try {
                    StringBuilder allArticles = new StringBuilder("All Articles by User " + username + ":\n");

                    // Call listArticles with the username
                    databaseHelper.listArticles(username);

                    // Capture the output of listArticles
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    PrintStream originalOut = System.out;
                    System.setOut(new PrintStream(outputStream));

                    try {
                        databaseHelper.listArticles(username); 
                    } finally {
                        System.setOut(originalOut);
                    }

                    allArticles.append(outputStream.toString());

                    // Show the alert with the list of articles
                    Alert articlesAlert = new Alert(Alert.AlertType.INFORMATION);
                    articlesAlert.setTitle("All Articles");
                    articlesAlert.setHeaderText("List of All Articles by " + username);
                    articlesAlert.setContentText(allArticles.toString());
                    articlesAlert.showAndWait();
                } catch (SQLException ex) {
                    showErrorDialog("Error Listing Articles", "An error occurred while listing articles by user.", ex.getMessage());
                } catch (Exception ex) {
                    showErrorDialog("Unexpected Error", "An unexpected error occurred.", ex.getMessage());
                }
            });
        });

        /** 4. Go Back */
        backButton.setOnAction(e -> {
            displayStage.close();
            /** Return to article management interface */
        });
    }

    
    /**
	 * Article deletion manager. Can delete an article by ID, or you may delete all of them 
	 * at the same time (purge). 
	 * 
	 * @throws SQLException
	 */
    private void articleDeleteManage() throws SQLException{
        Stage deleteStage = new Stage();
        deleteStage.setTitle("Article Deletion Manager");

        /** Menu Buttons */
        Button deleteByIdButton = new Button("Delete an Article by ID");
        Button deleteAllButton = new Button("Delete All Articles");
        Button backButton = new Button("Go Back");

        /** Layout */
        VBox buttonLayout = new VBox(10, deleteByIdButton, deleteAllButton, backButton);
        buttonLayout.setPadding(new javafx.geometry.Insets(20));
        buttonLayout.setAlignment(Pos.CENTER);

        /** Scene Setup */
        Scene deleteScene = new Scene(buttonLayout, 400, 300);
        deleteStage.setScene(deleteScene);
        deleteStage.show();

        /** Button Actions */

        /** 1. Delete Article by ID */
        deleteByIdButton.setOnAction(e -> {
            try {
				if (!databaseHelper.hasArticles()) {
				    Alert noArticlesAlert = new Alert(Alert.AlertType.INFORMATION);
				    noArticlesAlert.setTitle("No Articles");
				    noArticlesAlert.setHeaderText("No Articles Available");
				    noArticlesAlert.setContentText("There are currently no articles to delete.");
				    noArticlesAlert.showAndWait();
				    return;
				}
			} catch (SQLException e1) {

				e1.printStackTrace();
			}

            /** Input Dialog for Article ID */
            TextInputDialog idDialog = new TextInputDialog();
            idDialog.setTitle("Delete Article by ID");
            idDialog.setHeaderText("Enter the ID of the Article to Delete");
            idDialog.setContentText("Article ID:");

            idDialog.showAndWait().ifPresent(input -> {
                try {
                    int idToDelete = Integer.parseInt(input);

                    boolean isDeleted = databaseHelper.deleteArticle(idToDelete);
                    if (isDeleted) {
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Success");
                        successAlert.setHeaderText("Article Deleted");
                        successAlert.setContentText("The article with ID " + idToDelete + " has been deleted.");
                        successAlert.showAndWait();
                    } else {
                        Alert invalidIdAlert = new Alert(Alert.AlertType.WARNING);
                        invalidIdAlert.setTitle("Invalid ID");
                        invalidIdAlert.setHeaderText("Article Not Found");
                        invalidIdAlert.setContentText("No article exists with the provided ID.");
                        invalidIdAlert.showAndWait();
                    }
                } catch (NumberFormatException ex) {
                    showErrorDialog("Invalid Input", "Non-numeric ID Entered", "Please enter a valid numeric ID.");
                } catch (SQLException ex) {
                    showErrorDialog("Error Deleting Article", "An error occurred while deleting the article.", ex.getMessage());
                } catch (Exception ex) {
                    showErrorDialog("Unexpected Error", "An unexpected error occurred.", ex.getMessage());
                }
            });
        });

        /** 2. Delete All Articles */
        deleteAllButton.setOnAction(e -> {
            try {
				if (!databaseHelper.hasArticles()) {
				    Alert noArticlesAlert = new Alert(Alert.AlertType.INFORMATION);
				    noArticlesAlert.setTitle("No Articles");
				    noArticlesAlert.setHeaderText("No Articles Available");
				    noArticlesAlert.setContentText("There are currently no articles to delete.");
				    noArticlesAlert.showAndWait();
				    return;
				}
			} catch (SQLException e1) {

				e1.printStackTrace();
			}

            Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDelete.setTitle("Delete All Articles");
            confirmDelete.setHeaderText("Are you sure you want to delete all articles?");
            confirmDelete.setContentText("This action cannot be undone.");

            Optional<ButtonType> result = confirmDelete.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    databaseHelper.deleteAll();
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Success");
                    successAlert.setHeaderText("All Articles Deleted");
                    successAlert.setContentText("All articles have been successfully deleted.");
                    successAlert.showAndWait();
                } catch (Exception ex) {
                    showErrorDialog("Error Deleting Articles", "An error occurred while deleting all articles.", ex.getMessage());
                }
            } else {
                Alert cancelAlert = new Alert(Alert.AlertType.INFORMATION);
                cancelAlert.setTitle("Deletion Canceled");
                cancelAlert.setHeaderText("No Articles Deleted");
                cancelAlert.setContentText("The operation to delete all articles was canceled.");
                cancelAlert.showAndWait();
            }
        });

        /** 3. Go Back */
        backButton.setOnAction(e -> {
            deleteStage.close();
            /** Return to the article management interface */
        });
    }
    
    
    /**
	 * Back up article manager, you can back up the entire database or by group identifier, 
	 * and it will ask for the backup file name to be used. 
	 * 
	 * @throws SQLException
	 */
    private void articleBackUpManage() throws SQLException{
        Stage backupStage = new Stage();
        backupStage.setTitle("Article Backup Manager");

        /** Menu Buttons */
        Button backupAllButton = new Button("Backup All Articles");
        Button backupByGroupButton = new Button("Backup Articles by Group");
        Button backButton = new Button("Go Back");

        /** Layout */
        VBox buttonLayout = new VBox(10, backupAllButton, backupByGroupButton, backButton);
        buttonLayout.setPadding(new javafx.geometry.Insets(20));
        buttonLayout.setAlignment(Pos.CENTER);

        /** Scene Setup */
        Scene backupScene = new Scene(buttonLayout, 400, 300);
        backupStage.setScene(backupScene);
        backupStage.show();

        /** Button Actions */

        /** 1. Backup All Articles */
        backupAllButton.setOnAction(e -> {
            TextInputDialog filenameDialog = new TextInputDialog("backup.csv");
            filenameDialog.setTitle("Backup All Articles");
            filenameDialog.setHeaderText("Enter the Filename for Backup");
            filenameDialog.setContentText("Filename:");

            filenameDialog.showAndWait().ifPresent(filename -> {
                try {
                    databaseHelper.backupHelpSystemToFile(filename);
                    showInfoDialog("Backup Successful", "Backup Completed", "The entire help system has been backed up to " + filename + ".");
                } catch (Exception ex) {
                    showErrorDialog("Backup Failed", "An error occurred during the backup.", ex.getMessage());
                }
            });
        });

        /** 2. Backup Articles by Group */
        backupByGroupButton.setOnAction(e -> {
            Dialog<Pair<String, String>> groupBackupDialog = new Dialog<>();
            groupBackupDialog.setTitle("Backup Articles by Group");
            groupBackupDialog.setHeaderText("Enter the Group Identifier and Filename for Backup");

            /** Input Fields */
            TextField groupField = new TextField();
            groupField.setPromptText("Group Identifier");
            TextField filenameField = new TextField("groupBackup.csv");
            filenameField.setPromptText("Filename");

            VBox inputLayout = new VBox(10, new Label("Group Identifier:"), groupField, new Label("Filename:"), filenameField);
            inputLayout.setPadding(new javafx.geometry.Insets(10));

            groupBackupDialog.getDialogPane().setContent(inputLayout);
            groupBackupDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            groupBackupDialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    return new Pair<>(groupField.getText(), filenameField.getText());
                }
                return null;
            });

            groupBackupDialog.showAndWait().ifPresent(result -> {
                String groupIdentifier = result.getKey();
                String filename = result.getValue();

                try {
                    databaseHelper.backUpGroupToFile(filename, groupIdentifier);
                    showInfoDialog("Backup Successful", "Backup Completed", "Backup of group '" + groupIdentifier + "' has been saved to " + filename + ".");
                } catch (Exception ex) {
                    showErrorDialog("Backup Failed", "An error occurred during the backup.", ex.getMessage());
                }
            });
        });

        /** 3. Go Back */
        backButton.setOnAction(e -> {
            backupStage.close();
            /** Return to the article management interface */
        });
    }
    
    
    /**
     * Article Restore Manager, you can restore any previously backed up
     * file of articles into the current system, with the choice to either 
     * delete all articles currently in the system or to add onto the current
     * slate of articles. 
     * 
     * @throws SQLException
     */
    private void articleRestoreManage() throws SQLException{
        Stage restoreStage = new Stage();
        restoreStage.setTitle("Article Restore Manager");

        /** Menu Buttons */
        Button restoreWithDeleteButton = new Button("Restore with Deleting");
        Button restoreWithoutDeleteButton = new Button("Restore without Deleting");
        Button backButton = new Button("Go Back");

        /** Layout */
        VBox buttonLayout = new VBox(10, restoreWithDeleteButton, restoreWithoutDeleteButton, backButton);
        buttonLayout.setPadding(new javafx.geometry.Insets(20));
        buttonLayout.setAlignment(Pos.CENTER);

        /** Scene Setup */
        Scene restoreScene = new Scene(buttonLayout, 400, 300);
        restoreStage.setScene(restoreScene);
        restoreStage.show();

        /** Button Actions */

        /** 1. Restore with Deleting */
        restoreWithDeleteButton.setOnAction(e -> {
            TextInputDialog restoreFileDialog = new TextInputDialog("backup.csv");
            restoreFileDialog.setTitle("Restore with Deleting");
            restoreFileDialog.setHeaderText("Enter the Filename to Restore From");
            restoreFileDialog.setContentText("Filename:");

            restoreFileDialog.showAndWait().ifPresent(restoreFile -> {
                try {
                    databaseHelper.restoreSystem(restoreFile);
                    showInfoDialog("Restoration Successful", "System Restored", "The system was successfully restored from " + restoreFile + ".");
                } catch (Exception ex) {
                    showErrorDialog("Restoration Failed", "An error occurred during restoration.", ex.getMessage());
                }
            });
        });

        /** 2. Restore without Deleting */
        restoreWithoutDeleteButton.setOnAction(e -> {
            TextInputDialog restoreExistingFileDialog = new TextInputDialog("backup.csv");
            restoreExistingFileDialog.setTitle("Restore without Deleting");
            restoreExistingFileDialog.setHeaderText("Enter the Filename to Restore From");
            restoreExistingFileDialog.setContentText("Filename:");

            restoreExistingFileDialog.showAndWait().ifPresent(restoreFile -> {
                try {
                    databaseHelper.restoreSystemExisting(restoreFile);
                    showInfoDialog("Restoration Successful", "Existing System Restored", "The existing system was successfully restored from " + restoreFile + ".");
                } catch (Exception ex) {
                    showErrorDialog("Restoration Failed", "An error occurred during restoration.", ex.getMessage());
                }
            });
        });

        /** 3. Go Back */
        backButton.setOnAction(e -> {
            restoreStage.close();
            /** Return to the article management interface */
        });
    }
    
    
    /** ------------ Help Managers  ------------ */
    
    
    /**
     * The Help Manager is a menu to help students navigate 
     * and view help articles or create help requests.
     * 
     * @throws SQLException
     */
    private void helpManager() throws SQLException{
    	Stage helpStage = new Stage();
        helpStage.setTitle("Help System");

        /** Layout for Help Management */
        VBox articleLayout = new VBox(10);
        articleLayout.setPadding(new javafx.geometry.Insets(20));

        /** Buttons for help management functionalities */
        Button genericMessageButton = new Button("Send a Generic Message");
        Button specificMessageButton = new Button("Send a Specific Message");
        Button searchButton = new Button("Search for Articles");
        
        Button goBackButton = new Button("Go Back");
        

        /** Set button actions */
        genericMessageButton.setOnAction(e -> {
			try {
				genericMessage();
			} catch (SQLException e1) {

				e1.printStackTrace();
			}
		});
        
        specificMessageButton.setOnAction(e -> {
			try {
				specificMessage();
			} catch (SQLException e1) {

				e1.printStackTrace();
			}
		});
        
        searchButton.setOnAction(e -> {
			try {
				articleSearch();
			} catch (SQLException e1) {

				e1.printStackTrace();
			}
		});

        goBackButton.setOnAction(e -> {
            helpStage.close();
            
        });

        /** Add buttons to the article layout */
        articleLayout.getChildren().addAll(
        		genericMessageButton, specificMessageButton, searchButton, goBackButton
        );

        /** Create the scene */
        Scene articleScene = new Scene(articleLayout, 300, 300);
        helpStage.setScene(articleScene);
        helpStage.show();
    }
    
    /**
     * The generic message function helps the user
     * view help articles through a message system and 
     * can specify the level and group of the article to be
     * requested.
     * 
     * @throws SQLException
     */
    private void genericMessage() throws SQLException {
    	Stage genericMessageStage = new Stage();
    	genericMessageStage.setTitle("Send Generic Message");
    	
        /** Dropdown for Article Level */
        ChoiceBox<String> levelChoiceBox = new ChoiceBox<>();
        levelChoiceBox.getItems().addAll("All", "Beginner", "Intermediate", "Advanced", "Expert");
        levelChoiceBox.setValue("All");
        
        /**Generic Message*/
        TextField messageField = new TextField();
        messageField.setPromptText("Enter Help Question");
        
        /**Group Identifier*/
        TextField groupField = new TextField();
        groupField.setPromptText("Leave empty to search all groups");

        /** Send and Cancel Buttons*/
        Button sendButton = new Button("Send");
        Button cancelButton = new Button("Cancel");

        /** Form Layout */
        GridPane formLayout = new GridPane();
        formLayout.setPadding(new javafx.geometry.Insets(20));
        formLayout.setHgap(10);
        formLayout.setVgap(10);

        formLayout.add(new Label("Generic Message:"), 0, 0);
        formLayout.add(messageField, 1, 0);
        formLayout.add(new Label("Level:"), 0, 1);
        formLayout.add(levelChoiceBox, 1, 1);
        formLayout.add(new Label("Group:"), 0, 2);
        formLayout.add(groupField, 1, 2);
        
        
        HBox buttonLayout = new HBox(10, sendButton, cancelButton);
        buttonLayout.setAlignment(Pos.CENTER);

        VBox mainLayout = new VBox(10, formLayout, buttonLayout);
        mainLayout.setPadding(new javafx.geometry.Insets(20));

        /** Submit Button Action */
        sendButton.setOnAction(e -> {
            
            String level = levelChoiceBox.getValue().toLowerCase();
            String message = messageField.getText().toLowerCase();

            try {
            	/** Find help article and display it using Database*/
            	Alert genericArticlesFound = new Alert(Alert.AlertType.INFORMATION);
            	genericArticlesFound.setTitle("Help Articles");
            	genericArticlesFound.setHeaderText("Generic Help Found");
			    genericArticlesFound.setContentText(databaseHelper.displayGenericMessageArticles(message));
			    genericArticlesFound.showAndWait();
            	
                helpManager(); 
            } catch (SQLException ex) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Database Error");
                errorAlert.setHeaderText("Failed to Find Help Articles");
                errorAlert.setContentText("Error: " + ex.getMessage());
                errorAlert.showAndWait();
            } catch (Exception e1) {

				e1.printStackTrace();
			}
        });

        /** Cancel Button Action */
        cancelButton.setOnAction(e -> genericMessageStage.close());

        /** Set the Scene and Show */
        Scene genericMessageScene = new Scene(mainLayout, 400, 200);
        genericMessageStage.setScene(genericMessageScene);
        genericMessageStage.show();
    }
    
    
    /**
     * The specific message function helps users
     * create a help request to the system when the 
     * help message they need is not included in generic
     * messages. 
     * 
     * @throws SQLException
     */
    private void specificMessage() throws SQLException {
    	Stage specificMessageStage = new Stage();
    	specificMessageStage.setTitle("Send Specific Message");
    	
        /** Dropdown for Specific Message --- ADD NEW MESSAGES HERE*/
        TextField messageField = new TextField();
        messageField.setPromptText("Enter Message");
        
        TextField userField = new TextField();
        userField.setPromptText("Enter Your Username");


        /** Send and Cancel Buttons*/
        Button sendButton = new Button("Send");
        Button cancelButton = new Button("Cancel");

        /** Form Layout */
        GridPane formLayout = new GridPane();
        formLayout.setPadding(new javafx.geometry.Insets(20));
        formLayout.setHgap(10);
        formLayout.setVgap(10);

        formLayout.add(new Label("Specific Message:"), 0, 0);
        formLayout.add(messageField, 1, 0);
        formLayout.add(new Label("Username:"), 0, 1);
        formLayout.add(messageField, 1, 1);
        
        HBox buttonLayout = new HBox(10, sendButton, cancelButton);
        buttonLayout.setAlignment(Pos.CENTER);

        VBox mainLayout = new VBox(10, formLayout, buttonLayout);
        mainLayout.setPadding(new javafx.geometry.Insets(20));

        /** Submit Button Action */
        sendButton.setOnAction(e -> {
            
            String message = messageField.getText().trim();
            String user = userField.getText().trim();

            try {
            	/** Create Request in Database*/
            	
            	databaseHelper.addSpecificMessage(user,message);
            	
                helpManager(); 
            } catch (SQLException ex) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Database Error");
                errorAlert.setHeaderText("Failed to Generate Request");
                errorAlert.setContentText("Error: " + ex.getMessage());
                errorAlert.showAndWait();
            }
        });

        /** Cancel Button Action */
        cancelButton.setOnAction(e -> specificMessageStage.close());

        /** Set the Scene and Show */
        Scene specificMessageScene = new Scene(mainLayout, 400, 200);
        specificMessageStage.setScene(specificMessageScene);
        specificMessageStage.show();
    }
    
    
    /**
     * Article Search Manager to help a student or any other user
     * search for articles using IDs, Groups, and author names or words. 
     * 
     * @throws SQLException
     */
    private void articleSearch() throws SQLException{
        Stage displayStage = new Stage();
        displayStage.setTitle("Article Search Manager");

        /** Menu Buttons */
        Button viewByIdButton = new Button("Search Article by ID");
        Button displayByGroupButton = new Button("Search Articles by Group");
        Button searchByAuthorButton = new Button("Search by Author");
        Button searchByWordsButton = new Button("Search by Word or Phrase");
        Button backButton = new Button("Go Back");

        /** Layout */
        VBox buttonLayout = new VBox(10, viewByIdButton, displayByGroupButton, 
        		searchByAuthorButton, searchByWordsButton, backButton);
        buttonLayout.setPadding(new javafx.geometry.Insets(20));
        buttonLayout.setAlignment(Pos.CENTER);

        /** Scene Setup */
        Scene displayScene = new Scene(buttonLayout, 400, 300);
        displayStage.setScene(displayScene);
        displayStage.show();

        /** Button Actions */

        /** 1. View Article by ID */
        viewByIdButton.setOnAction(e -> {
            try {
				if (!databaseHelper.hasArticles()) {
				    Alert noArticlesAlert = new Alert(Alert.AlertType.INFORMATION);
				    noArticlesAlert.setTitle("No Articles");
				    noArticlesAlert.setHeaderText("No Articles Available");
				    noArticlesAlert.setContentText("There are currently no articles in the system.");
				    noArticlesAlert.showAndWait();
				    return;
				}
			} catch (SQLException e1) {

				e1.printStackTrace();
			}

            /** Input Dialog for Article ID */
            TextInputDialog idDialog = new TextInputDialog();
            idDialog.setTitle("View Article by ID");
            idDialog.setHeaderText("Enter the ID of the Article");
            idDialog.setContentText("Article ID:");

            idDialog.showAndWait().ifPresent(input -> {
                try {
                    int articleID = Integer.parseInt(input);
                    
                    // Prompt for the username
                    TextInputDialog usernameDialog = new TextInputDialog();
                    usernameDialog.setTitle("Enter Username");
                    usernameDialog.setHeaderText("Enter the Username for Article Retrieval");
                    usernameDialog.setContentText("Username:");

                    usernameDialog.showAndWait().ifPresent(username -> {
                        try {
  
                            String articleContent = databaseHelper.displayArticle(articleID, username);
                            if (articleContent.compareTo("") == 0) {
                                Alert invalidIdAlert = new Alert(Alert.AlertType.WARNING);
                                invalidIdAlert.setTitle("Invalid ID or Username");
                                invalidIdAlert.setHeaderText("Article Not Found");
                                invalidIdAlert.setContentText("No article exists with the provided ID and username.");
                                invalidIdAlert.showAndWait();
                            } else {
                                try {
                                    Alert articlesFound = new Alert(Alert.AlertType.INFORMATION);
                                    articlesFound.setTitle("Articles");
                                    articlesFound.setHeaderText("Articles by ID and Username");
                                    articlesFound.setContentText(articleContent); 
                                    articlesFound.showAndWait();
                                }
                                 catch (Exception ex) {
                                    showErrorDialog("Unexpected Error", "An unexpected error occurred.", ex.getMessage());
                                }
                            }
                        } catch (SQLException ex) {
                            showErrorDialog("Error Retrieving Article", "An error occurred while retrieving the article.", ex.getMessage());
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    });
                    
                } catch (NumberFormatException ex) {
                    Alert invalidInputAlert = new Alert(Alert.AlertType.ERROR);
                    invalidInputAlert.setTitle("Invalid Input");
                    invalidInputAlert.setHeaderText("Non-numeric ID Entered");
                    invalidInputAlert.setContentText("Please enter a valid numeric ID.");
                    invalidInputAlert.showAndWait();
                }  catch (Exception e1) {
                    e1.printStackTrace();
                }
            });

            displayByGroupButton.setOnAction(event -> {
                // Prompt for the Group Identifier
                TextInputDialog groupDialog = new TextInputDialog();
                groupDialog.setTitle("Display Articles by Group");
                groupDialog.setHeaderText("Enter the Group Identifier");
                groupDialog.setContentText("Group Identifier:");

                groupDialog.showAndWait().ifPresent(group -> {
                    // Prompt for the Username
                    TextInputDialog usernameDialog = new TextInputDialog();
                    usernameDialog.setTitle("Enter Username");
                    usernameDialog.setHeaderText("Enter the Username for Articles");
                    usernameDialog.setContentText("Username:");

                    usernameDialog.showAndWait().ifPresent(username -> {
                        try {
                            Alert articlesFound = new Alert(Alert.AlertType.INFORMATION);
                            articlesFound.setTitle("Articles");
                            articlesFound.setHeaderText("Articles by Group and User");
                            articlesFound.setContentText(databaseHelper.displayArticleByGroup(group, username)); 
                            articlesFound.showAndWait();
                        } catch (SQLException ex) {
                            showErrorDialog("Error Displaying Articles", "An error occurred while displaying articles by group and user.", ex.getMessage());
                        } catch (Exception ex) {
                            showErrorDialog("Unexpected Error", "An unexpected error occurred.", ex.getMessage());
                        }
                    });
                });
            });
        });
        
        /** Display Articles by Name */
        searchByAuthorButton.setOnAction(e -> {
        	searchArticleByAuthorManager();
        });
        
        /** Display Articles by Words */
        searchByWordsButton.setOnAction(e -> {
        	searchArticleByWordsManager();
        });

        /** 4. Go Back */
        backButton.setOnAction(e -> {
            displayStage.close();
            /** Return to article management interface */
        });
    }
    
    
    private void searchArticleByAuthorManager() {
    	Stage searchByAuthorStage = new Stage();
    	searchByAuthorStage.setTitle("Search by Author");
    	
    	/**Name Field*/
        TextField authorField = new TextField();
        authorField.setPromptText("Enter author to search");
        
        /**Group Identifier*/
        TextField groupField = new TextField();
        groupField.setPromptText("Leave empty to search all groups");
        
        /**Username*/
        TextField userField = new TextField();
        userField.setPromptText("Enter your username");

        /** Send and Cancel Buttons*/
        Button sendButton = new Button("Send");
        Button cancelButton = new Button("Cancel");

        /** Form Layout */
        GridPane formLayout = new GridPane();
        formLayout.setPadding(new javafx.geometry.Insets(20));
        formLayout.setHgap(10);
        formLayout.setVgap(10);

        formLayout.add(new Label("Name:"), 0, 0);
        formLayout.add(authorField, 1, 0);
        formLayout.add(new Label("Group:"), 0, 1);
        formLayout.add(groupField, 1, 1);
        formLayout.add(new Label("Username:"), 0, 2);
        formLayout.add(userField, 1, 2);
        
        
        HBox buttonLayout = new HBox(10, sendButton, cancelButton);
        buttonLayout.setAlignment(Pos.CENTER);

        VBox mainLayout = new VBox(10, formLayout, buttonLayout);
        mainLayout.setPadding(new javafx.geometry.Insets(20));

        /** Submit Button Action */
        sendButton.setOnAction(e -> {
            
            String author = authorField.getText().trim();
            String group =  groupField.getText().trim();
            String user = userField.getText().trim();

            try {
            	/** Find help article and display it using Database*/
            	Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            	infoAlert.setTitle("Articles Searched");
            	infoAlert.setHeaderText("Found Articles:");
            	infoAlert.setContentText(databaseHelper.searchArticle(user, "public", group, author));
            	infoAlert.showAndWait();
            	
                
                articleSearch(); 
            } catch (SQLException ex) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Database Error");
                errorAlert.setHeaderText("Failed to Find Help Articles");
                errorAlert.setContentText("Error: " + ex.getMessage());
                errorAlert.showAndWait();
            }
        });

        /** Cancel Button Action */
        cancelButton.setOnAction(e -> searchByAuthorStage.close());

        /** Set the Scene and Show */
        Scene searchByAuthorScene = new Scene(mainLayout, 300, 300);
        searchByAuthorStage.setScene(searchByAuthorScene);
        searchByAuthorStage.show();
    }
    
    private void searchArticleByWordsManager() {
    	Stage searchByWordsStage = new Stage();
    	searchByWordsStage.setTitle("Search by Word or Phrase");
    	
    	/**Words Field*/
        TextField wordsField = new TextField();
        wordsField.setPromptText("Enter word or phrase");
        
        /**Group Identifier*/
        TextField groupField = new TextField();
        groupField.setPromptText("Leave empty to search all groups");
        
        /**Username*/
        TextField userField = new TextField();
        userField.setPromptText("Enter your username");

        /** Send and Cancel Buttons*/
        Button sendButton = new Button("Send");
        Button cancelButton = new Button("Cancel");

        /** Form Layout */
        GridPane formLayout = new GridPane();
        formLayout.setPadding(new javafx.geometry.Insets(20));
        formLayout.setHgap(10);
        formLayout.setVgap(10);

        formLayout.add(new Label("Word or Phrase:"), 0, 0);
        formLayout.add(wordsField, 1, 0);
        formLayout.add(new Label("Group:"), 0, 1);
        formLayout.add(groupField, 1, 1);
        formLayout.add(new Label("Username:"), 0, 2);
        formLayout.add(userField, 1, 2);
        
        
        HBox buttonLayout = new HBox(10, sendButton, cancelButton);
        buttonLayout.setAlignment(Pos.CENTER);

        VBox mainLayout = new VBox(10, formLayout, buttonLayout);
        mainLayout.setPadding(new javafx.geometry.Insets(20));

        /** Submit Button Action */
        sendButton.setOnAction(e -> {
            
            String words = wordsField.getText().trim();
            String group =  groupField.getText().trim();
            String user = userField.getText().trim();

            try {
            	/** Find help article and display it using Database*/
            	Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            	infoAlert.setTitle("Articles Searched");
            	infoAlert.setHeaderText("Found Articles:");
            	infoAlert.setContentText(databaseHelper.searchArticle(user, "public", group, words));
            	infoAlert.showAndWait();
            	
            	
                
                articleSearch(); 
            } catch (SQLException ex) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Database Error");
                errorAlert.setHeaderText("Failed to Find Help Articles");
                errorAlert.setContentText("Error: " + ex.getMessage());
                errorAlert.showAndWait();
            }
        });

        /** Cancel Button Action */
        cancelButton.setOnAction(e -> searchByWordsStage.close());

        /** Set the Scene and Show */
        Scene searchByWordsScene = new Scene(mainLayout, 300, 300);
        searchByWordsStage.setScene(searchByWordsScene);
        searchByWordsStage.show();
    }
    
    

    /**
     *  Utility Methods for Dialogs
     * 
     * @param title
     * @param header
     * @param content
     */
    private void showInfoDialog(String title, String header, String content) {
    	Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
    	infoAlert.setTitle(title);
    	infoAlert.setHeaderText(header);
    	infoAlert.setContentText(content);
    	infoAlert.showAndWait();
    }

    /**
     * Utility to Show Error Dialogs
     * 
     * @param title
     * @param header
     * @param content
     */
    private void showErrorDialog(String title, String header, String content) {
    	Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    	errorAlert.setTitle(title);
    	errorAlert.setHeaderText(header);
    	errorAlert.setContentText(content);
    	errorAlert.showAndWait();
    }

    private void showAddPeopleDialog(Stage adminStage) {
       
        VBox addPeopleLayout = new VBox(10);
        addPeopleLayout.setPadding(new javafx.geometry.Insets(20));

      
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");

        TextField groupNameField = new TextField();
        groupNameField.setPromptText("Enter group name");

        
        Button addAdminButton = new Button("Add Admin");
        Button addInstructorButton = new Button("Add Instructor");
        Button addStudentButton = new Button("Add Student");
        Button backButton = new Button("Back");

       
        addAdminButton.setOnAction(e -> {
            try {
                String username = usernameField.getText();
                String groupName = groupNameField.getText();
                accessGroups.addAdmin(username, groupName);
                System.out.println("Admin added!");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        addInstructorButton.setOnAction(e -> {
            try {
                String username = usernameField.getText();
                String groupName = groupNameField.getText();
                accessGroups.addInstructor(username, groupName);
                System.out.println("Instructor added!");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        addStudentButton.setOnAction(e -> {
            try {
                String username = usernameField.getText();
                String groupName = groupNameField.getText();
                accessGroups.addStudent(username, groupName);
                System.out.println("Student added!");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        backButton.setOnAction(e -> {
          
            try {
                adminHome();
                adminStage.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

     
        addPeopleLayout.getChildren().addAll(usernameField, groupNameField, addAdminButton, addInstructorButton, 
                addStudentButton, backButton);

       
        Scene addPeopleScene = new Scene(addPeopleLayout, 400, 300);
        adminStage.setScene(addPeopleScene);
    }
    
    
    private void showListPeopleDialog(Stage adminStage) {
        VBox listPeopleLayout = new VBox(10);
        listPeopleLayout.setPadding(new javafx.geometry.Insets(20));


        Button listAdminButton = new Button("List Admins");
        Button listInstructorsAdminButton = new Button("List Instructors with Admin Rights");
        Button listInstructorsViewingButton = new Button("List Instructors with Viewing Rights");
        Button listStudentViewingButton = new Button("List Students with Viewing Rights");
        Button backButton = new Button("Back");


        listAdminButton.setOnAction(e -> listPeople("admin"));
        listInstructorsAdminButton.setOnAction(e -> listPeople("instructorsAdmin"));
        listInstructorsViewingButton.setOnAction(e -> listPeople("instructorsViewing"));
        listStudentViewingButton.setOnAction(e -> listPeople("studentViewing"));
        
        backButton.setOnAction(e -> {
            try {
                adminHome();
                adminStage.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        listPeopleLayout.getChildren().addAll(listAdminButton, listInstructorsAdminButton, listInstructorsViewingButton,
                listStudentViewingButton, backButton);


        Scene listPeopleScene = new Scene(listPeopleLayout, 400, 300);
        adminStage.setScene(listPeopleScene);
    }

    private void listPeople(String type) {
        String result = "";
        try {
            switch (type) {
                case "admin":
                    result = accessGroups.listAdmin();
                    break;
                case "instructorsAdmin":
                    result = accessGroups.listInstructorsAdmin();
                    break;
                case "instructorsViewing":
                    result = accessGroups.listInstructorsViewing();
                    break;
                case "studentViewing":
                    result = accessGroups.listStudentViewing();
                    break;
                default:
                    result = "Invalid option!";
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

 
        showResultDialog(result);
    }

    private void showResultDialog(String result) {
        Stage resultStage = new Stage();
        resultStage.setTitle("List Results");

        VBox resultLayout = new VBox(10);
        resultLayout.setPadding(new javafx.geometry.Insets(20));

        TextArea resultArea = new TextArea(result);
        resultArea.setEditable(false);
        resultLayout.getChildren().add(resultArea);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> resultStage.close());

        resultLayout.getChildren().add(backButton);

 
        Scene resultScene = new Scene(resultLayout, 400, 300);
        resultStage.setScene(resultScene);
        resultStage.show();
    }
    
    
	/** ------------ Alert Function for Errors or Certain Displays  ------------ */

    /**
     * Displays an alert with a specified title and message.
     *
     * @param title   the title of the alert
     * @param message the message of the alert
     */
    private void showAlert(String title, String message) {
    	Alert alert = new Alert(Alert.AlertType.INFORMATION);
    	alert.setTitle(title);
    	alert.setHeaderText(null);
    	alert.setContentText(message);
    	alert.showAndWait();
    }
    
    
	/** ------------ Main  ------------ */

    /**
     * The main method to launch the application.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
    	launch(args);
    }
}
