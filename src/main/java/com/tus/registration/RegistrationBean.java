package com.tus.registration;

import java.io.Serializable;

//imports the java files from com.tus/pethotel
import com.tus.pethotel.User;
import com.tus.pethotel.UserList;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("RegistrationBean")
@SessionScoped

public class RegistrationBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//declare user object to use in the array
	private User user;

	//allows us to inject the bean into the Userlist array object identified in the user list array.
	@Inject
	private UserList userList;
		
	@PostConstruct
	public void init() {
		user = new User();			
	}
	
	//jakarta faces calls the blank user we've just created get User().setName.....setUser()setemail)
	public User getUser() {
		return user;
	}
	
	//writes the current information stored in the jakartaa bean with the inforamtion we have
	public void setUser(User user) {
		this.user = user;
	}
	
	//registers the user before redirecting to the sample webpage.
	public String register() {
		boolean success = userList.addUser(user);		//use the boolean in Userlist to check if the user was added to the array list 
		
		if (!success) {									// if not successful 
			FacesContext.getCurrentInstance().addMessage("registrationForm:emailInput", 
					new FacesMessage(FacesMessage.SEVERITY_ERROR, 
							"Email address is already registered.", null)
				);
			return null; 								// Stay on the registration page so they can fix the issue 
		}
		
		//if it is successful then the user was in the boolean userList.addUser(user) so we don't need to do anything else 
		
		user = new User(); 					// reset the form to allow for the next registration, clears any data stored in the system 
		return "index?faces-redirect=true";  // redirects to the index page 
	}
	
	
//	this code has been moved to user.java.	
	
//	private String name;
//	private String email;
//	private String password;
//	
//	
	
//	Getters and setters created with duplicate code
	
//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	public String getEmail() {
//		return email;
//	}
//
//	public void setEmail(String email) {
//		this.email = email;
//	}
//
//	public String getPassword() {
//		return password;
//	}
//
//	public void setPassword(String password) {
//		this.password = password;
//	}
}

