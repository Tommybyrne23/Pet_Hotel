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
	
	//allows us to inject the bean into the Userlist array object identified in the user list array.
	@Inject
	private UserList userList;
	
	
	//registers the user before redirecting to the sample webpage.
	public String register() {
		userList.addUser(name, email, password);
		return "sampleWebpage?faces-redirect=true";  // change this to index. 
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
}
