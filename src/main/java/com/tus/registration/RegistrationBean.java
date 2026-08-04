package com.tus.registration;
import java.io.Serializable;

import com.tus.pethotel.UserList;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@SessionScoped

public class RegistrationBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String email;
	private String password;
	private String message;

	
	//allows us to inject the bean into the Userlist array object identified in the user list array.
	@Inject
	private UserList userList;
	
	
	//registers the user before redirecting to the sample webpage.
//	public String register() {
//		userList.addUser(name, email, password);
//		return "sampleWebpage?faces-redirect=true";  // change this to index. 
//	}
	public String register() {
	    if (name == null || name.isBlank() ||
	        email == null || email.isBlank() ||
	        password == null || password.isBlank()) {
	        message = "Please fill in all required fields.";
	        return null;
	    }

	    if (userList.userExists(email)) {
	        message = "User already exists.";
	        return null;
	    }

	    userList.addUser(name, email, password);
	    message = "Account created successfully.";
	    return "sampleWebpage?faces-redirect=true";
	}

	    userList.addUser(name, email, password);
	    message = "Account created successfully.";
	    return "sampleWebpage?faces-redirect=true";
	}
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
	public String getMessage() {
	    return message;
	}

	public void setMessage(String message) {
	    this.message = message;
	}
}
