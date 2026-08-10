package com.tus.pethotel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@Named("userList")
@ApplicationScoped			// use application scoped for this to run one the server  
public class UserList implements Serializable{
	private static final long serialVersionUID =1L; 	//so it matches the regsitration and user bean


	//set the array list from the User method so we can create users 
	private ArrayList<User> users;


	//create the user List array 
	public UserList() {
		this.users = new ArrayList<>();
		users.add(new User("Admin User", "admin@3apets.ie", "admin123", "admin"));
		users.add(new User("Pet Attendant", "attendant@3apets.ie", "attendant123", "petAttendant"));
		users.add(new User("Test User", "user@3apets.ie", "user123", "user"));

		//		These use the new 4-argument constructor you now have in User.java — new User(name, email, password, role). When the app starts, these three accounts exist immediately so you can test all three login paths. Anyone who registers through the public registration page still gets role = "user" automatically since RegistrationBean uses the 3-argument constructor which defaults the role to "user".


	}


	//how big is the size of the array 
	public int getNumberOfUsers() {
		return users.size();
	}

	//check if an email is already registered in the colleciton 
	public boolean isEmailRegistered(String email) {
		if (email == null) return false;						// there is no email entered, it can't match.

		//enhanced loop to use search through the variable
		for (User existingUser : users) {						// create a loop to search through the users registered to the account. 
			if (email.equalsIgnoreCase(existingUser.getEmail())) {
				return true;
			}
		}return false; 											// flase statement for loop
	} // end isEmailRegistered method

	// adds user to the list only if the email is not already being used

	public boolean addUser(User user) {
		if (isEmailRegistered(user.getEmail())) {				// runs check to confirm the email is registered using the above method 
			return false ;										//if the value returns true - do not allow the add user function to proceed 
		}
		users.add(user);	// add the user to the array list
		return true;		// return the value of true when successful... 
	}


	public ArrayList<User> getUsers(){
		return users;
	}


}
