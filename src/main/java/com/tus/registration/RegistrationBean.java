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

	//refactored testing so facesmessage is moved to a different method.
	public String register() {
		if (user.getName() == null || user.getName().trim().isEmpty()) {
			addFacesMessage("registrationForm:nameInput", "Your name is required.");					//return faces message 
	        return null;
		}
		
	    if (user.getEmail() == null || user.getEmail().trim().isEmpty() || !user.getEmail().contains("@")) {									//check email is not null, even though this is already checked in jakarta faces 
	        addFacesMessage("registrationForm:emailInput", "Email address is required.");					//return faces message 
	        return null;
	    }

		
	    if (!PasswordValidator.checkPasswordsMatch(user.getPassword(), confirmPassword)) {
	        addFacesMessage("registrationForm:confirmPasswordInput", "Passwords do not match.");			//send the faces message to the method below
	        return null;																					//returns a null string for JUNIT testing 
	    }

	    boolean success = userList.addUser(user);

	    if (!success) {
	        addFacesMessage("registrationForm:emailInput", "Email address is already registered.");			//same here, message goes to addFaces Message s
	        return null;																					//validation has failed, returns null for JUNIT purposes. 
	    }

	    user = new User();
	    confirmPassword = null;
	    return "login?faces-redirect=true";
	}

	// small private helper — avoids repeating the null-check twice											//this is a manual test on the registration dashboard.
	private void addFacesMessage(String clientId, String summary) {
	    FacesContext context = FacesContext.getCurrentInstance();
	    if (context != null) {
	        context.addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, null));
	    }
	}

	//method resets the form 
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

