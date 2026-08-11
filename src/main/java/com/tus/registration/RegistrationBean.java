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

@Named("RegistrationBean")				//this was named incorrectly in the xhtml page. Added the captial R in the xhtml file 
@SessionScoped

public class RegistrationBean implements Serializable {
	private static final long serialVersionUID = 1L;

	//declare user object to use in the array
	private User user;

	//allows us to inject the bean into the Userlist array object identified in the user list array.
	@Inject
	private UserList userList;
	
	private String confirmPassword;

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
	
	public String getConfirmPassword() {
	    return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
	    this.confirmPassword = confirmPassword;
	}

	//registers the user before redirecting to the sample webpage.
	public String register() {
		
		if (!PasswordValidator.checkPasswordsMatch(user.getPassword(), confirmPassword)) {
	        FacesContext.getCurrentInstance().addMessage("registrationForm:confirmPasswordInput",
	                new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                        "Passwords do not match.", null));
	        return null;
	    }
		
		boolean success = userList.addUser(user);		//use the boolean in Userlist to check if the user was added to the array list 

		if (!success) {									// if not successful 
			FacesContext.getCurrentInstance().addMessage("registrationForm:emailInput", 
					new FacesMessage(FacesMessage.SEVERITY_ERROR, 
							"Email address is already registered.", null)
					);
			return null; 								// Stay on the registration page so they can fix the issue 
		}

		//if it is successful then the user was in the boolean userList.addUser(user) so we don't need to do anything else 

		user = new User(); 		
		// reset the form to allow for the next registration, clears any data stored in the system 
		confirmPassword = null;
		return "login?faces-redirect=true";  // redirects to the index page 
		
		
	}

	public String reset() {
	    user = new User();
	    confirmPassword = null;
	    return "registration?faces-redirect=true";
	}
	
	//this is used for testing 
	public void setUserList(UserList userList) {
		this.userList = userList;
	}
	
}

