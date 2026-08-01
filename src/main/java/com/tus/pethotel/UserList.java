package com.tus.pethotel;

import java.util.ArrayList;			//import array list
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped			// use application scoped for this to run one the server  
public class UserList {

	//set the array list from the User method so we can create users 
		private ArrayList<User> users;
		

	//create the user List array 
		public UserList() {
			this.users = new ArrayList<>();
		}
		
		
		
		//how big is the size of the array 
		public int getNumberOfUsers() {
			return users.size();
		}
		
		
		//add the name from jakarta beans into the user array
		public void addUser(String name, String email, String password){
			User newUser = new User(name, email, password);			// delcare a new object in the method to be sent to the array
			users.add(newUser);										// delcare the object above as the new item to be added to the array. 
			
		}
		
		public ArrayList<User> getUsers(){
			return users;
		}
		
	//get the size of the list 
		
	
}
