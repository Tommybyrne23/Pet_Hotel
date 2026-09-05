package com.tus.pethotel;

import java.time.LocalDateTime;

public class ContactMessage {

	private String name;
	private String email;
	private String subject;
	private String message;
	private LocalDateTime receivedAt;

	public ContactMessage() {
	}

	public ContactMessage(String name, String email, String subject, String message) {
		this.name = name;
		this.email = email;
		this.subject = subject;
		this.message = message;
		this.receivedAt = LocalDateTime.now();
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

	public String getSubject() {
		return subject; 
		}
	public void setSubject(String subject) { 
		this.subject = subject; 
		}

	public String getMessage() {
		return message; 
		}
	public void setMessage(String message) {
		this.message = message; 
		}

	public LocalDateTime getReceivedAt() {
		return receivedAt; 
		}
	public void setReceivedAt(LocalDateTime receivedAt) {
		this.receivedAt = receivedAt; 
		}
}