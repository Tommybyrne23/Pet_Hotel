package com.tus.registration;
import java.io.Serializable;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

@Named
@SessionScoped

public class RegistrationBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String email;
	private String password;
	
	public String register() {
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
}
