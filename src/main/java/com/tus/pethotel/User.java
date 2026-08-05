package com.tus.pethotel;

import java.io.Serializable;

public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	
	private static int uuID = 0 ;		//make the unique user ID static so that object ++
	private int userID;					// we 
	private String name;
	private String email;
	private String password;

	
	public User() {				//default constructor that won't be called or do anything 
		}						// it is required by jakarta faces so will do nothing and should be easy to test 
	
	//only required fields are entered.

	public User(String name, String email, String password) {
		super();
		this.name= name;
		this.email = email;
		this.password = password;
		uuID++;				// increases the static count for how many registered users we have 
		userID = uuID;		//sets the user ID to the next value in the array
		
	}

	//GETTERS AND SETTERS


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}

	
	public String getPassword() {
		return password;
		
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

/*
 * NO SET UserID 
 */
	public int getUserID() {
		return userID;
	}
	

	

}
