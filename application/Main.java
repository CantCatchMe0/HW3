package application;

import databasePart1.DatabaseHelper;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
	private static final DatabaseHelper databaseHelper = new DatabaseHelper();
	
	static String[] users = {null, "Student A", "Student B", "Student C"};
	static String currentUser = null;
	
	// Prompt the user to sign in
	static void userSelect(Scanner scnr) {
		System.out.println("Select a user to sign in as: ");
		System.out.println("[1] Student A");
		System.out.println("[2] Student B");
		System.out.println("[3] Student C");
		System.out.println("Enter the # corresponding to your choice: ");
		int input = scnr.nextInt();
		if (input < users.length) {
			currentUser = users[input];
			System.out.println("You are now signed in as " + currentUser + ".");
		} else {
			System.out.println("Invalid selection. Please try again.");
			userSelect(scnr);
		}
	}
	
	// Start the application main menu
	public static void main(String[] args) {
        try {
            databaseHelper.connectToDatabase(); // Connect to the database
        } catch (SQLException e) {
        	System.out.println(e.getMessage());
        }
        Scanner scnr = new Scanner(System.in);
		userSelect(scnr);
		Menu m = new Menu(databaseHelper, currentUser);
		m.mainMenu(scnr);
		
		scnr.close();
	}

}
