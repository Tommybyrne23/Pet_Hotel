package com.tus.pethotel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@Named("userList")
@ApplicationScoped			// use application scoped for this to run one the server  
public class UserList implements Serializable{
		private static final long serialVersionUID =1L; 	//so it matches the registration and user bean
		
		
	//set the array list from the User method so we can create users 
		private ArrayList<User> users;
		

	//create the user List array 
		public UserList() {
			this.users = new ArrayList<>();
			users.add(new User("Admin User", "admin@3apets.ie", "admin123", Role.ADMIN));
			users.add(new User("Pet Attendant", "attendant@3apets.ie", "attendant123", Role.ATTENDANT));
			users.add(new User("Customer User", "user@3apets.ie", "user123", Role.CUSTOMER));
		}
		
				
		//how big is the size of the array 
		public int getNumberOfUsers() {
			return users.size();
		}
		
		//check if an email is already registered in the collection 
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
		
		public User findByEmail(String email) {
			if (email == null) return null;
			for (User existingUser : users) {
				if (email.equalsIgnoreCase(existingUser.getEmail())) {
					return existingUser;
				}
			}
			return null;
		}
		
		
		public ArrayList<User> getUsers(){
			return users;
		}
		
		
		/**
	     * Finds a user's name given their user ID.
	     */
	    public String getUserNameById(int userID) {
	        if (users == null) {
	            return "Unknown";
	        }

	        return users.stream()
	                .filter(u -> u.getUserID() == userID) // Match against User.getUserID()
	                .map(User::getName)                   // Map to User.getName()
	                .findFirst()
	                .orElse("Unknown");
	    }

	    public Role[] getRoles() {
	        return Role.values();
	    }
	    
}
